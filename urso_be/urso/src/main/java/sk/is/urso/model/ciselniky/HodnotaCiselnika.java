package sk.is.urso.model.ciselniky;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.alfa.model.common.IDeleted;
import org.alfa.model.common.IId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "hodnota_ciselnika")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Accessors(chain = true)
@ToString(onlyExplicitlyIncluded = true)
@SuperBuilder
public class HodnotaCiselnika implements IId<Long>, IDeleted {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "kod_polozky", nullable = false)
    private String kodPolozky;

    @NotNull
    @Column(name = "nazov_polozky", nullable = false)
    private String nazovPolozky;

    @NotNull
    @Column(name = "kod_ciselnika", nullable = false)
    private String kodCiselnika;

    @Column(name = "dodatocny_obsah")
    private String dodatocnyObsah;

    @Column(name = "poradie")
    private Integer poradie;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "ciselnik_id", nullable = false)
    private Ciselnik ciselnik;

    @ManyToOne
    @JoinColumn(name = "nadradena_hodnota_ciselnika_id")
    private HodnotaCiselnika nadradenaHodnotaCiselnika;

    @NotNull
    @Column(name = "platnost_od", nullable = false)
    private LocalDate platnostOd;

    @Column(name = "platnost_do")
    private LocalDate platnostDo;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean getDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
