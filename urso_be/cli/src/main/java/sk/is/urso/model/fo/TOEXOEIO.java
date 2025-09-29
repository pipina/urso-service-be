package sk.is.urso.model.fo;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.datatype.XMLGregorianCalendar;


@Getter
@XmlAccessorType(XmlAccessType.FIELD)
public class TOEXOEIO {
    @XmlElement(name = "ID", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected String id;
    @XmlElement(name = "PO", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected String po;
    @XmlElement(name = "RC", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected String rc;
    @XmlElement(name = "DN", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected XMLGregorianCalendar dn;
    @XmlElement(name = "UC", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected Integer uc;
    @XmlElement(name = "UCEUCNA", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected String uceucna;
    @XmlElement(name = "MN", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected String mn;
    @XmlElement(name = "SN", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected Integer sn;
    @XmlElement(name = "STASNNA", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected String stasnna;
    @XmlElement(name = "PI", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected Integer pi;
    @XmlElement(name = "POHPINA", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected String pohpina;
    @XmlElement(name = "RS", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected Integer rs;
    @XmlElement(name = "RSTRSNA", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected String rstrsna;
    @XmlElement(name = "RM", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected String rm;
    @XmlElement(name = "NI", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected Integer ni;
    @XmlElement(name = "NARNINA", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected String narnina;
    @XmlElement(name = "DU", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected XMLGregorianCalendar du;
    @XmlElement(name = "UE", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected Integer ue;
    @XmlElement(name = "UCEUENA", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected String uceuena;
    @XmlElement(name = "MU", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected String mu;
    @XmlElement(name = "UM", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected String um;
    @XmlElement(name = "SU", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected Integer su;
    @XmlElement(name = "STASUNA", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected String stasuna;
    @XmlElement(name = "BI", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected String bi;
    @XmlElement(name = "TV", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected Integer tv;
    @XmlElement(name = "TVKTVNA", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected String tvktvna;
    @XmlElement(name = "UL", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected Integer ul;
    @XmlElement(name = "UCEULNA", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected String uceulna;
    @XmlElement(name = "UO", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected Integer uo;
    @XmlElement(name = "UCEUONA", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected String uceuona;
    @XmlElement(name = "IC", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected String ic;
    @XmlElement(name = "SZ", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected Integer sz;
    @XmlElement(name = "SZVSZNA", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected String szvszna;
    @XmlElement(name = "RN", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected Integer rn;
    @XmlElement(name = "DK", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected XMLGregorianCalendar dk;
    @XmlElement(name = "DL", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected String dl;
    @XmlElement(name = "NK", namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected int nk;
    @XmlElement(name = "DP", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected String dp;
    @XmlElement(name = "PA", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected Integer pa;
    @XmlElement(name = "PG", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected Integer pg;
    @XmlElement(name = "MOSList", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = true)
    protected TMOSOList mosList;
    @XmlElement(name = "PRIList", required = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected TPRIOList priList;
    @XmlElement(name = "RPRList", required = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected TRPROList rprList;
    @XmlElement(name = "TOSList", required = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected TTOSTOIOList tosList;
    @XmlElement(name = "SPRList", required = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected TSPRSPIOList sprList;
    @XmlElement(name = "RVEList", required = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected TRVERVXOList rveList;
    @XmlElement(name = "POBList", required = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected TPOBPHROList pobList;
    @XmlElement(name = "ZPOList", required = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected TZPOOList zpoList;
    @XmlElement(name = "PZMList", required = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected TPZMOList pzmList;
    @XmlElement(name = "DCDList", required = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected TDCDDCIOList dcdList;
    @XmlElement(name = "USOList", required = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected TUSOOList usoList;
    @XmlElement(name = "SNRList", required = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected TSNRSNPOList snrList;
    @XmlElement(name = "ZUDList", required = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected TZUDZUNOList zudList;
    @XmlAttribute(name = "ID")
    protected Integer objectId;


}
