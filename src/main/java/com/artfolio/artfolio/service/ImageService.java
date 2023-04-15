package com.artfolio.artfolio.service;

import com.artfolio.artfolio.domain.ArtPiece;
import com.artfolio.artfolio.domain.ArtPiecePhoto;
import com.artfolio.artfolio.exception.ArtPieceNotFoundException;
import com.artfolio.artfolio.repository.ArtPiecePhotoRepository;
import com.artfolio.artfolio.repository.ArtPieceRepository;
import com.artfolio.artfolio.util.ImageUtil;
import com.artfolio.artfolio.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Transactional
@RequiredArgsConstructor
@Service
public class ImageService {
    private final ArtPiecePhotoRepository artPiecePhotoRepository;
    private final ArtPieceRepository artPieceRepository;
    private final S3Uploader s3Uploader;
    private static final String DEFAULT_IMAGE_DIR = System.getProperty("user.dir")
            + "/src/main/resources/images";

    public Long uploadImage(Long artPieceId, MultipartFile[] files) {
        ArtPiece artPiece = artPieceRepository.findById(artPieceId)
                .orElseThrow(() -> new ArtPieceNotFoundException(artPieceId));

        File imgDir = new File(DEFAULT_IMAGE_DIR);

        boolean isSaved = saveImageOnLocal(files, imgDir);
        if (!isSaved) return 0L;

        // S3에 업로드, 로컬 이미지 삭제, DB 저장 (DB에는 원본만 저장)
        List<ArtPiecePhoto> photos = new ArrayList<>();

        for (File img : Objects.requireNonNull(imgDir.listFiles())) {
            String s3Path = s3Uploader.upload(img);
            String fileFullName = img.getName();
            String fileName = fileFullName.substring(0, fileFullName.indexOf("."));
            String ext = fileFullName.substring(fileFullName.indexOf(".") + 1);

            if (fileName.contains("_compressed")) continue;

            ArtPiecePhoto entity = ArtPiecePhoto.builder()
                    .fileName(fileName)
                    .filePath(s3Path)
                    .fileExtension(ext)
                    .size(img.length())
                    .artPiece(artPiece)
                    .build();

            photos.add(entity);
            img.delete();
        }

        artPiecePhotoRepository.saveAll(photos);
        return 1L;
    }

    /* resources/images 경로에 원본 이미지와 압축된 이미지를 생성해주는 메서드 */
    private boolean saveImageOnLocal(MultipartFile[] files, File imgDir) {
        if (!imgDir.exists()) imgDir.mkdirs();

        for (MultipartFile img : files) {
            if (img.isEmpty()) continue;

            File imgFile = new File(DEFAULT_IMAGE_DIR + "/" + img.getOriginalFilename());
            String fileFullName = imgFile.getName();
            String fileName = fileFullName.substring(0, fileFullName.indexOf("."));
            String ext = fileFullName.substring(fileFullName.indexOf(".") + 1);

            if (!ext.equals("jpg") && !ext.equals("jpeg") && !ext.equals("png")) continue;

            try {
                img.transferTo(imgFile);
                ImageUtil.imageResize(DEFAULT_IMAGE_DIR, fileName, ext);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }
}
