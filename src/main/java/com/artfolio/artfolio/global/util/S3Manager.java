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

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /* S3 파일 업로드 메서드 */
    public String upload(Long artPieceId, File uploadFile) {
        String DEFAULT_DIR = "static/" + artPieceId;
        String fileName = DEFAULT_DIR + "/" + uploadFile.getName();
        return putS3(uploadFile, fileName);
    }

    /* 파일 확인 메서드 */
    public boolean doesObjectExist(String fileKey) {
        return amazonS3Client.doesObjectExist(bucket, fileKey);
    }

    /* 파일 삭제 메서드 */
    public void deleteObject(String fileKey) {
        log.info("s3 파일 삭제! 파일 이름 : {}", fileKey);
        amazonS3Client.deleteObject(bucket, fileKey);
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }
}
