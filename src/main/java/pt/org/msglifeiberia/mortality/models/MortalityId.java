package pt.org.msglifeiberia.mortality.models;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MortalityId implements Serializable {

    private String country;
    private Integer year;

    public MortalityId() {}

    public MortalityId(String country, Integer year) {
        this.country = country;
        this.year = year;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MortalityId)) return false;
        MortalityId that = (MortalityId) o;
        return Objects.equals(country, that.country) &&
                Objects.equals(year, that.year) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, year);
    }
}
