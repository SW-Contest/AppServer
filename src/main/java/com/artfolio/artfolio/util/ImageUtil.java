package com.artfolio.artfolio.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtil {
    /* 원본 파일 경로를 입력받아, 리사이즈를 수행하고 압축된 이미지를 같은 경로에 저장해주는 메서드 */
    public static void imageResize(String filePath, String fileName, String ext) throws IOException {
        File originalFile = new File(filePath + "/" + fileName + "." + ext);
        InputStream is = new FileInputStream(originalFile);
        Image img = new ImageIcon(originalFile.toString()).getImage();

        /* 원본대비 0.7배로 해상도 감축 */
        int width = (int) (img.getWidth(null) * 0.7);
        int height = (int) (img.getHeight(null) * 0.7);

        BufferedImage resizedImage = resize(is, width, height);
        File resizedFile = new File(filePath + "/" + fileName + "_compressed" + "." + ext);
        ImageIO.write(resizedImage, ext, resizedFile);
    }

    /* 리사이즈 실행 메서드 */
    public static BufferedImage resize(InputStream is, int width, int height) throws IOException {
        BufferedImage inputImage = ImageIO.read(is);
        BufferedImage outputImage = new BufferedImage(width, height, inputImage.getType());

        Graphics2D graphics2D = outputImage.createGraphics();
        graphics2D.drawImage(inputImage, 0, 0, width, height, null);
        graphics2D.dispose();

        return outputImage;
    }
}
