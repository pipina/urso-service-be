CREATE TABLE ciselnik
(
    id              BIGSERIAL PRIMARY KEY,
    kod_ciselnika   VARCHAR(100) UNIQUE NOT NULL,
    nazov_ciselnika VARCHAR(255)        NOT NULL,
    externy_kod     VARCHAR(100),
    verzia          BIGINT              NOT NULL,
    platnost_od     DATE                NOT NULL,
    platnost_do     DATE,
    deleted         BOOLEAN             NOT NULL DEFAULT TRUE
);

CREATE INDEX indx_enumeration_1 ON ciselnik USING btree
    (
     kod_ciselnika ASC,
     deleted ASC,
     platnost_od ASC,
     platnost_do ASC
        );

CREATE INDEX kod_ciselnika
    ON ciselnik (kod_ciselnika);