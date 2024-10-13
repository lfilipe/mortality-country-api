package pt.org.msglifeiberia.mortality.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.org.msglifeiberia.mortality.exceptions.ContentFileInvalidException;
import pt.org.msglifeiberia.mortality.exceptions.InvalidException;
import pt.org.msglifeiberia.mortality.exceptions.NotFoundException;
import pt.org.msglifeiberia.mortality.models.*;
import pt.org.msglifeiberia.mortality.services.MortalityService;
import pt.org.msglifeiberia.mortality.services.WorldBankService;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
            summary = "Indicators country, gender and year",
            description = "Search country by code [cca2] by ISO 3166-1 alpha2 country code, Gender FE for woman and MA for man, year between 1960 and 2023")
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

    @PostMapping("/upload")
    @Transactional // vamos garantir atomicidade núnica transação ExceptionErrorsResponse
    public ResponseEntity<List<Mortality>> uploadCsvFile(MultipartFile file) {

        List<String> errorMessages = new ArrayList<>();
        List<Mortality> recordsToSave = new ArrayList<>();
        int referenceYear = -1; // Valor de referência para o ano


        if (file.isEmpty()) {
            errorMessages.add("The uploaded file is empty.");

            throw new ContentFileInvalidException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessages);
        }


        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            if (!csvParser.iterator().hasNext()) {
                errorMessages.add("The uploaded file does not contain any data.");

                throw new ContentFileInvalidException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessages);
            }

            int lineNumber = 1;
            for (CSVRecord record : csvParser) {
                lineNumber++;
                try {
                    String country = record.get("country");
                    String yearStr = record.get("year").trim();
                    String menStr = record.get("men").trim();
                    String womenStr = record.get("women").trim();

                    if (record.size() < 4) {
                        throw new IllegalArgumentException("Missing data fields.");
                    }

                    if (country.length() != 2 || !country.matches("[A-Z]+")) {
                        throw new IllegalArgumentException("Invalid country format");
                    }

                    Integer year;
                    try {
                        year = Integer.parseInt(yearStr);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid year format");
                    }

                    if (year < 1960 || year > 2023) {
                        throw new IllegalArgumentException("Year must be between 1960 and 2023");
                    }

                    if (referenceYear == -1) {
                        referenceYear = year;
                    } else {
                        if (year != referenceYear) { // Verificar se o ano das linhas seguintes é o mesmo
                            throw new IllegalArgumentException("Year "+referenceYear+" must be unique across all rows.");
                        }
                    }


                    Integer men;
                    try {
                        men = Integer.parseInt(menStr);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid men population format");
                    }

                    if (men < 0) {
                        throw new IllegalArgumentException("Men population cannot be negative.");
                    }

                    Integer women;
                    try {
                        women = Integer.parseInt(womenStr);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid women population format");
                    }

                    if (women < 0) {
                        throw new IllegalArgumentException("Women population cannot be negative.");
                    }

                    Population populationMen = worldBankService.findByCountryGenderAndYear(country, "MA", year);

                    Population populationWomen = worldBankService.findByCountryGenderAndYear(country, "FE", year);

                    if (men > (populationMen != null ? populationMen.getValue() : 0))
                        throw new IllegalArgumentException("The value of male deaths cannot be greater than the total male population of that country and year.");

                    if (women > (populationWomen != null ? populationWomen.getValue() : 0))
                        throw new IllegalArgumentException("The value of female deaths cannot be greater than the total female population of that country and year.");


                    Mortality mortalityRecord = new Mortality(country, year, men, women, populationMen.getValue(), populationWomen.getValue());

                    // Se os dados forem válidos, adicionar à lista para salvar no fim
                    recordsToSave.add(mortalityRecord);

                } catch (IllegalArgumentException e) {
                    // Capturar e save do erro de validação com a linha correspondente
                    errorMessages.add("Error at line " + lineNumber + ": " + e.getMessage());
                }
            }

            // Se exist erros, não salvar nada e lançar exceção
            if (!errorMessages.isEmpty()) {
                throw new ContentFileInvalidException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessages);
            }

            // Se não existir erros, tudo ok
            List<Mortality> records = mortalityService.saveAll(recordsToSave, referenceYear);

            return new ResponseEntity<>(records, HttpStatus.CREATED);

        } catch (Exception e) {
            throw new ContentFileInvalidException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessages);
        }
    }



}
