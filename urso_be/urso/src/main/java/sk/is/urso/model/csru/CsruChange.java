package sk.is.urso.model.csru;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "csru_change")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
public class CsruChange {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Date dateFrom;

    @Column
    private Date dateTo;

    @Column
    private Timestamp start;

    @Column(name = "`end`")
    private Timestamp end;

    @Column
    private String type;

    @Column
    private String resultStatus;

    @Column
    private String errorMsg;

    @Column
    private Integer processedItems;
}
