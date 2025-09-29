package sk.is.urso.model.csru.rpo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
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

@Entity
@Table(name = "csru_rpo_osoba")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@FieldNameConstants
@SuperBuilder
public class RpoOsoba implements IId<Long> {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "person_given_name")
    private String personGivenName;

    @Column(name = "person_family_name")
    private String personFamilyName;

    @Column(name = "person_given_family_name")
    private String personGivenFamilyName;

    @Column(name = "person_prefix")
    private String personPrefix;

    @Column(name = "person_postfix")
    private String personPostfix;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "rpo_statutar_id", nullable = false)
    private RpoStatutar rpoStatutar;
}
