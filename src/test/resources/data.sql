insert into users(id, nickname, kakao_id, profile, role, is_deleted)
values (1, '닉네임1', 123, '프로필1', 'USER', 0);
insert into users(id, nickname, kakao_id, profile, role, is_deleted)
values (2, '닉네임2', 124, '프로필2', 'USER', 0);
insert into users(id, nickname, kakao_id, profile, role, is_deleted)
values (3, '닉네임3', 125, '프로필3', 'USER', 0);

insert into product(id, title, description, price, location, user_id, zzim_count, is_deleted)
values (1, '상품제목1', '상품내용1', 1500, '서울 강남구1', 1, 3, 0);
insert into product(id, title, description, price, location, user_id, zzim_count, is_deleted)
values (2, '상품제목2', '상품내용2', 1600, '서울 강남구2', 2, 1, 0);
insert into product(id, title, description, price, location, user_id, zzim_count, is_deleted)
values (3, '상품제목3', '상품내용3', 1700, '서울 강남구3', 3, 0, 0);
insert into product(id, title, description, price, location, user_id, zzim_count, is_deleted)
values (4, '상품제목4', '상품내용4', 1800, '서울 강남구4', 1, 0, 0);

insert into reservation(id, status, start_date, end_date, created_at, modified_at, user_id, product_id, is_deleted)
values (1, 'returned', '2023-04-20', '2023-04-20', '2023-04-17 05:20:07.508696', '2023-04-17 05:20:07.508696', 2, 1, 0);
insert into reservation(id, status, start_date, end_date, created_at, modified_at, user_id, product_id, is_deleted)
values (2, 'returned', '2023-04-21', '2023-04-21', '2023-04-17 05:20:07.508696', '2023-04-17 05:20:07.508696', 3, 1, 0);
insert into reservation(id, status, start_date, end_date, created_at, modified_at, user_id, product_id, is_deleted)
values (3, 'returned', '2023-04-22', '2023-04-22', '2023-04-17 05:20:07.508696', '2023-04-17 05:20:07.508696', 2, 1, 0);
insert into reservation(id, status, start_date, end_date, created_at, modified_at, user_id, product_id, is_deleted)
values (4, 'accepted', '2023-04-23', '2023-04-23', '2023-04-17 05:20:07.508696', '2023-04-17 05:20:07.508696', 2, 3, 0);
insert into reservation(id, status, start_date, end_date, created_at, modified_at, user_id, product_id, is_deleted)
values (5, 'accepted', '2023-04-24', '2023-04-24', '2023-04-17 05:20:07.508696', '2023-04-17 05:20:07.508696', 3, 1, 0);
insert into reservation(id, status, start_date, end_date, created_at, modified_at, user_id, product_id, is_deleted)
values (6, 'returned', '2023-04-25', '2023-04-25', '2023-04-17 05:20:07.508696', '2023-04-17 05:20:07.508696', 1, 2, 0);

insert into zzim(id, user_id, product_id, is_deleted)
values (1, 1, 1, 0);
insert into zzim(id, user_id, product_id, is_deleted)
values (2, 2, 1, 0);
insert into zzim(id, user_id, product_id, is_deleted)
values (3, 3, 1, 0);
insert into zzim(id, user_id, product_id, is_deleted)
values (4, 1, 2, 0);

insert into image(id, image_url, product_id, is_deleted)
values (1, 'image1', 1, 0);
insert into image(id, image_url, product_id, is_deleted)
values (2, 'image2', 2, 0);
insert into image(id, image_url, product_id, is_deleted)
values (3, 'image3', 3, 0);
insert into image(id, image_url, product_id, is_deleted)
values (4, 'image4', 4, 0);

insert into visitor(id, visitor_count)
values (1, 50);