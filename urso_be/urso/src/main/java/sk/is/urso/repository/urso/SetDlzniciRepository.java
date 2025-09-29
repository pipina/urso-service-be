package sk.is.urso.repository.urso;

import org.alfa.repository.EntityRepository;
import org.springframework.stereotype.Repository;
import sk.is.urso.model.urso.SetDlznici;

import java.util.List;

@Repository
public interface SetDlzniciRepository extends EntityRepository<SetDlznici, Long> {

    List<SetDlznici> findAllBySync(boolean sync);
}
