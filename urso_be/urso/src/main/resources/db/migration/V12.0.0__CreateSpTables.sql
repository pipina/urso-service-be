CREATE TYPE csru_stav_ziadosti AS ENUM (
    'PREBIEHA_SPRACOVANIE',
    'SPRACOVANIE_USPESNE_UKONCENE',
    'SPRACOVANIE_UKONCENE_S_CHYBOU',
    'NEZNAMA_POZIADAVKA',
    'SPRACOVANIE_UKONCENE_S_UPOZORNENIM',
    'NEZNAMY'
    );

CREATE TYPE csru_navratovy_kod_operacie AS ENUM (
    'OK',
    'CHYBA_OVERENIA_OPRAVNENI',
    'CHYBA_VALIDACIE_VSTUPNYCH_PARAMETROV',
    'INTERNA_CHYBA',
    'NEPLATNE_ID_POZIADAVKY',
    'NEZNAMY'
    );

CREATE TYPE sp_nedoplatok AS ENUM (
    'MA_NEDOPLATOK',
    'NEMA_NEDOPLATOK',
    'MA_NEDOPLATOK_NESPLNENIE_POVINNOSTI',
    'NIE_JE_V_EVIDENCII',
    'NEKOMPLETNE_DATA_TECHNICKA_CHYBA',
    'NEIDENTIFIKOVANA_OSOBA',
    'NEZNAMY'
    );

CREATE TABLE sp_vystupny_subor
(
    id   BIGSERIAL PRIMARY KEY,
    path VARCHAR NOT NULL
);

CREATE TABLE sp_vysledok_kontroly
(
    id              BIGSERIAL PRIMARY KEY,
    nedoplatok      sp_nedoplatok NOT NULL,
    osb_status_text VARCHAR
);

CREATE TABLE sp_osoba_zaznam
(
    id                BIGSERIAL PRIMARY KEY,
    rodne_cislo       VARCHAR,
    meno              VARCHAR,
    priezvisko        VARCHAR,
    ico               VARCHAR,
    nazov_spolocnosti VARCHAR
);

CREATE TABLE sp_stav_ziadost
(
    id                      BIGSERIAL PRIMARY KEY,
    request_id              BIGINT                      NOT NULL,
    ovm_transaction_id      VARCHAR                     NOT NULL,
    ovm_correlation_id      VARCHAR                     NOT NULL,
    cas_podania             TIMESTAMP                   NOT NULL,
    navratovy_kod_operacie  csru_navratovy_kod_operacie NOT NULL,
    chybova_hlaska_operacie VARCHAR,
    csru_transaction_id     VARCHAR,
    stav                    csru_stav_ziadosti          NOT NULL,
    osoba_zaznam            BIGINT REFERENCES sp_osoba_zaznam (id),
    platnost_do             TIMESTAMP                   NOT NULL,
    navratovy_kod_stavu     csru_navratovy_kod_operacie,
    chybova_hlaska_stavu    VARCHAR,
    vysledok_kontroly       BIGINT REFERENCES sp_vysledok_kontroly (id),
    vystupny_subor          BIGINT REFERENCES sp_vystupny_subor (id),
    ma_nedoplatok           BOOLEAN
);

CREATE UNIQUE INDEX idx_unique_rodne_cislo_meno_priezvisko_sp ON sp_osoba_zaznam (rodne_cislo, meno, priezvisko);
CREATE UNIQUE INDEX idx_unique_ico_nazov_spolocnosti_sp ON sp_osoba_zaznam (ico, nazov_spolocnosti);
