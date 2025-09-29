package sk.is.urso.model.csru.rpo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.alfa.model.common.ICreatedAt;
import org.alfa.model.common.IDeleted;
import org.alfa.model.common.IId;
import org.alfa.model.common.IModifiedOn;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "csru_rpo_zaznam")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@FieldNameConstants
@SuperBuilder
public class RpoZaznam implements IId<Long>, ICreatedAt, IModifiedOn, IDeleted {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ipo")
    private Long ipo;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "established_on", nullable = false)
    private LocalDate establishedOn;

    @Column(name = "terminated_on")
    private LocalDate terminatedOn;

    @NotNull
    @Column(name = "legal_form", nullable = false)
    private String legalForm;

    @Column(name = "registration_office")
    private String registrationOffice;

    @Column(name = "registration_number")
    private String registrationNumber;

    @Column(name = "main_activity_code")
    private Integer mainActivityCode;

    @Column(name = "main_activity_name")
    private String mainActivityName;

    @NotNull
    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "adresa_id", referencedColumnName = "id", unique = true, nullable = false)
    private RpoAdresa address;

    @ToString.Exclude
    @OneToMany(mappedBy = "rpoZaznam", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RpoStatutar> rpoStatutarList = new ArrayList<>();

    @Column(name = "record_xml")
    private String recordXml;

    @NotNull
    @Column(name = "datum_cas_vytvorenia", nullable = false)
    private LocalDateTime datumCasVytvorenia;

    @NotNull
    @Column(name = "datum_cas_upravy", nullable = false)
    private LocalDateTime datumCasUpravy;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @Override
    public LocalDateTime getDatumCasVytvorenia() {
        return this.datumCasVytvorenia;
    }

    @Override
    public void setDatumCasVytvorenia(LocalDateTime createdAt) {
        this.datumCasVytvorenia = createdAt;
    }

    @Override
    public LocalDateTime getDatumCasUpravy() {
        return this.datumCasUpravy;
    }

    @Override
    public void setDatumCasUpravy(LocalDateTime modifiedOn) {
        this.datumCasUpravy = modifiedOn;
    }

    @Override
    public boolean getDeleted() {
        return this.deleted;
    }

    @Override
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
