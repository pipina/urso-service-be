package sk.is.urso.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "zp_ziadatelia")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@FieldNameConstants
public class ZpZiadatelia {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vybavuje_osoba")
    private String vybavujeOsoba;

    @Column(name = "vybavuje_telefon")
    private String vybavujeTelefon;

    @Column(name = "vybavuje_email")
    private String vybavujeEmail;

    @NotNull
    @Column(name = "cas_poziadavky")
    private Date casPoziadavky;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "osoba_zaznam")
    private ZpOsobaZaznam osobaZaznam;
}
