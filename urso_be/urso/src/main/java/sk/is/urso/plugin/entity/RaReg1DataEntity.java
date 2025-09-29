package sk.is.urso.plugin.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import sk.is.urso.reg.AbstractRegEntityData;
import sk.is.urso.reg.AbstractRegEntityDataHistory;
import sk.is.urso.reg.AbstractRegEntityDataReference;
import sk.is.urso.reg.AbstractRegEntityIndex;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
@Entity(name="RA_1_DATA")
public class RaReg1DataEntity extends AbstractRegEntityData {
    @ToString.Exclude
    @OneToMany(targetEntity = RaReg1IndexEntity.class, mappedBy = "data", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AbstractRegEntityIndex> entityIndexes = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(targetEntity = RaReg1DataHistoryEntity.class, mappedBy = "zaznamId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AbstractRegEntityDataHistory> entityDataHistory = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(targetEntity = RaReg1DataReferenceEntity.class, mappedBy = "zaznamId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AbstractRegEntityDataReference> entityDataReferences = new ArrayList<>();
}
