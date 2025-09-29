package sk.is.urso.plugin.repository;

import org.springframework.stereotype.Repository;
import sk.is.urso.plugin.entity.RaInternalReg1DataReferenceEntity;
import sk.is.urso.reg.PluginRepositoryReferences;
import sk.is.urso.reg.RegisterEntryReferenceKey;

@Repository
public interface RaInternalReg1DataReferenceRepository extends PluginRepositoryReferences<RaInternalReg1DataReferenceEntity, RegisterEntryReferenceKey> {
}
