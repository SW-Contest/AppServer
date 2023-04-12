/* Member 테이블 */
/* (PK, CREATED_AT, CREATED_BY, MODIFIED_AT, MODIFIED_BY, CONTENT, EMAIL, IS_CREATOR, MEMBER_LIKE, NAME, PROFILE_PHOTO */

INSERT INTO member(id, created_at, created_by, modified_at, modified_by, member_content, member_email, is_creator, member_like, member_name, profile_photo)
VALUES(1, now(), 'seungsu', now(), 'seungsu', '소개글입니다... (황승수)', 'seungsu@naver.com', true, 0, '항승수', '/images/seungsu.png');

INSERT INTO member(id, created_at, created_by, modified_at, modified_by, member_content, member_email, is_creator, member_like, member_name, profile_photo)
VALUES(2, now(), 'seungsu', now(), 'seungsu', '소개글입니다... (이지영)', 'jiyoung@naver.com', true, 10, '이지영', '/images/jiyoung.jpg');

/* ------------------------------------------------------ */

/* ArtPiece 테이블 */
/* (PK, CREATED_AT, CREATED_BY, MODIFIED_AT, MODIFIED_BY, ART_LIKE, ART_CONTENT, ART_TITLE, CREATOR_ID) */
INSERT INTO art_piece(id, created_at, created_by, modified_at, modified_by, art_piece_like, art_piece_content, art_piece_title, creator_id)
VALUES(1, now(), 'seungsu', now(), 'seungsu', 5, '예술품 소개글1', '예술품 타이틀1', 1);

INSERT INTO art_piece(id, created_at, created_by, modified_at, modified_by, art_piece_like, art_piece_content, art_piece_title, creator_id)
VALUES(2, now(), 'seungsu', now(), 'seungsu', 10, '예술품 소개글2', '예술품 타이틀2', 1);

INSERT INTO art_piece(id, created_at, created_by, modified_at, modified_by, art_piece_like, art_piece_content, art_piece_title, creator_id)
VALUES(3, now(), 'seungsu', now(), 'seungsu', 15, '예술품 소개글3', '예술품 타이틀3', 2);

INSERT INTO art_piece(id, created_at, created_by, modified_at, modified_by, art_piece_like, art_piece_content, art_piece_title, creator_id)
VALUES(4, now(), 'seungsu', now(), 'seungsu', 20, '예술품 소개글4', '예술품 타이틀4', 2);

/* ------------------------------------------------------ */

/* ArtPiecePhoto 테이블 */
/* (PK, CREATED_AT, CREATED_BY, MODIFIED_AT, MODIFIED_BY, PATH, ART_PIECE_ID) */

/* ArtPiece 1번에 대한 사진 목록 */
INSERT INTO art_piece_photo(id, created_at, created_by, modified_at, modified_by, art_piece_photo_path, art_piece_id)
VALUES (1, now(), 'seungsu', now(), 'seungsu', '/image/artpiece/aaa1.jpg', 1);

INSERT INTO art_piece_photo(id, created_at, created_by, modified_at, modified_by, art_piece_photo_path, art_piece_id)
VALUES (2, now(), 'seungsu', now(), 'seungsu', '/image/artpiece/aaa2.jpg', 1);

INSERT INTO art_piece_photo(id, created_at, created_by, modified_at, modified_by, art_piece_photo_path, art_piece_id)
VALUES (3, now(), 'seungsu', now(), 'seungsu', '/image/artpiece/aaa2.jpg', 1);

/* ArtPiece 2번에 대한 사진 목록 */
INSERT INTO art_piece_photo(id, created_at, created_by, modified_at, modified_by, art_piece_photo_path, art_piece_id)
VALUES (4, now(), 'seungsu', now(), 'seungsu', '/image/artpiece/bbb1.jpg', 2);

INSERT INTO art_piece_photo(id, created_at, created_by, modified_at, modified_by, art_piece_photo_path, art_piece_id)
VALUES (5, now(), 'seungsu', now(), 'seungsu', '/image/artpiece/bbb2.jpg', 2);

INSERT INTO art_piece_photo(id, created_at, created_by, modified_at, modified_by, art_piece_photo_path, art_piece_id)
VALUES (6, now(), 'seungsu', now(), 'seungsu', '/image/artpiece/bbb3.jpg', 2);

/* ArtPiece 3번에 대한 사진 목록 */
INSERT INTO art_piece_photo(id, created_at, created_by, modified_at, modified_by, art_piece_photo_path, art_piece_id)
VALUES (7, now(), 'seungsu', now(), 'seungsu', '/image/artpiece/ccc1.jpg', 3);

INSERT INTO art_piece_photo(id, created_at, created_by, modified_at, modified_by, art_piece_photo_path, art_piece_id)
VALUES (8, now(), 'seungsu', now(), 'seungsu', '/image/artpiece/ccc2.jpg', 3);

INSERT INTO art_piece_photo(id, created_at, created_by, modified_at, modified_by, art_piece_photo_path, art_piece_id)
VALUES (9, now(), 'seungsu', now(), 'seungsu', '/image/artpiece/ccc3.jpg', 3);

/* ArtPiece 4번에 대한 사진 목록 */
INSERT INTO art_piece_photo(id, created_at, created_by, modified_at, modified_by, art_piece_photo_path, art_piece_id)
VALUES (10, now(), 'seungsu', now(), 'seungsu', '/image/artpiece/ddd1.jpg', 4);

INSERT INTO art_piece_photo(id, created_at, created_by, modified_at, modified_by, art_piece_photo_path, art_piece_id)
VALUES (11, now(), 'seungsu', now(), 'seungsu', '/image/artpiece/ddd2.jpg', 4);

INSERT INTO art_piece_photo(id, created_at, created_by, modified_at, modified_by, art_piece_photo_path, art_piece_id)
VALUES (12, now(), 'seungsu', now(), 'seungsu', '/image/artpiece/ddd3.jpg', 4);

/* ------------------------------------------------------ */

/* Auction 테이블 */
/* (PK, CREATED_AT, CREATED_BY, MODIFIED_AT, MODIFIED_BY, ART_START_PRICE, ART_FINAL_PRICE, IS_SOLD, ART_PIECE_ID, ART_BIDDER_ID) */

/* 예술품(1번)은 1회차에 유찰되었고 2회차에 승수(1)가 25000원에 최종 낙찰 받음 */
INSERT INTO auction(id, created_at, created_by, modified_at, modified_by, auction_start_price, auction_final_price, is_sold, art_piece_id, art_bidder_id)
VALUES (1, now(), 'seungsu', now(), 'seungsu', 15000, 15000, false, 1, NULL);

INSERT INTO auction(id, created_at, created_by, modified_at, modified_by, auction_start_price, auction_final_price, is_sold, art_piece_id, art_bidder_id)
VALUES (2, now(), 'seungsu', now(), 'seungsu', 15000, 25000, true, 1, 1);

/* 예술품(2번)은 1회차에 지영(2)이 50000원에 낙찰 받음 */
INSERT INTO auction(id, created_at, created_by, modified_at, modified_by, auction_start_price, auction_final_price, is_sold, art_piece_id, art_bidder_id)
VALUES (3, now(), 'seungsu', now(), 'seungsu', 35000, 50000, true, 2, 2);

/* 예술품(3번)은 3회차에 지영(2)이 150000원에 낙찰 받음 */
INSERT INTO auction(id, created_at, created_by, modified_at, modified_by, auction_start_price, auction_final_price, is_sold, art_piece_id, art_bidder_id)
VALUES (4, now(), 'seungsu', now(), 'seungsu', 100000, 100000, false, 3, NULL);

INSERT INTO auction(id, created_at, created_by, modified_at, modified_by, auction_start_price, auction_final_price, is_sold, art_piece_id, art_bidder_id)
VALUES (5, now(), 'seungsu', now(), 'seungsu', 100000, 100000, false, 3, NULL);

INSERT INTO auction(id, created_at, created_by, modified_at, modified_by, auction_start_price, auction_final_price, is_sold, art_piece_id, art_bidder_id)
VALUES (6, now(), 'seungsu', now(), 'seungsu', 100000, 150000, true, 3, 2);

/* 예술품(4번)은 2회차에 승수(1)가 200000원에 낙찰 받음 */
INSERT INTO auction(id, created_at, created_by, modified_at, modified_by, auction_start_price, auction_final_price, is_sold, art_piece_id, art_bidder_id)
VALUES (7, now(), 'seungsu', now(), 'seungsu', 150000, 150000, false, 4, NULL);

INSERT INTO auction(id, created_at, created_by, modified_at, modified_by, auction_start_price, auction_final_price, is_sold, art_piece_id, art_bidder_id)
VALUES (8, now(), 'seungsu', now(), 'seungsu', 160000, 200000, true, 4, 1);
