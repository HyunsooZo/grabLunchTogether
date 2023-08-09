create table user
(
    id                bigint auto_increment
        primary key,
    company           varchar(255) null,
    latitude          double       null,
    longitude         double       null,
    registered_at     datetime     null,
    user_email        varchar(255) null,
    user_name         varchar(20) null,
    user_password     varchar(255) null,
    user_phone_number varchar(20) null,
    user_rate         double       null
);

create table plan
(
    id              bigint auto_increment
        primary key,
    registered_at   datetime     null,
    plan_menu       varchar(255) null,
    plan_restaurant varchar(255) null,
    plan_status     varchar(255) null,
    plan_time       datetime     null,
    accepter_id     bigint       null,
    requester_id    bigint       null,
    constraint FK6c23clrqxcs96wtpu0v80wadq
        foreign key (requester_id) references user (id),
    constraint FKa74bo77da4qotcs7mfm4c6dev
        foreign key (accepter_id) references user (id)
);

create table plan_history
(
    id            bigint auto_increment
        primary key,
    registered_at datetime null,
    plan_id       bigint   null,
    constraint FKhntpm3o6bqyq7ldfqlgjm9xlt
        foreign key (plan_id) references plan (id)
);

ALTER TABLE plan_history
    ADD COLUMN requester_id BIGINT,
    ADD COLUMN accepter_id BIGINT,
    ADD CONSTRAINT FK_requester_id FOREIGN KEY (requester_id) REFERENCES user (id),
    ADD CONSTRAINT FK_accepter_id FOREIGN KEY (accepter_id) REFERENCES user (id);

create table review
(
    id             bigint       not null
        primary key,
    rate           double       null,
    registered_at  datetime     null,
    review_content varchar(255) null,
    updated_at     datetime     null,
    commenter_id   bigint       null,
    plan_id        bigint       null,
    reviewed_id    bigint       null,
    constraint FKkyyk7cdv7d1rh2v4b5jbt3b5y
        foreign key (reviewed_id) references user (id),
    constraint FKomheyskkgdrvp8f4d59ou606s
        foreign key (plan_id) references plan (id),
    constraint FKreb79wjxdshw4s1bhc49woxi5
        foreign key (commenter_id) references user (id)
);

create table must_eat_place
(
    id             bigint auto_increment
        primary key,
    address        varchar(255) null,
    operation_hour varchar(255) null,
    rate           varchar(255) null,
    restaurant     varchar(255) null,
    city           varchar(255) null,
    menu           varchar(255) null
);

create table favorite_spot
(
    id            bigint auto_increment
        primary key,
    menu          varchar(255) null,
    registered_at datetime     null,
    restaurant    varchar(255) null,
    user_id       bigint       null,
    constraint FKrlf4gs8a0xgnm038ve7dub8y2
        foreign key (user_id) references user (id)
);

create table favorite_user
(
    id               bigint       not null
        primary key,
    favorite_user_id bigint       null,
    nick_name        varchar(255) null,
    registered_at    datetime     null,
    user_id          bigint       null,
    constraint FKtfu89v11ff2qst40m2ne497hv
        foreign key (user_id) references user (id)
);




