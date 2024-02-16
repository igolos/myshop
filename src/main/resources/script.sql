insert into "product" (name, quantity, image_url, description,price)
values ('Кола',  25,'https://basket-07.wb.ru/vol1115/part111509/111509752/images/c246x328/1.jpg','газированный напиток',50),
       ('Фанта', 35,'https://avatars.mds.yandex.net/get-eda/2050703/272641595403bce9a97b313026f90b8b/M_height','газированный напиток',50);

insert into "role"(name) values ( 'ROLE_USER'),('ROLE_ADMIN' );

insert into "users"(login, name, password) values ( 'user','user','Qwerty12_' ),('admin','admin','admin');
insert into "users_roles" ("roles_id", "user_id") values ( 1,1 ),(2,2);