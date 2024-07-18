--liquibase formatted sql

--changeset breslavets:1
create table t_contacts
(
    id      bigserial primary key,
    phone   text not null unique,
    email   text not null unique,
    address text not null unique
);

--changeset breslavets:2
create table t_banks
(
    id            bigserial primary key,
    bik           text not null unique,
    name          text not null unique,
    legal_address text not null,
    corr_acc      text not null,
    contacts      bigint unique,
    foreign key (contacts) references t_contacts (id)
);

--changeset breslavets:3
create table t_users
(
    id         bigserial primary key,
    tg_id      bigint unique,
    fio        text not null,
    birth_date date not null,
    serial     text not null,
    number     text not null,
    phone      text not null unique
);

--changeset breslavets:4
create table t_accounts
(
    id         bigserial primary key,
    number     bigint         not null unique,
    owner      bigint         not null,
    date_begin date           not null,
    date_close date,
    balance    numeric(10, 2) not null,
    is_main    boolean default false,
    foreign key (owner) references t_users (id)
);

--changeset breslavets:5
create table t_cards
(
    id      bigserial primary key,
    type    text           not null,
    number  bigint         not null unique,
    status  text           not null,
    balance numeric(10, 2) not null,
    account bigint         not null,
    foreign key (account) references t_accounts (id)
);

--changeset breslavets:6
create table t_payments
(
    id        bigserial primary key,
    amount    numeric(10, 2) not null,
    status    text           not null,
    purpose   text           not null,
    payer     bigint         not null,
    payee     bigint         not null,
    oper_date timestamp      not null,
    foreign key (payer) references t_accounts (id),
    foreign key (payee) references t_accounts (id)
);

--changeset breslavets:7
insert into t_users(fio, birth_date, serial, number, phone)
VALUES ('Волобуев Роман Сергеевич', '1996-7-2', '5046', '777777', '9231817875');

--changeset breslavets:8
insert into t_contacts(phone, email, address)
VALUES ('3832503232', 'tgBank@gmail.com', 'г. Новосибирск, ул. Костычева 74/1');

--changeset breslavets:9
insert into t_banks(bik, name, legal_address, corr_acc, contacts)
VALUES ('12345', 'tgBank', 'г. Новосибирск, ул. Костычева 74/1', '12345',
        (select id from t_contacts where phone = '3832503232'));

--changeset breslavets:10
insert into t_accounts(number, owner, date_begin, balance, is_main)
VALUES (2071996, (select id from t_users where phone = '9231817875'), '2022-2-13', 10000000.45, true);

--changeset breslavets:11
insert into t_accounts(number, owner, date_begin, balance)
VALUES (2071997, (select id from t_users where phone = '9231817875'), '2023-2-13', 1000);

--changeset breslavets:12
insert into t_cards(type, number, status, balance, account)
VALUES ('DEBIT', 12345, 'WORK', 20000, (select id from t_accounts where t_accounts.number = 2071997));

--changeset breslavets:13
insert into t_users(fio, birth_date, serial, number, phone)
VALUES ('Бреславец Ольга Дмитриевна', '2000-6-12', '5046', '777777', '9963796022');

--changeset breslavets:14
insert into t_accounts(number, owner, date_begin, balance, is_main)
VALUES (12062000, (select id from t_users where phone = '9963796022'), '2022-2-13', 1000000.32, true);

--changeset breslavets:15
insert into t_accounts(number, owner, date_begin, balance)
VALUES (12062001, (select id from t_users where phone = '9963796022'), '2022-2-13', 777);

--changeset breslavets:16
insert into t_users(fio, birth_date, serial, number, phone)
VALUES ('Волобуева Ирина Анатольевна', '2000-6-12', '5006', '777777', '9231717262');

--changeset breslavets:17
insert into t_accounts(number, owner, date_begin, balance, is_main)
VALUES (22011983, (select id from t_users where phone = '9231717262'), '2022-2-13', 10000000.34, true);

--changeset breslavets:18
insert into t_accounts(number, owner, date_begin, balance)
VALUES (22011984, (select id from t_users where phone = '9231717262'), '2022-2-13', 777);
