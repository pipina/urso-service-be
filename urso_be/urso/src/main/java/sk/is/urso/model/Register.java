package sk.is.urso.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import sk.is.urso.reg.model.RegisterId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(RegisterId.class)
@Table(name = "register")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@SuperBuilder
@ToString
public class Register {

	@Id
	@Column(name = "register_id")
	@EqualsAndHashCode.Include
	@ToString.Include
	private String registerId;
	
	@Id
	@Column(name = "verzia_registra_id")
	@EqualsAndHashCode.Include
	@ToString.Include
	private Integer verziaRegistraId;

	@Column(name = "`schema`")
	private Long schema;
	
	@Column
	private String nazovRegistra;
	
	public Register (RegisterId registerId) {
		this.registerId = registerId.getRegisterId();
		this.verziaRegistraId = registerId.getVerziaRegistraId();
	}
	
	public Register (String registerId, Integer verziaRegistraId, Long schema) {
		this.registerId = registerId;
		this.verziaRegistraId = verziaRegistraId;
		this.schema = schema;
	}
}
