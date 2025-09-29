package sk.is.urso.plugin.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sk.is.urso.reg.PluginRepositoryData;
import sk.is.urso.plugin.entity.RaInternalReg1DataEntity;

@Repository
public interface RaInternalReg1DataRepository extends PluginRepositoryData<RaInternalReg1DataEntity> {

    @Override
    @Query(value = "SELECT nextval('ra_internal_1_data_id_seq')", nativeQuery = true)
    public long getNextSequence();
}
