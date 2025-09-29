package sk.is.urso.repository.ciselniky;

import org.alfa.repository.EntityRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sk.is.urso.model.ciselniky.HodnotaCiselnika;

import java.util.Optional;

@Repository
public interface HodnotaCiselnikaRepository extends EntityRepository<HodnotaCiselnika, Long>, JpaSpecificationExecutor<HodnotaCiselnika> {

    boolean existsByKodPolozkyAndKodCiselnikaAndDeletedIsFalse(String kodPolozky, String kodCiselnika);

    @Query(value = "SELECT EXISTS(" +
            "SELECT id " +
            "FROM hodnota_ciselnika " +
            "WHERE kod_polozky = ?1 " +
            "AND kod_ciselnika = ?2 " +
            "AND deleted = FALSE " +
            "AND platnost_od <= current_date " +
            "AND (platnost_do IS NULL OR platnost_do >= current_date)" +
            ")",
            nativeQuery = true)
    boolean existEffectiveByKodPolozkyAndKodCiselnika(String kodPolozky, String kodCiselnika);

    Optional<HodnotaCiselnika> findByKodPolozkyAndKodCiselnika(String kodPolozky, String kodCiselnika);
    Optional<HodnotaCiselnika> findByKodPolozkyAndKodCiselnikaAndDeletedIsFalse(String kodPolozky, String kodCiselnika);
    Optional<HodnotaCiselnika> findByKodPolozkyAndNazovPolozky(String kodPolozky, String nazovPolozky);
}
