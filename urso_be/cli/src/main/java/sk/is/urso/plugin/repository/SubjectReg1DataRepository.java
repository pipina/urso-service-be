package sk.is.urso.plugin.repository;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sk.is.urso.plugin.entity.SubjectReg1DataEntity;
import sk.is.urso.reg.PluginRepositoryData;

import java.math.BigDecimal;

@Repository
public interface SubjectReg1DataRepository extends PluginRepositoryData<SubjectReg1DataEntity>, JpaSpecificationExecutor<SubjectReg1DataEntity> {

    @Query(value = "SELECT nextval('subject_1_data_fo_id_seq')", nativeQuery = true)
    BigDecimal getSubjectFoIdSequence();

    @Query(value = "SELECT nextval('subject_1_data_po_id_seq')", nativeQuery = true)
    BigDecimal getSubjectPoIdSequence();

    @Query(value = "SELECT setval('subject_1_data_fo_id_seq', ?1)", nativeQuery = true)
    void setSubjectFoIdSequence(long foiId);

    @Query(value = "SELECT setval('subject_1_data_po_id_seq', ?1)", nativeQuery = true)
    void setSubjectPoIdSequence(long poiId);

    @Query(value = "SELECT nextval('subject_1_data_id_seq')", nativeQuery = true)
    long getNextSequence();
}
