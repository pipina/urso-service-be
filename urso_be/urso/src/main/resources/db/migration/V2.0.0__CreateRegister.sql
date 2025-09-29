CREATE TABLE register
(
    register_id        VARCHAR(512) NOT NULL,
    verzia_registra_id BIGINT       NOT NULL,
    nazov_registra     VARCHAR(255),
    "schema"           BIGINT       NOT NULL DEFAULT 0,
    PRIMARY KEY (register_id, verzia_registra_id)
);