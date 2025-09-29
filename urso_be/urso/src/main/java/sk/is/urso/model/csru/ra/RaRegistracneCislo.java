package sk.is.urso.model.csru.ra;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.alfa.model.common.IId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "csru_ra_registracne_cislo")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@FieldNameConstants
@SuperBuilder
public class RaRegistracneCislo implements IId<Long> {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    @Column(name = "number", nullable = false)
    private String number;

    @Column(name = "contains_flats")
    private Boolean containsFlats;

    @Column(name = "residential")
    private Boolean residential;

    @Column(name = "building_type_code")
    private String buildingTypeCode;

    @Column(name = "building_type_name")
    private String buildingTypeName;

    @Column(name = "created_reason")
    private String createdReason;

    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @Column(name = "valid_to")
    private LocalDateTime validTo;

    @Column(name = "effective_date")
    private LocalDateTime effectiveDate;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "obec_id", nullable = false)
    private RaObec obec;

    @ManyToOne
    @JoinColumn(name = "mestska_cast_id")
    private RaMestskaCast mestskaCast;
}
