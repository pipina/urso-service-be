package sk.is.urso.plugin.repository;


import org.springframework.data.jpa.repository.Query;
import sk.is.urso.reg.PluginRepositoryIndexes;
import sk.is.urso.plugin.entity.RpoReg1IndexEntity;

public interface RpoReg1IndexRepository extends PluginRepositoryIndexes<RpoReg1IndexEntity> {

    @Query(value = "SELECT sources.zaznam_id FROM " +
            "(SELECT zaznam_id FROM rpo_1_index WHERE kluc = 'SourceRegisterId' AND hodnota_zjednodusena = ?1) AS sources " +
            "JOIN (SELECT zaznam_id FROM rpo_1_index WHERE kluc = 'SourceRegisterType' AND hodnota_zjednodusena = ?2) AS sourceTypes " +
            "ON sources.zaznam_id = sourceTypes.zaznam_id " +
            "LIMIT 1",
            nativeQuery = true)
    Long findZaznamId(String sourceRegisterId, String sourceRegisterType);
}
