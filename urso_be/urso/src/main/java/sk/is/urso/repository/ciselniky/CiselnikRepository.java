package sk.is.urso.repository.ciselniky;

import org.alfa.repository.EntityRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import sk.is.urso.model.ciselniky.Ciselnik;

import java.util.Optional;

@Repository
public interface CiselnikRepository extends EntityRepository<Ciselnik, Long>, JpaSpecificationExecutor<Ciselnik> {
    boolean existsByKodCiselnikaAndDeletedIsFalse(String kodCiselnika);

    Ciselnik findByKodCiselnika(String kodCiselnika);
    Optional<Ciselnik> findByKodCiselnikaAndDeletedIsFalse(String kodCiselnika);
}
