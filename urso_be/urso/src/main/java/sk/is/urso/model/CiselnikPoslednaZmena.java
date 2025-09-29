package sk.is.urso.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "ciselnik_posledna_zmena")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class CiselnikPoslednaZmena {

    @EqualsAndHashCode.Include
    @Id
    @Column
    private String kodCiselnika;

    @Column
    private Date poslednaZmena;
}
