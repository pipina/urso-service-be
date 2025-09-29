package sk.is.urso.plugin.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.is.urso.reg.AbstractRegEntityData;
import sk.is.urso.reg.AbstractRegEntityIndex;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

;

@Getter
@Setter
@Entity(name="SUBJECT_1_INDEX")
public class SubjectReg1IndexEntity extends AbstractRegEntityIndex {
    @ToString.Exclude // nechceme vztahy v toString, nefunguje to bez transakcie lebo je to LAZY
    @ManyToOne(targetEntity = SubjectReg1DataEntity.class, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = DbFields.ZAZNAM_ID, nullable = false)
    AbstractRegEntityData data;
}
