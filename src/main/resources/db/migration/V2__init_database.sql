INSERT INTO users (id, username, password, firstname, lastname, photourl, isactive, isexpiried, created, lastseen) VALUES (1, 'user', '$2a$10$ZeChEN0IJW602heKK/T50OVYyhYGsWtgJ6tcvgSf39V6D7sVqaacC', 'Alexey', 'Vasyunin', null, true, false, '2019-09-11 10:04:38.674912', null);

INSERT INTO roles (role_id, role_name) VALUES (1, 'ADMIN');
INSERT INTO roles (role_id, role_name) VALUES (2, 'USER');
INSERT INTO roles (role_id, role_name) VALUES (3, 'MANAGER');

INSERT INTO users_roles (role_id, user_id) VALUES (1, 1);

