package nsr_json;

import exception.InvalidKeyException;
import exception.JSONFileException;

import java.util.ArrayList;
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

    public ConfigHandler() {
        JSONReader r;
        try {
            r = new JSONReader(JSONFileLoader.getInstance("src/main/resources/nsr_config.json"), false);
        } catch (JSONFileException ignore) {
            try{
                r = new JSONReader(JSONFileLoader
                        .getInstance("src/main/resources/config.json"), false);
            } catch (JSONFileException ignored) {
                r = null;
            }
        }

        this.reader = r;
    }

    public static ConfigHandler getInstance() {
        if (instance == null)
            instance = new ConfigHandler();

        return instance;
    }

    public Optional<List<String>> getEnvironments() {
        return fetchData(env, () -> reader.getListAs(env.key, Parse.String));
    }

    public Optional<Map<String, Object>> getGlobalVariables() {
        return fetchData(vars, () -> reader.getMapAs(vars.key, Parse.Object));
    }

    public Optional<String> getDateFormat() {
        return fetchData(dateFormat, () -> reader.getString(dateFormat.key));
    }

    public Optional<String> getTimezone() {
        return fetchData(timezone, () -> reader.getString(timezone.key));
    }

    private <T> Optional<T> fetchData(KeyFetch<T> keyFetch, Supplier<T> read) {
        T value;

        if (reader == null || (keyFetch.isFetched && !keyFetch.isExist))
            value = null;
        else if (!keyFetch.isFetched)
            try {
                value = read.get();
                keyFetch.isExist = true;
            } catch (InvalidKeyException ignore) {
                value = null;
            }
            finally {
                keyFetch.isFetched = true;
            }
        else
            value = keyFetch.value;

        return Optional.ofNullable(value);
    }


    static class KeyFetch<T>{
        private final String key;
        private Boolean isFetched =  false;
        private Boolean isExist = false;
        private T value;

        private KeyFetch(String key) {
            this.key = key;
        }
    }
}
