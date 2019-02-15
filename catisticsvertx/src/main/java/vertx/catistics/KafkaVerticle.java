package vertx.catistics;


import io.vertx.core.*;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import vertx.catistics.wrappers.TemperatureWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class KafkaVerticle extends AbstractVerticle {
  private Map<String, String> config = new HashMap<>();
  private KafkaConsumer<String, String> consumer;
  private static final Logger LOGGER = Logger.getLogger(KafkaVerticle.class.getName());

  public static final String KAFKA_MESSAGE_ADDRESS ="kaffaMessages";

  @Override
  public Vertx getVertx() {
    return vertx;
  }

  @Override
  public void init(Vertx vertx, Context context) {
    this.vertx = vertx;
    config.put("bootstrap.servers", "localhost:9092");
    config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    config.put("group.id", "my_group");
    config.put("auto.offset.reset", "latest");
    config.put("enable.auto.commit", "false");


  }

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    // use consumer for interacting with Apache Kafka
    consumer = KafkaConsumer.create(vertx, config);
    consumer.handler(record -> {
      LOGGER.info(() -> "message received: "+record.value()+", topic: "+record.topic()+", offset: "+record.offset());
      //Validate json before forwarding to other Verticles
      TemperatureWrapper temp = Json.decodeValue(record.value(),TemperatureWrapper.class);

      getVertx().eventBus().publish(KAFKA_MESSAGE_ADDRESS,Json.encode(temp));

    }).subscribe("temperature");
    startFuture.complete();
  }

  @Override
  public void stop(Future<Void> stopFuture) throws Exception {
    consumer.unsubscribe();
    super.stop(stopFuture);
  }
}
