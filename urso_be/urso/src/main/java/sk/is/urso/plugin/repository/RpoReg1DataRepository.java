package sk.is.urso.plugin.repository;

import org.springframework.data.jpa.repository.Query;
import sk.is.urso.reg.PluginRepositoryData;
import sk.is.urso.plugin.entity.RpoReg1DataEntity;

public interface RpoReg1DataRepository extends PluginRepositoryData<RpoReg1DataEntity> {

	@Override
	@Query(value = "SELECT nextval('rpo_1_data_id_seq')", nativeQuery = true)
	public long getNextSequence();
}
