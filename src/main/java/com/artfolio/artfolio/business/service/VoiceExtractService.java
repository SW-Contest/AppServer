package com.artfolio.artfolio.business.service;

import com.artfolio.artfolio.business.domain.AIInfo;
import com.artfolio.artfolio.business.repository.AIRedisRepository;
import com.artfolio.artfolio.global.util.S3Manager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class VoiceExtractService {
    private final AIRedisRepository aiRedisRepository;
    private final S3Manager s3Manager;

    @Value("${naver.voice.client-id}")
    private String clientId;

    @Value("${naver.voice.client-secret}")
    private String clientSecret;

    @Value("${naver.voice.api-url}")
    private String API_URL;

    private static final String SPEAKER = "nara";
    private static final String FORMAT = "mp3";
    private static final Integer VOLUME = 0;
    private static final Integer SPEED = 0;
    private static final Integer PITCH = 0;

    public String extractVoice(Long artPieceId, String text) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);

            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);

            String params = "speaker=" + SPEAKER + "&"
                    + "volume=" + VOLUME + "&"
                    + "speed=" + SPEED + "&"
                    + "pitch=" + PITCH + "&"
                    + "format=" + FORMAT + "&"
                    + "text=" + encodedText;

            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(params);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            BufferedReader br;

            // 정상 호출
            if (responseCode == 200) {
                InputStream is = con.getInputStream();
                int read = 0;
                byte[] bytes = new byte[1024];

                // 랜덤한 이름으로 mp3 파일을 생성
                String tmp = Long.valueOf(new Date().getTime()).toString();
                File f = new File(tmp + ".mp3");

                f.createNewFile();

                OutputStream outputStream = new FileOutputStream(f);

                while ((read = is.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }

                is.close();

                String voice = s3Manager.uploadArtPieceImage(artPieceId, f);
                f.delete();
                return voice;
            }

            // 오류 발생
            else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }

                br.close();
                log.error(response.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
