package pt.org.msglifeiberia.mortality.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@Entity
@Table(name = "mortality")
@IdClass(MortalityId.class)
public class Mortality {

    @Id
    @Column(name = "country", nullable = false)
    private String country;

    @Id
    @Column(name = "year_data", nullable = false)
    private Integer year;

    @Column(name = "men", nullable = false)
    private Integer men;

    @Column(name = "women", nullable = false)
    private Integer women;

    @Column(name = "men_population")
    private Long menPopulation;

    @Column(name = "women_population")
    private Long womenPopulation;

    // Propriedades @Transient
    @Transient
    private double menDeathsPerThousand;

    @Transient
    private double womenDeathsPerThousand;

    // calcular mortes por 1000 homens
    public double getMenDeathsPerThousand() {
        BigDecimal bd = new BigDecimal((this.getMen() / (double) this.getMenPopulation()) * 1000);
        return bd.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    // calcular mortes por 1000 mulheres
    public Double getWomenDeathsPerThousand() {
        BigDecimal bd = new BigDecimal((this.getWomen() / (double) this.getWomenPopulation()) * 1000);
        return bd.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }


    public Mortality() {}

    public Mortality(String country, Integer year, Integer men, Integer women) {
        this.country = country;
        this.year = year;
        this.men = men;
        this.women = women;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMen() {
        return men;
    }

    public void setMen(Integer men) {
        this.men = men;
    }

    public Integer getWomen() {
        return women;
    }

    public void setWomen(Integer women) {
        this.women = women;
    }

    public Long getMenPopulation() {
        return menPopulation;
    }

    public void setMenPopulation(Long menPopulation) {
        this.menPopulation = menPopulation;
    }

    public Long getWomenPopulation() {
        return womenPopulation;
    }

    public void setWomenPopulation(Long womenPopulation) {
        this.womenPopulation = womenPopulation;
    }


}
