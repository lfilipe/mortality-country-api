package pt.org.msglifeiberia.mortality.models;

import jakarta.validation.constraints.*;

public class MortalityDTO {

        @Pattern(regexp = "^[A-Z]{2}$", message = "Country must contain only 2 uppercase letters")
        private String country;

        @NotNull(message = "Value for year cannot be null or empty")
        @Min(value = 1960, message = "Year value must not be earlier than 1960")
        @Max(value = 2023, message = "Year value must not be later than {value}")
        private Integer year;

        @NotNull(message = "Value for men cannot be null or empty")
        @Min(value = 0, message = "Men value must be a positive number")
        private Integer men;

        @NotNull(message = "Value for women cannot be null or empty")
        @Min(value = 0, message = "Women value must be a positive number")
        private Integer women;

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
        @Override
        public String toString() {
            return "MortalityInDTO{" +
                    "country='" + country + '\'' +
                    ", year=" + year +
                    ", men=" + men +
                    ", women=" + women +
                    '}';
        }
}

