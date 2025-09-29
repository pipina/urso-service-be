package sk.is.urso.plugin.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import sk.is.urso.reg.AbstractRegEntityData;
import sk.is.urso.reg.PluginRepository;
import sk.is.urso.plugin.entity.SubjectReg1RfoIdentificationEntity;

@Repository
public interface SubjectReg1RfoIdentificationRepository extends PluginRepository<SubjectReg1RfoIdentificationEntity, Long>, JpaSpecificationExecutor<SubjectReg1RfoIdentificationEntity> {

    SubjectReg1RfoIdentificationEntity findByRfoId(String rfoId);

    boolean existsByRfoId(String rfoId);

    boolean existsByZaznamId(AbstractRegEntityData zaznamId);
}
