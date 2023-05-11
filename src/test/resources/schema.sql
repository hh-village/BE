drop table if exists users CASCADE;
create table users
(
    id bigint auto_increment,
    kakao_id bigint,
    nickname varchar(255),
    profile varchar(255),
    role varchar(255),
    is_deleted bit default 0,
    primary key (id)
);

drop table if exists product CASCADE;
create table product
(
    id bigint auto_increment,
    created_at datetime(6),
    modified_at datetime(6),
    description varchar(255),
    location varchar(255),
    price int,
    title varchar(255),
    user_id bigint,
    zzim_count int,
    is_deleted bit default 0,
    primary key (id),
    foreign key (user_id) references users(id)
);

drop table if exists reservation CASCADE;
create table reservation
(
    id bigint auto_increment,
    created_at datetime(6),
    modified_at datetime(6),
    end_date date,
    start_date date,
    status varchar(255),
    product_id bigint,
    user_id bigint,
    is_deleted bit default 0,
    primary key (id),
    foreign key (user_id) references users(id),
    foreign key (product_id) references product(id)
);

drop table if exists zzim CASCADE;
create table zzim
(
    id bigint auto_increment,
    product_id bigint,
    user_id bigint,
    is_deleted bit default 0,
    primary key (id),
    foreign key (user_id) references users(id),
    foreign key (product_id) references product(id)
);

drop table if exists image CASCADE;
create table image
(
    id bigint auto_increment,
    image_url varchar(500),
    product_id bigint,
    is_deleted bit default 0,
    primary key (id),
    foreign key (product_id) references product(id)
);

drop table if exists visitor CASCADE;
create table visitor
(
    id bigint auto_increment,
    visitor_count int,
    primary key (id)
);