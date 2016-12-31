drop table if exists products;
drop table if exists merchants;

create table merchants (
  id varchar(36),
  code varchar(10) not null,
  name varchar(255) not null,
  primary key (id),
  unique (code)
);

create table products (
  id varchar(36),
  id_merchant varchar(36) not null,
  code varchar(10) not null,
  name varchar(255) not null,
  price number(19,2) not null,
  primary key (id),
  unique (code),
  foreign key (id_merchant) references merchants(id)
);

create table product_photos (
  id varchar(36),
  id_product varchar(36) not null,
  url varchar(255) not null,
  caption varchar(255),
  primary key (id), 
  foreign key (id_product) references products(id)
);