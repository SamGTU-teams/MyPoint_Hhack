package DataStreams;

import java.util.logging.Logger;
import java.util.stream.Stream;

public interface WriteData<T> {
    Logger log();

    void writeData(T data);

    default void writeData(Stream<T> stream){
        stream.forEach(this::writeData);
    }

    default void writeData(DataStream<T> stream){
        writeData(stream.stream());
    }
}
