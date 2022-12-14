package nsr_json;

import exception.JSONFileException;

/**
 * Provide ways to manage JSON files or JSON objects
 */
public class JSON {
    private String filePath;
    private Object jsonObject;
    private JSONFileLoader jsonFileLoader;

    /**
     * To read data from JSON File
     * @param filePath the relative path of the file
     * @return an instance from {@link JSONReader}
     */
    public static JSONReader readFile(String filePath) {
        if (filePath == null || filePath.isEmpty() || filePath.isBlank())
            throw new JSONFileException("File path can't be null or empty");

        return new JSONReader(JSONFileLoader.getInstance(filePath));
    }

    /**
     * To read data from JSON Object
     * It should be parsable to Map
     * @param jsonObject the JSON object
     * @return an instance from {@link JSONReader}
     */
    public static JSONReader readObject(Object jsonObject) {
        if (jsonObject == null)
            throw new IllegalArgumentException("Json Object can't be null");

        return new JSONReader(jsonObject);
    }

    /**
     * Create an instance from {@link JSON} class to manage a JSON file
     *
     * @param filePath the path of the JSON file
     */
    public JSON(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Create an instance from {@link JSON} class to manage a JSON object
     *
     * @param jsonObject the json object
     */
    public JSON(Object jsonObject) {
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
     *
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
     *
     * @return an instance from {@link JSONReader}
     */
    private JSONReader readObject() {
        return new JSONReader(jsonObject);
    }
}
