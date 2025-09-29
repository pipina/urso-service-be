package sk.is.urso.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "sp_osoba_zaznam")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@FieldNameConstants
public class SpOsobaZaznam {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rodne_cislo")
    private String rodneCislo;

    @Column(name = "meno")
    private String meno;

    @Column(name = "priezvisko")
    private String priezvisko;

    @Column(name = "ico")
    private String ico;

    @Column(name = "nazov_spolocnosti")
    private String nazovSpolocnosti;

    @ToString.Exclude
    @OneToMany(mappedBy = "osobaZaznam", fetch = FetchType.LAZY)
    private List<SpStavZiadost> stavZiadosti;
}
