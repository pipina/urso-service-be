package sk.is.urso.reg;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface PluginRepositoryData<T extends AbstractRegEntityData> extends PluginRepository<T, Long> {

	public <U> U findById(Long id, Class<U> type);//NOSONAR

	public <U> Page<U> findAllByDatumCasPoslednejReferencieLessThanAndDatumCasPoslednejReferencieNotNull(LocalDateTime datumCasPoslednejReferencie, Class<U> type, Pageable pageable);

	public abstract long getNextSequence();
}
