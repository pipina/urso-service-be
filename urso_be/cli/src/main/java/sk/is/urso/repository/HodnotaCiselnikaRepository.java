package sk.is.urso.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sk.is.urso.model.HodnotaCiselnika;

import java.util.List;

@Repository
public interface HodnotaCiselnikaRepository extends JpaRepository<HodnotaCiselnika, Long>, JpaSpecificationExecutor<HodnotaCiselnika> {

    @Query(value = "SELECT hc.* " +
            "FROM hodnota_ciselnika hc " +
            "WHERE hc.kod_ciselnika = ?1 " +
            "AND hc.nazov_polozky = ?2 " +
            "AND hc.platnost_od <= current_date " +
            "AND (hc.platnost_do IS NULL OR hc.platnost_do >= current_date) " +
            "LIMIT ?3",
            nativeQuery = true)
    List<HodnotaCiselnika> findAllByCodelistCodeAndItemName(String codelistCode, String itemName, int limit);
}
