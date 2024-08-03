create database if not exists moviedb;
use moviedb;
create table if not exists stars(
	id varchar(10) not null default '',
    name varchar(100) not null default '',
    birthYear integer,
    primary key (id)
);
create table if not exists movies(
	id varchar(10) not null default '',
    title varchar(100) not null default '',
    year integer not null,
    director varchar(100) not null default '',
    primary key (id)
);
create table if not exists stars_in_movies(
	starId varchar(10) not null,
    movieId varchar(10) not null,
    foreign key (starId) references stars(id),
    foreign key (movieId) references movies(id),
    primary key (starId, movieId)
);
create table if not exists genres(
	id integer auto_increment not null,
    name varchar(32) not null default '',
    primary key (id)
);
create table if not exists genres_in_movies(
	genreId integer not null,
    movieId varchar(10) not null,
    foreign key (genreId) references genres(id),
    foreign key (movieId) references movies(id),
    primary key (genreId, movieId)
);
create table if not exists creditcards(
	id varchar(20) not null default '',
    firstName varchar(50) not null default '',
    lastName varchar(50) not null default '',
    expiration date not null,
    primary key (id)
);
create table if not exists customers(
	id integer auto_increment not null,
    firstName varchar(50) not null default '',
    lastName varchar(50) not null default '',
	ccId varchar(20) not null,
    address varchar(200) not null default '',
    email varchar(50) not null default '',
    password varchar(20) not null default '',
    foreign key (ccId) references creditcards(id),
    primary key (id)
);
create table if not exists sales(
	id integer not null,
    customerId integer not null,
    movieId varchar(10) not null,
    saleDate date not null,
    foreign key (customerId) references customers(id),
    foreign key (movieId) references movies(id),
    primary key (id)
);
create table if not exists ratings(
	movieId varchar(10) not null,
    rating float not null,
    numVotes integer not null,
    foreign key (movieId) references movies(id),
    primary key (movieId)
);
	
    



