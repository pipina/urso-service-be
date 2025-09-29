package sk.is.urso.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "zp_osoba_zaznam")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@FieldNameConstants
public class ZpOsobaZaznam {

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

    @Column(name = "datum_narodenia")
    private Date datumNarodenia;

    @Column(name = "vybavuje_osoba")
    private String vybavujeOsoba;

    @Column(name = "vybavuje_telefon")
    private String vybavujeTelefon;

    @Column(name = "vybavuje_email")
    private String vybavujeEmail;

    @ToString.Exclude
    @OneToMany(mappedBy = "osobaZaznam", fetch = FetchType.LAZY)
    private List<ZpStavZiadost> stavZiadostList;

    @ToString.Exclude
    @OneToMany(mappedBy = "osobaZaznam", fetch = FetchType.LAZY)
    private List<ZpZiadatelia> ziadatelias;
}
