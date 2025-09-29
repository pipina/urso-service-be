package sk.is.urso.repository;

import org.alfa.repository.EntityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sk.is.urso.model.SpStavZiadost;
import sk.is.urso.model.ZpStavZiadost;

import java.util.List;

@Repository
public interface ZpStavZiadostRepository extends EntityRepository<ZpStavZiadost, Long> {

    @Query(value = "SELECT id FROM zp_stav_ziadost WHERE navratovy_kod_operacie = 'OK' AND navratovy_kod_stavu IS NULL AND stav = 'PREBIEHA_SPRACOVANIE' AND platnost_do >= now()",
            nativeQuery = true)
    List<Long> findAllToCheckState();

    @Query(value = "SELECT nextval('zp_stav_ziadost_seq')", nativeQuery = true)
    Long nextval();
}
