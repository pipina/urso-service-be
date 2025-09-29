package sk.is.urso.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.Type;
import sk.is.urso.enums.CsruNavratovyKodOperacie;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "fs_osoba_zaznam")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@FieldNameConstants
public class FsOsobaZaznam {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "ovm_transaction_id")
    private String ovmTransactionId;

    @NotNull
    @Column(name = "ovm_correlation_id")
    private String ovmCorrelationId;

    @NotNull
    @Column(name = "csru_transaction_id")
    private String csruTransactionId;

    @NotNull
    @Column(name = "cas_podania")
    private Date casPodania;

    @NotNull
    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "navratovy_kod_operacie")
    @Type(type = "pgsql_enum")
    private CsruNavratovyKodOperacie navratovyKodOperacie;

    @Column(name = "chybova_hlaska_operacie")
    private String chybovaHlaskaOperacie;

    @NotNull
    @Column(name = "platnost_do")
    private Date platnostDo;

    @Column(name = "rodne_cislo")
    private String rodneCislo;

    @Column(name = "meno")
    private String meno;

    @Column(name = "priezvisko")
    private String priezvisko;

    @Column(name = "ico")
    private String ico;

    @Column(name = "dic")
    private String dic;

    @Column(name = "nazov_spolocnosti")
    private String nazovSpolocnosti;

    @Column(name = "ma_nedoplatok")
    private Boolean maNedoplatok;

    @ToString.Exclude
    @OneToMany(mappedBy = "osobaZaznam", fetch = FetchType.LAZY)
    private List<FsOsobaNedoplatok> nedoplatky;
}
