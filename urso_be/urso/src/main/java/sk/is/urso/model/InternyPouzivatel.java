package sk.is.urso.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.alfa.model.common.IUser;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "adm_interny_pouzivatel")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@SuperBuilder
@Accessors(chain = true)
@ToString(onlyExplicitlyIncluded = true)
public class InternyPouzivatel implements IUser<Long> {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "meno", nullable = false)
    private String meno;

    @NotNull
    @Column(name = "priezvisko", nullable = false)
    private String priezvisko;

    @NotNull
    @Column(name = "formatovane_meno", nullable = false)
    private String formatovaneMeno;

    @NotNull
    @Column(name = "domenovy_ucet", nullable = false)
    private String domenovyUcet;

    @Column(name = "email")
    private String email;

    @Column(name = "mobil")
    private String mobil;

    @Column(name = "telefon")
    private String telefon;

    @Column(name = "krajske_pracovisko")
    private String krajskePracovisko;

    @NotNull
    @Column(name = "veduci_utvaru", nullable = false)
    private Boolean veduciUtvaru;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "organizacny_utvar_id", nullable = false)
    private OrganizacnyUtvar organizacnyUtvar;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    //    todo urad;

    //todo rolees
}
