drop table if exists orders;

create table orders
(
    id   BIGSERIAL primary key,
    product varchar not null,
    sagaId varchar not null
);

create index orders_name_idx on orders (id);
create index orders_name_idx2 on orders (sagaId);