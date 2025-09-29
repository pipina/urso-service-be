package sk.is.urso.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.is.urso.model.CiselnikPoslednaZmena;

@Repository
public interface CiselnikPoslednaZmenaRepository extends JpaRepository<CiselnikPoslednaZmena, String> {
}
