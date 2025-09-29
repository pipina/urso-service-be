package sk.is.urso.repository.csru;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.is.urso.model.csru.CsroRpoXmlError;

@Repository
public interface CsroRpoXmlErrorRepository extends JpaRepository<CsroRpoXmlError, Long> {
}
