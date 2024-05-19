insert into "product" (name, quantity, image_url, description,price)
values ('Газировки',  25,'https://basket-07.wb.ru/vol1115/part111509/111509752/images/c246x328/1.jpg','Кола',50),
       ('Соки', 35,'https://www.healthbenefitstimes.com/9/gallery/navel-orange/Navel-orange-juice.jpg','Апельсиновый',50);

insert into "role"(name) values ( 'ROLE_USER'),('ROLE_ADMIN' );

insert into "users"(login, name, password) values ( 'user','user','Qwerty12_' ),('admin','admin','admin');
insert into "users_roles" ("roles_id", "user_id") values ( 1,1 ),(2,2);