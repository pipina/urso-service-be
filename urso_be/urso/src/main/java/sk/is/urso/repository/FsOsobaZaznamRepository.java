package sk.is.urso.repository;

import org.alfa.repository.EntityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sk.is.urso.model.FsOsobaZaznam;

import java.util.Optional;

@Repository
public interface FsOsobaZaznamRepository extends EntityRepository<FsOsobaZaznam, Long> {

    @Query(value = "SELECT * FROM fs_osoba_zaznam WHERE rodne_cislo = ?1 and platnost_do >= NOW()", nativeQuery = true)
    Optional<FsOsobaZaznam> findValidByRodneCislo(String rodneCislo);

    @Query(value = "SELECT * FROM fs_osoba_zaznam WHERE ico = ?1 and platnost_do >= NOW()", nativeQuery = true)
    Optional<FsOsobaZaznam> findValidByIco(String ico);

    @Query(value = "SELECT * FROM fs_osoba_zaznam WHERE dic = ?1 and platnost_do >= NOW()", nativeQuery = true)
    Optional<FsOsobaZaznam> findValidByDic(String dic);
}
