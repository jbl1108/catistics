package vertx.catistics;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class DeltaStorageVerticle extends AbstractVerticle {
  private static final Logger LOGGER = Logger.getLogger(DeltaStorageVerticle.class.getName());
  private MongoClient client;

  @Override
  public Vertx getVertx() {
    return vertx;
  }

  @Override
  public void init(Vertx vertx, Context context) {
    this.vertx = vertx;
    Map<String, Object> configMap = new HashMap<>();
    configMap.put("host", "127.0.0.1");
    configMap.put("port", 32782);
    JsonObject config = new JsonObject(configMap);
    client = MongoClient.createShared(vertx, config);
  }

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    MessageConsumer<Object> consumer = vertx.eventBus().consumer(KafkaVerticle.KAFKA_MESSAGE_ADDRESS, handler -> {
      client.save("temperature", new JsonObject(handler.body().toString()), mongoReply -> {
        LOGGER.info(() -> "Temperature stored, temp: " + mongoReply.result());
        if (mongoReply.succeeded()) {
          LOGGER.info(() -> "Temperature stored, temp: " + mongoReply.result());
        } else {
          LOGGER.warning(() -> "Error storing to MongoDB" + mongoReply.cause());
        }
      });
    });
    startFuture.complete();

  }

  @Override
  public void stop(Future<Void> stopFuture) throws Exception {
    super.stop(stopFuture);
  }
}
