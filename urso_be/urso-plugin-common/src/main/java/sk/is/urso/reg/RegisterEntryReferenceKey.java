package sk.is.urso.reg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterEntryReferenceKey implements Serializable {
	private static final long serialVersionUID = 1L;

	Long zaznamId;
	@Column(name = "modul", nullable = false)
	String modul;
}
