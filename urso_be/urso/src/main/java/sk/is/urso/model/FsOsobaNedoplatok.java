package sk.is.urso.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.Type;
import sk.is.urso.enums.FsDruhDanePohladavky;
import sk.is.urso.enums.FsNedoplatok;
import sk.is.urso.enums.FsNedoplatokChybovyKod;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "fs_osoba_nedoplatok")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@FieldNameConstants
public class FsOsobaNedoplatok {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "nedolpatok_chybovy_kod")
    @Type(type = "pgsql_enum")
    private FsNedoplatokChybovyKod nedoplatokChybovyKod;

    @Column(name = "nedolpatok_chybova_sprava")
    private String nedolpatokChybovaSprava;

    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "nedoplatok")
    @Type(type = "pgsql_enum")
    private FsNedoplatok nedoplatok;

    @Column(name = "vyska_nedoplatku")
    private String vyskaNedoplatku;

    @Column(name = "mena")
    private String mena;

    @Column(name = "datum_nedoplatku")
    private Date datumNedoplatku;

    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "druh_dane_pohladavky")
    @Type(type = "pgsql_enum")
    private FsDruhDanePohladavky druhDanePohladavky;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "osoba_zaznam")
    private FsOsobaZaznam osobaZaznam;
}
