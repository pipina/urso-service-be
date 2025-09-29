package sk.is.urso.controller;

import org.alfa.exception.CommonException;
import org.alfa.exception.IException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sk.is.urso.converter.NedoplatkyConverter;
import sk.is.urso.rest.api.NedoplatkyApi;
import sk.is.urso.rest.model.InstituciaEnum;
import sk.is.urso.rest.model.SubjektNedoplatokVstupnyDetail;
import sk.is.urso.rest.model.SubjektVystupnyDetail;
import sk.is.urso.service.NedoplatkyService;

@RestController
public class NedoplatkyController implements NedoplatkyApi, IException {

    private static final String ERROR_NEDOPLATKY = "Nastala chyba pri získavaní nedoplatkov subjektu.";
    private static final String ERROR_WRONG_INSTITUTE = "Bola zadaná nesprávna inštitúcia.";

    @Autowired
    private NedoplatkyService nedoplatkyService;

    @Autowired
    private NedoplatkyConverter nedoplatkyConverter;

    @Override
    public ResponseEntity<SubjektVystupnyDetail> nedoplatkyInstituciaPost(InstituciaEnum institucia, SubjektNedoplatokVstupnyDetail subjektNedoplatokVstupnyDetail) {
        try {
            SubjektVystupnyDetail subjektVystupnyDetail;
            switch (institucia) {
                case FS -> subjektVystupnyDetail = nedoplatkyConverter.FsOsobaZaznamToSubjektVystupnyDetail(nedoplatkyService.getNedoplatkyPreFS(subjektNedoplatokVstupnyDetail, institucia));
                case SP -> subjektVystupnyDetail = nedoplatkyConverter.SpStavZiadostToSubjektVystupnyDetail(nedoplatkyService.getNedoplatkyPreSP(subjektNedoplatokVstupnyDetail, institucia));
                case ZP -> subjektVystupnyDetail = nedoplatkyConverter.ZpStavZiadostToSubjektVystupnyDetail(nedoplatkyService.getNedoplatkyPreZP(subjektNedoplatokVstupnyDetail, institucia));
                default -> throw new CommonException(HttpStatus.BAD_REQUEST, ERROR_WRONG_INSTITUTE);
            }
            return new ResponseEntity<>(subjektVystupnyDetail, HttpStatus.OK);
        } catch (Exception ex) {
            throw toException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_NEDOPLATKY, ex);
        }
    }
}
