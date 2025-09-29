package sk.is.urso.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.alfa.utils.PostgreSQLEnumType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import sk.is.urso.enums.UrsoNedoplatokTyp;
import sk.is.urso.enums.UrsoSubjectStav;
import sk.is.urso.model.urso.SetDlznici;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "urso_subject_stack")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@FieldNameConstants
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class UrsoSubjectStack {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "request_id", nullable = false)
    private Long requestId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dlznici_id")
    private SetDlznici setDlznici;

    @Column(name = "doplnujuca_textova_informacia")
    private String doplnujucaTextovaInformacia;

    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "stav")
    @Type(type = "pgsql_enum")
    private UrsoSubjectStav ursoSubjectStav;

    @NotNull
    @Column(name = "cas_vytvorenia", nullable = false)
    private Date casVytvorenia;

    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "typ_nedoplatku")
    @Type(type = "pgsql_enum")
    private UrsoNedoplatokTyp ursoNedoplatokTyp;
}
