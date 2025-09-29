package sk.is.urso.plugin.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.is.urso.reg.AbstractRegEntityData;
import sk.is.urso.reg.AbstractRegEntityDataHistory;
import sk.is.urso.reg.AbstractRegEntityDataReference;
import sk.is.urso.reg.AbstractRegEntityIndex;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Entity(name="SUBJECT_1_DATA")
public class SubjectReg1DataEntity extends AbstractRegEntityData {
	
	@Column(name = "subjekt_id", nullable = false)
	String subjektId;
	
	@Column(name = "fo_id")
	String foId;

	@ToString.Exclude
	@OneToMany(mappedBy = "zaznamId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<SubjectReg1RfoIdentificationEntity> entityRfoIdentifications;

	@ToString.Exclude
	@OneToMany(targetEntity = SubjectReg1IndexEntity.class, mappedBy = "data", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<AbstractRegEntityIndex> entityIndexes = new ArrayList<>();

	@ToString.Exclude
	@OneToMany(targetEntity = SubjectReg1DataHistoryEntity.class, mappedBy = "zaznamId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<AbstractRegEntityDataHistory> entityDataHistory = new ArrayList<>();

	@ToString.Exclude
	@OneToMany(targetEntity = SubjectReg1DataReferenceEntity.class, mappedBy = "zaznamId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<AbstractRegEntityDataReference> entityDataReferences = new ArrayList<>();
}
