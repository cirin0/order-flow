create table reviews
(
  id         bigint  not null,
  content    TEXT    not null,
  created_at timestamp(6),
  rating     integer not null,
  updated_at timestamp(6),
  product_id bigint,
  user_id    bigint,
  primary key (id)
)

create sequence rewiews_seq start with 1 increment by 50

alter table if exists rewiews
  add constraint FKd1hqr30o7vovcgwuv5ucxntlc
  foreign key (product_id)
  references products

alter table if exists rewiews
  add constraint FKh7yloohgehdsrw1na6ybfr69u
  foreign key (user_id)
  references users
