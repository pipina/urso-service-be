package sk.is.urso.plugin.repository;

import org.springframework.data.jpa.repository.Query;
import sk.is.urso.plugin.entity.RfoReg1DataEntity;
import sk.is.urso.reg.PluginRepositoryData;

public interface RfoReg1DataRepository extends PluginRepositoryData<RfoReg1DataEntity> {

    @Override
    @Query(value = "SELECT nextval('rfo_1_data_id_seq')", nativeQuery = true)
    long getNextSequence();
}
