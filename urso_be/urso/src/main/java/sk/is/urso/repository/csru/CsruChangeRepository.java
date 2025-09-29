package sk.is.urso.repository.csru;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.is.urso.model.csru.CsruChange;

@Repository
public interface CsruChangeRepository extends JpaRepository<CsruChange, Long> {

    CsruChange findFirstByTypeAndDateFromNotNullAndDateToNotNullOrderByDateToDescEndDesc(String type);
}
