package nsr_json;

import lombok.NonNull;

/**
 * Provide ways to manage JSON file
 */
public class JSON {
    public static String DEFAULT_VARIABLES_KEY = "variables";

    private final String filePath;
    private JSONFileLoader jsonFileLoader;

    public JSON(@NonNull String filePath) {
        this.filePath = filePath;
    }

    /**
     * Read data in the JSON file
     * @return an instance from {@link JSONReader}
     */
    public JSONReader read() {
        if (jsonFileLoader == null)
            jsonFileLoader = JSONFileLoader.getInstance(filePath);

        return new JSONReader(jsonFileLoader);
    }

    /**
     * Close current JSON file
     */
    public void close() {
        if (jsonFileLoader != null)
            jsonFileLoader.clear();
    }

    /**
     * Close all JSON files that are loaded before
     */
    public static void closeAll() {
        JSONFileLoader.clearAll();
    }
}
