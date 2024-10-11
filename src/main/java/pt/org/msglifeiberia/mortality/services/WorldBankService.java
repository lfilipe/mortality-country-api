package pt.org.msglifeiberia.mortality.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pt.org.msglifeiberia.mortality.exceptions.NotFoundException;
import pt.org.msglifeiberia.mortality.models.Country;
import pt.org.msglifeiberia.mortality.models.Population;

import java.util.List;

@Service
public class WorldBankService {

    private final RestClient restClient;

    public WorldBankService() {
        restClient = RestClient.builder()
                .baseUrl("https://api.worldbank.org/v2/country")
                .build();
    }

    //https://api.worldbank.org/v2/country?format=json&per_page=296
    public List<Country> findAll() {

        ObjectMapper objectMapper = new ObjectMapper();

        List<Object> jsonResponseList = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("format", "json")
                        .queryParam("per_page", "296")//All countries in world by API
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<Object>>() {});

        // Extrair o segundo elemento que é a lista de países
        List<Country> countries = objectMapper.convertValue(jsonResponseList.get(1), new TypeReference<List<Country>>() {});

        return countries;
    }

    public Country findCountryByIso(String iso) {
        List<Country> countries = this.findAll();
        return countries.stream()
                .filter(c -> iso.equalsIgnoreCase(c.getIso2Code()))
                .findFirst()
                .get();
    }

    //https://api.worldbank.org/v2/country/PT/indicator/SP.POP.TOTL.FE.IN?format=json&date=2023
    public Population findByCountryGenderAndYear(String country, String gender, Integer year) {
        //gender => FE or MA
        ObjectMapper objectMapper = new ObjectMapper();

        List<Object> jsonResponseList = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{country}/indicator/SP.POP.TOTL.{gender}.IN")
                        .queryParam("format", "json")
                        .queryParam("date", year)  // + parâmetro de data
                        .build(country, gender))    // replace {country} e {gender}
                .retrieve()
                .body(new ParameterizedTypeReference<List<Object>>() {});

        List<Population> indicatores = objectMapper.convertValue(jsonResponseList.get(1), new TypeReference<List<Population>>() {});

        // Verificar se a lista de indicadores é nula ou está vazia
        if (indicatores == null || indicatores.isEmpty()) {
            // Lançar a exceção personalizada
            throw new NotFoundException("Nenhum indicador encontrado na resposta da API.");
        }

        // Retornar apenas o primeiro indicador
        return  indicatores.get(0);
    }

}
