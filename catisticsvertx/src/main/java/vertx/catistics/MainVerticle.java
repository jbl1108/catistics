package vertx.catistics;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    Future<String> dbVerticleDeployment = Future.future();  // <1>
    vertx.deployVerticle(new KafkaVerticle(), dbVerticleDeployment.completer());  // <2>

  }

}
