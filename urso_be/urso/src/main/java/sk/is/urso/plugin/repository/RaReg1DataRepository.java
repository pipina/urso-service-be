package sk.is.urso.plugin.repository;

import org.springframework.data.jpa.repository.Query;
import sk.is.urso.reg.PluginRepositoryData;
import sk.is.urso.plugin.entity.RaReg1DataEntity;

public interface RaReg1DataRepository extends PluginRepositoryData<RaReg1DataEntity>{

	@Override
	@Query(value = "SELECT nextval('ra_1_data_id_seq')", nativeQuery = true)
	public long getNextSequence();
}
