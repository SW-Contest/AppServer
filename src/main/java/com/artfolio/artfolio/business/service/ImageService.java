package com.artfolio.artfolio.business.service;

import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.*;
import com.artfolio.artfolio.business.domain.AIInfo;
import com.artfolio.artfolio.business.domain.ArtPiecePhoto;
import com.artfolio.artfolio.business.dto.AuctionDto;
import com.artfolio.artfolio.business.dto.ImageDto;
import com.artfolio.artfolio.business.repository.AIRedisRepository;
import com.artfolio.artfolio.business.repository.ArtPiecePhotoRepository;
import com.artfolio.artfolio.business.repository.ArtPieceRepository;
import com.artfolio.artfolio.business.domain.ArtPiece;
import com.artfolio.artfolio.global.exception.ArtPieceNotFoundException;
import com.artfolio.artfolio.global.exception.UserNotFoundException;
import com.artfolio.artfolio.global.util.ImageUtil;
import com.artfolio.artfolio.global.util.S3Manager;
import com.artfolio.artfolio.user.entity.User;
import com.artfolio.artfolio.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImageService {
    private final AmazonRekognitionClient rekognitionClient;
    private static final String DEFAULT_IMAGE_DIR = System.getProperty("user.dir")
            + "/src/main/resources/images";
    private static final String REKOGNITION_BUCKET_NAME = "artfolio-bucket";
    private final ArtPiecePhotoRepository artPiecePhotoRepository;
    private final ArtPieceRepository artPieceRepository;
    private final UserRepository userRepository;
    private final S3Manager s3Manager;
    private final AIRedisRepository aiRedisRepository;
    private final ChatGptService chatGptService;
    private String thumbnailFileName;

    @Transactional
    public Long uploadImage(Long artistId, Long artPieceId, MultipartFile[] files) {
        ArtPiece artPiece = artPieceRepository.findById(artPieceId)
                .orElseThrow(() -> new ArtPieceNotFoundException(artPieceId));

        User user = userRepository.findById(artistId)
                .orElseThrow(() -> new UserNotFoundException(artistId));

        if (!Objects.equals(user.getId(), artPiece.getArtist().getId())) {
            return 0L;
        }

        File imgDir = new File(DEFAULT_IMAGE_DIR);

        boolean isSaved = saveImageOnLocal(files, imgDir);
        if (!isSaved) return 0L;

        // S3에 업로드, 로컬 이미지 삭제, DB 저장 (DB에는 원본만 저장)
        List<ArtPiecePhoto> photos = new ArrayList<>();

        for (File img : Objects.requireNonNull(imgDir.listFiles())) {
            String s3Path = s3Manager.uploadArtPieceImage(artPieceId, img);
            String fileFullName = img.getName();
            String fileName = fileFullName.substring(0, fileFullName.indexOf("."));
            String ext = fileFullName.substring(fileFullName.indexOf(".") + 1);

            if (!fileName.contains("_compressed")) {
                ArtPiecePhoto entity = ArtPiecePhoto.builder()
                        .fileName(fileName)
                        .filePath(s3Path)
                        .fileExtension(ext)
                        .size(img.length())
                        .isThumbnail(fileName.equals(thumbnailFileName))
                        .artPiece(artPiece)
                        .build();

                photos.add(entity);
                artPiece.updatePhoto(entity);
            }
        }

        // 로컬 경로 내 모든 사진 삭제
        for (File img : Objects.requireNonNull(imgDir.listFiles())) {
            img.delete();
        }

        artPiecePhotoRepository.saveAll(photos);

        return 1L;
    }

    @Transactional
    public Long deleteFile(ImageDto.DeleteReq req) {
        Long artistId = req.getArtistId();
        Long artPieceId = req.getArtPieceId();
        String fileName = req.getFileName();

        ArtPiece artPiece = artPieceRepository.findById(artPieceId)
                .orElseThrow(() -> new ArtPieceNotFoundException(artPieceId));

        User user = userRepository.findById(artistId)
                .orElseThrow(() -> new UserNotFoundException(artistId));

        if (!Objects.equals(user.getId(), artPiece.getArtist().getId())) {
            return 0L;
        }

        List<ArtPiecePhoto> artPiecePhotos = artPiece.getArtPiecePhotos();

        for (ArtPiecePhoto artPiecePhoto : artPiecePhotos) {
            String s3Filename = artPiecePhoto.getFileName() + "." + artPiecePhoto.getFileExtension();

            if (s3Filename.equals(fileName)) {
                try {
                    boolean isExist = s3Manager.doesObjectExist(artPieceId, fileName);

                    if (isExist) {
                        log.info("file 발견! -> filename : {}, s3Filename : {}", fileName, s3Filename);
                        s3Manager.deleteObject(artPieceId, s3Filename);
                        artPiecePhotoRepository.deleteById(artPiecePhoto.getId());
                        return 1L;
                    }
                } catch (Exception e) {
                    log.debug("S3 Delete Failed", e);
                }
            }
        }

        return 0L;
    }

    public List<Label> analyzeS3BucketImage(Long artPieceId, String path) {
        String S3_PATH = "static/" + "artPiece/" + artPieceId + "/" + path;

        S3Object s3Object = new S3Object()
                .withBucket(REKOGNITION_BUCKET_NAME)
                .withName(S3_PATH);

        Image image = new Image().withS3Object(s3Object);

        DetectLabelsRequest request = new DetectLabelsRequest()
                .withImage(image)
                .withMaxLabels(10);

        DetectLabelsResult detectLabelsResult = rekognitionClient.detectLabels(request);

        return detectLabelsResult.getLabels();
    }

    public AuctionDto.AIInfo analyzeImage(Long artPieceId) {
        Optional<AIInfo> aiInfoOp = aiRedisRepository.findById(artPieceId);

        if (aiInfoOp.isPresent()) {
            AIInfo aiInfo = aiInfoOp.get();
            return AuctionDto.AIInfo.of(aiInfo.getLabels(), aiInfo.getContent());
        }

        ArtPiece artPiece = artPieceRepository.findById(artPieceId)
                .orElseThrow(() -> new ArtPieceNotFoundException(artPieceId));

        List<ArtPiecePhoto> artPiecePhotos = artPiece.getArtPiecePhotos();

        try {
            if (artPiecePhotos.isEmpty()) {
                throw new Exception("해당 예술품에 등록된 사진이 없습니다.");
            }

            ArtPiecePhoto artPiecePhoto = artPiecePhotos.get(0);

            String DEFAULT_BUCKET_PATH = "static/artPiece/" + artPieceId + "/";
            String FILE_NAME = artPiecePhoto.getFileName() + "." + artPiecePhoto.getFileExtension();
            String S3_PATH = DEFAULT_BUCKET_PATH + FILE_NAME;

            S3Object s3Object = new S3Object()
                    .withBucket(REKOGNITION_BUCKET_NAME)
                    .withName(S3_PATH);

            Image image = new Image().withS3Object(s3Object);

            DetectLabelsRequest request = new DetectLabelsRequest()
                    .withImage(image)
                    .withMaxLabels(10);

            DetectLabelsResult detectLabelsResult = rekognitionClient.detectLabels(request);
            List<Label> labels = detectLabelsResult.getLabels();

            String content = chatGptService.createDesc(artPieceId, labels);

            AIInfo aiInfo = AIInfo.builder()
                    .artPieceId(artPieceId)
                    .labels(labels)
                    .content(content)
                    .build();

            aiRedisRepository.save(aiInfo);
            return AuctionDto.AIInfo.of(labels, content);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /* resources/images 경로에 원본 이미지와 압축된 이미지를 생성해주는 메서드 */
    private boolean saveImageOnLocal(MultipartFile[] files, File imgDir) {
        if (!imgDir.exists()) imgDir.mkdirs();

        boolean isFirst = true;
        for (MultipartFile img : files) {
            if (img.isEmpty()) continue;

            File imgFile = new File(DEFAULT_IMAGE_DIR + "/" + img.getOriginalFilename());
            String fileFullName = imgFile.getName();
            String fileName = fileFullName.substring(0, fileFullName.indexOf("."));
            String ext = fileFullName.substring(fileFullName.indexOf(".") + 1);

            if (!ext.equals("jpg") && !ext.equals("jpeg") && !ext.equals("png")) continue;

            try {
                img.transferTo(imgFile);
                if (isFirst) {
                    ImageUtil.imageResize(DEFAULT_IMAGE_DIR, fileName, ext);
                    isFirst = false;
                    thumbnailFileName = fileName;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }
}
