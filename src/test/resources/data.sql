-- Member 데이터 삽입 (member_id 사용하여 생성)
INSERT INTO Member (id, authority, blame_count, blamed_count, create_time, email, nickname, provider, provider_id, refresh_token, token)
VALUES
(1, 'USER', 0, 0, '2024-01-01 12:00:00', 'user1@example.com', 'User1', 'google', 'google-1', NULL, NULL),
(2, 'USER', 0, 0, '2024-01-02 12:00:00', 'user2@example.com', 'User2', 'google', 'google-2', NULL, NULL),
(3, 'USER', 0, 0, '2024-01-03 12:00:00', 'user3@example.com', 'User3', 'google', 'google-3', NULL, NULL),
(4, 'USER', 0, 0, '2024-01-04 12:00:00', 'user4@example.com', 'User4', 'google', 'google-4', NULL, NULL),
(5, 'USER', 0, 0, '2024-01-05 12:00:00', 'user5@example.com', 'User5', 'google', 'google-5', NULL, NULL);

-- Post 데이터 삽입
INSERT INTO Post (post_id, blamed_count, emoticon, post_create_time, post_like, post_picture, secret, member_id)
VALUES
(1, 0, '', 20250130115515, 0, 0, 1, 1),
(2, 0, '', 20241129143717, 0, 0, 1, 2),
(3, 0, '', 20241109013125, 0, 0, 1, 3),
(4, 0, '', 20241112130859, 0, 0, 1, 4),
(5, 0, '', 20241114192227, 0, 0, 1, 5),
(6, 0, '', 20250129070949, 0, 0, 1, 1),
(7, 0, '', 20241024212529, 0, 0, 1, 2),
(8, 0, '', 20250125070428, 0, 0, 1, 3),
(9, 0, '', 20250129065131, 0, 0, 1, 4),
(10, 0, '', 20250206205819, 0, 0, 1, 5);

-- HashTagMap 데이터 삽입
INSERT INTO HashTagMap (hash_tag_id, post_id)
VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5),
(6, 6),
(7, 7),
(8, 8),
(9, 9),
(10, 10);
INSERT INTO HashTag (hash_tag_id, name, tag_count) VALUES
('1', 'hashtag_1', '14'),
('2', 'hashtag_2', '14'),
('3', 'hashtag_3', '14'),
('4', 'hashtag_4', '14'),
('5', 'hashtag_5', '14'),
('6', 'hashtag_6', '14'),
('7', 'hashtag_7', '14'),
('8', 'hashtag_8', '14'),
('9', 'hashtag_9', '14'),
('10', 'hashtag_10', '14');
