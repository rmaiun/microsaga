drop table if exists orders;

create table orders
(
    id      bigserial primary key,
    product varchar not null,
    saga_id varchar not null
);

create index orders_id_idx on orders (id);
create index orders_sagaid_idx on orders (saga_id);

create table account
(
    id     bigserial primary key,
    code   varchar not null,
    amount bigint  not null
);

create index account_id_idx on account (id);
create index account_code_idx on account (code);
insert into account (code, amount)
values ('user1', 1000),
       ('user2', 300),
       ('company1', 10000);

create table payment
(
    id       bigserial primary key,
    acc_from bigint  not null,
    acc_to   bigint  not null,
    amount   bigint  not null,
    order_id bigint  not null,
    saga_id  varchar not null
);

create index payment_sagaid_idx on payment (saga_id);
