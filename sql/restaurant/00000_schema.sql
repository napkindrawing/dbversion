create table restaurant (
    id integer auto_increment not null,
    name varchar(200) not null,
    primary key (id) 
);

create table menu (
    id integer auto_increment not null,
    name varchar(200) not null,
    primary key (id) 
);

create table menu_item (
    id integer auto_increment not null,
    menu_id integer not null ,
    name varchar(200) not null,
    primary key (id) 
);


