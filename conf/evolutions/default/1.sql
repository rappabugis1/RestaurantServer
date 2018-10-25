# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table countries (
  id                            bigserial not null,
  country_name                  varchar(255) not null,
  constraint pk_countries primary key (id)
);

create table locations (
  id                            bigserial not null,
  city_name                     varchar(255) not null,
  country_id                    bigint not null,
  constraint pk_locations primary key (id)
);

create table users (
  id                            bigserial not null,
  email                         varchar(255) not null,
  sha_password                  bytea not null,
  user_type                     boolean default false not null,
  constraint pk_users primary key (id)
);

create table user_data (
  id                            bigserial not null,
  first_name                    varchar(255) not null,
  last_name                     varchar(255) not null,
  phone                         varchar(255) not null,
  user_id                       bigint,
  location_id                   bigint not null,
  constraint uq_user_data_user_id unique (user_id),
  constraint pk_user_data primary key (id)
);

alter table locations add constraint fk_locations_country_id foreign key (country_id) references countries (id) on delete restrict on update restrict;
create index ix_locations_country_id on locations (country_id);

alter table user_data add constraint fk_user_data_user_id foreign key (user_id) references users (id) on delete restrict on update restrict;

alter table user_data add constraint fk_user_data_location_id foreign key (location_id) references locations (id) on delete restrict on update restrict;
create index ix_user_data_location_id on user_data (location_id);


# --- !Downs

alter table if exists locations drop constraint if exists fk_locations_country_id;
drop index if exists ix_locations_country_id;

alter table if exists user_data drop constraint if exists fk_user_data_user_id;

alter table if exists user_data drop constraint if exists fk_user_data_location_id;
drop index if exists ix_user_data_location_id;

drop table if exists countries cascade;

drop table if exists locations cascade;

drop table if exists users cascade;

drop table if exists user_data cascade;

