package pt.org.msglifeiberia.mortality.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pt.org.msglifeiberia.mortality.models.Mortality;
import pt.org.msglifeiberia.mortality.models.MortalityId;

import java.util.List;

public interface MortalityRepository extends JpaRepository<Mortality, MortalityId> {

    @Query("SELECT m FROM Mortality m WHERE "
            + "(:country IS NULL OR m.country LIKE :country) AND "
            + "(:year IS NULL OR m.year = :year)")
    List<Mortality> findByCountryAndYear(
            @Param("country") String country,
            @Param("year") Integer year
    );
}
