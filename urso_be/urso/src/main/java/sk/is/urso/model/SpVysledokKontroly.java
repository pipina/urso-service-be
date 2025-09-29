package sk.is.urso.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.alfa.utils.PostgreSQLEnumType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import sk.is.urso.enums.SpNedoplatok;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "sp_vysledok_kontroly")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@FieldNameConstants
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class SpVysledokKontroly {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "nedoplatok")
    @Type(type = "pgsql_enum")
    private SpNedoplatok nedoplatok;

    @Column(name = "osb_status_text")
    private String osbStatusText;

    @OneToOne(mappedBy = "vysledokKontroly", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private SpStavZiadost stavZiadost;
}
