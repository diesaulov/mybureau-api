-- create table bureau_user (
--     id bigserial primary key,
--     email varchar(100) not null unique,
--     password varchar(200) not null,
--     first_name varchar(100) not null,
--     last_name varchar(100) not null,
--     created timestamp not null,
--     updated timestamp not null
-- );

create table client (
    id bigserial primary key,
--     code varchar(50) not null,
    name varchar(100) not null,
--     user_id bigint not null references bureau_user(id),
    created_ts timestamp not null,
    updated_ts timestamp
--     constraint code_user_unq unique (code, user_id)
);

create table project (
    id bigserial primary key,
--     code varchar(50) not null,
    name varchar(100) not null,
    client_id bigint not null references client(id),
    created_ts timestamp not null,
    updated_ts timestamp
--     constraint code_client_unq unique (code, client_id)
);

create table project_task (
    id bigserial primary key,
--     code varchar(50) not null,
    name varchar(100) not null,
    project_id bigint not null references project(id),
    created_ts timestamp not null,
    updated_ts timestamp
);

create table timer_entry (
    id bigserial primary key,
    type varchar(20) not null,
    date date,
    duration int,
    timer_started timestamp,
    timer_stopped timestamp,
    task_id bigint not null references project_task(id),
    notes varchar(500),
    deleted boolean not null default false,
    deleted_ts timestamp,
    inserted_ts timestamp not null,
    updated_ts timestamp,

    constraint non_empty_deleted_ts_on_deletion CHECK ( deleted = false OR deleted_ts is not null),
    constraint non_empty_timer_started_or_date CHECK ( timer_started is not null OR date is not null)
);

-- INSERT INTO bureau_user (id, email, password, first_name, last_name, created, updated)
-- VALUES (1, 'esauldan@gmail.com', '{bcrypt}$2a$10$HmY7AKE2fIJadfkvNwUCHexfkhzy9ryJApx6siqGCSvBo8ccHwaIu', 'Danylo', 'Iesaulov', now(), now());

INSERT INTO client (id, name, created_ts, updated_ts) VALUES (1, 'Celonis', now(), NULL);
INSERT INTO client (id, name, created_ts, updated_ts) VALUES (2, 'paintgun.io', now(), NULL);

INSERT INTO project (id, name, client_id, created_ts, updated_ts) VALUES (1, 'CPM4', 1, now(), NULL);
INSERT INTO project (id, name, client_id, created_ts, updated_ts) VALUES (2, 'Paintgun Platform', 2, now(), NULL);

INSERT INTO project_task(id, name, project_id, created_ts, updated_ts) VALUES (1, 'Task', 1, now(), NULL);
INSERT INTO project_task(id, name, project_id, created_ts, updated_ts) VALUES (2, 'Code Review', 1, now(), NULL);
INSERT INTO project_task(id, name, project_id, created_ts, updated_ts) VALUES (3, 'Meeting', 1, now(), NULL);
INSERT INTO project_task(id, name, project_id, created_ts, updated_ts) VALUES (4, 'Documentation', 1, now(), NULL);
