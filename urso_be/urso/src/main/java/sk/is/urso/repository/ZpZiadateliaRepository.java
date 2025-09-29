package sk.is.urso.repository;

import org.alfa.repository.EntityRepository;
import org.springframework.stereotype.Repository;
import sk.is.urso.model.ZpZiadatelia;

@Repository
public interface ZpZiadateliaRepository extends EntityRepository<ZpZiadatelia, Long> {
}
