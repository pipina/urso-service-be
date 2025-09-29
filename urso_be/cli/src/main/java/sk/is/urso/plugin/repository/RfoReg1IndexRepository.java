package sk.is.urso.plugin.repository;

import org.springframework.data.jpa.repository.Query;
import sk.is.urso.plugin.entity.RfoReg1IndexEntity;
import sk.is.urso.reg.PluginRepositoryIndexes;

public interface RfoReg1IndexRepository extends PluginRepositoryIndexes<RfoReg1IndexEntity> {

    @Query(value = "SELECT zaznam_id FROM rfo_1_index WHERE kluc = 'ID' AND hodnota_zjednodusena = ?1 LIMIT 1",
            nativeQuery = true)
    Long findEntryId(String id);
}
