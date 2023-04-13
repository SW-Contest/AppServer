/* Member 테이블 */
/* (PK, CREATED_AT, CREATED_BY, CONTENT, EMAIL, IS_CREATOR, MEMBER_LIKE, NAME, PROFILE_PHOTO */

INSERT INTO member(id, content, email, is_creator, member_like, name, profile_photo)
VALUES(1, '소개글입니다... (황승수)', 'seungsu@naver.com', true, 0, '황승수', '/images/seungsu.png');

INSERT INTO member(id, content, email, is_creator, member_like, name, profile_photo)
VALUES(2, '소개글입니다... (이지영)', 'jiyoung@naver.com', true, 10, '이지영', '/images/jiyoung.jpg');

/* ------------------------------------------------------ */

/* ArtPiece 테이블 */
/* (PK, CREATED_AT, CREATED_BY, art_piece_like, ART_CONTENT, ART_TITLE, CREATOR_ID) */
INSERT INTO art_piece(id, created_at, created_by, art_piece_like, content, title, creator_id)
VALUES(1, now(), 'seungsu', 5, '예술품 소개글1', '예술품 타이틀1', 1);

INSERT INTO art_piece(id, created_at, created_by, art_piece_like, content, title, creator_id)
VALUES(2, now(), 'seungsu', 10, '예술품 소개글2', '예술품 타이틀2', 1);

INSERT INTO art_piece(id, created_at, created_by, art_piece_like, content, title, creator_id)
VALUES(3, now(), 'seungsu', 15, '예술품 소개글3', '예술품 타이틀3', 2);

INSERT INTO art_piece(id, created_at, created_by, art_piece_like, content, title, creator_id)
VALUES(4, now(), 'seungsu', 20, '예술품 소개글4', '예술품 타이틀4', 2);

/* ------------------------------------------------------ */


/* ------------------------------------------------------ */

/* Auction 테이블 */
/* (PK, CREATED_AT, CREATED_BY, ART_START_PRICE, ART_FINAL_PRICE, IS_SOLD, ART_PIECE_ID, ART_BIDDER_ID) */

/* 예술품(1번)은 1회차에 유찰되었고 2회차에 승수(1)가 25000원에 최종 낙찰 받음 */
INSERT INTO auction(id, created_at, created_by, start_price, final_price, is_sold, art_piece_id, bidder_id)
VALUES (1, now(), 'seungsu', 15000, 15000, false, 1, NULL);

INSERT INTO auction(id, created_at, created_by, start_price, final_price, is_sold, art_piece_id, bidder_id)
VALUES (2, now(), 'seungsu', 15000, 25000, true, 1, 1);

/* 예술품(2번)은 1회차에 지영(2)이 50000원에 낙찰 받음 */
INSERT INTO auction(id, created_at, created_by, start_price, final_price, is_sold, art_piece_id, bidder_id)
VALUES (3, now(), 'seungsu', 35000, 50000, true, 2, 2);

/* 예술품(3번)은 3회차에 지영(2)이 150000원에 낙찰 받음 */
INSERT INTO auction(id, created_at, created_by, start_price, final_price, is_sold, art_piece_id, bidder_id)
VALUES (4, now(), 'seungsu', 100000, 100000, false, 3, NULL);

INSERT INTO auction(id, created_at, created_by, start_price, final_price, is_sold, art_piece_id, bidder_id)
VALUES (5, now(), 'seungsu', 100000, 100000, false, 3, NULL);

INSERT INTO auction(id, created_at, created_by, start_price, final_price, is_sold, art_piece_id, bidder_id)
VALUES (6, now(), 'seungsu', 100000, 150000, true, 3, 2);

/* 예술품(4번)은 2회차에 승수(1)가 200000원에 낙찰 받음 */
INSERT INTO auction(id, created_at, created_by, start_price, final_price, is_sold, art_piece_id, bidder_id)
VALUES (7, now(), 'seungsu', 150000, 150000, false, 4, NULL);

INSERT INTO auction(id, created_at, created_by, start_price, final_price, is_sold, art_piece_id, bidder_id)
VALUES (8, now(), 'seungsu', 160000, 200000, true, 4, 1);
