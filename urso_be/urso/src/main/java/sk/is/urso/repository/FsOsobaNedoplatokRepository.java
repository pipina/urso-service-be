package sk.is.urso.repository;

import org.alfa.repository.EntityRepository;
import org.springframework.stereotype.Repository;
import sk.is.urso.model.FsOsobaNedoplatok;

@Repository
public interface FsOsobaNedoplatokRepository extends EntityRepository<FsOsobaNedoplatok, Long> {
}
