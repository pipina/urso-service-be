package sk.is.urso.repository;

import org.alfa.repository.EntityRepository;
import org.springframework.stereotype.Repository;
import sk.is.urso.model.ZpVysledokKontroly;

@Repository
public interface ZpVysledokKontrolyRepository extends EntityRepository<ZpVysledokKontroly, Long> {
}
