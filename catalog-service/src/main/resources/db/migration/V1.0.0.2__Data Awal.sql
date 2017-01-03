insert into merchants (id, code, name) values 
('m001', 'M-001', 'Merchant 001');

insert into products (id, id_merchant, code, slug, name, price, thumbnail) values
('p001', 'm001', 'P-001',  'p001m001', 'Product 001', 101.00, '/p001/img001.jpg');

insert into products (id, id_merchant, code, slug, name, price, thumbnail) values
('p002', 'm001', 'P-002',  'p002m001', 'Product 002', 102.00, '/p002/img001.jpg');

insert into products (id, id_merchant, code, slug, name, price, thumbnail) values
('p003', 'm001', 'P-003',  'p003m001', 'Product 003', 103.00, '/p003/img001.jpg');

insert into product_photos (id, id_product, url, caption) values
('i1001', 'p001', '/p001/img001.jpg', 'Image 001 Product 001');

insert into product_photos (id, id_product, url, caption) values
('i1002', 'p001', '/p001/img002.jpg', 'Image 002 Product 001');

insert into product_photos (id, id_product, url, caption) values
('i1003', 'p001', '/p001/img003.jpg', 'Image 003 Product 001');

insert into product_photos (id, id_product, url, caption) values
('i2001', 'p002', '/p002/img001.jpg', 'Image 001 Product 002');

insert into product_photos (id, id_product, url, caption) values
('i2002', 'p002', '/p002/img002.jpg', 'Image 002 Product 002');

insert into product_photos (id, id_product, url, caption) values
('i2003', 'p002', '/p002/img003.jpg', 'Image 003 Product 002');

insert into product_photos (id, id_product, url, caption) values
('i3001', 'p003', '/p003/img001.jpg', 'Image 001 Product 003');

insert into product_photos (id, id_product, url, caption) values
('i3002', 'p003', '/p003/img002.jpg', 'Image 002 Product 003');

insert into product_photos (id, id_product, url, caption) values
('i3003', 'p003', '/p003/img003.jpg', 'Image 003 Product 003');
