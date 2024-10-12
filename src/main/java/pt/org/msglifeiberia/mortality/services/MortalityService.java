package pt.org.msglifeiberia.mortality.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import pt.org.msglifeiberia.mortality.models.*;
import pt.org.msglifeiberia.mortality.repository.MortalityRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class MortalityService {

    @Autowired
    private MortalityRepository repository;

    @Autowired
    private WorldBankService worldBankService;

    public List<Mortality> getMortalityRecords(String country, Integer year) {
        // Se nenhum filtro for fornecido, retorna todos os registros
        if ((country == null || country.isBlank()) && (year == null || year == 0)) {
            return repository.findAll();
        }
        // Caso uma ou ambas as listas sejam fornecidas
        return repository.findByCountryAndYear(country, year);
    }

    public Mortality createOrUpdateMortality(MortalityDTO rec) {

        Country country = worldBankService.findCountryByIso(rec.getCountry());
        if (country == null) throw new NoSuchElementException();

        MortalityId id = new MortalityId(rec.getCountry(), rec.getYear());

        Mortality record = repository.findById(id).orElse(null);

        if (record != null) {
            //  existe, então atualizamos

            //OBTER DA BD
            if(rec.getMen() > record.getMenPopulation())
                throw new IllegalArgumentException("The value of male deaths cannot be greater than the total male population of that country and year.");
            if(rec.getWomen() > record.getWomenPopulation())
                throw new IllegalArgumentException("The value of female deaths cannot be greater than the total female population of that country and year.");

            // Atualiza os valores se eles forem diferentes
            if (!record.getMen().equals(rec.getMen())) {
                record.setMen(rec.getMen());
            }

            if (!record.getWomen().equals(rec.getWomen())) {
                record.setWomen(rec.getWomen());
            }
            // Salva as alterações
            return repository.save(record);
        } else {
            //NOVO 1.º OBTER DA API
            Population populationMen = worldBankService.findByCountryGenderAndYear(rec.getCountry(), "MA", rec.getYear());
            Population populationWomen = worldBankService.findByCountryGenderAndYear(rec.getCountry(), "FE", rec.getYear());

            if(rec.getMen() > populationMen.getValue())
                throw new IllegalArgumentException("The value of male deaths cannot be greater than the total male population of that country and year.");
            if(rec.getWomen() > populationWomen.getValue())
                throw new IllegalArgumentException("The value of female deaths cannot be greater than the total female population of that country and year.");

            Mortality newRecord = new Mortality();

            newRecord.setCountry(rec.getCountry());
            newRecord.setYear(rec.getYear());
            newRecord.setMen(rec.getMen());
            newRecord.setWomen(rec.getWomen());

            newRecord.setMenPopulation(populationMen.getValue());
            newRecord.setWomenPopulation(populationWomen.getValue());

            return repository.save(newRecord);
        }
    }


    public List<Mortality> saveAll(List<Mortality> mortalityList, Integer year) {
        repository.deleteByYear(year);

        return mortalityList.stream()
                .map(mortality -> {

                    Population populationMen = worldBankService.findByCountryGenderAndYear(mortality.getCountry(), "MA", mortality.getYear());

                    Population populationWomen = worldBankService.findByCountryGenderAndYear(mortality.getCountry(), "FE", mortality.getYear());

                    if (mortality.getMen() > (populationMen != null ? populationMen.getValue() : 0))
                        throw new IllegalArgumentException("The value of male deaths cannot be greater than the total male population of that country and year.");

                    if (mortality.getWomen() > (populationWomen != null ? populationWomen.getValue() : 0))
                        throw new IllegalArgumentException("The value of female deaths cannot be greater than the total female population of that country and year.");

                    Mortality newRecord = new Mortality(mortality.getCountry(), mortality.getYear(), mortality.getMen(), mortality.getWomen());

                    newRecord.setMenPopulation(populationMen.getValue());
                    newRecord.setWomenPopulation(populationWomen.getValue());

                    return repository.save(newRecord);

                }).collect(Collectors.toList());


    }

    public void deleteMortalityRecord(String country, Integer year) {
        MortalityId id = new MortalityId(country, year);

        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Record not found for country code "+country+ " and year "+year);
        }
        repository.deleteById(id);
    }
}

