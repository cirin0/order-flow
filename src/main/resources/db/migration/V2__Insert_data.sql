INSERT INTO categories (id, created_at, image_url, name, updated_at)
VALUES (2, '2025-02-13 16:11:12.214695', 'https://gotomexico.today/media/img/tequila-1.jpg', 'Алкоголь',
        '2025-02-14 21:53:20.911923');
INSERT INTO categories (id, created_at, image_url, name, updated_at)
VALUES (3, '2025-02-14 21:55:09.422658',
        'https://www.stall.com.ua/components/com_virtuemart/shop_image/product/500px/_________________6089297c64ab0.jpg',
        'Годинники', '2025-02-14 21:55:09.422658');
INSERT INTO categories (id, created_at, image_url, name, updated_at)
VALUES (5, '2025-02-14 22:17:15.357107', 'https://blog.eva.ua/wp-content/uploads/2019/08/Oryginal-chy-pidrobka-03.jpg',
        'Парфуми', '2025-02-14 22:17:15.357107');

INSERT INTO products (id, created_at, description, image_url, name, price, stock, updated_at, category_id)
VALUES (3, '2025-02-13 16:19:35.393301',
        'М''який смак грає яскравими тонами агави, з нюансами ванілі, ананаса, перцю і прянощів, делікатний аромат розкривається солодкими тонами агави, трав''яними нюансами, відтінками лимонної цедри, тропічних плодів і перцю.',
        'https://auchan.ua/media/catalog/product/d/1/d1d61f5597f28fe30e12b3051a8740f8ae6a36e4361753f953707584b9657a50.jpeg',
        'Armando''s', 500, 23, '2025-02-13 16:19:35.393301', 2);
INSERT INTO products (id, created_at, description, image_url, name, price, stock, updated_at, category_id)
VALUES (5, '2025-02-14 22:08:04.613497', e'Діаметр: 40 мм
Товщина: 8,3 мм
Матеріал корпусу: нержавіюча сталь
Колір циферблата: блакитний Тіффані
Індекси: нанесене чорнене золото',
        'https://www.pragnell.co.uk/images/original/566d9b98-e07d-4888-a4b9-439534a079f9.jpg', 'Patek Philippe', 70000,
        10, '2025-02-14 22:32:05.154511', 3);
INSERT INTO products (id, created_at, description, image_url, name, price, stock, updated_at, category_id)
VALUES (2, '2025-02-13 16:16:55.972641',
        'Витримана текіла. Термін витримки - 9 місяців у бочках з деревини американського білого дуба, що надає текілі чарівного аромату, багатого кольору, глибокого і насиченого смаку.',
        'https://images.silpo.ua/products/1600x1600/2d15eea4-2a4e-47f9-a54d-7aa88cc11063.png', 'Sierra', 800, 30,
        '2025-02-14 22:36:48.933672', 2);
INSERT INTO products (id, created_at, description, image_url, name, price, stock, updated_at, category_id)
VALUES (4, '2025-02-14 22:01:21.773050', e'Тип механізму: кварцовий
Корпус: сталевий
Скло: мінеральне, антидрапне
Ширина браслета: 2.1 см.
Застібка ременя: класичний метелик
',
        'https://cdn-jnbkl.nitrocdn.com/DSyXimFVbLZsDRQHgxpswYJEIlNbdghE/assets/images/optimized/rev-f733be1/luxurysouq.com/wp-content/uploads/2023/07/batman_-removebg-preview.png',
        'Rolex', 5000, 5,
        '2025-02-14 22:41:50.078288', 3);
INSERT INTO products (id, created_at, description, image_url, name, price, stock, updated_at, category_id)
VALUES (6, '2025-02-14 22:20:06.852356',
        'Tom Ford Lost Cherry Eau de Parfum – аромат-унісекс, випущений у 2018 році. Як і в інших своїх парфумерних творіннях, у цьому Том Форд грає на об''єднанні протилежностей – невинності та хтивості, легкості та сили.',
        'https://blotters.com.ua/image/cache/catalog/%D1%82%D0%BE%D0%BC%20%D0%B2%D1%83%D0%B4-600x600.png', 'Tom Ford',
        20000, 20, '2025-02-14 22:43:19.696303', 5);
INSERT INTO products (id, created_at, description, image_url, name, price, stock, updated_at, category_id)
VALUES (7, '2025-02-14 22:21:34.268201',
        'Аромат як талісман! Дорогоцінний еліксир, випущений до 65-річної річниці весілля Королеви Великобританії Єлизавети II та принца Філіпа, миттєво став фаворитом у Віндзорському палаці.',
        'https://parfumer.org/photos/shares/Boadicea%20the%20Victorious/%D0%9F%D0%B0%D1%80%D1%84%D1%83%D0%BC%D0%BE%D0%B2%D0%B0%D0%BD%D0%B0-%D0%B2%D0%BE%D0%B4%D0%B0-Boadicea-the-Victorious-Blue-Sapphire.png',
        'Boadicea', 36000, 40, '2025-02-14 22:48:18.745193', 5);
