CREATE TYPE fs_nedoplatok AS ENUM (
    'MA_NEDOPLATOK',
    'NEMA_NEDOPLATOK',
    'CHYBA',
    'NEZNAMY'
    );

CREATE TYPE fs_nedolpatok_chybovy_kod AS ENUM (
    'OK',
    'SUBJEKT_MA_DUPLICITU_V_EVIDENCII',
    'NEZNAMY'
    );

CREATE TYPE fs_druh_dane_pohladavky AS ENUM (
    'NDS',
    'SPD',
    'COL',
    'NEZNAMY'
    );

CREATE TABLE fs_osoba_zaznam
(
    id                      BIGSERIAL PRIMARY KEY,
    ovm_transaction_id      VARCHAR                     NOT NULL,
    ovm_correlation_id      VARCHAR                     NOT NULL,
    csru_transaction_id     VARCHAR                     NOT NULL,
    cas_podania             TIMESTAMP                   NOT NULL,
    navratovy_kod_operacie  csru_navratovy_kod_operacie NOT NULL,
    chybova_hlaska_operacie VARCHAR,
    platnost_do             TIMESTAMP                   NOT NULL,
    rodne_cislo             VARCHAR,
    meno                    VARCHAR,
    priezvisko              VARCHAR,
    ico                     VARCHAR,
    dic                     VARCHAR,
    nazov_spolocnosti       VARCHAR,
    ma_nedoplatok           BOOLEAN
);

CREATE INDEX idx_ico ON fs_osoba_zaznam (ico);
CREATE INDEX idx_dic ON fs_osoba_zaznam (dic);
CREATE INDEX idx_rodne_cislo ON fs_osoba_zaznam (rodne_cislo);

CREATE TABLE fs_osoba_nedoplatok
(
    id                        BIGSERIAL PRIMARY KEY,
    nedolpatok_chybovy_kod    fs_nedolpatok_chybovy_kod NOT NULL,
    nedolpatok_chybova_sprava VARCHAR,
    nedoplatok                fs_nedoplatok,
    vyska_nedoplatku          VARCHAR,
    mena                      VARCHAR,
    datum_nedoplatku          DATE,
    druh_dane_pohladavky      fs_druh_dane_pohladavky,
    osoba_zaznam              BIGINT REFERENCES fs_osoba_zaznam (id)
);
