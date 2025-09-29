package sk.is.urso.plugin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import sk.is.urso.reg.AbstractRegEntityDataReference;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity(name = "SUBJECT_1_DATA_REFERENCE")
public class SubjectReg1DataReferenceEntity extends AbstractRegEntityDataReference {

    @Column(name = "subjekt_id")
    String subjektId;
}
