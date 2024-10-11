package pt.org.msglifeiberia.mortality.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.org.msglifeiberia.mortality.exceptions.InvalidException;
import pt.org.msglifeiberia.mortality.exceptions.NotFoundException;
import pt.org.msglifeiberia.mortality.models.*;
import pt.org.msglifeiberia.mortality.services.MortalityService;
import pt.org.msglifeiberia.mortality.services.WorldBankService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/mortality")
@Tag(name = "Mortality data", description = "Mortality data api")
public class MortalityController {

    public WorldBankService worldBankService;

    public MortalityService mortalityService;

    public MortalityController(WorldBankService worldBankService, MortalityService mortalityService) {
        this.worldBankService = worldBankService;
        this.mortalityService = mortalityService;
    }

    @GetMapping()
    public List<Mortality> getMortalityRecords(@RequestParam(required = false) String country,
                                               @RequestParam(required = false) Integer year) {
        return mortalityService.getMortalityRecords(country, year);
    }

    @DeleteMapping()
    public ResponseEntity<Boolean> deleteMortalityRecord(@RequestParam(required = true) String country, @RequestParam(required = true) Integer year){

        try {
            mortalityService.deleteMortalityRecord(country, year);

            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        }
        catch (NoSuchElementException e) {
            throw new NotFoundException(e.getMessage());
        }
        catch (Exception e) {
            throw new InvalidException(e.getMessage());
        }
    }

    @PostMapping()
    public ResponseEntity<Mortality> saveRecord(@Valid @RequestBody MortalityDTO rec) {

        try {
            Mortality bean = mortalityService.createOrUpdateMortality(rec);

            return new ResponseEntity<>(bean, HttpStatus.CREATED);

        } catch (NoSuchElementException e) {
            throw new NotFoundException("Country with code "+rec.getCountry()+ " was not found");
        } catch (IllegalArgumentException e) {
            throw new InvalidException(e.getMessage());
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "List countries",
            description = " ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = Country.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Countries not found.", content = { @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json") })
    })
    @GetMapping("/countries")
    public List<Country> findAll() {
        return worldBankService.findAll();
    }


    @Operation(
            summary = "Indicators country",
            description = "Search country by code [cca2] by ISO 3166-1 alpha2 country code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = Population.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "Country with code cca2 was not found.", content = { @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json") })
    })
    @GetMapping("/country/{country}/gender/{gender}/year/{year}")
    public ResponseEntity<Population> findByCountryGenderAndYear(@Parameter(description = "Country code ISO 3166-1 alpha2", required = true) @PathVariable String country,
                                                                 @Parameter(description = "Gender", required = true) @PathVariable String gender,
                                                                 @Parameter(description = "Year", required = true) @PathVariable Integer year) {
        Population bean = null;
        try {
            bean = worldBankService.findByCountryGenderAndYear(country, gender, year);
            return new ResponseEntity<>(bean,
                    HttpStatus.OK);
        } catch (NoSuchElementException k) {
            throw new NotFoundException("Country with code "+country+ " was not found");
        }
    }



}
