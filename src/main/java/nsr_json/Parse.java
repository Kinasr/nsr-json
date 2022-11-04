package nsr_json;

import exception.ParsingException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.function.Function;

import static nsr_json.Helper.DEFAULT_DATE_FORMAT;

public class Parse {

    public static final Function<Object, Object> Object =
            obj -> obj;

    public static final Function<Object, Boolean> Boolean =
            obj -> {
                java.lang.Boolean value;

                if (obj == null)
                    value = null;
                else if (obj instanceof Boolean bool)
                    value = bool;
                else if (obj instanceof String str)
                    value = java.lang.Boolean.valueOf(str);
                else
                    throw new ParsingException("Can't parse [" + obj + "] to be Boolean");

                return value;
            };

    public static Function<Object, Byte> Byte =
            obj -> {
                java.lang.Byte value;

                if (obj == null)
                    value = null;
                else if (obj instanceof Number num)
                    value = num.byteValue();
                else if (obj instanceof String str)
                    try {
                        value = java.lang.Byte.valueOf(str);
                    } catch (NumberFormatException e) {
                        throw new ParsingException("Can't parse [" + obj + "] to be Byte -- " + e);
                    }
                else
                    throw new ParsingException("Can't parse [" + obj + "] to be Byte");

                return value;
            };

    public static Function<Object, Short> Short =
            obj -> {
                java.lang.Short value;

                if (obj == null)
                    value = null;
                else if (obj instanceof Number num)
                    value = num.shortValue();
                else if (obj instanceof String str)
                    try {
                        value = java.lang.Short.valueOf(str);
                    } catch (NumberFormatException e) {
                        throw new ParsingException("Can't parse [" + obj + "] to be Short -- " + e);
                    }
                else
                    throw new ParsingException("Can't parse [" + obj + "] to be Short");

                return value;
            };

    public static final Function<Object, Integer> Integer =
            obj -> {
                java.lang.Integer value;

                if (obj == null)
                    value = null;
                else if (obj instanceof Number num)
                    value = num.intValue();
                else if (obj instanceof String str)
                    try {
                        value = java.lang.Integer.valueOf(str);
                    } catch (NumberFormatException e) {
                        throw new ParsingException("Can't parse [" + obj + "] to be Integer -- " + e);
                    }
                else
                    throw new ParsingException("Can't parse [" + obj + "] to be Integer");

                return value;
            };

    public static final Function<Object, Long> Long =
            obj -> {
                java.lang.Long value;

                if (obj == null)
                    value = null;
                else if (obj instanceof Number num)
                    value = num.longValue();
                else if (obj instanceof String str) {
                    try {
                        if (str.endsWith("L"))
                            value = java.lang.Long.valueOf(str.replace("L", ""));
                        else
                            value = java.lang.Long.valueOf(str);
                    } catch (NumberFormatException e) {
                        throw new ParsingException("Can't parse [" + obj + "] to be Long -- " + e);
                    }
                } else
                    throw new ParsingException("Can't parse [" + obj + "] to be Long");

                return value;
            };

    public static Function<Object, Float> Float =
            obj -> {
                java.lang.Float value;

                if (obj == null)
                    value = null;
                else if (obj instanceof Number num)
                    value = num.floatValue();
                else if (obj instanceof String str)
                    try {
                        value = java.lang.Float.valueOf(str);
                    } catch (NumberFormatException e) {
                        throw new ParsingException("Can't parse [" + obj + "] to be Float -- " + e);
                    }
                else
                    throw new ParsingException("Can't parse [" + obj + "] to be Float");

                return value;
            };

    public static final Function<Object, Double> Double =
            obj -> {
                java.lang.Double value;

                if (obj == null)
                    value = null;
                else if (obj instanceof Number num)
                    value = num.doubleValue();
                else if (obj instanceof String str)
                    try {
                        value = java.lang.Double.valueOf(str);
                    } catch (NumberFormatException e) {
                        throw new ParsingException("Can't parse [" + obj + "] to be Double -- " + e);
                    }
                else
                    throw new ParsingException("Can't parse [" + obj + "] to be Double");

                return value;
            };

    public static final Function<Object, String> String =
            obj -> obj != null ? java.lang.String.valueOf(obj) : null;


    public static final Function<Object, Calendar> Calendar =
            obj -> {
                var config = ConfigHandler.getInstance();
                var configDateFormat = config.getDateFormat();
                var configTimezone = config.getTimezone();

                var calendar = configTimezone.isPresent() ?
                        java.util.Calendar.getInstance(TimeZone.getTimeZone(configTimezone.get())) :
                        java.util.Calendar.getInstance();
                try {
                    calendar.setTime(new SimpleDateFormat(configDateFormat.orElse(DEFAULT_DATE_FORMAT))
                            .parse(String.apply(obj)));
                } catch (ParseException e) {
                    throw new ParsingException("Can't parse [" + obj + "] to be Calender");
                }
                return calendar;
            };
}
