INSERT INTO users (id, username, password, firstname, lastname, photourl, isactive, isexpiried, isblocked, created, lastseen) VALUES (1, 'user', '$2a$10$ZeChEN0IJW602heKK/T50OVYyhYGsWtgJ6tcvgSf39V6D7sVqaacC', 'Alexey', 'Vasyunin', null, true, false, false, '2019-09-11 10:04:38.674912', null);
INSERT INTO users (id, username, password, firstname, lastname, photourl, isactive, isexpiried, isblocked, created, lastseen) VALUES (2, 'user2', '$2a$10$ZeChEN0IJW602heKK/T50OVYyhYGsWtgJ6tcvgSf39V6D7sVqaacC', 'User2', 'SecondNameU2', null, true, false, false, '2019-09-11 10:04:38.674912', null);
select setval('users_id_seq', 2);

INSERT INTO roles (role_id, role_name) VALUES (0, 'NO_ROLE');
INSERT INTO roles (role_id, role_name) VALUES (1, 'ADMIN');
INSERT INTO roles (role_id, role_name) VALUES (2, 'USER');
INSERT INTO roles (role_id, role_name) VALUES (3, 'MANAGER');
select setval('roles_role_id_seq', 3);

INSERT INTO users_roles (role_id, user_id) VALUES (1, 1);

INSERT INTO directories (id, dirname, parent, user_id) VALUES (3, 'rootdir', null, 1);
INSERT INTO directories (id, dirname, parent, user_id) VALUES (4, 'dir1', 3, 1);
INSERT INTO directories (id, dirname, parent, user_id) VALUES (5, 'root2', null, 2);
select setval('directories_id_seq', 5);


INSERT INTO files (file_id, filename, user_id, filesize, filetype, directory_id, last_modified, origin_filename, is_completed) VALUES (2, '18bfb972-fe2b-4156-96b6-c336fbc7ad46', 2, 878, 'docx', 5, '2019-09-23 15:14:22.000000', 'document(office).docx', true);
INSERT INTO files (file_id, filename, user_id, filesize, filetype, directory_id, last_modified, origin_filename, is_completed) VALUES (1, '3e8eed6f-cac8-462f-8a38-5b462cb8c408', 1, 234234, 'jpg', 3, '2019-09-23 15:14:22.000000', 'original_photo.jpg', true);
INSERT INTO files (file_id, filename, user_id, filesize, filetype, directory_id, last_modified, origin_filename, is_completed) VALUES (4, 'dea709a1-b664-438e-9e5f-af4557f74f2d', 1, 543, 'txt', 4, '2019-09-23 16:14:26.000000', 'some_text_in_file.txt', true);
INSERT INTO files (file_id, filename, user_id, filesize, filetype, directory_id, last_modified, origin_filename, is_completed) VALUES (3, '692fa7ea-9e59-4266-bde1-4cc80d0a9474', 1, 2, 'xlsx', 3, '2019-09-23 16:06:18.000000', 'rent_car.xlsx', true);
select setval('files_file_id_seq', 4);
