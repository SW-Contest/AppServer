package com.artfolio.artfolio.business.service;

import com.artfolio.artfolio.business.domain.ArtPiece;
import com.artfolio.artfolio.business.dto.ArtPieceDto;
import com.artfolio.artfolio.business.repository.ArtPieceRepository;
import com.artfolio.artfolio.global.exception.UserNotFoundException;
import com.artfolio.artfolio.user.entity.User;
import com.artfolio.artfolio.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ArtPieceService {
    private final ArtPieceRepository artPieceRepository;
    private final UserRepository userRepository;

    public Long createArtPiece(ArtPieceDto.CreationReq req) {
        log.info("[ createArtPiece() ] req : {}", req);

        Long artistId = req.getArtistId();
        User user = userRepository.findById(artistId)
                .orElseThrow(() -> new UserNotFoundException(artistId));

        ArtPiece artPiece = ArtPiece.of(req, user);
        user.addArtPiece(artPiece);

        return artPieceRepository.save(artPiece).getId();
    }
}
