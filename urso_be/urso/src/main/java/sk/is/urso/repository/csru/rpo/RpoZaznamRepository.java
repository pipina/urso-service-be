package sk.is.urso.repository.csru.rpo;

import org.alfa.repository.EntityRepository;
import org.springframework.stereotype.Repository;
import sk.is.urso.model.csru.rpo.RpoZaznam;

@Repository
public interface RpoZaznamRepository extends EntityRepository<RpoZaznam, Long> {

    boolean existsByIpo(Long ipo);
}
