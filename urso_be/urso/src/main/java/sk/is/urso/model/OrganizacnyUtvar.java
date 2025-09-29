package sk.is.urso.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "adm_organizacny_utvar")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@FieldNameConstants
@SuperBuilder
public class OrganizacnyUtvar implements IDeleted, IId<Long> {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "nazov", nullable = false)
    private String nazov;

    @ManyToOne
    @JoinColumn(name = "nadriadeny_utvar_id")
    private OrganizacnyUtvar nadriadenyUtvar;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "odbor_id", nullable = false)
    private Odbor odbor;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @ToString.Exclude
    @OneToMany(mappedBy = "organizacnyUtvar", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<InternyPouzivatel> internyPouzivatelList = new ArrayList<>();

    @Override
    public boolean getDeleted() {
        return deleted;
    }
}
