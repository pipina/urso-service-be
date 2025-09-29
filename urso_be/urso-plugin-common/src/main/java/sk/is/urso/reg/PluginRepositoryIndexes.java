package sk.is.urso.reg;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface PluginRepositoryIndexes<T extends AbstractRegEntityIndex> extends PluginRepository<T, Long> {

	List<T> findAllByDataIdAndKluc(Long data, String kluc);

	List<T> findAllByZaznamIdAndKluc(Long zaznamId, String kluc);

	boolean existsByKlucAndHodnotaAndAktualny(String kluc, String hodnota, boolean aktualny);

	void deleteByZaznamId(Long zaznamId);
}
