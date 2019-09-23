INSERT INTO users (id, username, password, firstname, lastname, photourl, isactive, isexpiried, created, lastseen) VALUES (1, 'user', '$2a$10$ZeChEN0IJW602heKK/T50OVYyhYGsWtgJ6tcvgSf39V6D7sVqaacC', 'Alexey', 'Vasyunin', null, true, false, '2019-09-11 10:04:38.674912', null);
INSERT INTO users (id, username, password, firstname, lastname, photourl, isactive, isexpiried, created, lastseen) VALUES (2, 'user2', '$2a$10$ZeChEN0IJW602heKK/T50OVYyhYGsWtgJ6tcvgSf39V6D7sVqaacC', 'User2', 'SecondNameU2', null, true, false, '2019-09-11 10:04:38.674912', null);

INSERT INTO roles (role_id, role_name) VALUES (1, 'ADMIN');
INSERT INTO roles (role_id, role_name) VALUES (2, 'USER');
INSERT INTO roles (role_id, role_name) VALUES (3, 'MANAGER');

INSERT INTO users_roles (role_id, user_id) VALUES (1, 1);

INSERT INTO directories (id, dirname, parent, user_id) VALUES (3, 'rootdir', null, 1);
INSERT INTO directories (id, dirname, parent, user_id) VALUES (4, 'dir1', 3, 1);
INSERT INTO directories (id, dirname, parent, user_id) VALUES (5, 'root2', null, 2);

INSERT INTO files (file_id, filename, user_id, filesize, filetype, directory_id, last_modified, origin_filename) VALUES (2, '18bfb972-fe2b-4156-96b6-c336fbc7ad46', 2, 878, 'docx', 5, '2019-09-23 15:14:22.000000', 'document(office)');
INSERT INTO files (file_id, filename, user_id, filesize, filetype, directory_id, last_modified, origin_filename) VALUES (1, '3e8eed6f-cac8-462f-8a38-5b462cb8c408', 1, 234234, 'jpg', 3, '2019-09-23 15:14:22.000000', 'original_photo');
INSERT INTO files (file_id, filename, user_id, filesize, filetype, directory_id, last_modified, origin_filename) VALUES (4, 'dea709a1-b664-438e-9e5f-af4557f74f2d', 1, 543, 'txt', 4, '2019-09-23 16:14:26.000000', 'some_text_in_file');
INSERT INTO files (file_id, filename, user_id, filesize, filetype, directory_id, last_modified, origin_filename) VALUES (3, '692fa7ea-9e59-4266-bde1-4cc80d0a9474', 1, 2, 'xlsx', 3, '2019-09-23 16:06:18.000000', 'rent_car');