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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ciselnik")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Accessors(chain = true)
@ToString(onlyExplicitlyIncluded = true)
@SuperBuilder
public class Ciselnik implements IId<Long>, IDeleted {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "kod_ciselnika", nullable = false)
    private String kodCiselnika;

    @NotNull
    @Column(name = "nazov_ciselnika", nullable = false)
    private String nazovCiselnika;

    @Column(name = "externy_kod")
    private String externyKod;

    @NotNull
    @Column(name = "verzia", nullable = false)
    private Integer verzia = 1;

    @NotNull
    @Column(name = "platnost_od", nullable = false)
    private LocalDate platnostOd;

    @Column(name = "platnost_do")
    private LocalDate platnostDo;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @ToString.Exclude
    @OneToMany(mappedBy = "ciselnik", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final List<HodnotaCiselnika> hodnotyCiselnika = new ArrayList<>();

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
