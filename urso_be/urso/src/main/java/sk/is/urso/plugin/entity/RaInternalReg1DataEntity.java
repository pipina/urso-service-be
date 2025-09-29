package sk.is.urso.plugin.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
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
@Entity(name="RA_INTERNAL_1_DATA")
public class RaInternalReg1DataEntity extends AbstractRegEntityData {

    @ToString.Exclude
    @NotFound(action= NotFoundAction.IGNORE)
    @OneToMany(mappedBy = "data", fetch = FetchType.LAZY , cascade = CascadeType.ALL)// NotFoundAction.IGNORE @ManyToOne and @OneToOne associations are always fetched eagerly.
//    @JoinColumn(name = "id", referencedColumnName = "entry_id", insertable = false, updatable = false)
    private List<RaInternalReg1IndexEntity> raInternalIndex;

    @ToString.Exclude
    @OneToMany(targetEntity = RaInternalReg1IndexEntity.class, mappedBy = "data", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AbstractRegEntityIndex> entityIndexes = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(targetEntity = RaInternalReg1DataHistoryEntity.class, mappedBy = "zaznamId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AbstractRegEntityDataHistory> entityDataHistory = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(targetEntity = RaInternalReg1DataReferenceEntity.class, mappedBy = "zaznamId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AbstractRegEntityDataReference> entityDataReferences = new ArrayList<>();
}
