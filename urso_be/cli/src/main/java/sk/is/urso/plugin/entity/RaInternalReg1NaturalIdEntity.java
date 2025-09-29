package sk.is.urso.plugin.entity;

import sk.is.urso.reg.AbstractRegEntityNaturalId;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity(name = "RA_INTERNAL_1_NATURAL_ID")
public class RaInternalReg1NaturalIdEntity extends AbstractRegEntityNaturalId {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entry_id", nullable = false, insertable = false, updatable = false)
    RaInternalReg1DataEntity raInternalData;

}
