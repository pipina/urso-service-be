package sk.is.urso.converter;

import org.alfa.utils.DateUtils;
import org.springframework.stereotype.Component;
import sk.is.urso.enums.*;
import sk.is.urso.model.*;
import sk.is.urso.rest.model.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class NedoplatkyConverter {

    public SubjektVystupnyDetail SpStavZiadostToSubjektVystupnyDetail(SpStavZiadost spStavZiadost) {
        SubjektVystupnyDetail vystupnyDetail = new SubjektVystupnyDetail();

        CsruNavratovyKodOperacie kodZiadosti = spStavZiadost.getNavratovyKodOperacie();
        CsruNavratovyKodOperacie kodStavu = spStavZiadost.getNavratovyKodStavu();
        CsruStavZiadosti stavZiadosti = spStavZiadost.getStav();
        SpVysledokKontroly vysledokKontroly = spStavZiadost.getVysledokKontroly();

        if (kodZiadosti != null) {
            vystupnyDetail.setNavratovyKodOperacie(CsruNavratovyKodOperacieEnum.fromValue(kodZiadosti.getValue()));
        }
        vystupnyDetail.setChybovaHlaskaOperacie(spStavZiadost.getChybovaHlaskaOperacie());
        if (kodStavu != null) {
            vystupnyDetail.setNavratovyKodStavu(CsruNavratovyKodOperacieEnum.fromValue(kodStavu.getValue()));
        }
        vystupnyDetail.setChybovaHlaskaStavu(spStavZiadost.getChybovaHlaskaStavu());
        if (stavZiadosti != null) {
            vystupnyDetail.setStavZiadosti(CsruStavZiadostiEnum.fromValue(stavZiadosti.getValue()));
        }
        if (vysledokKontroly != null) {
            SpNedoplatok spNedoplatok = vysledokKontroly.getNedoplatok();
            if (spNedoplatok != null) {
                Nedoplatok nedoplatok = new Nedoplatok();
                nedoplatok.setNedoplatok(CsruNedoplatokEnum.fromValue(spNedoplatok.getValue()));
                nedoplatok.setPopisOsbStavu(vysledokKontroly.getOsbStatusText());
                vystupnyDetail.setNedoplatky(List.of(nedoplatok));
            }
        }
        return vystupnyDetail;
    }

    public SubjektVystupnyDetail FsOsobaZaznamToSubjektVystupnyDetail(FsOsobaZaznam fsOsobaZaznam) {
        SubjektVystupnyDetail subjektVystupnyDetail = new SubjektVystupnyDetail();

        subjektVystupnyDetail.setNavratovyKodOperacie(CsruNavratovyKodOperacieEnum.fromValue(fsOsobaZaznam.getNavratovyKodOperacie().getValue()));
        subjektVystupnyDetail.setChybovaHlaskaOperacie(fsOsobaZaznam.getChybovaHlaskaOperacie());
        subjektVystupnyDetail.setMaNedoplatok(fsOsobaZaznam.getMaNedoplatok());

        List<FsOsobaNedoplatok> nedoplatky = fsOsobaZaznam.getNedoplatky();
        if (nedoplatky != null) {
            subjektVystupnyDetail.setNedoplatky(FsOsobaNedoplatkyToNedoplatky(nedoplatky));
        }
        return subjektVystupnyDetail;
    }

    public SubjektVystupnyDetail ZpStavZiadostToSubjektVystupnyDetail(ZpStavZiadost zpStavZiadost) {
        SubjektVystupnyDetail vystupnyDetail = new SubjektVystupnyDetail();

        CsruNavratovyKodOperacie kodZiadosti = zpStavZiadost.getNavratovyKodOperacie();
        CsruNavratovyKodOperacie kodStavu = zpStavZiadost.getNavratovyKodStavu();
        CsruStavZiadosti stavZiadosti = zpStavZiadost.getStav();
        List<ZpVysledokKontroly> vysledkyKontrol = zpStavZiadost.getVysledkyKontrol();

        if (kodZiadosti != null) {
            vystupnyDetail.setNavratovyKodOperacie(CsruNavratovyKodOperacieEnum.fromValue(kodZiadosti.getValue()));
        }
        vystupnyDetail.setChybovaHlaskaOperacie(zpStavZiadost.getChybovaHlaskaOperacie());
        if (kodStavu != null) {
            vystupnyDetail.setNavratovyKodStavu(CsruNavratovyKodOperacieEnum.fromValue(kodStavu.getValue()));
        }
        vystupnyDetail.setChybovaHlaskaStavu(zpStavZiadost.getChybovaHlaskaStavu());
        if (stavZiadosti != null) {
            vystupnyDetail.setStavZiadosti(CsruStavZiadostiEnum.fromValue(stavZiadosti.getValue()));
        }
        if (vysledkyKontrol != null) {
            vystupnyDetail.setNedoplatky(new ArrayList<>());
            for (ZpVysledokKontroly vysdledokKontroly : vysledkyKontrol) {
                Nedoplatok nedoplatok = new Nedoplatok();
                nedoplatok.setChybovyKod(CsruNedoplatokChybovyKodEnum.valueOf(vysdledokKontroly.getNavratovyKod().getValue()));
                nedoplatok.setChybovaSprava(vysdledokKontroly.getChybovaHlaska());
                nedoplatok.setVysledokSpracovania(CsruVysledokSpracovaniaEnum.valueOf(vysdledokKontroly.getVysledokSpracovania().getValue()));

                ZpPoistovna poistovna = vysdledokKontroly.getPoistovna();
                if (poistovna != null) {
                    nedoplatok.setZdravotnaPoistovna(CsruZdravotnaPoistovnaEnum.fromValue(poistovna.getValue()));
                }
                switch (vysdledokKontroly.getNedoplatok()) {
                    case A -> nedoplatok.setNedoplatok(CsruNedoplatokEnum.MA_NEDOPLATOK);
                    case N -> nedoplatok.setNedoplatok(CsruNedoplatokEnum.NEMA_NEDOPLATOK);
                    case C -> nedoplatok.setNedoplatok(CsruNedoplatokEnum.MA_NEDOPLATOK_NIE_JE_MOZNE_VYCISLIT);
                    case NEZNAMY -> nedoplatok.setNedoplatok(CsruNedoplatokEnum.NEZNAMY);
                }
                Float vyskaNedoplatku = vysdledokKontroly.getVyskaNedoplatku();
                if (vyskaNedoplatku != null) {
                    nedoplatok.setVyskaNedoplatku(vyskaNedoplatku.toString());
                }
                vystupnyDetail.getNedoplatky().add(nedoplatok);
            }
        }
        return vystupnyDetail;
    }

    private List<Nedoplatok> FsOsobaNedoplatkyToNedoplatky(List<FsOsobaNedoplatok> fsOsobaNedoplatky) {
        List<Nedoplatok> nedoplatky = new ArrayList<>();
        for (FsOsobaNedoplatok fsOsobaNedoplatok : fsOsobaNedoplatky) {
            Nedoplatok nedoplatokVystup = new Nedoplatok();

            nedoplatokVystup.setChybovyKod(CsruNedoplatokChybovyKodEnum.valueOf(fsOsobaNedoplatok.getNedoplatokChybovyKod().getValue()));
            nedoplatokVystup.setChybovaSprava(fsOsobaNedoplatok.getNedolpatokChybovaSprava());

            nedoplatky.add(nedoplatokVystup);
            nedoplatokVystup.setMena(fsOsobaNedoplatok.getMena());
            nedoplatokVystup.setVyskaNedoplatku(fsOsobaNedoplatok.getVyskaNedoplatku());

            FsNedoplatok fsNedoplatok = fsOsobaNedoplatok.getNedoplatok();
            FsDruhDanePohladavky fsDruhDanePohladavky = fsOsobaNedoplatok.getDruhDanePohladavky();
            Date datumNedoplatku = fsOsobaNedoplatok.getDatumNedoplatku();

            if (fsNedoplatok != null) {
                nedoplatokVystup.setNedoplatok(CsruNedoplatokEnum.valueOf(fsNedoplatok.getValue()));
            }
            if (fsDruhDanePohladavky != null) {
                nedoplatokVystup.setDruhDaneAleboPohladavky(CsruDruhDaneAleboPohladavkyEnum.fromValue(fsDruhDanePohladavky.getValue()));
            }
            if (datumNedoplatku != null) {
                nedoplatokVystup.setDatumNedoplatku(DateUtils.toLocalDate(datumNedoplatku));
            }
        }
        return nedoplatky;
    }
}
