package sk.is.urso.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.alfa.utils.PostgreSQLEnumType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import sk.is.urso.enums.UdalostDomena;
import sk.is.urso.enums.UdalostKategoria;
import sk.is.urso.rest.model.UdalostDomenaEnum;
import sk.is.urso.rest.model.UdalostKategoriaEnum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "udalost")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@SuperBuilder
@ToString
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class Udalost {

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ToString.Include
	@Enumerated(EnumType.STRING)
	@Column(name = "domena")
	@Type(type = "pgsql_enum")
	private UdalostDomena domena;

	@ToString.Include
	@Enumerated(EnumType.STRING)
	@Column(name = "kategoria")
	@Type(type = "pgsql_enum")
	private UdalostKategoria kategoria;
	
	@Column
	private LocalDateTime datumCasVytvorenia;
	
	@Column
	private Boolean uspesna;
	
	@Column
	private String pouzivatel;
	
	@Column
	private String popis;
	
//	/**
//	 * Vráti {@link #kategoria} ako {@link UdalostKategoriaEnum}
//	 * @return {@link #kategoria} ako {@link UdalostKategoriaEnum}
//	 */
//	public UdalostKategoriaEnum getKategoriaEnum() {
//		return UdalostKategoriaEnum.fromValue(kategoria.getValue());
//	}
//
//	/**
//	 * Vráti {@link #domena} ako {@link UdalostDomenaEnum}
//	 * @return {@link #domena} ako {@link UdalostDomenaEnum}
//	 */
//	public UdalostDomenaEnum getDomenaEnum() {
//		return UdalostDomenaEnum.fromValue(domena.getValue());
//	}
}
