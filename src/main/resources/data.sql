/* Member 테이블 */
/* (PK, CREATED_AT, CREATED_BY, CONTENT, EMAIL, IS_CREATOR, MEMBER_LIKE, NAME, PROFILE_PHOTO */

INSERT INTO member(id, content, email, is_artist, member_like, name, profile_photo)
VALUES(1, '소개글입니다... (황승수)', 'seungsu@naver.com', true, 0, '황승수', '/images/seungsu.png');

INSERT INTO member(id, content, email, is_artist, member_like, name, profile_photo)
VALUES(2, '소개글입니다... (이지영)', 'jiyoung@naver.com', true, 10, '이지영', '/images/jiyoung.jpg');

/* ------------------------------------------------------ */

/* ArtPiece 테이블 */
/* (PK, CREATED_AT, CREATED_BY, art_piece_like, ART_CONTENT, ART_TITLE, artist_id
) */
INSERT INTO art_piece(id, created_at, created_by, art_piece_like, content, title, artist_id)
VALUES(1, now(), 'seungsu', 5, '예술품 소개글1', '예술품 타이틀1', 1);

INSERT INTO art_piece(id, created_at, created_by, art_piece_like, content, title, artist_id
)
VALUES(2, now(), 'seungsu', 10, '예술품 소개글2', '예술품 타이틀2', 1);

INSERT INTO art_piece(id, created_at, created_by, art_piece_like, content, title, artist_id
)
VALUES(3, now(), 'seungsu', 15, '예술품 소개글3', '예술품 타이틀3', 2);

INSERT INTO art_piece(id, created_at, created_by, art_piece_like, content, title, artist_id
)
VALUES(4, now(), 'seungsu', 20, '예술품 소개글4', '예술품 타이틀4', 2);

INSERT INTO art_piece(id, created_at, created_by, art_piece_like, content, title, artist_id
)
VALUES(5, now(), 'seungsu', 25, '예술품 소개글5', '예술품 타이틀5', 1);

INSERT INTO art_piece(id, created_at, created_by, art_piece_like, content, title, artist_id
)
VALUES(6, now(), 'seungsu', 30, '예술품 소개글6', '예술품 타이틀6', 2);

INSERT INTO art_piece(id, created_at, created_by, art_piece_like, content, title, artist_id
)
VALUES(7, now(), 'seungsu', 35, '예술품 소개글7', '예술품 타이틀7', 1);

INSERT INTO art_piece(id, created_at, created_by, art_piece_like, content, title, artist_id
)
VALUES(8, now(), 'seungsu', 40, '예술품 소개글8', '예술품 타이틀8', 1);

INSERT INTO art_piece(id, created_at, created_by, art_piece_like, content, title, artist_id
)
VALUES(9, now(), 'seungsu', 45, '예술품 소개글9', '예술품 타이틀9', 2);

INSERT INTO art_piece(id, created_at, created_by, art_piece_like, content, title, artist_id
)
VALUES(10, now(), 'seungsu', 55, '예술품 소개글10', '예술품 타이틀10', 1);

INSERT INTO art_piece(id, created_at, created_by, art_piece_like, content, title, artist_id
)
VALUES(11, now(), 'seungsu', 10, '예술품 소개글11', '예술품 타이틀11', 2);

INSERT INTO art_piece(id, created_at, created_by, art_piece_like, content, title, artist_id
)
VALUES(12, now(), 'seungsu', 45, '예술품 소개글12', '예술품 타이틀12', 2);

INSERT INTO art_piece(id, created_at, created_by, art_piece_like, content, title, artist_id
)
VALUES(13, now(), 'seungsu', 45, '예술품 소개글13', '예술품 타이틀13', 1);

/* ------------------------------------------------------ */


/* ------------------------------------------------------ */

/* Auction 테이블 */
/* (PK, CREATED_AT, CREATED_BY, ART_START_PRICE, ART_FINAL_PRICE, IS_SOLD, ART_PIECE_ID, ART_BIDDER_ID) */