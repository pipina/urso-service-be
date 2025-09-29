CREATE TYPE udalost_domena AS ENUM ('ciselnik', 'hodnota-ciselnika', 'register', 'hodnota-registra');
CREATE TYPE udalost_kategoria AS ENUM ('create', 'read', 'update', 'delete', 'import', 'export');

CREATE TABLE udalost
(
    id                   BIGSERIAL PRIMARY KEY,
    domena               udalost_domena,
    kategoria            udalost_kategoria,
    datum_cas_vytvorenia TIMESTAMP         NOT NULL,
    uspesna              BOOLEAN,
    pouzivatel           VARCHAR(256)      NOT NULL,
    popis                VARCHAR(1024)
);