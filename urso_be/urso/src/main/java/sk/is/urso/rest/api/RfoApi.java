package sk.is.urso.rest.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;
import sk.is.urso.reg.model.ZaznamRegistraOutputDetail;
import sk.is.urso.rest.model.ConfirmChangesIdObject;
import sk.is.urso.rest.model.HodnotaCiselnikaOutputDetail;
import sk.is.urso.rest.model.HodnotaCiselnikaShortDetail;
import sk.is.urso.rest.model.RfoExternalIdsObject;
import sk.is.urso.rest.model.ZaznamRegistra;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-03-07T12:13:45.400042500+01:00[Europe/Berlin]")
@Validated
@Api(value = "rfo", description = "the rfo API")
public interface RfoApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /rfo/{rodneCislo} :
     * TODO
     *
     * @return Todo (status code 200)
     * <p>
     * or Forbidden - authorization error (status code 403)
     * <p>
     * or Item not found (status code 404)
     * <p>
     * or Standard error response - unexpected error (status code 200)
     */

    @ApiOperation(value = "TODO", nickname = "rfoIdByAttributesGet", notes = "", response = ZaznamRegistra.class, authorizations = {@Authorization(value = "bearerAuth")}, tags = {"TestovanieRfoController",})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Todo", response = ZaznamRegistra.class), @ApiResponse(code = 403, message = "Forbidden - authorization error", response = Error.class), @ApiResponse(code = 404, message = "Item not found", response = Error.class), @ApiResponse(code = 200, message = "Standard error response - unexpected error", response = Error.class)})
    @RequestMapping(method = RequestMethod.POST, value = "/rfo", produces = {"application/json"}, consumes = {"application/json"})
    default ResponseEntity<ZaznamRegistra> rfoByRodneCisloPost(@Parameter(name = "rodneCislo", description = "") @Valid @RequestBody(required = false) String rodneCislo) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"result\" : [ null, null ], \"total\" : 0 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * POST /rfoDataById :
     * TODO
     *
     * @param rfoExternalIdsObject TODO (optional)
     * @return Todo (status code 200)
     * <p>
     * or Forbidden - authorization error (status code 403)
     * <p>
     * or Item not found (status code 404)
     * <p>
     * or Standard error response - unexpected error (status code 200)
     */

    @ApiOperation(value = "TODO", nickname = "rfoDataByIdPost", notes = "", response = ZaznamRegistraOutputDetail.class, responseContainer = "List", authorizations = {

            @Authorization(value = "bearerAuth")}, tags = {"TestovanieRfoController",})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Todo", response = ZaznamRegistraOutputDetail.class, responseContainer = "List"),

            @ApiResponse(code = 403, message = "Forbidden - authorization error", response = Error.class),

            @ApiResponse(code = 404, message = "Item not found", response = Error.class),

            @ApiResponse(code = 200, message = "Standard error response - unexpected error", response = Error.class)})
    @PostMapping(value = "/rfoDataById", produces = {"application/json"}, consumes = {"application/json"})
    default ResponseEntity<List<ZaznamRegistraOutputDetail>> rfoDataByIdPost(@ApiParam(value = "TODO") @Valid @RequestBody(required = false) RfoExternalIdsObject rfoExternalIdsObject) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "null";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * POST /poiMarking :
     * TODO
     *
     * @param rfoExternalIdsObject TODO (optional)
     * @return TODO (status code 200)
     * <p>
     * or Forbidden - authorization error (status code 403)
     * <p>
     * or Item not found (status code 404)
     * <p>
     * or Standard error response - unexpected error (status code 200)
     */
    @ApiOperation(value = "TODO", nickname = "poiMarkingPost", notes = "", authorizations = {

            @Authorization(value = "bearerAuth")}, tags = {"TestovanieRfoController",})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "TODO"), @ApiResponse(code = 403, message = "Forbidden - authorization error", response = Error.class), @ApiResponse(code = 404, message = "Item not found", response = Error.class), @ApiResponse(code = 200, message = "Standard error response - unexpected error", response = Error.class)})
    @PostMapping(value = "/poiMarking", produces = {"application/json"}, consumes = {"application/json"})
    default ResponseEntity<Void> poiMarkingPost(@ApiParam(value = "TODO") @Valid @RequestBody(required = false) RfoExternalIdsObject rfoExternalIdsObject) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * POST /poiUnmarking :
     * TODO
     *
     * @param rfoExternalIdsObject TODO (optional)
     * @return TODO (status code 200)
     * <p>
     * or Forbidden - authorization error (status code 403)
     * <p>
     * or Item not found (status code 404)
     * <p>
     * or Standard error response - unexpected error (status code 200)
     */
    @ApiOperation(value = "TODO", nickname = "poiUnmarkingPost", notes = "", authorizations = {

            @Authorization(value = "bearerAuth")}, tags = {"TestovanieRfoController",})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "TODO"), @ApiResponse(code = 403, message = "Forbidden - authorization error", response = Error.class), @ApiResponse(code = 404, message = "Item not found", response = Error.class), @ApiResponse(code = 200, message = "Standard error response - unexpected error", response = Error.class)})
    @PostMapping(value = "/poiUnmarking", produces = {"application/json"}, consumes = {"application/json"})
    default ResponseEntity<Void> poiUnmarkingPost(@ApiParam(value = "TODO") @Valid @RequestBody(required = false) RfoExternalIdsObject rfoExternalIdsObject) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    /**
     * POST /confirmChange/{changeId} :
     * TODO
     *
     * @param changeId (required)
     * @return TestovanieRfoController (status code 200)
     * <p>
     * or Forbidden - authorization error (status code 403)
     * <p>
     * or Standard error response - unexpected error (status code 200)
     */
    @ApiOperation(value = "TODO", nickname = "confirmChangeChangeIdPost", notes = "", authorizations = {@Authorization(value = "bearerAuth")}, tags = {"TestovanieRfoController",})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "TestovanieRfoController"), @ApiResponse(code = 403, message = "Forbidden - authorization error", response = Error.class), @ApiResponse(code = 200, message = "Standard error response - unexpected error", response = Error.class)})
    @PostMapping(value = "/confirmChange/{changeId}", produces = {"application/json"})
    default ResponseEntity<Void> confirmChangeChangeIdPost(@ApiParam(value = "", required = true) @PathVariable("changeId") Long changeId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * GET /rfoChanges :
     * TODO
     *
     * @return TODO (status code 200)
     * <p>
     * or Forbidden - authorization error (status code 403)
     * <p>
     * or Standard error response - unexpected error (status code 200)
     */
    @ApiOperation(value = "TODO", nickname = "rfoChangesGet", notes = "", response = ConfirmChangesIdObject.class, authorizations = {
            @Authorization(value = "bearerAuth")}, tags = {"TestovanieRfoController",})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "TODO", response = ConfirmChangesIdObject.class),
            @ApiResponse(code = 403, message = "Forbidden - authorization error", response = Error.class),
            @ApiResponse(code = 200, message = "Standard error response - unexpected error", response = Error.class)})
    @GetMapping(value = "/rfoChanges", produces = {"application/json"})
    default ResponseEntity<ConfirmChangesIdObject> rfoChangesGet() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{\" confirmChangesIds\": [null, null ]} ";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });

        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

//    /**
//     * GET /titleCodelist :
//     * TODO
//     *
//     * @return TODO (status code 200)
//     * <p>
//     * or Forbidden - authorization error (status code 403)
//     * <p>
//     * or Standard error response - unexpected error (status code 200)
//     */
//    @ApiOperation(value = "TODO", nickname = "titleCodelistGet", notes = "", response = HodnotaCiselnikaOutputDetail.class, authorizations = {
//            @Authorization(value = "bearerAuth")}, tags = {"TestovanieRfoController",})
//    @ApiResponses(value = {@ApiResponse(code = 200, message = "TODO", response = HodnotaCiselnikaOutputDetail.class),
//            @ApiResponse(code = 403, message = "Forbidden - authorization error", response = Error.class),
//            @ApiResponse(code = 200, message = "Standard error response - unexpected error", response = Error.class)})
//    @GetMapping(value = "/titleCodelist", produces = {"application/json"})
//    default ResponseEntity<ConfirmChangesIdObject> titleCodelistGet() {
//        getRequest().ifPresent(request -> {
//            for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
//                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
//                    String exampleString = "{\" confirmChangesIds\": [null, null ]} ";
//                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
//                    break;
//                }
//            }
//        });
//
//        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
//    }

    /**
     * GET /titleCodelist :
     * TODO
     *
     * @return TODO (status code 200)
     * <p>
     * or Forbidden - authorization error (status code 403)
     * <p>
     * or Standard error response - unexpected error (status code 200)
     */
    @Operation(
            operationId = "titleCodelistGet",
            tags = { "TestsovanieRfo" },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Zoznam hodnôt číselníka", content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = HodnotaCiselnikaShortDetail.class)))
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Nesprávny vstup.", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Žiadateľ služby nie je autorizovaný.", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Operácia zamietnutá.", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
                    })
            }
    )
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/titleCodelistGet",
            produces = { "application/json" }
    )
    default ResponseEntity<List<HodnotaCiselnikaShortDetail>> titleCodelistGet() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"kodPolozky\" : \"kodPolozky\", \"id\" : 0, \"nazovPolozky\" : \"nazovPolozky\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
