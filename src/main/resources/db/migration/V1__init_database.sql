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

