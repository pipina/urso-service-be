package sk.is.urso.reg;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AbstractRegEntityDataWithoutXml {

	Long id;

	Date platnostOd;
	
	Date ucinnostOd;

	Date ucinnostDo;
	
	boolean neplatny;

	String pouzivatel;
	
	String modul;
}
