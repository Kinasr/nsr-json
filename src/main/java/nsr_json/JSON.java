package nsr_json;

import exception.JSONFileException;
import lombok.NonNull;

/**
 * Provide ways to manage JSON files or JSON objects
 */
public class JSON {
    // Default key to support variables
    public static String DEFAULT_VARIABLES_KEY = "variables";

    private String filePath;
    private Object jsonObject;
    private JSONFileLoader jsonFileLoader;

    /**
     * Create an instance from {@link JSON} class to manage a JSON file
     * @param filePath the path of the JSON file
     */
    public JSON(@NonNull String filePath) {
        this.filePath = filePath;
    }

    /**
     * Create an instance from {@link JSON} class to manage a JSON object
     * @param jsonObject the json object
     */
    public JSON(@NonNull Object jsonObject) {
        this.jsonObject = jsonObject;
    }

    /**
     * Read data in the JSON file or Object
     *
     * @return an instance from {@link JSONReader}
     */
    public JSONReader read() {
        return filePath != null ?
                readFile() :
                readObject();
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

    /**
     * Checking the file path and create an instance form {@link JSONFileLoader} if not exist.
     * @return an instance from {@link JSONReader}
     */
    private JSONReader readFile() {
        if (filePath == null || filePath.isEmpty() || filePath.isBlank())
            throw new JSONFileException("File path can't be null or empty");

        if (jsonFileLoader == null)
            jsonFileLoader = JSONFileLoader.getInstance(filePath);

        return new JSONReader(jsonFileLoader);
    }

    /**
     * Passing the JSON object to the JSON reader
     * @return an instance from {@link JSONReader}
     */
    private JSONReader readObject() {
        return new JSONReader(jsonObject);
    }
}
