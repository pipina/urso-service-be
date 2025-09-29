package sk.is.urso.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.alfa.utils.PostgreSQLEnumType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import sk.is.urso.enums.CsruNavratovyKodOperacie;
import sk.is.urso.enums.ZpNedoplatok;
import sk.is.urso.enums.ZpPoistovna;
import sk.is.urso.enums.ZpPopisKoduVysledkuSpracovania;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "zp_vysledok_kontroly")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@FieldNameConstants
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class ZpVysledokKontroly {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "poistovna")
    @Type(type = "pgsql_enum")
    private ZpPoistovna poistovna;

    @NotNull
    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "nedoplatok")
    @Type(type = "pgsql_enum")
    private ZpNedoplatok nedoplatok;

    @Column(name = "vyska_nedoplatku")
    private Float vyskaNedoplatku;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stav_ziadost")
    private ZpStavZiadost stavZiadost;

    @NotNull
    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "vysledok_spracovania")
    @Type(type = "pgsql_enum")
    private ZpPopisKoduVysledkuSpracovania vysledokSpracovania;

    @NotNull
    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "navratovy_kod")
    @Type(type = "pgsql_enum")
    private CsruNavratovyKodOperacie navratovyKod;

    @Column(name = "chybova_hlaska")
    private String chybovaHlaska;
}
