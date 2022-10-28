package nsr_json;

import exception.DateFormatException;
import exception.NotAListException;
import exception.NotAMapException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

class Helper {
    protected final static String KEY_CONTAINS_LIST_REGEX = "^[-a-zA-Z\\d_!@#$%^&*()+=|\\\\/?><\"'{}~]*(\\[\\d+])+$";
    protected final static String NUMBER_IN_SQUARE_BRACKETS_REGEX = "\\[\\d+]";
    protected final static String SQUARE_BRACKETS_REGEX = "[\\[\\]]";
    protected final static String KEY_SEPARATOR_REGEX = "\\.";
    protected final static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private Helper() {
    }

    // Splitting the key to a list of keys using the dot as the splitter.
    protected static final Function<String, List<String>> splitKey =
            key -> Arrays.stream(key.split(KEY_SEPARATOR_REGEX)).toList();

    // Checking if the key has an index for a list [index]
    protected static final Predicate<String> keyHasList =
            key -> key.matches(KEY_CONTAINS_LIST_REGEX);

    /**
     * Used internally to fetch the index or indexes from the key that support list
     *
     * @param key the key that contains one or more index
     * @return a {@link List<Integer>} that contains one or more index
     */
    protected static List<Integer> getIndexesFromKeyList(String key) {
        var indexes = new ArrayList<Integer>();

        var matcher = Pattern
                .compile(NUMBER_IN_SQUARE_BRACKETS_REGEX).matcher(key);

        while (matcher.find()) {
            indexes.add(Integer.parseInt(
                    matcher
                            .group()
                            .replaceAll(SQUARE_BRACKETS_REGEX, "")
            ));
        }

        return indexes;
    }

    /**
     * Used internally to parse {@link Object} to be any type
     *
     * @param obj   the value wanted to be parsed
     * @param clazz the class that data wanted to be parsed for
     * @param <T>   the wanted type
     * @return the value as {@link T}
     */
    protected static <T> T parseObjectTo(Object obj, Class<T> clazz) {
        T v;
        Method valueOf = null;
        Object invokedValue = null;
        var isValueOfMethodUseString = true;

        try {
            valueOf = clazz.getMethod("valueOf", String.class);
        } catch (NoSuchMethodException ignore) {
            try {
                valueOf = clazz.getMethod("valueOf", Object.class);
                isValueOfMethodUseString = false;
            } catch (NoSuchMethodException ignored) {
            }
        }

        if (valueOf != null) {
            try {
                invokedValue = valueOf.invoke(clazz, isValueOfMethodUseString ? obj.toString() : obj);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ClassCastException("Can't cast [" + obj + "] to be [" + clazz + "], " +
                        "can't invoke valueOf method --- " + e);
            }
        }

        try {
            v = clazz.cast(invokedValue != null ? invokedValue : obj);
        } catch (ClassCastException e) {
            throw new ClassCastException("Can't cast [" + obj + "] to be [" + clazz + "] --- " + e);
        }
        return v;
    }

    /**
     * Used internally to parse {@link Object} to be a {@link List<T>}
     *
     * @param obj   the value wanted to be parsed
     * @param clazz the class that data wanted to be parsed for
     * @param <T>   the wanted type
     * @return the value as {@link List<T>}
     */
    @Deprecated
    protected static <T> List<T> parseObjectToList(Object obj, Class<T> clazz) {
        if (obj instanceof List<?> list) {
            return list.stream()
                    .map(item -> parseObjectTo(item, clazz)).toList();
        }

        throw new NotAListException();
    }

    /**
     * Used internally to parse {@link Object} to be a {@link List<T>}
     *
     * @param obj   the value wanted to be parsed
     * @param parsing the parsing function
     * @param <T>   the wanted type
     * @return the value as {@link List<T>}
     */
    protected static <T> List<T> parseObjectToList(Object obj, Function<Object, T> parsing) {
        if (obj instanceof List<?> list) {
            return list.stream()
                    .map(parsing).toList();
        }

        throw new NotAListException();
    }

    /**
     * Used internally to parse {@link Object} to be a {@link Map} of {@link String} and {@link T}
     *
     * @param obj   the value wanted to be parsed
     * @param clazz the class that data wanted to be parsed for
     * @param <T>   the wanted type
     * @return the value as {@link Map} of {@link String} and {@link T}
     */
    @Deprecated
    protected static <T> Map<String, T> parseObjectToMap(Object obj, Class<T> clazz) {
        if (obj instanceof Map<?, ?> map) {
            var nMap = new HashMap<String, T>();
            map.forEach(
                    (k, v) -> nMap.put(k.toString(), parseObjectTo(v, clazz))
            );
            return nMap;
        }

        throw new NotAMapException();
    }

    /**
     * Used internally to parse {@link Object} to be a {@link Map} of {@link String} and {@link T}
     *
     * @param obj   the value wanted to be parsed
     * @param parsing the parsing function
     * @param <T>   the wanted type
     * @return the value as {@link Map} of {@link String} and {@link T}
     */
    protected static <T> Map<String, T> parseObjectToMap(Object obj, Function<Object, T> parsing) {
        if (obj instanceof Map<?, ?> map) {
            var nMap = new HashMap<String, T>();
            map.forEach(
                    (k, v) -> nMap.put(k.toString(), parsing.apply(v))
            );
            return nMap;
        }

        throw new NotAMapException();
    }

    /**
     * Used internally to parse a {@link String} to a {@link Calendar}
     *
     * @param stringDate date in string
     * @param dateFormat optional date format
     * @param timeZone   optional timezone
     * @return date as {@link Calendar}
     */
    protected static Calendar parseStringToCalender(String stringDate, String dateFormat, String timeZone) {
        var calendar = (timeZone == null || timeZone.isEmpty()) ?
                Calendar.getInstance() :
                Calendar.getInstance(TimeZone.getTimeZone(timeZone));

        try {
            calendar.setTime(new SimpleDateFormat(
                    (dateFormat == null || dateFormat.isEmpty()) ? DEFAULT_DATE_FORMAT : dateFormat
            )
                    .parse(stringDate));
        } catch (ParseException e) {
            throw new DateFormatException(e);
        }
        return calendar;
    }

    /**
     * Used internally to parse {@link Object} ends with "L" to a {@link Long}
     *
     * @param obj the value wanted to be parsed
     * @return the parsed value as {@link Long}
     */
    protected static Long parseObjectToLong(Object obj) {
        if (obj instanceof String sValue && sValue.endsWith("L")) {
            return Long.parseLong(sValue.replace("L", ""));
        }
        return parseObjectTo(obj, Long.class);
    }

    /**
     * Supports JSON files without extension
     *
     * @param filePath the relative path of the file with or without extension
     * @return the relative path of the file with extension ".json"
     */
    protected static String prepareFilePath(String filePath) {
        return filePath.matches(".*.json$") ? filePath : filePath + ".json";
    }
}
