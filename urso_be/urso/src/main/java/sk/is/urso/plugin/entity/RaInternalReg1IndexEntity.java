package sk.is.urso.plugin.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import sk.is.urso.reg.AbstractRegEntityData;
import sk.is.urso.reg.AbstractRegEntityIndex;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity(name="RA_INTERNAL_1_INDEX")
public class RaInternalReg1IndexEntity extends AbstractRegEntityIndex {

    @ToString.Exclude
    @NotFound(action= NotFoundAction.IGNORE)
    @ManyToOne(cascade = CascadeType.ALL)// NotFoundAction.IGNORE @ManyToOne and @OneToOne associations are always fetched eagerly.
    @JoinColumn(name = "hodnota_zjednodusena", referencedColumnName = "povodne_id", insertable = false, updatable = false)
    private RaInternalReg1NaturalIdEntity raInternalNaturalId;

    @ToString.Exclude
    @NotFound(action= NotFoundAction.IGNORE)
    @ManyToOne(cascade = CascadeType.ALL)// NotFoundAction.IGNORE @ManyToOne and @OneToOne associations are always fetched eagerly.
    @JoinColumn(name = DbFields.ZAZNAM_ID, referencedColumnName = "id", insertable = false, updatable = false)
    private RaInternalReg1DataEntity raInternalData;

    @ToString.Exclude // nechceme vztahy v toString, nefunguje to bez transakcie lebo je to LAZY
    @ManyToOne(targetEntity = RaInternalReg1DataEntity.class, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = DbFields.ZAZNAM_ID, nullable = false)
    AbstractRegEntityData data;
}
