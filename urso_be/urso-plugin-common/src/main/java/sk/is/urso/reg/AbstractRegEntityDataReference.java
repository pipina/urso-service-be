package sk.is.urso.reg;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

/**
 * Táto trieda obsahuje polia pre sledovanie referencií k záznamom registra

 *
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Entity
public abstract class AbstractRegEntityDataReference {
	
	public static class DbFields{
		private DbFields() {}
		public static final String ZAZNAM_ID = "zaznam_id";
		public static final String MODUL = "modul";
	}
	@EqualsAndHashCode.Include
	@EmbeddedId
	RegisterEntryReferenceKey id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("zaznamId")
	@JoinColumn(name = "zaznam_id")
	private AbstractRegEntityData zaznamId;
	
	@Column(name = "pocet_referencii", nullable = false)
	int pocetReferencii;
}
