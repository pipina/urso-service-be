package sk.is.urso.reg;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.util.Date;

/**
 * Spoločná definícia pre  tabuľku obsahujúcu index registra

 *
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Entity
@FieldNameConstants
public abstract class AbstractRegEntityIndex {

	/**
	 * Obshauje konstanty pre standardne indexovane polia
	
	 *
	 */
	public static class IndexedFields {
		public static final String ZAZNAM_ID = "zaznamId";
		private IndexedFields() {}
	}
	/**
	 * Obsahuje konstanty pre databazove nazvy poli v index tabulke
	
	 *
	 */
	public static class DbFields{

		private DbFields() {}
		public static final String ZAZNAM_ID = "zaznam_id";
		public static final String KLUC = "kluc";
		public static final String HODNOTA_ZJEDNODUSENA = "hodnota_zjednodusena";
		public static final String HODNOTA = "hodnota";
		public static final String UCINNOST_OD = "ucinnost_od";
		public static final String UCINNOST_DO = "ucinnost_do";
		public static final String SEKVENCIA = "sekvencia";
		public static final String AKTUALNY = "aktualny";
		public static final String KONTEXT = "kontext";
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", nullable = false)
	@EqualsAndHashCode.Include
	Long id;
	
	@NonNull
	@Column(name = DbFields.KLUC, nullable = false)
	String kluc;
	
	@NonNull
	@Column(name = DbFields.HODNOTA, nullable = false)
	String hodnota;
	
	@NonNull
	@Column(name = DbFields.HODNOTA_ZJEDNODUSENA, nullable = false)
	String hodnotaZjednodusena;
	
	@NonNull
	@Column(name = DbFields.UCINNOST_OD, nullable = false)
    Date ucinnostOd;

	@Column(name = DbFields.UCINNOST_DO)
	Date ucinnostDo;
	
	@Column(name = DbFields.SEKVENCIA, nullable = false)
	@NonNull
    Integer sekvencia;
	
	@NonNull
	@Column(name = DbFields.AKTUALNY, nullable = false)
    Boolean aktualny;
	
	@NonNull
	@Column(name = DbFields.ZAZNAM_ID, insertable = false, updatable = false, nullable = false)
    Long zaznamId;

	@NonNull
	@Column(name = DbFields.KONTEXT, nullable = false)
	String kontext;
	
	public abstract AbstractRegEntityData getData();
	public abstract void setData(AbstractRegEntityData data);
}
