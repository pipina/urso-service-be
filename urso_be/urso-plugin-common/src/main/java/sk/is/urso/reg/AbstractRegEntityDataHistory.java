package sk.is.urso.reg;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Entity
public abstract class AbstractRegEntityDataHistory {

	@EqualsAndHashCode.Include
	@EmbeddedId
	RegisterEntryHistoryKey id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("zaznamId")
	@JoinColumn(name = "zaznam_id")
	private AbstractRegEntityData zaznamId;

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

	@Column(name = "datum_cas_vytvorenia", nullable = false)
	LocalDateTime datumCasVytvorenia;

	@Column(name = "pouzivatel")
	String pouzivatel;

	@Column
	String modul;
}
