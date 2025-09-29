package sk.is.urso.model.csru.ra;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.alfa.model.common.IId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "csru_ra_ulica")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@FieldNameConstants
@SuperBuilder
public class RaUlica implements IId<Long> {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "obec_id")
    private RaObec obec;

    @Column(name = "created_reason")
    private String createdReason;

    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @Column(name = "valid_to")
    private LocalDateTime validTo;

    @Column(name = "effective_date")
    private LocalDateTime effectiveDate;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            name = "csru_ra_ulica_mestska_cast",
            joinColumns = {@JoinColumn(name = "ulica_id")},
            inverseJoinColumns = {@JoinColumn(name = "mestska_cast_id")}
    )
    private List<RaMestskaCast> raMestskaCastList = new ArrayList<>();
}
