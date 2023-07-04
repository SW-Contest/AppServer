package com.artfolio.artfolio.business.service;

import com.artfolio.artfolio.business.domain.ArtPiecePhoto;
import com.artfolio.artfolio.business.dto.ImageDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImageService {
    private static final String DEFAULT_IMAGE_DIR = System.getProperty("user.dir")
            + "/src/main/resources/images";
    private final ArtPiecePhotoRepository artPiecePhotoRepository;
    private final ArtPieceRepository artPieceRepository;
    private final UserRepository userRepository;
    private final S3Manager s3Manager;
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
            String s3Path = s3Manager.upload(artPieceId, img);
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

        System.out.println("---------------- 1 --------------------");

        List<ArtPiecePhoto> artPiecePhotos = artPiece.getArtPiecePhotos();

        for (ArtPiecePhoto artPiecePhoto : artPiecePhotos) {
            String s3Filename = artPiecePhoto.getFileName() + "." + artPiecePhoto.getFileExtension();

            if (s3Filename.equals(fileName)) {
                String fileKey = "static/" + artistId + "/" + fileName;

                try {
                    boolean isExist = s3Manager.doesObjectExist(fileKey);

                    if (isExist) {
                        s3Manager.deleteObject(fileKey);
                        artPiecePhotoRepository.deleteById(artPiecePhoto.getId());
                        artPiecePhotoRepository.flush();
                        return 1L;
                    }
                } catch (Exception e) {
                    log.debug("S3 Delete Failed", e);
                }
            }
        }

        return 0L;
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
