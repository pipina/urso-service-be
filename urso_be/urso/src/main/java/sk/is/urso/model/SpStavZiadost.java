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
import sk.is.urso.enums.CsruStavZiadosti;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "sp_stav_ziadost")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@FieldNameConstants
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class SpStavZiadost {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "request_id")
    private Long requestId;

    @NotNull
    @Column(name = "ovm_transaction_id")
    private String ovmTransactionId;

    @NotNull
    @Column(name = "ovm_correlation_id")
    private String ovmCorrelationId;

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

    @Column(name = "csru_transaction_id")
    private String csruTransactionId;

    @NotNull
    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "stav")
    @Type(type = "pgsql_enum")
    private CsruStavZiadosti stav;

    @NotNull
    @Column(name = "platnost_do")
    private Date platnostDo;

    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "navratovy_kod_stavu")
    @Type(type = "pgsql_enum")
    private CsruNavratovyKodOperacie navratovyKodStavu;

    @Column(name = "chybova_hlaska_stavu")
    private String chybovaHlaskaStavu;

    @Column(name = "ma_nedoplatok")
    private Boolean maNedoplatok;

    @Column(name = "result_date")
    private Date resultDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "osoba_zaznam")
    private SpOsobaZaznam osobaZaznam;

    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "vystupny_subor")
    private SpVystupnySubor vystupnySubor;

    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "vysledok_kontroly")
    private SpVysledokKontroly vysledokKontroly;
}
