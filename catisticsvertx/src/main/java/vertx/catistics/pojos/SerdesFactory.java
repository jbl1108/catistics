package vertx.catistics.pojos;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import vertx.catistics.JsonPOJODeserializer;
import vertx.catistics.JsonPOJOSerializer;

public class SerdesFactory {
  static final Serializer<TemperatureDecorator> temperatureDeserializer = new JsonPOJOSerializer<>();
  static final Deserializer<TemperatureDecorator> temperatureSerializer = new JsonPOJODeserializer<>(TemperatureDecorator.class);
  static final Serde<TemperatureDecorator> temperaturSerde = Serdes.serdeFrom(temperatureDeserializer, temperatureSerializer);

  public final static Serde getTemperatureSerde() {
    return temperaturSerde;
  }
}
