create table if not exists reviews
(
  id
  bigint
  generated
  by
  default as
  identity
  primary
  key,
  content
  TEXT
  not
  null,
  created_at
  timestamp
(
  6
),
  rating integer not null,
  updated_at timestamp
(
  6
),
  product_id bigint,
  user_id bigint
  );

alter table if exists reviews
  add constraint fk_reviews_product_id
  foreign key (product_id)
  references products;

alter table if exists reviews
  add constraint fk_reviews_user_id
  foreign key (user_id)
  references users;
