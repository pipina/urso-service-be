package sk.is.urso.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sk.is.urso.rest.api.VersionApi;
import sk.is.urso.rest.model.Version;

@RestController
public class VersionController implements VersionApi {

    @Override
    public ResponseEntity<Version> versionGet() {
        return new ResponseEntity<>(new Version().version("1.0.0"), HttpStatus.OK);
    }
}
