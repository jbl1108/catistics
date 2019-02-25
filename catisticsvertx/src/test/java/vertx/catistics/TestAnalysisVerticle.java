package vertx.catistics;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import vertx.catistics.pojos.Temperature;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestAnalysisVerticle {
  private Map<String, String> producerConfig = new HashMap<>();
  private Map<String, String> consumerConfig = new HashMap<>();

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    producerConfig = new HashMap<>();
    producerConfig.put("bootstrap.servers", "localhost:9092");
    producerConfig.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    producerConfig.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    producerConfig.put("acks", "1");

    consumerConfig.put("bootstrap.servers", "localhost:9092");
    consumerConfig.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    consumerConfig.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    consumerConfig.put("group.id", "my_group");
    consumerConfig.put("auto.offset.reset", "latest");
    consumerConfig.put("enable.auto.commit", "false");

    vertx.deployVerticle(new AnalysisVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @DisplayName("Should start a kafka consumer")
  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  @Test
  void startKafkaConsumer(Vertx vertx, VertxTestContext testContext) throws Throwable {

    List<Temperature> tempList = new ArrayList<>();
    tempList.add(new Temperature(10f, "C"));
    tempList.add(new Temperature(12.5f, "C"));
    tempList.add(new Temperature(10f, "C"));

    Iterator<Temperature> iterator = tempList.iterator();
    KafkaConsumer<String, String> consumer = KafkaConsumer.create(vertx, consumerConfig);
    consumer.handler(record -> {
      //Validate json before forwarding to other Verticles
      Temperature temp = Json.decodeValue(record.value(), Temperature.class);
      if (iterator.hasNext()) {
        Temperature expectTemp = iterator.next();
        assertEquals(expectTemp, temp);
        if (iterator.hasNext()) {
          testContext.completeNow();
        }
      } else {
        testContext.completeNow();
      }
    }).subscribe("temperature-average");


    //Test producer
    KafkaProducer<String, String> producer = KafkaProducer.create(vertx, producerConfig);
    Temperature temp1 = new Temperature(10f, "C");
    KafkaProducerRecord<String, String> record1 = KafkaProducerRecord.create("temperature", Json.encode(temp1));
    producer.write(record1);


    Temperature temp2 = new Temperature(15f, "C");
    KafkaProducerRecord<String, String> record2 = KafkaProducerRecord.create("temperature", Json.encode(temp2));
    producer.write(record2);

    Temperature temp3 = new Temperature(5f, "C");
    KafkaProducerRecord<String, String> record3 = KafkaProducerRecord.create("temperature", Json.encode(temp3));
    producer.write(record3);

  }
}
