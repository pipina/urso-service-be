package sk.is.urso.plugin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sk.is.urso.reg.AbstractRegEntityData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "subject_1_rfo_identification")
public class SubjectReg1RfoIdentificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    @EqualsAndHashCode.Include
    Long id;

    @ToString.Exclude
    @Column
    String rfoId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "zaznam_id")
    AbstractRegEntityData zaznamId;

    @Column(nullable = false)
    Boolean identifikovany;

    @Column(nullable = false)
    Boolean oznaceny;

    @Column(nullable = false)
    Boolean zluceny;

    @Column(nullable = false)
    Boolean chybny;

    @ToString.Exclude
    @Column
    String sprava;

    @Column(nullable = false)
    LocalDateTime datumCasVytvorenia;
}
