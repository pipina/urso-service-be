package sk.is.urso.repository;

import org.alfa.repository.EntityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sk.is.urso.model.SpStavZiadost;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpStavZiadostRepository extends EntityRepository<SpStavZiadost, Long> {

    @Query(value = "SELECT id FROM sp_stav_ziadost WHERE navratovy_kod_operacie = 'OK' AND navratovy_kod_stavu IS NULL AND stav = 'PREBIEHA_SPRACOVANIE' AND platnost_do >= now()",
    nativeQuery = true)
    List<Long> findAllToCheckState();
}
