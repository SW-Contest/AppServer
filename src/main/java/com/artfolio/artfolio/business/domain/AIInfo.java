package com.artfolio.artfolio.business.domain;

import com.amazonaws.services.rekognition.model.Label;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Getter @AllArgsConstructor @ToString @Builder
@RedisHash(value = "ai", timeToLive = 3600 * 24 * 7)
public class AIInfo {
    @Id private Long artPieceId;
    private String content;
    private List<Label> labels;
    private String voice;
}
