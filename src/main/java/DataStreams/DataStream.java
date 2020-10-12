package DataStreams;

import java.util.logging.Logger;
import java.util.stream.Stream;

public interface DataStream<T> {

    Logger log();

    default Stream<T> stream(){
        log().info("Start generate stream");
        return generateStream();
    }

    Stream<T> generateStream();
}
