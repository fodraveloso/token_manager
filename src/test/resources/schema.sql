drop table if exists authority CASCADE;
drop table if exists role CASCADE;
drop table if exists role_authority CASCADE;
drop table if exists user CASCADE;
drop table if exists user_role CASCADE;

drop sequence if exists hibernate_sequence;
create sequence hibernate_sequence start with 1 increment by 1;
  
create table user (
	id bigint not null,
	account_non_expired boolean not null,
	account_non_locked boolean not null,
	created_date timestamp,
	credentials_non_expired boolean not null,
	enabled boolean not null,
	last_modified_date timestamp,
	password varchar(255) not null,
	username varchar(255) not null unique,
	created_by_id bigint,
	primary key (id),
	foreign key (created_by_id) references user
);
  
create table authority (
	id bigint not null,
	permissions varchar(255) not null unique,
	created_by_id bigint not null,
	primary key (id),
	foreign key (created_by_id) references user
);

create table role (
	id bigint not null,
	name varchar(255) not null unique,
	created_by_id bigint not null,
	primary key (id),
	foreign key (created_by_id) references user
);
    
create table role_authority (
	role_id bigint not null,
	authority_id bigint not null,
	primary key (role_id, authority_id),
	foreign key (authority_id) references authority,
	foreign key (role_id) references role
);
    
create table user_role (
	user_id bigint not null,
	role_id bigint not null,
	primary key (user_id, role_id),
	foreign key (role_id) references role,
	foreign key (user_id) references user
);