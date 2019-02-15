package vertx.catistics;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import vertx.catistics.wrappers.TemperatureWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ExtendWith(VertxExtension.class)
public class TestDeltaStorageVerticle {

  @BeforeEach
  public void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new DeltaStorageVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @DisplayName("Should store event in Mongo")
  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  @Test
  void startDeltaStore(Vertx vertx, VertxTestContext testContext) throws Throwable {

    TemperatureWrapper temp = new TemperatureWrapper(10.2f, "C");
    String tempJson = Json.encode(temp);
    vertx.eventBus().publish(KafkaVerticle.KAFKA_MESSAGE_ADDRESS, tempJson);

    Map<String, Object> configMap = new HashMap<>();
    configMap.put("host", "127.0.0.1");
    configMap.put("port", 32782);
    JsonObject config = new JsonObject(configMap);
    MongoClient client = MongoClient.createShared(vertx, config);
    JsonObject query = new JsonObject();
    query.put("temperature",10.2);
    client.find("temperature", query, res -> {
      if (res.succeeded()) {
        for (JsonObject json : res.result()) {
          System.out.println(json.encodePrettily());
          testContext.completeNow();
        }
      } else {
        res.cause().printStackTrace();
      }
    });

  }
}
