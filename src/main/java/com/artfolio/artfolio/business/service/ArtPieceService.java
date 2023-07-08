package com.artfolio.artfolio.business.service;

import com.artfolio.artfolio.business.domain.ArtPiece;
import com.artfolio.artfolio.business.domain.ArtPiecePhoto;
import com.artfolio.artfolio.business.domain.UserArtPiece;
import com.artfolio.artfolio.business.dto.ArtPieceDto;
import com.artfolio.artfolio.business.dto.ImageDto;
import com.artfolio.artfolio.business.repository.ArtPieceRepository;
import com.artfolio.artfolio.business.repository.UserArtPieceRepository;
import com.artfolio.artfolio.global.exception.ArtPieceNotFoundException;
import com.artfolio.artfolio.global.exception.UserNotFoundException;
import com.artfolio.artfolio.user.entity.User;
import com.artfolio.artfolio.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArtPieceService {
    private final ArtPieceRepository artPieceRepository;
    // private final ArtPiecePhotoRepository artPiecePhotoRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final UserArtPieceRepository userArtPieceRepository;

    public Long createArtPiece(ArtPieceDto.CreationReq req) {
        log.info("[ createArtPiece() ] req : {}", req);

        Long artistId = req.getArtistId();
        User user = userRepository.findById(artistId)
                .orElseThrow(() -> new UserNotFoundException(artistId));

        ArtPiece artPiece = artPieceRepository.saveAndFlush(ArtPiece.of(req, user));
        user.addArtPiece(artPiece);

        return artPiece.getId();
    }

    @Transactional(readOnly = true)
    public ArtPieceDto.ArtPieceInfoRes getArtPiece(Long artPieceId) {
        ArtPiece artPiece = artPieceRepository.findById(artPieceId)
                .orElseThrow(() -> new ArtPieceNotFoundException(artPieceId));

        return ArtPieceDto.ArtPieceInfoRes.of(artPiece);
    }

    public Long deleteArtPiece(ArtPieceDto.DeletionReq req) {
        log.info("[ deleteArtPiece() ] req : {}", req);

        Long artistId = req.getArtistId();
        Long artPieceId = req.getArtPieceId();

        User user = userRepository.findById(artistId)
                .orElseThrow(() -> new UserNotFoundException(artistId));

        ArtPiece artPiece = artPieceRepository.findById(artPieceId)
                .orElseThrow(() -> new ArtPieceNotFoundException(artPieceId));

        if (!user.equals(artPiece.getArtist())) {
            return 0L;
        }

        List<ArtPiecePhoto> artPiecePhotos = artPiece.getArtPiecePhotos();

        for (ArtPiecePhoto artPiecePhoto : artPiecePhotos) {
            ImageDto.DeleteReq deleteReq = new ImageDto.DeleteReq(
                    artistId,
                    artPieceId,
                    artPiecePhoto.getFileName() + "." + artPiecePhoto.getFileExtension()
            );

            imageService.deleteFile(deleteReq);
        }

        // artPiecePhotoRepository.deleteAll(artPiecePhotos); --> cascade
        artPieceRepository.delete(artPiece);

        return 1L;
    }

    public Long updateTitle(ArtPieceDto.UpdateTitleReq req) {
        Long artistId = req.getArtistId();
        Long artPieceId = req.getArtPieceId();
        String title = req.getTitle();

        User user = userRepository.findById(artistId)
                .orElseThrow(() -> new UserNotFoundException(artistId));

        ArtPiece artPiece = artPieceRepository.findById(artPieceId)
                .orElseThrow(() -> new ArtPieceNotFoundException(artPieceId));

        if (!user.equals(artPiece.getArtist())) {
            return 0L;
        }

        artPiece.updateTitle(title);
        return 1L;
    }

    public Long updateContent(ArtPieceDto.UpdateContentReq req) {
        Long artistId = req.getArtistId();
        Long artPieceId = req.getArtPieceId();
        String content = req.getContent();

        User user = userRepository.findById(artistId)
                .orElseThrow(() -> new UserNotFoundException(artistId));

        ArtPiece artPiece = artPieceRepository.findById(artPieceId)
                .orElseThrow(() -> new ArtPieceNotFoundException(artPieceId));

        if (!user.equals(artPiece.getArtist())) {
            return 0L;
        }

        artPiece.updateContent(content);
        return 1L;
    }

    public Integer updateLike(ArtPieceDto.UpdateLikeReq req) {
        Long artistId = req.getArtistId();
        Long artPieceId = req.getArtPieceId();

        User user = userRepository.findById(artistId)
                .orElseThrow(() -> new UserNotFoundException(artistId));

        ArtPiece artPiece = artPieceRepository.findById(artPieceId)
                .orElseThrow(() -> new ArtPieceNotFoundException(artPieceId));

        Optional<UserArtPiece> userArtPieceOp = userArtPieceRepository.findByUserAndArtPiece(user, artPiece);

        if (userArtPieceOp.isPresent()) {
            UserArtPiece userArtPiece = userArtPieceOp.get();
            if (userArtPiece.getIsLiked()) artPiece.decreaseLike(userArtPiece);
            else artPiece.increaseLike(userArtPiece);
        } else {
            UserArtPiece userArtPiece = new UserArtPiece(user, artPiece);
            userArtPieceRepository.save(userArtPiece);
            artPiece.updateUserArtPiece(userArtPiece);
            artPiece.increaseLike(userArtPiece);
        }

        return artPieceRepository.saveAndFlush(artPiece).getLikes();
    }
}
