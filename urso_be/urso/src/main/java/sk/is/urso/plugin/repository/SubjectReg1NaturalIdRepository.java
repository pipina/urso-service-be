package sk.is.urso.plugin.repository;

import org.springframework.stereotype.Repository;
import sk.is.urso.reg.PluginRepositoryNaturalId;
import sk.is.urso.plugin.entity.SubjectReg1NaturalIdEntity;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Repository
public interface SubjectReg1NaturalIdRepository extends PluginRepositoryNaturalId<SubjectReg1NaturalIdEntity> {

	Optional<SubjectReg1NaturalIdEntity> findByZaznamId(@NotNull Long zaznamId);

}
