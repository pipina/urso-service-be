package sk.is.urso.plugin.repository;

import org.springframework.stereotype.Repository;
import sk.is.urso.reg.PluginRepository;
import sk.is.urso.reg.RegisterEntryHistoryKey;
import sk.is.urso.plugin.entity.RaInternalReg1DataHistoryEntity;

@Repository
public interface RaInternalReg1DataHistoryRepository extends PluginRepository<RaInternalReg1DataHistoryEntity, RegisterEntryHistoryKey> {
}
