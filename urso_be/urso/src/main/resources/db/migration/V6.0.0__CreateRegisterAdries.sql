CREATE SEQUENCE ra_1_data_id_seq;
CREATE TABLE ra_1_data
(
    id                             BIGSERIAL PRIMARY KEY,
    xml                            TEXT    NOT NULL,
    platnost_od                    DATE    NOT NULL,
    ucinnost_od                    DATE    NOT NULL,
    ucinnost_do                    DATE DEFAULT NULL,
    neplatny                       BOOLEAN NOT NULL,
    datum_cas_poslednej_referencie TIMESTAMP,
    pouzivatel                     VARCHAR(256),
    modul                          VARCHAR(8)
);
ALTER TABLE ra_1_data
    ALTER COLUMN id SET DEFAULT nextval('ra_1_data_id_seq');

CREATE SEQUENCE ra_1_index_id_seq;
CREATE TABLE ra_1_index
(
    id                   BIGSERIAL PRIMARY KEY,
    zaznam_id            BIGINT       NOT NULL,
    kluc                 VARCHAR(100) NOT NULL,
    hodnota              VARCHAR(100) NOT NULL,
    hodnota_zjednodusena VARCHAR(100) NOT NULL,
    ucinnost_od          DATE,
    ucinnost_do          DATE,
    sekvencia            INT          NOT NULL,
    aktualny             BOOLEAN      NOT NULL,
    kontext              TEXT         NOT NULL DEFAULT '',
    FOREIGN KEY (zaznam_id) REFERENCES ra_1_data (id)
);
ALTER TABLE ra_1_index
    ALTER COLUMN id SET DEFAULT nextval('ra_1_index_id_seq');

CREATE TABLE ra_1_data_reference
(
    zaznam_id        BIGINT     NOT NULL,
    modul            VARCHAR(8) NOT NULL,
    pocet_referencii INT        NOT NULL,
    FOREIGN KEY (zaznam_id) REFERENCES ra_1_data (id)
);

CREATE TABLE ra_1_data_history
(
    zaznam_id            BIGINT    NOT NULL,
    udalost_id           BIGINT    NOT NULL,
    xml                  TEXT      NOT NULL,
    platnost_od          DATE      NOT NULL,
    ucinnost_od          DATE      NOT NULL,
    ucinnost_do          DATE DEFAULT NULL,
    neplatny             BOOLEAN   NOT NULL,
    datum_cas_vytvorenia TIMESTAMP NULL,
    pouzivatel           VARCHAR(256),
    modul                VARCHAR(8),
    FOREIGN KEY (zaznam_id) REFERENCES ra_1_data (id),
    FOREIGN KEY (udalost_id) REFERENCES udalost (id),
    PRIMARY KEY (zaznam_id, udalost_id)
);

CREATE TABLE ra_1_natural_id
(
    zaznam_id  BIGINT UNIQUE NOT NULL,
    povodne_id VARCHAR(255)  NOT NULL,
    FOREIGN KEY (zaznam_id) REFERENCES ra_1_data (id),
    PRIMARY KEY (povodne_id)
);

INSERT INTO register (register_id, verzia_registra_id)
VALUES ('RA', 1);

CREATE INDEX ind_ra_1_data_id ON ra_1_data (id);
CREATE INDEX ind_ra_1_data_id_disabled ON ra_1_data (neplatny, id);
CREATE INDEX ind_ra_1_data_reference_zaznam_id ON ra_1_data_reference (zaznam_id);
CREATE INDEX ra_internal_index_keyAndValueSimplified ON ra_1_index (kluc, hodnota_zjednodusena);
CREATE INDEX indx_ra_1_entrykeyseq ON ra_1_index (zaznam_id, kluc, sekvencia);
CREATE INDEX ind_ra_1_natural_key_zaznam_id ON ra_1_natural_id (zaznam_id);
CREATE INDEX ind_ra_1_data_history_zaznam_id ON ra_1_data_history (zaznam_id);
CREATE INDEX indx_ra_1 ON ra_1_index USING btree (zaznam_id ASC, kluc ASC);

