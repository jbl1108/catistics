package vertx.catistics;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import vertx.catistics.pojos.SerdesFactory;
import vertx.catistics.pojos.Temperature;
import vertx.catistics.pojos.TemperatureDecorator;

import java.time.Duration;
import java.util.Properties;
import java.util.logging.Logger;

public class AnalysisVerticle extends AbstractVerticle {
  private static final Logger LOGGER = Logger.getLogger(AnalysisVerticle.class.getName());
  private Properties props;
  private KafkaStreams streams;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    props = new Properties();
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-pipe");
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);

  }

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    final StreamsBuilder builder = new StreamsBuilder();
    KStream<String, String> stream = builder.stream("temperature");

    KGroupedStream<String, TemperatureDecorator> groupedStream = stream.map((key, value) -> {
      TemperatureDecorator temp = new TemperatureDecorator(Json.decodeValue(value, Temperature.class));
      return new KeyValue<>(temp.getUnit(), temp);
    }).groupByKey(Grouped.with(Serdes.String(), SerdesFactory.getTemperatureSerde()));

    KTable<Windowed<String>, TemperatureDecorator> ktable = groupedStream.windowedBy(TimeWindows.of(Duration.ofSeconds(5))).aggregate(new Initializer<TemperatureDecorator>() {
      @Override
      public TemperatureDecorator apply() {
        return new TemperatureDecorator();
      }
    }, new Aggregator<String, TemperatureDecorator, TemperatureDecorator>() {
      @Override
      public TemperatureDecorator apply(String key, TemperatureDecorator newTemp, TemperatureDecorator aggrTemp) {
        return aggrTemp.aggregate(newTemp);
      }
    }, Materialized.with(Serdes.String(), SerdesFactory.getTemperatureSerde()));

    ktable.toStream()
      .map((key, value) -> {
        Temperature avgTemp = new Temperature(value.getTemperature() / value.getAggCount(), value.getUnit());
        return new KeyValue<>("key", Json.encode(avgTemp));
      }).to("temperature-average");

    final Topology topology = builder.build();
    streams = new KafkaStreams(topology, props);
    LOGGER.warning("start stream");
    streams.start();
    super.start(startFuture);
  }

  @Override
  public void stop(Future<Void> stopFuture) throws Exception {
    streams.close();
    super.stop(stopFuture);
  }
}
