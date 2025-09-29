package sk.is.urso.plugin.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sk.is.urso.reg.PluginRepositoryData;
import sk.is.urso.plugin.entity.SubjectReg1DataEntity;

import java.math.BigDecimal;

@Repository
public interface SubjectReg1DataRepository extends PluginRepositoryData<SubjectReg1DataEntity>, JpaSpecificationExecutor<SubjectReg1DataEntity>{
	
	@Query(value = "SELECT nextval('subject_1_data_fo_id_seq')", nativeQuery = true)
	public BigDecimal getSubjectFoIdSequence();
	
	@Query(value = "SELECT nextval('subject_1_data_po_id_seq')", nativeQuery = true)
	public BigDecimal getSubjectPoIdSequence();
	
	@Query(value = "SELECT nextval('subject_1_data_sz_id_seq')", nativeQuery = true)
	public BigDecimal getSubjectSzIdSequence();
	
	@Query(value = "SELECT nextval('subject_1_data_zo_id_seq')", nativeQuery = true)
	public BigDecimal getSubjectZoIdSequence();
	
	@Query(value = "SELECT nextval('subject_1_data_zp_id_seq')", nativeQuery = true)
	public BigDecimal getSubjectZpIdSequence();
	
	@Query(value = "SELECT nextval('subject_1_data_id_seq')", nativeQuery = true)
	@Override
	public long getNextSequence();

	public boolean existsByFoId(String foId);

	SubjectReg1DataEntity findByFoId(String foId);
}
