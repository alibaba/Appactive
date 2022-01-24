create table product(
	id char(100) not null primary key,
	description varchar(2000) not null,
	name char(100) not null,
	number int not null,
	price int not null
);

insert into product.product (id, description, name, number, price)
  values (12, 'description of 12', 'name of description', 50, 1559);
