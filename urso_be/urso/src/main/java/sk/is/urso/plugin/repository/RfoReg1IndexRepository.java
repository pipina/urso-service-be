package sk.is.urso.plugin.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sk.is.urso.reg.PluginRepositoryIndexes;
import sk.is.urso.plugin.entity.RfoReg1IndexEntity;

import java.util.Optional;

public interface RfoReg1IndexRepository extends PluginRepositoryIndexes<RfoReg1IndexEntity>{

    Optional<RfoReg1IndexEntity> findByKlucAndHodnotaAndAktualny(String kluc, String hodnota, boolean aktualny);

    @Modifying
    @Query(value = "UPDATE rfo_1_index SET aktualny = false WHERE zaznam_id = :zaznamId", nativeQuery = true)
    void zneaktualizuj(@Param("zaznamId") Long zaznamId);
}
