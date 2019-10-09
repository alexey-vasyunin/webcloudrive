-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create table users
(
    id serial constraint users_pk primary key,
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

create table roles
(
    role_id serial,
    role_name varchar(32) not null
);

create unique index roles_role_id_uindex on roles (role_id);
create unique index roles_role_name_uindex on roles (role_name);
alter table roles add constraint roles_pk primary key (role_id);
alter table users owner to cloudrive;
create unique index users_id_uindex on users (id);
create unique index users_username_uindex on users (username);

create table users_roles
(
    user_id integer not null constraint users_roles_users_id_fk references users,
    role_id integer not null constraint users_roles_roles_role_id_fk references roles
);

alter table users_roles owner to cloudrive;

create table filetypes
(
    type_id serial not null constraint filetypes_pk primary key,
    name varchar(255) not null
);

alter table filetypes owner to cloudrive;
create unique index filetypes_name_uindex on filetypes (name);
create unique index filetypes_type_id_uindex on filetypes (type_id);

create table directories
(
    id bigserial not null constraint directories_pk primary key,
    dirname varchar(255) not null,
    parent integer constraint directories_directories_id_fk references directories,
    user_id integer not null constraint directories_users_id_fk references users
);

alter table directories owner to cloudrive;

create table files
(
    file_id bigserial not null constraint files_pk primary key,
    filename varchar(36) default uuid_generate_v4(),
    origin_filename varchar(255) not null,
    user_id integer not null constraint files_users_id_fk references users,
    filesize integer not null,
    filetype varchar(255) not null,
    directory_id integer constraint files_directories_id_fk references directories,
    last_modified timestamp not null,
    is_completed boolean default false
);

alter table files owner to cloudrive;

create unique index files_file_id_uindex on files (file_id);

create table registration_token
(
    user_id bigint not null
        constraint registration_token_users_id_fk
            references users,
    token varchar(56) default uuid_generate_v4(),
    created timestamp default CURRENT_TIMESTAMP,
    id bigserial not null
        constraint registration_token_pk
            primary key
);

alter table registration_token owner to cloudrive;

create unique index registration_token_token_uindex
    on registration_token (token);

create unique index registration_token_user_id_uindex
    on registration_token (user_id);




