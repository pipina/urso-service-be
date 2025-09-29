package sk.is.urso.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.is.urso.model.RaInternalInicialneZaznamy;

@Repository
public interface RaInternalInicialneZaznamyRepository extends JpaRepository<RaInternalInicialneZaznamy, Long> {
}