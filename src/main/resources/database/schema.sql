create table hibernate_sequence
(
    next_val bigint null
);

create table must_eat_place
(
    id             bigint auto_increment
        primary key,
    address        varchar(255) null,
    city           varchar(255) null,
    menu           varchar(255) null,
    operation_hour varchar(255) null,
    rate           varchar(255) null,
    restaurant     varchar(255) null,
    registered_at  datetime     null,
    updated_at     datetime     null
);

create table user
(
    id                bigint auto_increment
        primary key,
    company           varchar(255) null,
    latitude          double       null,
    longitude         double       null,
    registered_at     datetime     null,
    user_email        varchar(255) null,
    user_name         varchar(10)  null,
    user_password     varchar(255) null,
    user_phone_number varchar(12)  null,
    user_rate         double       null,
    updated_at        datetime     null
);

create table bookmark_spot
(
    id             bigint auto_increment
        primary key,
    address        varchar(255) null,
    menu           varchar(255) null,
    operation_hour varchar(255) null,
    rate           varchar(255) null,
    registered_at  datetime     null,
    restaurant     varchar(255) null,
    user_id        bigint       null,
    updated_at     datetime     null,
    constraint FKne4tidw6hivo2ly5ceftevund
        foreign key (user_id) references user (id)
);

create table favorite_user
(
    id               bigint       not null
        primary key,
    nick_name        varchar(255) null,
    registered_at    datetime     null,
    favorite_user_id bigint       null,
    user_id          bigint       null,
    updated_at       datetime     null,
    constraint FKp3dhuamhj6dhqaiamusooq4i4
        foreign key (favorite_user_id) references user (id),
    constraint FKtfu89v11ff2qst40m2ne497hv
        foreign key (user_id) references user (id)
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
    request_message varchar(255) null,
    updated_at      datetime     null,
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
    requester_id  bigint   null,
    accepter_id   bigint   null,
    updated_at    datetime null,
    constraint FK_accepter_id
        foreign key (accepter_id) references user (id),
    constraint FK_requester_id
        foreign key (requester_id) references user (id),
    constraint FKhntpm3o6bqyq7ldfqlgjm9xlt
        foreign key (plan_id) references plan (id)
);

create table user_review
(
    id             bigint       not null
        primary key,
    rate           double       null,
    registered_at  datetime     null,
    review_content varchar(255) null,
    updated_at     datetime     null,
    plan_id        bigint       null,
    reviewer_id    bigint       null,
    targeted_id    bigint       null,
    constraint FK6r4d45i008kyohe6tob4dbnd4
        foreign key (targeted_id) references user (id),
    constraint FKkysqcqfumpg7bg7l7wba26a4n
        foreign key (reviewer_id) references user (id),
    constraint FKr44nufscxienme7cm55klxycg
        foreign key (plan_id) references plan (id)
);