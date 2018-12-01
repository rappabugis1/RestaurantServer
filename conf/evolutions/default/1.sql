# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table categories (
  id                            bigserial not null,
  name                          varchar(255) not null,
  constraint uq_categories_name unique (name),
  constraint pk_categories primary key (id)
);

create table categories_restaurants (
  categories_id                 bigint not null,
  restaurants_id                bigint not null,
  constraint pk_categories_restaurants primary key (categories_id,restaurants_id)
);

create table countries (
  id                            bigserial not null,
  name                          varchar(255) not null,
  constraint uq_countries_name unique (name),
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

create table guest_stay (
  id                            bigserial not null,
  guest_number                  integer not null,
  restaurant_id                 bigint not null,
  constraint pk_guest_stay primary key (id)
);

create table locations (
  id                            bigserial not null,
  name                          varchar(30) not null,
  country_id                    bigint not null,
  constraint uq_locations_name unique (name),
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
  reservation_date_time         timestamptz not null,
  reservation_end_date_time     timestamptz not null,
  request                       varchar(255),
  temp                          boolean,
  time_created                  timestamptz not null,
  user_id                       bigint not null,
  restaurant_id                 bigint not null,
  table_id                      bigint not null,
  constraint pk_reservations primary key (id)
);

create table restaurants (
  id                            bigserial not null,
  restaurant_name               varchar(255) not null,
  description                   varchar(2000) not null,
  price_range                   integer not null,
  latitude                      float not null,
  longitude                     float not null,
  image_file_name               varchar(255) not null,
  cover_file_name               varchar(255) not null,
  default_stay                  integer not null,
  mark                          integer not null,
  location_id                   bigint not null,
  constraint uq_restaurants_restaurant_name unique (restaurant_name),
  constraint pk_restaurants primary key (id)
);

create table reviews (
  id                            bigserial not null,
  mark                          integer not null,
  comment                       varchar(500) not null,
  insert_time                   varchar(255) not null,
  user_id                       bigint not null,
  restaurant_id                 bigint not null,
  constraint pk_reviews primary key (id)
);

create table stay_by_day_type (
  id                            bigserial not null,
  day_type                      varchar(255) not null,
  morning                       integer not null,
  day                           integer not null,
  evening                       integer not null,
  guest_stay_id                 bigint not null,
  constraint pk_stay_by_day_type primary key (id)
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
  user_id                       bigint not null,
  location_id                   bigint not null,
  constraint uq_user_data_user_id unique (user_id),
  constraint pk_user_data primary key (id)
);

alter table categories_restaurants add constraint fk_categories_restaurants_categories foreign key (categories_id) references categories (id) on delete restrict on update restrict;
create index ix_categories_restaurants_categories on categories_restaurants (categories_id);

alter table categories_restaurants add constraint fk_categories_restaurants_restaurants foreign key (restaurants_id) references restaurants (id) on delete restrict on update restrict;
create index ix_categories_restaurants_restaurants on categories_restaurants (restaurants_id);

alter table dishes add constraint fk_dishes_menu_id foreign key (menu_id) references menus (id) on delete restrict on update restrict;
create index ix_dishes_menu_id on dishes (menu_id);

alter table dishes add constraint fk_dishes_type_id foreign key (type_id) references dish_types (id) on delete restrict on update restrict;
create index ix_dishes_type_id on dishes (type_id);

alter table guest_stay add constraint fk_guest_stay_restaurant_id foreign key (restaurant_id) references restaurants (id) on delete restrict on update restrict;
create index ix_guest_stay_restaurant_id on guest_stay (restaurant_id);

alter table locations add constraint fk_locations_country_id foreign key (country_id) references countries (id) on delete restrict on update restrict;
create index ix_locations_country_id on locations (country_id);

alter table menus add constraint fk_menus_restaurant_id foreign key (restaurant_id) references restaurants (id) on delete restrict on update restrict;
create index ix_menus_restaurant_id on menus (restaurant_id);

alter table reservations add constraint fk_reservations_user_id foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_reservations_user_id on reservations (user_id);

alter table reservations add constraint fk_reservations_restaurant_id foreign key (restaurant_id) references restaurants (id) on delete restrict on update restrict;
create index ix_reservations_restaurant_id on reservations (restaurant_id);

alter table reservations add constraint fk_reservations_table_id foreign key (table_id) references tables (id) on delete restrict on update restrict;
create index ix_reservations_table_id on reservations (table_id);

alter table restaurants add constraint fk_restaurants_location_id foreign key (location_id) references locations (id) on delete restrict on update restrict;
create index ix_restaurants_location_id on restaurants (location_id);

alter table reviews add constraint fk_reviews_user_id foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_reviews_user_id on reviews (user_id);

alter table reviews add constraint fk_reviews_restaurant_id foreign key (restaurant_id) references restaurants (id) on delete restrict on update restrict;
create index ix_reviews_restaurant_id on reviews (restaurant_id);

alter table stay_by_day_type add constraint fk_stay_by_day_type_guest_stay_id foreign key (guest_stay_id) references guest_stay (id) on delete restrict on update restrict;
create index ix_stay_by_day_type_guest_stay_id on stay_by_day_type (guest_stay_id);

alter table tables add constraint fk_tables_restaurant_id foreign key (restaurant_id) references restaurants (id) on delete restrict on update restrict;
create index ix_tables_restaurant_id on tables (restaurant_id);

alter table user_data add constraint fk_user_data_user_id foreign key (user_id) references users (id) on delete restrict on update restrict;

alter table user_data add constraint fk_user_data_location_id foreign key (location_id) references locations (id) on delete restrict on update restrict;
create index ix_user_data_location_id on user_data (location_id);


# --- !Downs

alter table if exists categories_restaurants drop constraint if exists fk_categories_restaurants_categories;
drop index if exists ix_categories_restaurants_categories;

alter table if exists categories_restaurants drop constraint if exists fk_categories_restaurants_restaurants;
drop index if exists ix_categories_restaurants_restaurants;

alter table if exists dishes drop constraint if exists fk_dishes_menu_id;
drop index if exists ix_dishes_menu_id;

alter table if exists dishes drop constraint if exists fk_dishes_type_id;
drop index if exists ix_dishes_type_id;

alter table if exists guest_stay drop constraint if exists fk_guest_stay_restaurant_id;
drop index if exists ix_guest_stay_restaurant_id;

alter table if exists locations drop constraint if exists fk_locations_country_id;
drop index if exists ix_locations_country_id;

alter table if exists menus drop constraint if exists fk_menus_restaurant_id;
drop index if exists ix_menus_restaurant_id;

alter table if exists reservations drop constraint if exists fk_reservations_user_id;
drop index if exists ix_reservations_user_id;

alter table if exists reservations drop constraint if exists fk_reservations_restaurant_id;
drop index if exists ix_reservations_restaurant_id;

alter table if exists reservations drop constraint if exists fk_reservations_table_id;
drop index if exists ix_reservations_table_id;

alter table if exists restaurants drop constraint if exists fk_restaurants_location_id;
drop index if exists ix_restaurants_location_id;

alter table if exists reviews drop constraint if exists fk_reviews_user_id;
drop index if exists ix_reviews_user_id;

alter table if exists reviews drop constraint if exists fk_reviews_restaurant_id;
drop index if exists ix_reviews_restaurant_id;

alter table if exists stay_by_day_type drop constraint if exists fk_stay_by_day_type_guest_stay_id;
drop index if exists ix_stay_by_day_type_guest_stay_id;

alter table if exists tables drop constraint if exists fk_tables_restaurant_id;
drop index if exists ix_tables_restaurant_id;

alter table if exists user_data drop constraint if exists fk_user_data_user_id;

alter table if exists user_data drop constraint if exists fk_user_data_location_id;
drop index if exists ix_user_data_location_id;

drop table if exists categories cascade;

drop table if exists categories_restaurants cascade;

drop table if exists countries cascade;

drop table if exists dishes cascade;

drop table if exists dish_types cascade;

drop table if exists guest_stay cascade;

drop table if exists locations cascade;

drop table if exists menus cascade;

drop table if exists reservations cascade;

drop table if exists restaurants cascade;

drop table if exists reviews cascade;

drop table if exists stay_by_day_type cascade;

drop table if exists tables cascade;

drop table if exists users cascade;

drop table if exists user_data cascade;

