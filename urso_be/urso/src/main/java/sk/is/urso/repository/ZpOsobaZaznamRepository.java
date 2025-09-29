package sk.is.urso.repository;

import org.alfa.repository.EntityRepository;
import org.springframework.stereotype.Repository;
import sk.is.urso.model.ZpOsobaZaznam;

import java.util.Date;
import java.util.Optional;

@Repository
public interface ZpOsobaZaznamRepository extends EntityRepository<ZpOsobaZaznam, Long> {

    Optional<ZpOsobaZaznam> findByRodneCislo(String rodneCislo);

    Optional<ZpOsobaZaznam> findByMenoAndPriezviskoAndDatumNarodenia(String meno, String priezvisko, Date datumNarodenia);

    Optional<ZpOsobaZaznam> findByIco(String ico);
}
