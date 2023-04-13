package com.artfolio.artfolio.controller;

import com.artfolio.artfolio.dto.ArtPieceImageReq;
import com.artfolio.artfolio.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.artfolio.artfolio.util.ImageUtil.imageResize;

@Slf4j
@RequestMapping(value = "/image")
@RequiredArgsConstructor
@Controller
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/upload")
    public String uploadArtPiecePhoto(
            @RequestParam("artpieceId") Long artPieceId,
            @RequestParam("thumbnail") Boolean isThumbnail,
            @RequestParam("files") MultipartFile[] files,
            Model model
    ) throws IllegalStateException, IOException {

        /* TODO: S3를 구축하고 파일 경로를 S3 주소로 변경 */
        String saveDir = System.getProperty("user.dir") + "/src/main/resources/images";
        File dir = new File(saveDir);
        List<ArtPieceImageReq> images = new ArrayList<>();

        if (!dir.exists()) dir.mkdirs();

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            String originalFileName = file.getOriginalFilename();
            String ext = originalFileName.substring(originalFileName.indexOf(".") + 1);

            if (!ext.equals("jpg") && !ext.equals("jpeg") && !ext.equals("png")) continue;

            // rename
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSS");
            String hashedFileName = sdf.format(new Date(System.currentTimeMillis()));
            String hashedFileFullName = hashedFileName + "." + ext;

            ArtPieceImageReq dto = ArtPieceImageReq.builder()
                    .originalFileName(file.getOriginalFilename())
                    .hashedFileName(hashedFileFullName)
                    .extension(ext)
                    .size(file.getSize())
                    .contentType(file.getContentType())
                    .build();

            images.add(dto);
            log.info("DTO 생성: {}", dto);

            file.transferTo(new File(saveDir + "/" + hashedFileFullName));
            imageResize(saveDir, hashedFileName, ext);
        }

        imageService.uploadImage(artPieceId, saveDir, isThumbnail, images);
        model.addAttribute("files", images);

        return "result";
    }
}
