drop table if exists saga_instance;
create table saga_instance
(
    id          bigserial primary key,
    saga_id     varchar not null unique,
    state       varchar not null,
    input       varchar,
    finished_at timestamp with time zone,
    retry_after timestamp with time zone
);
create index saga_instance_idx on saga_instance (saga_id);

create table saga_invocation
(
    id           bigserial primary key,
    saga_id      varchar not null,
    name         varchar not null,
    success      boolean,
    compensation boolean,
    result       varchar,
    constraint saga_instance_fk foreign key (saga_id) references saga_instance (saga_id)
);

create index saga_invocation_idx on saga_invocation (saga_id);