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
@Table(name = "csru_ra_cislo_budovy")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@FieldNameConstants
@SuperBuilder
public class RaCisloBudovy implements IId<Long> {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "`number`")
    private String number;

    @Column(name = "`index`")
    private String index;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "created_reason")
    private String createdReason;

    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @Column(name = "valid_to")
    private LocalDateTime validTo;

    @Column(name = "effective_date")
    private LocalDateTime effectiveDate;

    @ManyToOne
    @JoinColumn(name = "obec_id")
    private RaObec obec;

    @ManyToOne
    @JoinColumn(name = "ulica_id")
    private RaUlica ulica;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "reg_cislo_id", nullable = false)
    private RaRegistracneCislo registracneCislo;
}
