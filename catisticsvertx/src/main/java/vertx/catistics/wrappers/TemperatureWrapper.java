package vertx.catistics.wrappers;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

public class TemperatureWrapper {
  private float temperature;
  private String unit;

  public TemperatureWrapper(){}

  public TemperatureWrapper(float temperature, String unit){
    this.temperature=temperature;
    this.unit = unit;
  }

  public float getTemperature() {
    return temperature;
  }

  public void setTemperature(float temperature) {
    this.temperature = temperature;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }



}

