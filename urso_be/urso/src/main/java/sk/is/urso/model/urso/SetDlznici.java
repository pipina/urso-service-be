package sk.is.urso.model.urso;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import sk.is.urso.model.SpStavZiadost;

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
@Table(name = "set_dlznici", schema = "csru_set")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@FieldNameConstants
@SuperBuilder
public class SetDlznici {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    @Column(name = "nazov", nullable = false)
    private String nazov;

    @NotNull
    @Column(name = "ico", nullable = false)
    private String ico;

    @Column(name = "id_po")
    private Integer idPo;

    @Column(name = "synchronized")
    private Boolean sync;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_refreshed")
    private SetDlzniciRefresh setDlzniciRefresh;

    @ToString.Exclude
    @OneToMany(mappedBy = "setDlznici", fetch = FetchType.EAGER)
    private List<SetDlzniciObdobie> setDlzniciObdobieList = new ArrayList<>();
}
