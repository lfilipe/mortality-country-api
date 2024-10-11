package pt.org.msglifeiberia.mortality.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pt.org.msglifeiberia.mortality.models.Mortality;
import pt.org.msglifeiberia.mortality.models.MortalityId;

import java.util.List;

public interface MortalityRepository extends JpaRepository<Mortality, MortalityId> {

    @Query("SELECT m FROM Mortality m WHERE "
            + "(:countries IS NULL OR m.country IN :countries) AND "
            + "(:years IS NULL OR m.year IN :years)")
    List<Mortality> findByCountryAndYear(
            @Param("countries") List<String> countries,
            @Param("years") List<Integer> years
    );
}
