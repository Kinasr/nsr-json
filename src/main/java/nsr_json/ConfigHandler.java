package nsr_json;

import exception.InvalidKeyException;
import exception.JSONFileException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ConfigHandler {
    private static ConfigHandler instance;

    private final JSONReader reader;
    private final KeyFetch<List<String>> env = new KeyFetch<>("environments");
    private final KeyFetch<Map<String, Object>> vars = new KeyFetch<>("variables");
    private final KeyFetch<String> dateFormat = new KeyFetch<>("date-config.data-format");
    private final KeyFetch<String> timezone = new KeyFetch<>("date-config.timezone");

    protected ConfigHandler() {
        JSONReader r;
        try {
            r = new JSONReader(JSONFileLoader.getInstance("src/main/resources/nsr_config.json"), false);
        } catch (JSONFileException ignore) {
            try {
                r = new JSONReader(JSONFileLoader
                        .getInstance("src/main/resources/config.json"), false);
            } catch (JSONFileException ignored) {
                r = null;
            }
        }

        this.reader = r;
    }

    protected static ConfigHandler getInstance() {
        if (instance == null)
            instance = new ConfigHandler();

        return instance;
    }

    protected Optional<List<String>> getEnvironments() {
        return fetchData(env, () -> reader.getListAs(env.key, Parse.String));
    }

    protected Optional<Map<String, Object>> getGlobalVariables() {
        return fetchData(vars, () -> reader.getMapAs(vars.key, Parse.Object));
    }

    protected Optional<String> getDateFormat() {
        return fetchData(dateFormat, () -> reader.getString(dateFormat.key));
    }

    protected Optional<String> getTimezone() {
        return fetchData(timezone, () -> reader.getString(timezone.key));
    }

    private <T> Optional<T> fetchData(KeyFetch<T> keyFetch, Supplier<T> read) {
        if (reader != null && !keyFetch.isFetched)
            try {
                keyFetch.value = read.get();
            } catch (InvalidKeyException ignore) {
            } finally {
                keyFetch.isFetched = true;
            }

        return Optional.ofNullable(keyFetch.value);
    }


    static class KeyFetch<T> {
        private final String key;
        private Boolean isFetched = false;
        private T value;

        private KeyFetch(String key) {
            this.key = key;
        }
    }
}
