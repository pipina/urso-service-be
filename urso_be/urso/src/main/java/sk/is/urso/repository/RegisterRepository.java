package sk.is.urso.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.is.urso.model.Register;
import sk.is.urso.reg.model.RegisterId;

@Repository
public interface RegisterRepository extends JpaRepository<Register, RegisterId> {
}
