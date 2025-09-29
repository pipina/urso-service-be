package sk.is.urso.plugin.repository;

import org.springframework.stereotype.Repository;
import sk.is.urso.reg.PluginRepository;
import sk.is.urso.reg.RegisterEntryHistoryKey;
import sk.is.urso.plugin.entity.SubjectReg1DataHistoryEntity;

@Repository
public interface SubjectReg1DataHistoryRepository extends PluginRepository<SubjectReg1DataHistoryEntity, RegisterEntryHistoryKey> {

}
