package nsr_json;

import exception.JSONFileException;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import static nsr_json.Helper.prepareFilePath;

/**
 * Load and Parse JSON file
 */
class JSONFileLoader {

    // It has all loaded JSON files as relative file path as the key and instance of this class as the value
    private final static Map<String, JSONFileLoader> loadedJSONs;
    private final String filePath;
    private final Object data;

    static {
        loadedJSONs = new HashMap<>();
    }

    private JSONFileLoader(String filePath) {
        this.filePath = filePath;
        this.data = loadData();
    }

    /**
     * Manage instance for {@link JSONFileLoader}
     *
     * @param filePath JSON file path
     * @return an instance of {@link JSONFileLoader} if already loaded return it or create a new instance
     */
    protected static JSONFileLoader getInstance(String filePath) {
        if (filePath == null || filePath.isEmpty())
            throw new JSONFileException("File path can't be null or empty");

        filePath = prepareFilePath(filePath);

        if (loadedJSONs.containsKey(filePath))
            return loadedJSONs.get(filePath);

        var newLoader = new JSONFileLoader(filePath);
        loadedJSONs.put(filePath, newLoader);
        return newLoader;
    }

    /**
     * A getter for the fetched data from the JSON file
     *
     * @return all data in the JSON file as {@link Object}
     */
    protected Object getData() {
        return data;
    }

    /**
     * Clears all the loaded data from all files
     */
    protected static void clearAll() {
        loadedJSONs.clear();
    }

    /**
     * Clears the current instance from the loaded files
     */
    protected void clear() {
        loadedJSONs.remove(filePath);
    }

    /**
     * Loads the JSON file and fetch its data as Object.
     *
     * @return the data at that file as {@link Object}
     */
    private Object loadData() {
        Object parsedData;
        var parser = new JSONParser();
        var reader = getFile();

        try {
            parsedData = parser.parse(reader);
        } catch (IOException | ParseException e) {
            throw new JSONFileException("Can't parse this file [" + filePath + "]", e);
        } finally {
            closeReader(reader);
        }

        return parsedData;
    }

    /**
     * Loads the JSON file
     *
     * @return the reader of the file
     */
    private Reader getFile() {
        try {
            return new FileReader(filePath);
        } catch (IOException e) {
            loadedJSONs.remove(filePath);

            throw new JSONFileException("Can't load this file [" + filePath + "]", e);
        }
    }

    /**
     * Close reader
     *
     * @param reader want to be closed
     */
    private void closeReader(Reader reader) {
        try {
            reader.close();
        } catch (IOException e) {
            throw new JSONFileException("Can't close this file [" + filePath + "]", e);
        }
    }
}
