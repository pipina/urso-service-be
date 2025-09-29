DO
$$
    DECLARE
        constraint_to_drop text;
    BEGIN
        SELECT constraint_name
        INTO constraint_to_drop
        FROM information_schema.constraint_column_usage
        WHERE table_name = 'hodnota_ciselnika'
          AND column_name = 'kod_polozky';

        EXECUTE 'ALTER TABLE hodnota_ciselnika DROP CONSTRAINT ' || constraint_to_drop;
    END
$$;