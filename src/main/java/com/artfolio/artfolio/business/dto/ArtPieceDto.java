package com.artfolio.artfolio.business.dto;

import com.artfolio.artfolio.business.domain.ArtPiece;
import com.artfolio.artfolio.business.domain.ArtPiecePhoto;
import com.artfolio.artfolio.user.entity.User;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

public class ArtPieceDto {
    @Getter @Setter @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreationReq {
        private String title;
        private String content;
        private Long artistId;
    }

    @Getter @Setter @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeletionReq {
        private Long artistId;
        private Long artPieceId;
    }

    @Getter @Setter @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateTitleReq {
        private Long artistId;
        private Long artPieceId;
        private String title;
    }

    @Getter @Setter @ToString
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateContentReq {
        private Long artistId;
        private Long artPieceId;
        private String content;
    }

    @Getter @Setter @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateLikeReq {
        private Long artistId;
        private Long artPieceId;
    }

    @Getter @Setter @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArtPieceInfoRes {
        private ArtPieceInfo artPieceInfo;
        private ArtistInfo artistInfo;
        private List<String> photos;

        public static ArtPieceInfoRes of(ArtPiece artPiece) {
            User artist = artPiece.getArtist();
            List<String> artPiecePhotos = artPiece.getArtPiecePhotos()
                    .stream()
                    .map(ArtPiecePhoto::getFilePath)
                    .collect(Collectors.toList());

            return ArtPieceInfoRes.builder()
                    .artistInfo(ArtistInfo.of(artist))
                    .artPieceInfo(ArtPieceInfo.of(artPiece))
                    .photos(artPiecePhotos)
                    .build();
        }
    }

    @Getter @Builder
    @AllArgsConstructor
    private static class ArtPieceInfo {
        private Long id;
        private String title;
        private String content;
        private Integer likes;

        public static ArtPieceInfo of(ArtPiece artPiece) {
            return ArtPieceInfo.builder()
                    .id(artPiece.getId())
                    .title(artPiece.getTitle())
                    .content(artPiece.getContent())
                    .likes(artPiece.getLikes())
                    .build();
        }
    }

    @Getter @Builder
    @AllArgsConstructor
    private static class ArtistInfo {
        private Long id;
        private String username;
        private String name;
        private String email;
        private String photoPath;

        public static ArtistInfo of(User artist) {
            return ArtistInfo.builder()
                    .id(artist.getId())
                    .username(artist.getUsername())
                    .name(artist.getNickname())
                    .email(artist.getEmail())
                    .photoPath(artist.getProfilePhoto())
                    .build();
        }
    }
}
