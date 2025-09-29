package sk.is.urso.reg.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sk.is.urso.common.regconfig.plugin.v1.RegisterField;
import sk.is.urso.reg.AbstractRegPlugin;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterId implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String registerId;
	private int verziaRegistraId;
	
	public RegisterId(RegisterField field) {
		this.registerId = field.getRegisterId();
		this.verziaRegistraId = field.getVersion();
	}
	
	@Override
	public String toString(){
		return AbstractRegPlugin.getFullRegisterId(this.registerId, this.verziaRegistraId);
	}
}
