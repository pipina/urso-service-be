CREATE TYPE zp_nedoplatok AS ENUM (
    'A',
    'N',
    'C',
    'NEZNAMY'
    );

CREATE TYPE zp_poistovna AS ENUM (
    'VSEOBECNA_ZDRAVOTNA',
    'DOVERA',
    'UNION',
    'NEZNAMA'
    );

CREATE TYPE zp_popis_kodu_vysledku_spracovania AS ENUM (
    'ZASLANE_UDAJE_OD_ZP',
    'NIE_JE_EVIDOVANY',
    'NIE_SU_EVIDOVANE_ZIADNE_UDAJE',
    'NESULAD_SUBJEKTU_V_OBALKE_A_OBSAHU',
    'NESULAD_RC_ICO_IFO',
    'NEEVIDOVANIY_PARTNER_PRE_POSKYTNUTIE_UDAJOV',
    'NEZNAMY'
    );

CREATE TABLE zp_vystupny_subor
(
    id   BIGSERIAL PRIMARY KEY,
    path VARCHAR NOT NULL
);

CREATE TABLE zp_osoba_zaznam
(
    id               BIGSERIAL PRIMARY KEY,
    rodne_cislo      VARCHAR,
    meno             VARCHAR,
    priezvisko       VARCHAR,
    datum_narodenia  DATE,
    ico              VARCHAR,
    vybavuje_osoba   VARCHAR,
    vybavuje_telefon VARCHAR,
    vybavuje_email   VARCHAR
);

CREATE TABLE zp_ziadatelia
(
    id               BIGSERIAL PRIMARY KEY,
    osoba_zaznam     BIGINT REFERENCES zp_osoba_zaznam (id),
    vybavuje_osoba   VARCHAR,
    vybavuje_telefon VARCHAR,
    vybavuje_email   VARCHAR,
    cas_poziadavky   TIMESTAMP
);

CREATE SEQUENCE zp_stav_ziadost_seq;

CREATE TABLE zp_stav_ziadost
(
    id                      BIGINT PRIMARY KEY DEFAULT nextval('zp_stav_ziadost_seq'),
    request_id              BIGINT                      NOT NULL,
    ovm_transaction_id      VARCHAR                     NOT NULL,
    ovm_correlation_id      VARCHAR                     NOT NULL,
    cas_podania             TIMESTAMP                   NOT NULL,
    navratovy_kod_operacie  csru_navratovy_kod_operacie NOT NULL,
    chybova_hlaska_operacie VARCHAR,
    csru_transaction_id     VARCHAR,
    stav                    csru_stav_ziadosti          NOT NULL,
    osoba_zaznam            BIGINT REFERENCES zp_osoba_zaznam (id),
    platnost_do             TIMESTAMP                   NOT NULL,
    navratovy_kod_stavu     csru_navratovy_kod_operacie,
    chybova_hlaska_stavu    VARCHAR,
    vystupny_subor          BIGINT REFERENCES zp_vystupny_subor (id),
    ma_nedoplatok           BOOLEAN
);

CREATE TABLE zp_vysledok_kontroly
(
    id                        BIGSERIAL PRIMARY KEY,
    stav_ziadost              BIGINT REFERENCES zp_stav_ziadost (id),
    poistovna                 zp_poistovna                NOT NULL,
    nedoplatok                zp_nedoplatok               NOT NULL,
    vyska_nedoplatku          NUMERIC,
    navratovy_kod             csru_navratovy_kod_operacie NOT NULL,
    chybova_hlaska            VARCHAR,
    vysledok_spracovania zp_popis_kodu_vysledku_spracovania
);

CREATE UNIQUE INDEX idx_unique_rodne_cislo_zp ON zp_osoba_zaznam (rodne_cislo);
CREATE UNIQUE INDEX idx_unique_ico_zp ON zp_osoba_zaznam (ico);
CREATE UNIQUE INDEX idx_unique_meno_priezvisko_datum_narodenia_zp ON zp_osoba_zaznam (meno, priezvisko, datum_narodenia);