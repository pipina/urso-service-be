package sk.is.urso.reg;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Táto trieda obsahuje polia ktoré musí použivať entity trieda pre konkrétne dáta registra 

 *
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Entity
public abstract class AbstractRegEntityData {
	public static class DbFields {
		private DbFields() {}
		public static final String ID = "id";
		public static final String NEPLATNY = "neplatny";
		public static final String UCINNOST_OD = "ucinnost_od";
		public static final String UCINNOST_DO = "ucinnost_do";
	}
	
	@Id
	@Column(name = "id", nullable = false)
	@EqualsAndHashCode.Include
	Long id;
	
	@Column(name = "xml", nullable = false)
	@ToString.Exclude
	String xml;
	
	@Column(name="platnost_od", nullable = false)
	Date platnostOd;
	
	@Column(name="ucinnost_od", nullable = false)
	Date ucinnostOd;
	
	@Column(name="ucinnost_do", nullable = false)
	Date ucinnostDo;
	
	@Column(name = "neplatny", nullable = false)
	boolean neplatny;
	
	@Column(name = "datum_cas_poslednej_referencie")
	LocalDateTime datumCasPoslednejReferencie;
	
	@Column(name = "pouzivatel")
	String pouzivatel;
	
	@Column
	String modul;
	
	@Transient
	String povodneId;

	public abstract List<AbstractRegEntityDataHistory> getEntityDataHistory();
	public abstract void setEntityDataHistory(List<AbstractRegEntityDataHistory> entityDataHistory);
	public abstract List<AbstractRegEntityDataReference> getEntityDataReferences();
	public abstract void setEntityDataReferences(List<AbstractRegEntityDataReference> entityDataReferences);
	public abstract List<AbstractRegEntityIndex> getEntityIndexes();
	public abstract void setEntityIndexes(List<AbstractRegEntityIndex> abstractRegEntityIndices);

}