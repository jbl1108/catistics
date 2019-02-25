package vertx.catistics;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.Json;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import vertx.catistics.pojos.Temperature;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestKafkaVerticle {

  private Map<String, String> config = new HashMap<>();

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    config = new HashMap<>();
    config.put("bootstrap.servers", "localhost:9092");
    config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    config.put("acks", "1");
    vertx.deployVerticle(new KafkaVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @DisplayName("Should start a kafka consumer")
  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  @Test
  void startKafkaConsumer(Vertx vertx, VertxTestContext testContext) throws Throwable {
    MessageConsumer<Object> consumer = vertx.eventBus().consumer(KafkaVerticle.KAFKA_MESSAGE_ADDRESS, handler -> {

      Temperature recTemp = Json.decodeValue(handler.body().toString(), Temperature.class);
      assertEquals(10.2f,recTemp.getTemperature());
      assertEquals("C",recTemp.getUnit());
      testContext.completeNow();
    });

    //Test producer
    KafkaProducer<String, String> producer = KafkaProducer.create(vertx, config);
    Temperature temp = new Temperature(10.2f, "C");
    KafkaProducerRecord<String, String> record = KafkaProducerRecord.create("temperature",Json.encode(temp) );
    producer.write(record);


  }
}
