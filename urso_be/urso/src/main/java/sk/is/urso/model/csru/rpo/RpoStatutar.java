package sk.is.urso.model.csru.rpo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "csru_rpo_statutar")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@FieldNameConstants
@SuperBuilder
public class RpoStatutar implements IId<Long> {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type")
    private String type;

    @Column(name = "body_member")
    private String bodyMember;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "rpo_zaznam_id", nullable = false)
    private RpoZaznam rpoZaznam;

    @NotNull
    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "adresa_id", referencedColumnName = "id", unique = true, nullable = false)
    private RpoAdresa address;

    @ToString.Exclude
    @OneToMany(mappedBy = "rpoStatutar", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RpoOsoba> rpoOsobaList = new ArrayList<>();
}
