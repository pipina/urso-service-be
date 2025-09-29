CREATE TYPE urso_subject_stav AS ENUM ('PREBIEHA', 'SPRACOVANE', 'CHYBA');
CREATE TYPE urso_nedoplatok_typ AS ENUM ('FS', 'SP', 'ZP');

CREATE TABLE IF NOT EXISTS urso_subject_stack
(
    id BIGSERIAL PRIMARY KEY,
    request_id BIGINT NOT NULL UNIQUE,
    dlznici_id INTEGER REFERENCES csru_set.set_dlznici(id) NOT NULL ,
    stav urso_subject_stav NOT NULL,
    doplnujuca_textova_informacia VARCHAR,
    cas_vytvorenia TIMESTAMP NOT NULL,
    typ_nedoplatku urso_nedoplatok_typ NOT NULL
)