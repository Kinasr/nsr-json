package nsr_json;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import test_helper.Company;
import test_helper.Person;
import test_helper.Pet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

class JSONTest {
    private static JSON json;

    @BeforeAll
    static void setup() {
        json = new JSON("src/test/resources/json_test.json");
    }

    @Nested
    class MainCases {
        @Test
        void readObject() {
            assertThat(json.read().get("person.name"))
                    .isInstanceOf(Object.class)
                    .isEqualTo("Ahmed");
        }

        @Test
        void readString() {
            assertThat(json.read().getString("person.name"))
                    .isInstanceOf(String.class)
                    .isEqualTo("Ahmed");
        }

        @Test
        void readInteger() {
            assertThat(json.read().getInteger("person.age"))
                    .isInstanceOf(Integer.class)
                    .isEqualTo(35);
        }

        @Test
        void readDouble() {
            assertThat(json.read().getDouble("person.balance"))
                    .isInstanceOf(Double.class)
                    .isEqualTo(10573.65);
        }

        @Test
        void readLong() {
            assertThat(json.read().getLong("person.id"))
                    .isInstanceOf(Long.class)
                    .isEqualTo(12462164789564L);
        }

        @Test
        void readBoolean() {
            assertThat(json.read().getBoolean("person.isMarred"))
                    .isInstanceOf(Boolean.class)
                    .isEqualTo(true);
        }

        @Test
        void readDateWithDefaultFormat() throws ParseException {
            var expectedDate = Calendar.getInstance();
            expectedDate.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .parse("2020-06-23 10:25:55"));

            assertThat(json.read().getDate("person.marriageDate"))
                    .isInstanceOf(Calendar.class)
                    .isEqualTo(expectedDate);
        }

        @Test
        void readDateWithCustomFormatAndZone() throws ParseException {
            var dateFormat = "MM/dd/yyyy";
            var timeZone = "UTC";

            var expectedDate = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
            expectedDate.setTime(new SimpleDateFormat(dateFormat).parse("09/20/2005"));

            assertThat(json.read().getDate("person.graduationDate", dateFormat, timeZone))
                    .isInstanceOf(Calendar.class)
                    .isEqualTo(expectedDate);
        }

        @Test
        void readIntegerAs() {
            assertThat(json.read().getAs("person.age", Integer.class))
                    .isInstanceOf(Integer.class)
                    .isEqualTo(35);
        }

        @Test
        void readIntegerAsWithParser() {
            var number = json.read().getAs("person.age",
                    (intObj) -> Integer.valueOf(intObj.toString()));

            assertThat(number)
                    .isInstanceOf(Integer.class)
                    .isEqualTo(35);
        }

        @Test
        void readStringList() {
            assertThat(json.read().getListAs("person.children", String.class))
                    .isInstanceOf(List.class)
                    .isEqualTo(List.of("Mohamed", "Ali", "Fatima"));
        }

        @Test
        void readIntegerList() {
            assertThat(json.read().getListAs("person.favoriteNumbers", Integer.class))
                    .isInstanceOf(List.class)
                    .isEqualTo(List.of(0, 5, 9));
        }

        @Test
        void readStringMap() {
            assertThat(json.read().getMapAs("person.currentCompany", String.class))
                    .isInstanceOf(Map.class)
                    .isEqualTo(Map.of(
                            "name", "ABC Company",
                            "email", "abc@abc.com"
                    ));
        }

        @Test
        void readIntegerMap() {
            assertThat(json.read().getMapAs("person.importantNumbers", Integer.class))
                    .isInstanceOf(Map.class)
                    .isEqualTo(Map.of(
                            "roomNumber", 415,
                            "streetNumber", 10
                    ));
        }

        @Test
        void readIntegerWithBreakPoint() {
            assertThat(json.read().setBreakPoint("person").getInteger("age"))
                    .isEqualTo(35);
        }

        @Test
        void readWithVariables() {
            assertThat(json.read().getString("code"))
                    .isEqualTo("12345");
        }
    }

    @Nested
    class CustomObject {
        @Test
        void readCustomObjectAs() {
            var expectedPet = new Pet()
                    .setName("Bunny")
                    .setAge(5);

            assertThat(json.read().getAs("pet", Pet.class))
                    .isInstanceOf(Pet.class)
                    .isEqualTo(expectedPet);
        }

        @Test
        void readCustomObjectList() {
            assertThat(json.read().getListAs("pets", Pet.class))
                    .isEqualTo(List.of(
                            new Pet().setName("Lucy").setAge(10),
                            new Pet().setName("Milo").setAge(11)
                    ));
        }

        @Test
        void readCustomObject() {
            assertThat(json.read().getCustomObject("pet", Pet.class))
                    .isEqualTo(new Pet().setName("Bunny").setAge(5));
        }

        @Test
        void readCustomObjectWithAllAdditions() throws ParseException {
            var dateOfBirth = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            dateOfBirth.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("1990-10-05 00:00:00"));

            DateTimeFormatter dFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            var establishDate = LocalDateTime.parse("1986-04-08 22:00", dFormatter);

            var person = new Person()
                    .setName("Ali")
                    .setEmail("ali@email.com")
                    .setDateOfBirth(dateOfBirth)
                    .setWorkAt(new Company()
                            .setName("ABC Company")
                            .setAddress("XYZ City")
                            .setNumOfEmployees(100)
                            .setEstablishDate(establishDate))
                    .setPetsList(List.of(
                            new Pet().setName("Lucy").setAge(10),
                            new Pet().setName("Milo").setAge(11)
                    ));


            assertThat(json.read().getCustomObject(
                    "person2", Person.class,
                    "yyyy-MM-dd HH:mm:ss", "UTC",
                    Map.of("establishDate", (obj) -> {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        return LocalDateTime.parse(obj.toString(), formatter);
                    }),
                    Company.class, Pet.class
            )).isEqualTo(person);
        }
    }
}