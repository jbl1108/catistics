package vertx.catistics.pojos;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Temperature {
  private Float temperature;
  private String unit;

  public Temperature() {
  }

  public Temperature(Float temperature, String unit) {
    this.temperature = temperature;
    this.unit = unit;
  }

  public float getTemperature() {
    return temperature;
  }

  public void setTemperature(Float temperature) {
    this.temperature = temperature;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  @Override
  public String toString() {
    return "Temperature{" +
      "temperature=" + temperature +
      ", unit='" + unit + '\'' +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Temperature that = (Temperature) o;
    return Objects.equals(temperature, that.temperature) &&
      Objects.equals(unit, that.unit);
  }

  @Override
  public int hashCode() {
    return Objects.hash(temperature, unit);
  }
}

