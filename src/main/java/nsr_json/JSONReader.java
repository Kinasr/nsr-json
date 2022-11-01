package nsr_json;

import exception.InvalidCustomObjectException;
import exception.InvalidKeyException;
import exception.NotAMapException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

import static nsr_json.Helper.*;
import static nsr_json.Helper.changeEnvironmentsKeys;

/**
 * A class helps to read data from a JSON file or JSON object
 */
public class JSONReader {
    private final Object data;
    private final Map<String, Object> vars;
    private final Boolean enableEnv;

    /**
     * Creating an instance of {@link JSONReader} for JSON files
     *
     * @param loader an instance of {@link JSONReader} class
     */
    protected JSONReader(JSONFileLoader loader) {
        this.enableEnv = true;
        this.data = loader.getData();
        this.vars = getJSONVariables();
    }

    /**
     * Creating an instance of {@link JSONReader} for JSON Objects
     *
     * @param jsonObject JSON object
     */
    protected JSONReader(Object jsonObject) {
        this.enableEnv = true;
        this.data = jsonObject;
        this.vars = getJSONVariables();
    }

    private JSONReader(Object data, Map<String, Object> vars) {
        this.enableEnv = true;
        this.data = data;
        this.vars = vars;
    }

    protected JSONReader(JSONFileLoader loader, Boolean enableEnv) {
        this.enableEnv = enableEnv;
        this.data = loader.getData();
        this.vars = getJSONVariables();
    }

    /**
     * Define a key and make its value to be the base to read from in the next time
     *
     * @param key the path to the wanted data can be a single key or a series of keys
     * @return an instance of {@link JSONReader} with base data is the value of the key
     */
    public JSONReader setBreakPoint(String key) {
        return new JSONReader(get(key), vars);
    }

    /**
     * Fetch all data from the JSON file
     *
     * @return all data existed in the JSON file as {@link Object}
     */
    public Object getAll() {
        return data;
    }

    /**
     * Fetch a single piece of data from the JSON file using a single key or a series of keys
     * <p>
     * to support key series use a dot to separate keys "key1.key2"
     * to support list in the key series "key[index]"
     *
     * @param key the path to the wanted data can be a single key or a series of keys
     * @return the wanted value as {@link Object}
     */
    public Object get(String key) {
        if (key == null || key.isEmpty()) {
            throw new InvalidKeyException();
        }

        var keys = splitKey.apply(key);

        if (keys.size() > 1 || keyHasList.test(key))
            return getValueFromKeys(data, keys);

        return getValueFromMap(data, key);
    }

    /**
     * Fetch a single piece of data from the JSON file using a single key or a series of keys
     * <p>
     * to support key series use a dot to separate keys "key1.key2"
     * to support list in the key series "key[index]"
     *
     * @param key the path to the wanted data can be a single key or a series of keys
     * @return the wanted value as {@link String} even if it's not String it will parse it.
     */
    public String getString(String key) {
        var obj = changeVariablesIfExist(get(key));
        return Parse.String.apply(obj);
    }

    /**
     * Fetch a single piece of data from the JSON file using a single key or a series of keys
     * <p>
     * to support key series use a dot to separate keys "key1.key2"
     * to support list in the key series "key[index]"
     *
     * @param key the path to the wanted data can be a single key or a series of keys
     * @return the wanted value as {@link Integer}
     */
    public Integer getInteger(String key) {
        return Parse.Integer.apply(get(key));
    }

    /**
     * Fetch a single piece of data from the JSON file using a single key or a series of keys
     * <p>
     * to support key series use a dot to separate keys "key1.key2"
     * to support list in the key series "key[index]"
     *
     * @param key the path to the wanted data can be a single key or a series of keys
     * @return the wanted value as {@link Double}
     */
    public Double getDouble(String key) {
        return Parse.Double.apply(get(key));
    }

    /**
     * Fetch a single piece of data from the JSON file using a single key or a series of keys
     * <p>
     * to support key series use a dot to separate keys "key1.key2"
     * to support list in the key series "key[index]"
     *
     * @param key the path to the wanted data can be a single key or a series of keys
     * @return the wanted value as {@link Long}
     */
    public Long getLong(String key) {
        return Parse.Long.apply(get(key));
    }

    /**
     * Fetch a single piece of data from the JSON file using a single key or a series of keys
     * <p>
     * to support key series use a dot to separate keys "key1.key2"
     * to support list in the key series "key[index]"
     *
     * @param key the path to the wanted data can be a single key or a series of keys
     * @return the wanted value as {@link Boolean}
     */
    public Boolean getBoolean(String key) {
        return Parse.Boolean.apply(get(key));
    }

    /**
     * Fetch a single piece of data from the JSON file using a single key or a series of keys
     * <p>
     * to support key series use a dot to separate keys "key1.key2"
     * to support list in the key series "key[index]"
     * <p>
     * Will use the configured format and timezone in the config file if exist otherwise
     * will use default format "yyyy-MM-dd HH:mm:ss" and local timezone
     *
     * @param key the path to the wanted data can be a single key or a series of keys
     * @return the wanted value as {@link Calendar}
     */
    public Calendar getDate(String key) {
        return Parse.Calendar.apply(get(key));
    }

    /**
     * Fetch a single piece of data from the JSON file using a single key or a series of keys
     * <p>
     * to support key series use a dot to separate keys "key1.key2"
     * to support list in the key series "key[index]"
     * <p>
     * Will ignore the configured date format and timezone even if exist
     *
     * @param key        the path to the wanted data can be a single key or a series of keys
     * @param dateFormat to override on the configured date format or the default one, can be null
     * @param timeZone   to override on the configured timezone or the default one, can be null
     * @return the wanted value as {@link Calendar}
     */
    public Calendar getDate(String key, String dateFormat, String timeZone) {
        return parseStringToCalender(getString(key), dateFormat, timeZone);
    }

    /**
     * Fetch a single piece of data from the JSON file using a single key or a series of keys
     * <p>
     * to support key series use a dot to separate keys "key1.key2"
     * to support list in the key series "key[index]"
     *
     * @param key   the path to the wanted data can be a single key or a series of keys
     * @param clazz the class that data wanted to be parsed for
     * @param <T>   The class type
     * @return the wanted value as {@link T}
     */
    public <T> T getAs(String key, Class<T> clazz) {
        return parseObjectTo(get(key), clazz);
    }

    /**
     * Fetch a single piece of data from the JSON file using a single key or a series of keys
     * <p>
     * to support key series use a dot to separate keys "key1.key2"
     * to support list in the key series "key[index]"
     *
     * @param key     the path to the wanted data can be a single key or a series of keys
     * @param parsing a {@link Function} that will give you an object to parse it as you want
     * @param <T>     The class type
     * @return will return what the parsing function returns
     */
    public <T> T getAs(String key, Function<Object, T> parsing) {
        return parsing.apply(get(key));
    }

    /**
     * Fetch a single piece of data from the YAML file using a single key or a series of keys
     * <p>
     * to support key series use a dot to separate keys "key1.key2"
     * to support list in the key series "key[index]"
     * <p>
     * to return all data as {@link List} send "." as a key
     *
     * @param key   the path to the wanted data can be a single key or a series of keys
     * @param clazz the class that data wanted to be parsed for
     * @param <T>   The class type
     * @return the wanted value as {@link List<T>}
     */
    public <T> List<T> getListAs(String key, Class<T> clazz) {
        if (key.equals("."))
            return parseObjectToList(data, clazz);

        return parseObjectToList(get(key), clazz);
    }

    public <T> List<T> getListAs(String key, Function<Object, T> parsing) {
        if (key.equals("."))
            return parseObjectToList(data, parsing);

        return parseObjectToList(get(key), parsing);
    }

    /**
     * Fetch a single piece of data from the YAML file using a single key or a series of keys
     * <p>
     * to support key series use a dot to separate keys "key1.key2"
     * to support list in the key series "key[index]"
     * <p>
     * to return all data as a {@link Map} send "." as a key
     *
     * @param key   the path to the wanted data can be a single key or a series of keys
     * @param clazz the class that data wanted to be parsed for
     * @param <T>   The class type
     * @return the wanted value as {@link Map} of {@link String} and {@link T}
     */
    public <T> Map<String, T> getMapAs(String key, Class<T> clazz) {
        if (key.equals("."))
            return changeEnvIfEnabled(
                    parseObjectToMap(data, clazz)
            );

        return parseObjectToMap(get(key), clazz);
    }

    public <T> Map<String, T> getMapAs(String key, Function<Object, T> parsing) {
        if (key.equals("."))
            return changeEnvIfEnabled(
                    parseObjectToMap(data, parsing)
            );

        return parseObjectToMap(get(key), parsing);
    }

    /**
     * Fetch data from the JSON file as a custom object
     * <p>
     * Please note: the custom object must have a constructor without any arguments.
     * The keys in the JSON file must be the same as the custom object fields name.
     *
     * @param key   the path to the wanted data can be a single key or a series of keys
     * @param clazz the custom object class
     * @param <T>   The class type
     * @return an instance form the custom object with the data if any field not existed in the JSON file will be null
     */
    public <T> T getCustomObject(String key, Class<T> clazz) {
        return getCustomObject(key, clazz, null);
    }

    /**
     * Fetch data from the JSON file as a custom object
     * <p>
     * Please note: the custom object must have a constructor without any arguments.
     * The keys in the JSON file must be the same as the custom object fields name.
     * <p>
     * to support custom parsing please send the field name as the key of the map and the value should be the parsing function.
     * <p>
     * to support custom objects in the custom object please pass them as a list of classes.
     *
     * @param key                    the path to the wanted data can be a single key or a series of keys
     * @param clazz                  the custom object class
     * @param customFieldParsing     supporting custom parsing with field name as the key and the parser function as value
     * @param supportedCustomObjects supporting custom objects that may be used in the main custom object
     *                               if the main one used as a child object please send it again here
     * @param <T>                    The class type
     * @return an instance form the custom object with the data if any field not existed in the JSON file will be null
     */
    public <T> T getCustomObject(String key,
                                 Class<T> clazz,
                                 Map<String, Function<Object, ?>> customFieldParsing,
                                 Class<?>... supportedCustomObjects) {
        return getCustomObject(key, clazz, null, null, customFieldParsing, supportedCustomObjects);
    }

    /**
     * Fetch data from the JSON file as a custom object
     *
     * <p>
     * Please note: the custom object must have a constructor without any arguments.
     * The keys in the JSON file must be the same as the custom object fields name.
     * <p>
     * to support custom parsing please send the field name as the key of the map and the value should be the parsing function.
     * <p>
     * to support custom objects in the custom object please pass them as a list of classes.
     *
     * @param key                    the path to the wanted data can be a single key or a series of keys
     * @param clazz                  the custom object class
     * @param dateFormat             optional date format if the custom object may have a {@link Calendar} field
     * @param timezone               optional timezone if the custom object may have a {@link Calendar} field
     * @param customFieldParsing     supporting custom parsing with field name as the key and the parser function as value
     * @param supportedCustomObjects supporting custom objects that may be used in the main custom object
     *                               if the main one used as a child object please send it again here
     * @param <T>                    The class type
     * @return an instance form the custom object with the data if any field not existed in the JSON file will be null
     */
    public <T> T getCustomObject(String key,
                                 Class<T> clazz,
                                 String dateFormat,
                                 String timezone,
                                 Map<String, Function<Object, ?>> customFieldParsing,
                                 Class<?>... supportedCustomObjects) {
        T obj;
        try {
            obj = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new InvalidCustomObjectException(
                    "To use this feature the class must has a constructor without any arguments", e
            );
        }
        var fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            Object fetchedValue = null;
            Object fieldValue = null;
            var fieldName = field.getName();
            var fieldType = field.getType();
            var subKey = key + "." + fieldName;

            field.setAccessible(true);

            try {
                fetchedValue = get(subKey);
            } catch (InvalidKeyException ignore) {
            }

            if (fetchedValue != null) {
                if (customFieldParsing != null && customFieldParsing.containsKey(fieldName)) {
                    fieldValue = customFieldParsing.get(fieldName).apply(fetchedValue);
                } else if (fieldType.isAssignableFrom(String.class)) {
                    fieldValue = changeVariablesIfExist(fetchedValue);
                } else if (fieldType.isAssignableFrom(Calendar.class)) {
                    fieldValue = parseStringToCalender(fetchedValue.toString(), dateFormat, timezone);
                } else if (fieldType.isAssignableFrom(Long.class)) {
                    fieldValue = parseObjectToLong(fetchedValue);
                } else if (fieldType.isAssignableFrom(Double.class)) {
                    fieldValue = Double.parseDouble(fetchedValue.toString());
                } else if (supportedCustomObjects.length > 0) {
                    var customObjectFieldType = fieldType;
                    var isList = fieldType.isAssignableFrom(List.class);
                    var isMap = fieldType.isAssignableFrom(Map.class);

                    if (isList || isMap) {
                        var parameterizedType = (ParameterizedType) field.getGenericType();
                        customObjectFieldType = ((Class<?>) parameterizedType.getActualTypeArguments()[isList ? 0 : 1]);
                    }

                    var customObject = Arrays.stream(supportedCustomObjects)
                            .filter(customObjectFieldType::isAssignableFrom)
                            .toList().stream()
                            .findFirst().orElse(null);

                    if (customObject != null) {
                        if (isList) {
                            var list = new ArrayList<>();

                            var fetchedList = parseObjectToList(fetchedValue, Parse.Object);
                            for (int i = 0; i < fetchedList.size(); i++) {
                                list.add(
                                        getCustomObject(subKey + "[" + i + "]", customObject, customFieldParsing, supportedCustomObjects)
                                );
                            }
                            fieldValue = list;
                        } else if (isMap) {
                            var map = new HashMap<>();

                            var fetchedMap =
                                    parseObjectToMap(fetchedValue, Parse.Object);
                            for (String mKey : fetchedMap.keySet()) {
                                map.put(mKey, getCustomObject(subKey + "." + mKey, customObject, customFieldParsing, supportedCustomObjects));
                            }
                            fieldValue = map;
                        } else
                            fieldValue = getCustomObject(subKey, customObject, customFieldParsing, supportedCustomObjects);
                    }
                }

                fieldValue = fieldValue == null ?
                        parseObjectTo(fetchedValue, field.getType()) :
                        fieldValue;
            }


            try {
                field.set(obj, fieldValue);
            } catch (IllegalAccessException e) {
                throw new InvalidCustomObjectException(
                        "Can't set the of [" + fieldName + "] to be [" + fetchedValue + "]", e
                );
            }
        }
        return obj;
    }

    private Object getValueFromKeys(Object data, List<String> keys) {
        Object obj = data;

        for (String key : keys) {
            obj = changeMapAndListVariablesIfExist(obj);

            if (keyHasList.test(key))
                obj = getValueFromKeyList(obj, key);

            else
                obj = getValueFromMap(obj, key);
        }

        return obj;
    }

    private Object getValueFromKeyList(Object obj, String key) {
        var indexes = getIndexesFromKeyList(key);
        key = key.replaceAll(NUMBER_IN_SQUARE_BRACKETS_REGEX, "");

        if (!key.isEmpty())
            obj = getValueFromMap(obj, key);

        obj = changeMapAndListVariablesIfExist(obj);

        for (Integer index : indexes) {
            obj = getValueFromList(obj, index);
        }

        return obj;
    }

    private Object getValueFromMap(Object obj, String key) {
        var map = changeEnvIfEnabled(parseObjectToMap(obj, Parse.Object));

        if (!map.containsKey(key)) {
            throw new InvalidKeyException("This key [" + key + "] does not exist in [" + obj + "]");
        }

        return map.get(key);
    }

    private Object getValueFromList(Object obj, Integer index) {
        var list = parseObjectToList(obj, Parse.Object);

        if (index >= list.size()) {
            throw new InvalidKeyException("This index [" + index + "] is out of the boundary of [" + obj + "]");
        }

        return list.get(index);
    }

    private Object changeVariablesIfExist(Object obj) {
        var globalVariables = ConfigHandler.getInstance().getGlobalVariables();
        var stringObj = Parse.String.apply(obj);

        if ((vars == null && globalVariables.isEmpty()) || !stringObj.matches(".*\\$\\{.+}.*"))
            return obj;

        if (vars != null) {
            for (String key : vars.keySet()) {
                stringObj = stringObj
                        .replaceAll("\\$\\{" + key + "}", Parse.String.apply(vars.get(key)));
            }
        }

        if (globalVariables.isPresent()) {
            var gVars = globalVariables.get();
            for (String key : gVars.keySet()) {
                stringObj = stringObj
                        .replaceAll("\\$\\{" + key + "}", Parse.String.apply(gVars.get(key)));
            }
        }

        return stringObj;
    }

    private Object changeMapAndListVariablesIfExist(Object obj) {
        var keyPlaceholderRegex = "\\$\\{(.*?)}";

        if (vars != null && obj instanceof String str && str.matches(keyPlaceholderRegex)) {
            var matcher = Pattern.compile(keyPlaceholderRegex).matcher(str);

            if (matcher.find()) {
                var varKey = matcher.group(1);

                if (vars.containsKey(varKey)) {

                    return vars.get(varKey);
                }
            }
        }

        return obj;
    }

    private Map<String, Object> getJSONVariables() {
        var jsonVarsKey = JSON.DEFAULT_VARIABLES_KEY;

        Map<String, Object> variables = null;
        try {
            variables = getMapAs(jsonVarsKey, Parse.Object);
        } catch (InvalidKeyException | NotAMapException ignore) {
        }

        return changeEnvIfEnabled(variables);
    }

    private <T> Map<String, T> changeEnvIfEnabled(Map<String, T> map) {
        return this.enableEnv ? changeEnvironmentsKeys(map) : map;
    }
}
