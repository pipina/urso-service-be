package sk.is.urso.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import sk.is.urso.model.Udalost;

@Repository
public interface UdalostRepository extends JpaRepository<Udalost, Long>, JpaSpecificationExecutor<Udalost> {
}
