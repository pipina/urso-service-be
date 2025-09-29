package sk.is.urso.repository;

import org.alfa.repository.EntityRepository;
import org.springframework.stereotype.Repository;
import sk.is.urso.model.SpOsobaZaznam;

import java.util.Optional;

@Repository
public interface SpOsobaZaznamRepository extends EntityRepository<SpOsobaZaznam, Long> {

    Optional<SpOsobaZaznam> findByRodneCisloAndMenoAndPriezvisko(String rodneCislo, String meno, String priezvisko);

    Optional<SpOsobaZaznam> findByIcoAndNazovSpolocnosti(String ico, String nazovSpolocnosti);
}
