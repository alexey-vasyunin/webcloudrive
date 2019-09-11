create table users
(
    id         serial  not null
        constraint users_pk
        primary key,
    username   varchar not null,
    password   varchar not null,
    firstname  varchar,
    lastname   varchar,
    photourl   varchar,
    isactive   boolean   default false,
    isexpiried boolean   default false,
    created    timestamp default CURRENT_TIMESTAMP,
    lastseen   timestamp
);

alter table users
    owner to cloudrive;

create unique index users_id_uindex
    on users (id);

create unique index users_username_uindex
    on users (username);

INSERT INTO public.users (id, username, password, firstname, lastname, photourl, isactive, isexpiried, created, lastseen) VALUES (1, 'user', '$2a$10$ZeChEN0IJW602heKK/T50OVYyhYGsWtgJ6tcvgSf39V6D7sVqaacC', 'Alexey', 'Vasyunin', null, true, false, '2019-09-11 10:04:38.674912', null);