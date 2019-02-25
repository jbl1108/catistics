package vertx.catistics.pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemperatureDecorator extends Temperature {

  private Integer aggCount;

  public TemperatureDecorator() {
    super(0.0f, "C");
    this.setAggCount(0);
  }

  public TemperatureDecorator(Temperature temperature) {
    super(temperature.getTemperature(), temperature.getUnit());
    this.setAggCount(0);
  }

  public TemperatureDecorator aggregate(TemperatureDecorator newTemp) {
    this.setTemperature(this.getTemperature() + newTemp.getTemperature());
    this.setAggCount(this.getAggCount() + 1);
    return this;
  }

  public void setAggCount(int aggCount) {
    this.aggCount = aggCount;
  }

  public int getAggCount() {
    return aggCount;
  }

  @JsonIgnore
  public Temperature getParent() {
    return new Temperature(getTemperature(), getUnit());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    TemperatureDecorator that = (TemperatureDecorator) o;
    return Objects.equals(aggCount, that.aggCount);
  }

  @Override
  public String toString() {
    return "TemperatureDecorator{" +
      "aggCount=" + aggCount +
      "} " + super.toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), aggCount);
  }
}
