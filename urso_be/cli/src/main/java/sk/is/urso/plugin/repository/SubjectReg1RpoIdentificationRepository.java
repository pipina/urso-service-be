package sk.is.urso.plugin.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import sk.is.urso.plugin.entity.SubjectReg1RpoIdentificationEntity;
import sk.is.urso.reg.PluginRepository;

@Repository
public interface SubjectReg1RpoIdentificationRepository extends PluginRepository<SubjectReg1RpoIdentificationEntity, Long>, JpaSpecificationExecutor<SubjectReg1RpoIdentificationEntity> {
}
