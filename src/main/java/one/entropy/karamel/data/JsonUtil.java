package one.entropy.karamel.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class JsonUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class.getCanonicalName());

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T fromJson(String json, Class<T> rootType) {
        return Try.of(() -> (T) mapper.reader().forType(rootType).readValue(json))
                .onFailure(throwable -> LOGGER.error("", throwable))
                .get();
    }

    public static String toJson(Object object) {
        return Try.of(() -> mapper.writer().forType(Object.class).writeValueAsString(object))
                .onFailure(throwable -> LOGGER.error("", throwable))
                .get();
    }

    public static <T> String toJson(Object object, Class<T> rootType) {
        return toJson(object, mapper, rootType);
    }

    public static <T> String toJson(Object object, ObjectMapper jsonMapper, Class<T> rootType) {
        return Try.of(() -> Optional.of(jsonMapper).orElse(mapper).writer().forType(rootType).writeValueAsString(object))
                .onFailure(throwable -> LOGGER.error("", throwable))
                .get();
    }
}
