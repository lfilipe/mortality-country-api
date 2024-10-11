package pt.org.msglifeiberia.mortality.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonPropertyOrder({"iso2Code", "name"})
@JsonIgnoreProperties({"id", "region", "incomeLevel", "lendingType", "capitalCity", "longitude", "latitude", "adminregion"})
@Schema(description = "Country model")
public class Country {

    @Schema(description = "Country name", example = "Portugal")
    private String name;

    @Schema(description = "ISO 3166-1 alpha2 country code", example = "PT")
    private String iso2Code;


    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public String getIso2Code() {
        return iso2Code;
    }

    @JsonProperty("iso2Code")
    public void setIso2Code(String iso2Code) {
        this.iso2Code = iso2Code;
    }


/*
    private String id;

    private Map<String, String> region;

    private Map<String, String> adminregion;
    public Map<String, String> getAdminRegion() {
        return this.adminregion;
    }

    @JsonProperty("adminregion")
    public void setAdminRegion(Map<String, String> adminregion) {
        this.adminregion = adminregion;
    }

    private Map<String, String>  incomeLevel;
    private Map<String, String>  lendingType;
    private String capitalCity;
    private String longitude;
    private String latitude;


    public Map<String, String> getRegion() {
        return this.region;
    }

    @JsonProperty("region")
    public void setRegion(Map<String, String> region) {
        this.region = region;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getIncomeLevel() { return incomeLevel; }
    public void setIncomeLevel(Map<String, String> value) { this.incomeLevel = value; }

    public Map<String, String> getLendingType() { return lendingType; }
    public void setLendingType(Map<String, String> value) { this.lendingType = value; }

    public String getCapitalCity() { return capitalCity; }
    public void setCapitalCity(String value) { this.capitalCity = value; }

    public String getLongitude() { return longitude; }
    public void setLongitude(String value) { this.longitude = value; }

    public String getLatitude() { return latitude; }
    public void setLatitude(String value) { this.latitude = value; }
*/
}
