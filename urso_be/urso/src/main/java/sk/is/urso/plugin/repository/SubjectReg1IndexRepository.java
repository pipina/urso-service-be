package sk.is.urso.plugin.repository;

import org.springframework.stereotype.Repository;
import sk.is.urso.reg.PluginRepositoryIndexes;
import sk.is.urso.plugin.entity.SubjectReg1IndexEntity;

import java.util.Optional;

@Repository
public interface SubjectReg1IndexRepository extends PluginRepositoryIndexes<SubjectReg1IndexEntity> {

    public Optional<SubjectReg1IndexEntity> findByKlucAndHodnotaAndAktualny(String kluc, String hodnota, boolean aktualny);

    public Optional<SubjectReg1IndexEntity> findByZaznamIdAndKlucAndAktualny(Long zaznamId, String kluc, boolean aktualny);
}
