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

@Entity
@Table(name = "csru_ra_address_search")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@FieldNameConstants
@SuperBuilder
public class RaAddressSearch implements IId<Long> {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "kraj_id")
    private RaKraj kraj;

    @ManyToOne
    @JoinColumn(name = "okres_id")
    private RaOkres okres;

    @ManyToOne
    @JoinColumn(name = "obec_id")
    private RaObec obec;

    @ManyToOne
    @JoinColumn(name = "ulica_id")
    private RaUlica ulica;

    @ManyToOne
    @JoinColumn(name = "registracne_cislo_id")
    private RaRegistracneCislo registracneCislo;

    @ManyToOne
    @JoinColumn(name = "cislo_budovy_id")
    private RaCisloBudovy cisloBudovy;

    @Column(name = "fulltext_search")
    private String fulltextSearch;
}