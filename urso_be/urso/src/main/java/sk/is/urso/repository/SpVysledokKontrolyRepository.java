package sk.is.urso.repository;

import org.alfa.repository.EntityRepository;
import org.springframework.stereotype.Repository;
import sk.is.urso.model.SpVysledokKontroly;

@Repository
public interface SpVysledokKontrolyRepository extends EntityRepository<SpVysledokKontroly, Long> {
}
