package com.artfolio.artfolio.global.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Manager {
    private final AmazonS3Client amazonS3Client;
    private static final String DEFAULT_ART_PIECE_DIR = "static/artPiece/";

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /* S3 파일 업로드 메서드 */
    public String uploadArtPieceImage(Long artPieceId, File uploadFile) {
        String PATH = DEFAULT_ART_PIECE_DIR + artPieceId + "/";
        return putS3(uploadFile, PATH + uploadFile.getName());
    }

    /* 파일 확인 메서드 */
    public boolean doesObjectExist(Long artPieceId, String fileName) {
        String PATH = DEFAULT_ART_PIECE_DIR + artPieceId + "/" + fileName;
        return amazonS3Client.doesObjectExist(bucket, PATH);
    }

    /* 파일 삭제 메서드 */
    public void deleteObject(Long artPieceId, String fileName) {
        String PATH = DEFAULT_ART_PIECE_DIR + artPieceId + "/" + fileName;
        amazonS3Client.deleteObject(bucket, PATH);
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }
}
