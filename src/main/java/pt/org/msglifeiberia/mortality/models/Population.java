package pt.org.msglifeiberia.mortality.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonPropertyOrder({"date", "value"})
@JsonIgnoreProperties({"indicator", "country", "countryiso3code", "unit", "obs_status", "decimal"})
@Schema(description = "Dados demográficos, incluindo população masculina e feminina para cada país")
public class Population {

    @Schema(description = "Year date", example = "2023")
    private Integer date;

    @Schema(description = "Population", example = "5557489")
    private Long value;


    public Integer getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(Integer date) {
        this.date = date;
    }

    public Long getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(Long value) {
        this.value = value;
    }
}
