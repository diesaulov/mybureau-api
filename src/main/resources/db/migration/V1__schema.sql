CREATE TABLE client (
    id bigserial PRIMARY KEY,
    name varchar(100) NOT NULL,
    created_ts timestamp NOT NULL,
    updated_ts timestamp
);

CREATE TABLE project (
    id bigserial PRIMARY KEY,
    name varchar(100) NOT NULL,
    client_id bigint NOT NULL REFERENCES client (id),
    created_ts timestamp NOT NULL,
    updated_ts timestamp
);

CREATE TABLE project_task (
    id bigserial PRIMARY KEY,
    name varchar(100) NOT NULL,
    description varchar(200) NOT NULL,
    project_id bigint NOT NULL REFERENCES project (id),
    created_ts timestamp NOT NULL,
    updated_ts timestamp
);

CREATE TABLE timer_entry (
    id bigserial PRIMARY KEY,
    type varchar(20) NOT NULL,
    duration_sec int,
    started timestamp NOT NULL,
    task_id bigint NOT NULL REFERENCES project_task (id),
    notes varchar(500) NOT NULL,
    deleted boolean NOT NULL DEFAULT false,
    deleted_ts timestamp,
    inserted_ts timestamp NOT NULL,
    updated_ts timestamp,

    CONSTRAINT non_empty_deleted_ts_on_deletion CHECK ( deleted = false OR deleted_ts is NOT NULL)
);
