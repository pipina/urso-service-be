CREATE TABLE hodnota_ciselnika
(
    id                             BIGSERIAL PRIMARY KEY,
    kod_polozky                    VARCHAR(255) UNIQUE NOT NULL,
    nazov_polozky                  VARCHAR(255)        NOT NULL,
    kod_ciselnika                  VARCHAR(100)        NOT NULL,
    nadradena_hodnota_ciselnika_id BIGINT,
    platnost_od                    DATE                NOT NULL,
    platnost_do                    DATE,
    deleted                        BOOLEAN             NOT NULL DEFAULT TRUE,
    ciselnik_id                    BIGINT              NOT NULL,
    dodatocny_obsah                VARCHAR(256),
    poradie                        BIGINT,
    FOREIGN KEY (ciselnik_id) REFERENCES ciselnik (id),
    FOREIGN KEY (nadradena_hodnota_ciselnika_id) REFERENCES hodnota_ciselnika (id)
);

CREATE INDEX indx_enumeratiON_value_1 ON hodnota_ciselnika USING btree
    (
     deleted ASC,
     platnost_od ASC,
     platnost_do ASC
        );
CREATE INDEX idx_ev_itemCode_codelistCode ON hodnota_ciselnika (kod_polozky, kod_ciselnika);

CREATE INDEX kod_polozky
    ON hodnota_ciselnika (kod_polozky);