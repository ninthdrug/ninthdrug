create table users (
  userid       varchar(20),
  name         varchar(100),
  password     varchar(200),
  email        varchar(200)
);

create table groups (
  groupname    varchar(20)
);

create table user_groups (
  userid       varchar(20),
  groupname    varchar(20)
);

create table websites (
  websiteid    serial primary key,
  name         varchar(100),
  description  varchar(1000),
  hostname     varchar(100) not null,
  port         integer
);

create table roles (
  rolename     varchar(20)
);

create table group_roles (
  groupname    varchar(20),
  websiteid    integer,
  rolename     varchar(20)
);

create table pages (
  pageid       serial primary key,
  websiteid    integer,
  url          varchar(500),
  name         varchar(100),
  description  varchar(1000),
  layoutid     integer
);

create table widgets (
  widgetid     serial primary key,
  name         varchar(100),
  html         varchar(4000)
);

create table javascripts (
  javascriptid serial primary key,
  name         varchar(100),
  javascript   varchar(4000)
);

create table widget_jslibs (
  javascriptid integer,
  jslibid      integer
);

create table jslibs (
  jslibid      serial primary key,
  name         varchar(100),
  version      varchar(100),
  link         varchar(100)
);

create table layouts ( 
  layoutid     serial primary key,
  html         varchar(4000),
  csslink      varchar(100)
);

create table themes (
  themeid      serial primary key,
  name         varchar(100)
); 

