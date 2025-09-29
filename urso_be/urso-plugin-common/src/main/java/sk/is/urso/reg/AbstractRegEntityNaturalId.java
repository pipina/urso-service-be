package sk.is.urso.reg;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Entity
public class AbstractRegEntityNaturalId {
	
	@Id
	@Column(name = "povodne_id")
	String povodneId;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "zaznam_id", nullable = false)
	AbstractRegEntityData zaznamId;
}
