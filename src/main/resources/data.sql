--categoryテーブル
INSERT IGNORE INTO category (id, category) VALUES (1, '定食');
INSERT IGNORE INTO category (id, category) VALUES (2, '焼肉');
INSERT IGNORE INTO category (id, category) VALUES (3, '寿司');
INSERT IGNORE INTO category (id, category) VALUES (4, '手羽先');
INSERT IGNORE INTO category (id, category) VALUES (5, '天ぷら');
INSERT IGNORE INTO category (id, category) VALUES (6, 'ラーメン');
INSERT IGNORE INTO category (id, category) VALUES (7, 'うどん');
INSERT IGNORE INTO category (id, category) VALUES (8, '居酒屋');
INSERT IGNORE INTO category (id, category) VALUES (9, 'イタリアン');
INSERT IGNORE INTO category (id, category) VALUES (10, '喫茶店');
INSERT IGNORE INTO category (id, category) VALUES (11, 'カフェ');
INSERT IGNORE INTO category (id, category) VALUES (12, '焼き鳥');
INSERT IGNORE INTO category (id, category) VALUES (13, '鉄板焼き');
INSERT IGNORE INTO category (id, category) VALUES (14, 'スイーツ');
INSERT IGNORE INTO category (id, category) VALUES (15, 'パン');



--roleテーブル
INSERT IGNORE INTO roles (id, name) VALUES (1, 'ROLE_GENERAL');
INSERT IGNORE INTO roles (id, name) VALUES (2, 'ROLE_PREMIUM');
INSERT IGNORE INTO roles (id, name) VALUES (3, 'ROLE_ADMIN');


--shopテーブル
INSERT IGNORE INTO shop (id, name, image_name, description, category_id, address, phone_number,email,created_at, updated_at) VALUES (1, 'いちょうの実', 'shop1.jpg', '味が自慢のお店です。', 1, '愛知県名古屋市中区栄2-17-1','052-111-0000', 'taro.samurai@example.com','2024-12-25','2024-12-25');
INSERT IGNORE INTO shop (id, name, image_name, description, category_id, address, phone_number,email,created_at, updated_at) VALUES (2, 'いてふの実', 'shop1.jpg', '名古屋の味をご堪能ください。', 1, '愛知県名古屋市中区栄2-17-1','052-111-0000', 'taro.samurai@example.com','2024-12-25','2024-12-25');
INSERT IGNORE INTO shop (id, name, image_name, description, category_id, address, phone_number,email,created_at, updated_at) VALUES (3, 'おきなぐさ', 'shop1.jpg', 'リーズナブルな価格です。', 1, '愛知県名古屋市中区栄2-17-1','052-111-0000', 'taro.samurai@example.com','2024-12-25','2024-12-25');
INSERT IGNORE INTO shop (id, name, image_name, description, category_id, address, phone_number,email,created_at, updated_at) VALUES (4, '革トランク', 'shop1.jpg', '名古屋の味をご堪能ください。', 1, '愛知県名古屋市中区栄2-17-1','052-111-0000', 'taro.samurai@example.com','2024-12-25','2024-12-25');
INSERT IGNORE INTO shop (id, name, image_name, description, category_id, address, phone_number,email,created_at, updated_at) VALUES (5, '黄いろのトマト', 'shop1.jpg', '名古屋の味をご堪能ください。', 1, '愛知県名古屋市中区栄2-17-1','052-111-0000', 'taro.samurai@example.com','2024-12-25','2024-12-25');
INSERT IGNORE INTO shop (id, name, image_name, description, category_id, address, phone_number,email,created_at, updated_at) VALUES (6, '銀河鉄道の夜', 'shop2.jpg', '名古屋の味をご賞味ください。', 1, '愛知県名古屋市中区栄2-17-1','052-111-0000', 'taro.samurai@example.com','2024-12-25','2024-12-25');
INSERT IGNORE INTO shop (id, name, image_name, description, category_id, address, phone_number,email,created_at, updated_at) VALUES (7, '幻想', 'shop5.jpg', '名古屋の味をご堪能ください。', 1, '愛知県名古屋市中区栄2-17-1','052-111-0000', 'taro.samurai@example.com','2024-12-25','2024-12-25');
INSERT IGNORE INTO shop (id, name, image_name, description, category_id, address, phone_number,email,created_at, updated_at) VALUES (8, '県道', 'shop2.jpg', '名古屋コーチンを使用。', 1, '愛知県名古屋市中区栄2-17-1','052-111-0000', 'taro.samurai@example.com','2024-12-25','2024-12-25');
INSERT IGNORE INTO shop (id, name, image_name, description, category_id, address, phone_number,email,created_at, updated_at) VALUES (9, '校庭', 'shop2.jpg', '名古屋の味をご堪能ください。', 1, '愛知県名古屋市中区栄2-17-1','052-111-0000', 'taro.samurai@example.com','2024-12-25','2024-12-25');
INSERT IGNORE INTO shop (id, name, image_name, description, category_id, address, phone_number,email,created_at, updated_at) VALUES (10, '氷と後光', 'shop2.jpg', '名古屋の味を賞味ください。', 1, '愛知県名古屋市中区栄2-17-1','052-111-0000', 'taro.samurai@example.com','2024-12-25','2024-12-25');
INSERT IGNORE INTO shop (id, name, image_name, description, category_id, address, phone_number,email,created_at, updated_at) VALUES (11, '国柱会', 'shop3.jpg', '二次会にぜひ！。', 1, '愛知県名古屋市中区栄2-17-1','052-111-0000', 'taro.samurai@example.com','2024-12-25','2024-12-25');
INSERT IGNORE INTO shop (id, name, image_name, description, category_id, address, phone_number,email,created_at, updated_at) VALUES (12, 'こゝろ', 'shop3.jpg', '名古屋の味をご堪能ください。', 1, '愛知県名古屋市中区栄2-17-1','052-111-0000', 'taro.samurai@example.com','2024-12-25','2024-12-25');
INSERT IGNORE INTO shop (id, name, image_name, description, category_id, address, phone_number,email,created_at, updated_at) VALUES (13, 'さるのこしかけ', 'shop3.jpg', '名古屋コーチンを使用。', 1, '愛知県名古屋市中区栄2-17-1','052-111-0000', 'taro.samurai@example.com','2024-12-25','2024-12-25');
INSERT IGNORE INTO shop (id, name, image_name, description, category_id, address, phone_number,email,created_at, updated_at) VALUES(14, '樹園', 'shop3.jpg', '名古屋の味をご堪能ください。', 1, '愛知県名古屋市中区栄2-17-1','052-111-0000', 'taro.samurai@example.com','2024-12-25','2024-12-25');
INSERT IGNORE INTO shop (id, name, image_name, description, category_id, address, phone_number,email,created_at, updated_at) VALUES(15, 'セロ弾きのゴーシュ', 'shop3.jpg', '名古屋の味をご堪能ください。', 1, '愛知県名古屋市中区栄2-17-1','052-111-0000', 'taro.samurai@example.com','2024-12-25','2024-12-25');
INSERT IGNORE INTO shop (id, name, image_name, description, category_id, address, phone_number,email,created_at, updated_at) VALUES (16, '谷', 'shop5.jpg', '名古屋の味をご堪能ください。', 1, '愛知県名古屋市中区栄2-17-1','052-111-0000', 'taro.samurai@example.com','2024-12-25','2024-12-25');
INSERT IGNORE INTO shop (id, name, image_name, description, category_id, address, phone_number,email,created_at, updated_at) VALUES (17, '月夜のけだもの', 'shop4.jpg', 'リーズナブルな価格です。', 1, '愛知県名古屋市中区栄2-17-1','052-111-0000', 'taro.samurai@example.com','2024-12-25','2024-12-25');
INSERT IGNORE INTO shop (id, name, image_name, description, category_id, address, phone_number,email,created_at, updated_at) VALUES (18, '手紙', 'shop4.jpg', '名古屋の味を賞味ください。', 1, '愛知県名古屋市中区栄2-17-1','052-111-0000', 'taro.samurai@example.com','2024-12-25','2024-12-25');
INSERT IGNORE INTO shop (id, name, image_name, description, category_id, address, phone_number,email,created_at, updated_at) VALUES(19, 'どんぐりと山猫', 'shop4.jpg', '味には自信があります。', 1, '愛知県名古屋市中区栄2-17-1','052-111-0000', 'taro.samurai@example.com','2024-12-25','2024-12-25');
INSERT IGNORE INTO shop (id, name, image_name, description, category_id, address, phone_number,email,created_at, updated_at) VALUES (20, '沼森', 'shop4.jpg', '秘伝のたれを使っています。', 1, '愛知県名古屋市中区栄2-17-1','052-111-0000', 'taro.samurai@example.com','2024-12-25','2024-12-25');
INSERT IGNORE INTO shop (id, name, image_name, description, category_id, address, phone_number,email,created_at, updated_at) VALUES (21, '猫', 'shop5.jpg', '旅行の方にもおすすめ。', 1, '愛知県名古屋市中区栄2-17-1','052-111-0000', 'taro.samurai@example.com','2024-12-25','2024-12-25');
INSERT IGNORE INTO shop (id, name, image_name, description, category_id, address, phone_number,email,created_at, updated_at) VALUES (22, '星めぐりの歌', 'shop5.jpg', '名古屋の味をご堪能ください。', 1, '愛知県名古屋市中区栄2-17-1','052-111-0000', 'taro.samurai@example.com','2024-12-25','2024-12-25');




--userテーブル
INSERT IGNORE INTO users (id, name, furigana, email, password, role_id, enabled, created_at, updated_at) VALUES (1, '侍太郎', 'サムライタロウ','taro.samurai@example.com','$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO',1,1,'2025-01-20 00:00:00','2025-01-29 10:10:10');
INSERT IGNORE INTO users (id, name, furigana, email, password, role_id, enabled, created_at, updated_at) VALUES (2, '侍花子', 'サムライハナコ','hanako.samurai@email.com','$2a$10$GPr2c0MIf.4RHXXOjjef0eLzQBeA6Em07MU91mLbExApirZbQN5Rm',1,0,'2025-01-20 00:00:00','2025-01-29 10:10:10');
INSERT IGNORE INTO users (id, name, furigana, email, password, role_id, enabled, created_at, updated_at) VALUES (3, '侍 義勝', 'サムライ ヨシカツ','yosikatu.samurai@email.com','$2a$10$KXs/fgtZ5FTypBCquXAQwuC7hWod1qs7NWDYJSe8M2Kgq7kZvMapC',1,0,'2025-01-20 00:00:00','2025-01-29 10:10:10');
INSERT IGNORE INTO users (id, name, furigana, email, password, role_id, enabled, created_at, updated_at) VALUES (4, '侍 幸美', 'サムライ サチミ','satimi.samurai@email.com','$2a$10$sryfCtVqpkOh0DrtC0GRY.gOTND8kYCV0QnmwdN4zvGzu3Z7FxYvS',1,1,'2025-01-20 00:00:00','2025-01-29 10:10:10');
INSERT IGNORE INTO users (id, name, furigana, email, password, role_id, enabled, created_at, updated_at) VALUES (5, '侍 雅', 'サムライ ミヤビ','miyabi.samurai@email.com','$2a$10$9yCw88/hdg0Q3dK6tqgeEuPWDG48oWXWmMwLd6kVTmQa5RJb.9h/6',1,1,'2025-01-20 00:00:00','2025-01-29 10:10:10');
INSERT IGNORE INTO users (id, name, furigana, email, password, role_id, enabled, created_at, updated_at) VALUES (6, '侍 正保', 'サムライ マサヤス','masayasu.samurai@email.com','$2a$10$E0WRAGkfvncOd1pG/Y6joOodHyTmSgklAH0QcPg5vqnjkFULVnK0G',1,0,'2025-01-20 00:00:00','2025-01-29 10:10:10');
INSERT IGNORE INTO users (id, name, furigana, email, password, role_id, enabled, created_at, updated_at) VALUES(7, '侍 真由美', 'サムライ マユミ','mayumi.samurai@email.com','$2a$10$RgvZi/8lW9Yn1PFerRKVGeHXryk/U8xKFO2f1xrLNOIxlqcIlnHcO',1,1,'2025-01-20 00:00:00','2025-01-29 10:10:10');
INSERT IGNORE INTO users (id, name, furigana, email, password, role_id, enabled, created_at, updated_at) VALUES (8, '侍 安民', 'サムライ ヤスタミ','yasutami.samurai.samurai@example.com','$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO',2,1,'2025-01-20 00:00:00','2025-01-29 10:10:10');
INSERT IGNORE INTO users (id, name, furigana, email, password, role_id, enabled, created_at, updated_at) VALUES (9, '侍 章緒', 'サムライ アキオ','sabu.samurai.samurai@example.com','$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO',3,1,'2025-01-20 00:00:00','2025-01-29 10:10:10');


--reservationsテーブル
INSERT IGNORE INTO reservations (id, shop_id, user_id, checkin_date,created_at, updated_at) VALUES (1, 1, 1, '2025-02-01','2025-02-01','2025-02-01');


--reviewテーブル

INSERT IGNORE INTO review(id,user_id,shop_id,content,created_at) VALUES(1,1,1,5,'2025-01-18 00:00:00');
INSERT IGNORE INTO review(id,user_id,shop_id,content,created_at) VALUES(2,2,1,5,'2025-01-17 00:00:00');
INSERT IGNORE INTO review(id,user_id,shop_id,content,created_at) VALUES(3,3,1,5,'2025-01-17 00:00:00');
INSERT IGNORE INTO review(id,user_id,shop_id,content,created_at) VALUES(4,4,1,5,'2025-01-17 00:00:00');
INSERT IGNORE INTO review(id,user_id,shop_id,content,created_at) VALUES(5,5,1,4,'2025-01-17 00:00:00');
INSERT IGNORE INTO review(id,user_id,shop_id,content,created_at) VALUES(6,6,1,5,'2025-01-17 00:00:00');
INSERT IGNORE INTO review(id,user_id,shop_id,content,created_at) VALUES(7,1,2,3,'2025-01-21 00:00:00');
INSERT IGNORE INTO review(id,user_id,shop_id,content,created_at) VALUES(8,2,3,4,'2025-01-30 00:00:00');
INSERT IGNORE INTO review(id,user_id,shop_id,content,created_at) VALUES(9,3,4,5,'2025-02-02 00:00:00');
INSERT IGNORE INTO review(id,user_id,shop_id,content,created_at) VALUES(10,4,5,4,'2025-02-09 00:00:00');
INSERT IGNORE INTO review(id,user_id,shop_id,content,created_at) VALUES(11,5,6,5,'2025-02-12 00:00:00');
INSERT IGNORE INTO review(id,user_id,shop_id,content,created_at) VALUES(12,1,3,1,'2025-02-03 00:00:00');
INSERT IGNORE INTO review(id,user_id,shop_id,content,created_at) VALUES(14,7,1,2,'2025-02-08 00:00:00');