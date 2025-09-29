CREATE SEQUENCE csru_change_id_seq;

CREATE TABLE csru_type
(
    id varchar(32) not null primary key
);

INSERT INTO csru_type
values ('RA'),
       ('RFO'),
       ('RPO'),
       ('ZC');

CREATE TABLE csru_result_status
(
    id varchar(32) not null primary key
);

INSERT INTO csru_result_status
values ('OK'),
       ('ERROR');

CREATE TABLE csru_change
(
    id              bigint      not null primary key DEFAULT NEXTVAL('csru_change_id_seq'),
    date_from       date,
    date_to         date,
    start           timestamp,
    "end"           timestamp,
    type            varchar(32) not null references csru_type (id),
    result_status   varchar(32) references csru_result_status (id),
    error_msg       varchar(256),
    processed_items bigint
);

CREATE TABLE shedlock
(
    name       VARCHAR(64),
    lock_until TIMESTAMP(3) NULL,
    locked_at  TIMESTAMP(3) NULL,
    locked_by  VARCHAR(255),
    PRIMARY KEY (name)
);