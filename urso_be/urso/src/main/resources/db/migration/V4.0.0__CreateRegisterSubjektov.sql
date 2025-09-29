CREATE SEQUENCE subject_1_data_fo_id_seq;
CREATE SEQUENCE subject_1_data_po_id_seq;
CREATE SEQUENCE subject_1_data_zo_id_seq;
CREATE SEQUENCE subject_1_data_sz_id_seq;
CREATE SEQUENCE subject_1_data_zp_id_seq;

CREATE SEQUENCE subject_1_data_id_seq;
CREATE TABLE subject_1_data
(
    id                             BIGSERIAL PRIMARY KEY,
    xml                            TEXT            NOT NULL,
    platnost_od                    DATE            NOT NULL,
    ucinnost_od                    DATE            NOT NULL,
    ucinnost_do                    DATE DEFAULT NULL,
    neplatny                       BOOLEAN         NOT NULL,
    datum_cas_poslednej_referencie TIMESTAMP,
    subjekt_id                     CHAR(12) UNIQUE NOT NULL,
    fo_id                          VARCHAR(256),
    pouzivatel                     VARCHAR(256),
    modul                          VARCHAR(8)
);
CREATE INDEX ind_subject_1_subject_id ON subject_1_data (subjekt_id);
ALTER TABLE subject_1_data
    ALTER COLUMN id SET DEFAULT nextval('subject_1_data_id_seq');

CREATE SEQUENCE subject_1_index_id_seq;
CREATE TABLE subject_1_index
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
    FOREIGN KEY (zaznam_id) REFERENCES subject_1_data (id)
);
ALTER TABLE subject_1_index
    ALTER COLUMN id SET DEFAULT nextval('subject_1_index_id_seq');

CREATE TABLE subject_1_data_reference
(
    zaznam_id        BIGINT          NOT NULL,
    modul            VARCHAR(8)      NOT NULL,
    pocet_referencii INT             NOT NULL,
    subjekt_id       CHAR(12) UNIQUE NOT NULL,
    PRIMARY KEY (zaznam_id, modul),
    FOREIGN KEY (zaznam_id) REFERENCES subject_1_data (id),
    FOREIGN KEY (subjekt_id) REFERENCES subject_1_data (subjekt_id)
);

CREATE TABLE subject_1_data_history
(
    zaznam_id            BIGINT  NOT NULL,
    udalost_id           BIGINT  NOT NULL,
    xml                  TEXT    NOT NULL,
    platnost_od          DATE    NOT NULL,
    ucinnost_od          DATE    NOT NULL,
    ucinnost_do          DATE DEFAULT NULL,
    neplatny             BOOLEAN NOT NULL,
    datum_cas_vytvorenia TIMESTAMP,
    pouzivatel           VARCHAR(256),
    modul                VARCHAR(8),
    FOREIGN KEY (zaznam_id) REFERENCES subject_1_data (id),
    FOREIGN KEY (udalost_id) REFERENCES udalost (id),
    PRIMARY KEY (zaznam_id, udalost_id)
);

CREATE TABLE subject_1_natural_id
(
    zaznam_id  BIGINT UNIQUE NOT NULL,
    povodne_id VARCHAR(255)  NOT NULL,
    FOREIGN KEY (zaznam_id) REFERENCES subject_1_data (id),
    PRIMARY KEY (povodne_id)
);

INSERT INTO register (register_id, verzia_registra_id)
VALUES ('SUBJECT', 1);

CREATE INDEX ind_subject_1_data_id ON subject_1_data (id);
CREATE INDEX ind_subject_1_data_id_disabled ON subject_1_data (neplatny, id);
CREATE INDEX ind_subject_1_data_reference_entry_id ON subject_1_data_reference (zaznam_id);
CREATE INDEX ind_subject_keyAndValueSimplified ON subject_1_index (kluc, hodnota_zjednodusena);
CREATE INDEX indx_subject_1_entrykeyseq ON subject_1_index (zaznam_id, kluc, sekvencia);
CREATE INDEX ind_subject_1_natural_key_entry_id ON subject_1_natural_id (zaznam_id);
CREATE INDEX ind_subject_1_data_history_entry_id ON subject_1_data_history (zaznam_id);
CREATE INDEX indx_subject_1 ON subject_1_index USING btree (zaznam_id ASC, kluc ASC);

