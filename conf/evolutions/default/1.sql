# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table categories (
  id                            bigserial not null,
  name                          varchar(255) not null,
  constraint uq_categories_name unique (name),
  constraint pk_categories primary key (id)
);

create table countries (
  id                            bigserial not null,
  country                       varchar(50) not null,
  constraint uq_countries_country unique (country),
  constraint pk_countries primary key (id)
);

create table dishes (
  id                            bigserial not null,
  name                          varchar(255) not null,
  description                   varchar(255) not null,
  price                         integer not null,
  menu_id                       bigint not null,
  type_id                       bigint not null,
  constraint pk_dishes primary key (id)
);

create table dish_types (
  id                            bigserial not null,
  type                          varchar(255) not null,
  constraint uq_dish_types_type unique (type),
  constraint pk_dish_types primary key (id)
);

create table locations (
  id                            bigserial not null,
  city                          varchar(30) not null,
  country_id                    bigint not null,
  constraint uq_locations_city unique (city),
  constraint pk_locations primary key (id)
);

create table menus (
  id                            bigserial not null,
  type                          varchar(255) not null,
  restaurant_id                 bigint not null,
  constraint pk_menus primary key (id)
);

create table reservations (
  id                            bigserial not null,
  persons                       integer not null,
  reservation_date_time         varchar(255) not null,
  user_id                       bigint,
  restaurant_id                 bigint,
  constraint pk_reservations primary key (id)
);

create table restaurants (
  id                            bigserial not null,
  restaurant_name               varchar(255) not null,
  description                   varchar(255) not null,
  price_range                   integer not null,
  latitude                      float not null,
  longitude                     float not null,
  image_file_name               varchar(255) not null,
  cover_file_name               varchar(255) not null,
  location_id                   bigint not null,
  constraint pk_restaurants primary key (id)
);

create table restaurant_categories (
  restaurant_id                 bigint not null,
  category_id                   bigint not null,
  constraint pk_restaurant_categories primary key (restaurant_id,category_id)
);

create table reviews (
  id                            bigserial not null,
  mark                          integer not null,
  comment                       varchar(255) not null,
  insert_time                   varchar(255) not null,
  user_id                       bigint not null,
  restaurant_id                 bigint not null,
  constraint pk_reviews primary key (id)
);

create table tables (
  id                            bigserial not null,
  sitting_places                integer not null,
  restaurant_id                 bigint not null,
  constraint pk_tables primary key (id)
);

create table users (
  id                            bigserial not null,
  email                         varchar(255) not null,
  password                      varchar(255) not null,
  salt                          varchar(255) not null,
  user_type                     varchar(255) not null,
  constraint uq_users_email unique (email),
  constraint pk_users primary key (id)
);

create table user_data (
  id                            bigserial not null,
  firstname                     varchar(255) not null,
  last_name                     varchar(255) not null,
  phone                         varchar(255) not null,
  user_id                       bigint,
  location_id                   bigint not null,
  constraint uq_user_data_user_id unique (user_id),
  constraint pk_user_data primary key (id)
);

alter table dishes add constraint fk_dishes_menu_id foreign key (menu_id) references menus (id) on delete restrict on update restrict;
create index ix_dishes_menu_id on dishes (menu_id);

alter table dishes add constraint fk_dishes_type_id foreign key (type_id) references dish_types (id) on delete restrict on update restrict;
create index ix_dishes_type_id on dishes (type_id);

alter table locations add constraint fk_locations_country_id foreign key (country_id) references countries (id) on delete restrict on update restrict;
create index ix_locations_country_id on locations (country_id);

alter table menus add constraint fk_menus_restaurant_id foreign key (restaurant_id) references restaurants (id) on delete restrict on update restrict;
create index ix_menus_restaurant_id on menus (restaurant_id);

alter table reservations add constraint fk_reservations_user_id foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_reservations_user_id on reservations (user_id);

alter table reservations add constraint fk_reservations_restaurant_id foreign key (restaurant_id) references restaurants (id) on delete restrict on update restrict;
create index ix_reservations_restaurant_id on reservations (restaurant_id);

alter table restaurants add constraint fk_restaurants_location_id foreign key (location_id) references locations (id) on delete restrict on update restrict;
create index ix_restaurants_location_id on restaurants (location_id);

alter table restaurant_categories add constraint fk_restaurant_categories_restaurants foreign key (restaurant_id) references restaurants (id) on delete restrict on update restrict;
create index ix_restaurant_categories_restaurants on restaurant_categories (restaurant_id);

alter table restaurant_categories add constraint fk_restaurant_categories_categories foreign key (category_id) references categories (id) on delete restrict on update restrict;
create index ix_restaurant_categories_categories on restaurant_categories (category_id);

alter table reviews add constraint fk_reviews_user_id foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_reviews_user_id on reviews (user_id);

alter table reviews add constraint fk_reviews_restaurant_id foreign key (restaurant_id) references restaurants (id) on delete restrict on update restrict;
create index ix_reviews_restaurant_id on reviews (restaurant_id);

alter table tables add constraint fk_tables_restaurant_id foreign key (restaurant_id) references restaurants (id) on delete restrict on update restrict;
create index ix_tables_restaurant_id on tables (restaurant_id);

alter table user_data add constraint fk_user_data_user_id foreign key (user_id) references users (id) on delete restrict on update restrict;

alter table user_data add constraint fk_user_data_location_id foreign key (location_id) references locations (id) on delete restrict on update restrict;
create index ix_user_data_location_id on user_data (location_id);


# --- !Downs

alter table if exists dishes drop constraint if exists fk_dishes_menu_id;
drop index if exists ix_dishes_menu_id;

alter table if exists dishes drop constraint if exists fk_dishes_type_id;
drop index if exists ix_dishes_type_id;

alter table if exists locations drop constraint if exists fk_locations_country_id;
drop index if exists ix_locations_country_id;

alter table if exists menus drop constraint if exists fk_menus_restaurant_id;
drop index if exists ix_menus_restaurant_id;

alter table if exists reservations drop constraint if exists fk_reservations_user_id;
drop index if exists ix_reservations_user_id;

alter table if exists reservations drop constraint if exists fk_reservations_restaurant_id;
drop index if exists ix_reservations_restaurant_id;

alter table if exists restaurants drop constraint if exists fk_restaurants_location_id;
drop index if exists ix_restaurants_location_id;

alter table if exists restaurant_categories drop constraint if exists fk_restaurant_categories_restaurants;
drop index if exists ix_restaurant_categories_restaurants;

alter table if exists restaurant_categories drop constraint if exists fk_restaurant_categories_categories;
drop index if exists ix_restaurant_categories_categories;

alter table if exists reviews drop constraint if exists fk_reviews_user_id;
drop index if exists ix_reviews_user_id;

alter table if exists reviews drop constraint if exists fk_reviews_restaurant_id;
drop index if exists ix_reviews_restaurant_id;

alter table if exists tables drop constraint if exists fk_tables_restaurant_id;
drop index if exists ix_tables_restaurant_id;

alter table if exists user_data drop constraint if exists fk_user_data_user_id;

alter table if exists user_data drop constraint if exists fk_user_data_location_id;
drop index if exists ix_user_data_location_id;

drop table if exists categories cascade;

drop table if exists countries cascade;

drop table if exists dishes cascade;

drop table if exists dish_types cascade;

drop table if exists locations cascade;

drop table if exists menus cascade;

drop table if exists reservations cascade;

drop table if exists restaurants cascade;

drop table if exists restaurant_categories cascade;

drop table if exists reviews cascade;

drop table if exists tables cascade;

drop table if exists users cascade;

drop table if exists user_data cascade;

