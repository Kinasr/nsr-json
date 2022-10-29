package nsr_json;

import exception.DateFormatException;
import exception.InvalidKeyException;
import exception.NotAMapException;
import exception.ParsingException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import test_helper.Company;
import test_helper.Person;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JSONReaderTest {
    @Mock
    JSONFileLoader jsonLoader;

    @Nested
    class ValidCases {
        @Test
        void getAll() {
            var json = new JSONObject("""
                    {
                      "object": "object"
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getAll())
                    .isInstanceOf(Object.class)
                    .isEqualTo(Map.of("object", "object"));
        }

        @Test
        void getValidObjectFromJSONObject() {
            var json = new JSONObject("""
                    {
                      "object": "object"
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).get("object"))
                    .isInstanceOf(Object.class)
                    .isEqualTo("object");
        }

        @Test
        void getObjectFromNestedMap() {
            var json = new JSONObject("""
                    {
                      "map": {
                        "sub-map": {
                            "value": "I'm here"
                        }
                      }
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).get("map.sub-map.value"))
                    .isInstanceOf(Object.class)
                    .isEqualTo("I'm here");
        }

        @Test
        void getValidObjectFromJSONArray() {
            var json = new JSONArray("""
                    [
                       "object1",
                       "object2"
                     ]
                    """);
            when(jsonLoader.getData()).thenReturn(json.toList());

            assertThat(new JSONReader(jsonLoader).get("[1]"))
                    .isInstanceOf(Object.class)
                    .isEqualTo("object2");
        }

        @Test
        void getObjectFromNestedList() {
            var json = new JSONArray("""
                    [
                      [
                        [
                          [
                            "first",
                            "second"
                          ]
                        ]
                      ]
                    ]
                    """);
            when(jsonLoader.getData()).thenReturn(json.toList());

            assertThat(new JSONReader(jsonLoader).get("[0][0][0][1]"))
                    .isInstanceOf(Object.class)
                    .isEqualTo("second");
        }

        @Test
        void getObjectFromMixedMapAndListSimpleCase() {
            var json = new JSONObject("""
                    {
                      "map": [
                        "first",
                        "second"
                      ]
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).get("map[0]"))
                    .isInstanceOf(Object.class)
                    .isEqualTo("first");
        }

        @Test
        void getObjectFromMixedMapAndListComplexCase() {
            var json = new JSONObject("""
                    {
                       "map": [
                         {
                           "sub-map": [
                             {
                               "value": "I'm here"
                             }
                           ]
                         }
                       ]
                     }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).get("map[0].sub-map[0].value"))
                    .isInstanceOf(Object.class)
                    .isEqualTo("I'm here");
        }

        @Test
        void getValidString() {
            var json = new JSONObject("""
                    {
                      "string": "text"
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getString("string"))
                    .isInstanceOf(String.class)
                    .isEqualTo("text");
        }

        @Test
        void getStringForIntegerValue() {
            var json = new JSONObject("""
                    {
                      "integer": 10
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getString("integer"))
                    .isInstanceOf(String.class)
                    .isEqualTo("10");
        }

        @Test
        void getEmptyString() {
            var json = new JSONObject("""
                    {
                      "string": ""
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getString("string"))
                    .isInstanceOf(String.class)
                    .isEqualTo("");
        }

        @Test
        void getValidInteger() {
            var json = new JSONObject("""
                    {
                      "integer": 10
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getInteger("integer"))
                    .isInstanceOf(Integer.class)
                    .isEqualTo(10);
        }

        @Test
        void getValidDouble() {
            var json = new JSONObject("""
                    {
                      "double": 5.3
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getDouble("double"))
                    .isInstanceOf(Double.class)
                    .isEqualTo(5.3);
        }

        @Test
        void getValidLong() {
            var json = new JSONObject("""
                    {
                      "long": 999999999999999
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getLong("long"))
                    .isInstanceOf(Long.class)
                    .isEqualTo(999999999999999L);
        }

        @Test
        void getValidLongFromString() {
            var json = new JSONObject("""
                    {
                      "long": "999999999999999L"
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getLong("long"))
                    .isInstanceOf(Long.class)
                    .isEqualTo(999999999999999L);
        }

        @Test
        void getValidBoolean() {
            var json = new JSONObject("""
                    {
                      "boolean": true
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getBoolean("boolean"))
                    .isInstanceOf(Boolean.class)
                    .isEqualTo(true);
        }

        @Test
        void getValidDate() throws ParseException {
            var json = new JSONObject("""
                    {
                      "date": "2022-08-10 10:30:00"
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            var dateFormat = "yyyy-MM-dd HH:mm:ss";
            var timeZone = "UTC";

            var expectedDate = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
            expectedDate.setTime(new SimpleDateFormat(dateFormat).parse("2022-08-10 10:30:00"));

            assertThat(new JSONReader(jsonLoader).getDate("date", dateFormat, timeZone))
                    .isInstanceOf(Calendar.class)
                    .isEqualTo(expectedDate);
        }

        @Test
        void getValidDateUsingConfiguredConfig() throws ParseException {
            var json = new JSONObject("""
                    {
                      "date": "2022-08-10 10:30:00"
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            var dateFormat = "yyyy-MM-dd HH:mm:ss";

            var expectedDate = Calendar.getInstance();
            expectedDate.setTime(new SimpleDateFormat(dateFormat).parse("2022-08-10 10:30:00"));

            assertThat(new JSONReader(jsonLoader).getDate("date"))
                    .isInstanceOf(Calendar.class)
                    .isEqualTo(expectedDate);
        }

        @Test
        void getValidStringAsString() {
            var json = new JSONObject("""
                    {
                      "string": "text"
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getAs("string", String.class))
                    .isInstanceOf(String.class)
                    .isEqualTo("text");
        }

        @Test
        void getValidDataAsWithCustomParser() {
            var json = new JSONObject("""
                    {
                      "person": {
                        "id": 1,
                        "name": "Ahmed",
                        "dateOfBirth": "1986-04-08 12:30"
                      }
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            Function<Object, LocalDateTime> pLocalDateTime =
                    obj -> {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        return LocalDateTime.parse(obj.toString(), formatter);
                    };

            assertThat(new JSONReader(jsonLoader).getAs("person.dateOfBirth", pLocalDateTime))
                    .isInstanceOf(LocalDateTime.class)
                    .isEqualTo(pLocalDateTime.apply("1986-04-08 12:30"));
        }

        @Test
        void getValidListAs() {
            var json = new JSONObject("""
                    {
                      "list": [
                        "list1",
                        "list2"
                      ]
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getListAs("list", String.class))
                    .isInstanceOf(List.class)
                    .isEqualTo(List.of("list1", "list2"));
        }

        @Test
        void getAllDataAsList() {
            var json = new JSONArray("""
                    [
                      "first",
                      "second"
                    ]
                    """);
            when(jsonLoader.getData()).thenReturn(json.toList());
            assertThat(new JSONReader(jsonLoader).getListAs(".", String.class))
                    .isInstanceOf(List.class)
                    .isEqualTo(List.of("first", "second"));
        }

        @Test
        void getValidMapAs() {
            var json = new JSONObject("""
                    {
                      "map": {
                        "map1": "m1",
                        "map2": "m2"
                      }
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getMapAs("map", String.class))
                    .isInstanceOf(Map.class)
                    .isEqualTo(Map.of("map1", "m1", "map2", "m2"));
        }

        @Test
        void getAllDataAsMap() {
            var json = new JSONObject("""
                    {
                       "map1": "first",
                       "map2": "second"
                     }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getMapAs(".", String.class))
                    .isInstanceOf(Map.class)
                    .isEqualTo(Map.of("map1", "first", "map2", "second"));
        }

        @Test
        void setPointShouldReturnAnInstanceFromJSONReader() {
            var json = new JSONObject("""
                    {
                      "map": {
                        "sub-map": {
                            "value": "I'm here"
                        }
                      }
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).setBreakPoint("map.sub-map"))
                    .isInstanceOf(JSONReader.class);
        }

        @Test
        void getObjectUsingSetPoint() {
            var json = new JSONObject("""
                    {
                      "map": {
                        "sub-map": {
                            "value": "I'm here"
                        }
                      }
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).setBreakPoint("map.sub-map").get("value"))
                    .isInstanceOf(Object.class)
                    .isEqualTo("I'm here");
        }

        @Test
        void getAsIntegerNull() {
            var json = new JSONObject("""
                    {
                      "integer": null
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());


            assertThat(new JSONReader(jsonLoader).getAs("integer", Parse.Integer))
                    .isNull();
        }

        @Test
        void getAsInteger() {
            var json = new JSONObject("""
                    {
                      "integer": 10
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getAs("integer", Parse.Integer))
                    .isInstanceOf(Integer.class)
                    .isEqualTo(10);
        }

        @Test
        void getAsIntegerString() {
            var json = new JSONObject("""
                    {
                      "integer": 10
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getAs("integer", Parse.Integer))
                    .isInstanceOf(Integer.class)
                    .isEqualTo(10);
        }

        @Test
        void getAsIntegerArray() {
            var json = new JSONObject("""
                    {
                      "integer": []
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThatThrownBy(() -> new JSONReader(jsonLoader).getAs("integer", Parse.Integer))
                    .isInstanceOf(ParsingException.class);
        }
    }

    @Nested
    class InvalidCases {
        @ParameterizedTest
        @NullAndEmptySource
        void nullAndEmptyKey(String key) {
            assertThatThrownBy(
                    () -> new JSONReader(jsonLoader).get(key)
            ).isInstanceOf(InvalidKeyException.class);
        }

        @Test
        void notExistedKey() {
            var json = new JSONObject("""
                    {
                      "object": "object"
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThatThrownBy(
                    () -> new JSONReader(jsonLoader).get("not-existed-key")
            ).isInstanceOf(InvalidKeyException.class);
        }

        @Test
        void keyOutOfTheListBoundary() {
            var json = new JSONArray("""
                    [
                       "object1",
                       "object2"
                     ]
                    """);
            when(jsonLoader.getData()).thenReturn(json.toList());

            assertThatThrownBy(
                    () -> new JSONReader(jsonLoader).get("[2]")
            ).isInstanceOf(InvalidKeyException.class);
        }

        @Test
        void getIntegerForNonIntegerValue() {
            var json = new JSONObject("""
                    {
                      "string": "text"
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThatThrownBy(
                    () -> new JSONReader(jsonLoader).getInteger("string")
            ).isInstanceOf(ParsingException.class);
        }

        @Test
        void getDateWithWrongFormat() {
            var json = new JSONObject("""
                    {
                      "date": "2022-08-10 10:30:00"
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThatThrownBy(
                    () -> new JSONReader(jsonLoader)
                            .getDate("date", "yyyy/MM/dd HH:mm:ss", "UTC")
            ).isInstanceOf(DateFormatException.class);
        }

    }

    @Nested
    class VariableCases {
        @Test
        void getStringContainsVariablePlaceHolder() {
            var json = new JSONObject("""
                    {
                        "variables": {
                             "don't-change-me": ", I found YOU"
                        },
                       "map": [
                         {
                           "sub-map": [
                             {
                               "value": "I'm here${don't-change-me}"
                             }
                           ]
                         }
                       ]
                     }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getString("map[0].sub-map[0].value"))
                    .isEqualTo("I'm here, I found YOU");
        }

        @Test
        void getIntegerWhileThereAreVariablesDefinedIntegerShouldBeNotAffected() {
            var json = new JSONObject("""
                    {
                        "variables": {
                             "don't-change-me": ", I found YOU"
                        },
                       "integer": 10
                     }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());
            assertThat(new JSONReader(jsonLoader).getInteger("integer"))
                    .isEqualTo(10);
        }

        @Test
        void getStringWhileVariablesEnabledButNotExistInTheFile() {
            var json = new JSONObject("""
                    {
                       "string": "text"
                     }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getString("string"))
                    .isEqualTo("text");
        }

        @Test
        void getStringWithMultiVariablesInTheSameString() {
            var json = new JSONObject("""
                    {
                        "variables": {
                             "v1": "v1",
                             "v2": "v2",
                             "v3": "v3"
                        },
                       "string": "first var is ${v1}, second is ${v2}, then third is ${v3}"
                     }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getString("string"))
                    .isEqualTo("first var is v1, second is v2, then third is v3");
        }

        @Test
        void getStringWhileVariablesEnabledButFromFileContainsList() {
            var json = new JSONArray("""
                    [
                       "object1",
                       "object2"
                     ]
                    """);
            when(jsonLoader.getData()).thenReturn(json.toList());

            assertThat(new JSONReader(jsonLoader).getString("[1]"))
                    .isEqualTo("object2");
        }

        @Test
        void getValueInVariableOfMap() {
            var json = new JSONObject("""
                    {
                        "variables": {
                          "person": {
                            "id": 15,
                            "name": "Mohamed",
                            "email": "mohamed@email.com",
                            "age": 28
                          }
                        },
                        "person1": {
                          "id": 1,
                          "name": "Ahmed ${last-name}",
                          "email": "ahmed@email.com",
                          "age": 35
                        },
                        "person2": "${person}"
                      }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getInteger("person2.id"))
                    .isEqualTo(15);
        }

        @Test
        void getValueInVariableOfList() {
            var json = new JSONObject("""
                    {
                         "variables": {
                           "companies": [
                             "A company",
                             "B company"
                           ]
                         },
                         "person": {
                           "id": 1,
                           "name": "Ahmed ${last-name}",
                           "email": "ahmed@email.com",
                           "age": 35,
                           "companies": "${companies}"
                         }
                       }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getString("person.companies[0]"))
                    .isEqualTo("A company");
        }

        @Test
        void getValueInVariableOfMapAndList() {
            var json = new JSONObject("""
                    {
                         "variables": {
                           "person": {
                             "id": 15,
                             "name": "Mohamed",
                             "email": "mohamed@email.com",
                             "age": 28,
                             "companies": [
                               "A company",
                               "B company"
                             ]
                           }
                         },
                         "person1": {
                           "id": 1,
                           "name": "Ahmed ${last-name}",
                           "email": "ahmed@email.com",
                           "age": 35,
                           "companies": "${companies}"
                         },
                         "person2": "${person}"
                       }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getString("person2.companies[1]"))
                    .isEqualTo("B company");
        }

        @Test
        void getStringContainsVariablePlaceHolderNotExistInVariables() {
            var json = new JSONObject("""
                    {
                        "variables": {
                             "don't-change-me": ", I found YOU"
                        },
                       "map": [
                         {
                           "sub-map": [
                             {
                               "value": "I'm here${change-me}"
                             }
                           ]
                         }
                       ]
                     }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getString("map[0].sub-map[0].value"))
                    .isEqualTo("I'm here${change-me}");
        }

        @Test
        void getValueInVariableOfMapNotExisted() {
            var json = new JSONObject("""
                    {
                        "variables": {
                          "person": {
                            "id": 15,
                            "name": "Mohamed",
                            "email": "mohamed@email.com",
                            "age": 28
                          }
                        },
                        "person1": {
                          "id": 1,
                          "name": "Ahmed ${last-name}",
                          "email": "ahmed@email.com",
                          "age": 35
                        },
                        "person2": "${person0}"
                      }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThatThrownBy(
                    () -> new JSONReader(jsonLoader).getInteger("person2.id")
            ).isInstanceOf(NotAMapException.class);
        }
    }

    @Nested
    class CustomObjectCases {
        @Test
        void getCustomObject() {
            var json = new JSONObject("""
                    {
                       "person": {
                         "id": 1,
                         "name": "Ahmed",
                         "email": "ahmed@email.com",
                         "age": 35
                       }
                     }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getCustomObject("person", Person.class))
                    .isInstanceOf(Person.class)
                    .isEqualTo(new Person(
                            1,
                            "Ahmed",
                            "ahmed@email.com",
                            35
                    ));
        }

        @Test
        void getCustomObjectContainsDouble() {
            var json = new JSONObject("""
                    {
                      "person": {
                        "id": 1,
                        "name": "Ahmed",
                        "balance": 999.57
                      }
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getCustomObject("person", Person.class))
                    .isInstanceOf(Person.class)
                    .isEqualTo(new Person(
                            1,
                            "Ahmed",
                            999.57
                    ));
        }

        @Test
        void getCustomObjectContainsBoolean() {
            var json = new JSONObject("""
                    {
                          "person": {
                            "id": 1,
                            "name": "Ahmed",
                            "email": "ahmed@email.com",
                            "age": 35,
                            "isMarried": true
                          }
                        }
                     }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getCustomObject("person", Person.class))
                    .isInstanceOf(Person.class)
                    .isEqualTo(new Person(
                            1,
                            "Ahmed",
                            "ahmed@email.com",
                            35,
                            true
                    ));
        }

        @Test
        void getCustomObjectContainsLong() {
            var json = new JSONObject("""
                    {
                        "person": {
                          "id": 1,
                          "name": "Ahmed",
                          "phoneNumber": 2487954879
                        }
                      }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getCustomObject("person", Person.class))
                    .isInstanceOf(Person.class)
                    .isEqualTo(new Person(
                            1,
                            "Ahmed",
                            2487954879L
                    ));
        }

        @Test
        void getCustomObjectContainsLongEndWithL() {
            var json = new JSONObject("""
                    {
                        "person": {
                          "id": 1,
                          "name": "Ahmed",
                          "phoneNumber": "2487954879L"
                        }
                      }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getCustomObject("person", Person.class))
                    .isInstanceOf(Person.class)
                    .isEqualTo(new Person(
                            1,
                            "Ahmed",
                            2487954879L
                    ));
        }

        @Test
        void getCustomObjectMissingSomeData() {
            var json = new JSONObject("""
                    {
                       "person": {
                         "id": 1,
                         "name": "Ahmed",
                         "email": "ahmed@email.com",
                       }
                     }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getCustomObject("person", Person.class))
                    .isInstanceOf(Person.class)
                    .isEqualTo(new Person(
                            1,
                            "Ahmed",
                            "ahmed@email.com",
                            null
                    ));
        }

        @Test
        void getCustomObjectContainsList() {
            var json = new JSONObject("""
                    {
                        "person": {
                          "id": 1,
                          "name": "Ahmed",
                          "email": "ahmed@email.com",
                          "age": 35,
                          "pets": [
                            "aaa",
                            "bbb"
                          ]
                        }
                      }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getCustomObject("person", Person.class))
                    .isInstanceOf(Person.class)
                    .isEqualTo(new Person(
                            1,
                            "Ahmed",
                            "ahmed@email.com",
                            35,
                            List.of("aaa", "bbb")
                    ));
        }

        @Test
        void getCustomObjectContainsMap() {
            var json = new JSONObject("""
                    {
                      "person": {
                        "id": 1,
                        "name": "Ahmed",
                        "email": "ahmed@email.com",
                        "age": 35,
                        "skills": {
                          "foot-boll": "good",
                          "swimming": "not bad"
                        }
                      }
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getCustomObject("person", Person.class))
                    .isInstanceOf(Person.class)
                    .isEqualTo(new Person(
                            1,
                            "Ahmed",
                            "ahmed@email.com",
                            35,
                            Map.of("foot-boll", "good", "swimming", "not bad")
                    ));
        }

        @Test
        void getCustomObjectContainsCalenderObject() throws ParseException {
            var json = new JSONObject("""
                    {
                      "person": {
                        "id": 1,
                        "name": "Ahmed",
                        "email": "ahmed@email.com",
                        "age": 35,
                        "dateOfBirth": "1990-10-15 00:00:00"
                      }
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            var dateFormat = "yyyy-MM-dd HH:mm:ss";
            var timezone = "GMT+2";

            var dateOfBirth = Calendar.getInstance(TimeZone.getTimeZone(timezone));
            dateOfBirth.setTime(new SimpleDateFormat(dateFormat).parse("1990-10-15 00:00:00"));

            assertThat(new JSONReader(jsonLoader)
                    .getCustomObject("person", Person.class, dateFormat, timezone, null))
                    .isInstanceOf(Person.class)
                    .isEqualTo(new Person(
                            1,
                            "Ahmed",
                            "ahmed@email.com",
                            35,
                            dateOfBirth
                    ));
        }

        @Test
        void getCustomObjectUsingCustomParser() {
            var json = new JSONObject("""
                    {
                      "person": {
                        "id": 1,
                        "name": "Ahmed",
                        "dateOfBirth2": "1986-04-08 12:30"
                      }
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            Function<Object, LocalDateTime> pLocalDateTime =
                    obj -> {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        return LocalDateTime.parse(obj.toString(), formatter);
                    };

            assertThat(new JSONReader(jsonLoader).getCustomObject("person", Person.class, Map.of(
                    "dateOfBirth2",
                    pLocalDateTime
            )))
                    .isInstanceOf(Person.class)
                    .isEqualTo(new Person(
                            1,
                            "Ahmed",
                            pLocalDateTime.apply("1986-04-08 12:30")
                    ));
        }

        @Test
        void getCustomObjectWithNestedCustomObject() {
            var json = new JSONObject("""
                    {
                       "person": {
                         "id": 1,
                         "name": "Ahmed",
                         "workAt": {
                           "name": "Test Company",
                           "numOfEmployees": 10
                         }
                       }
                     }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getCustomObject("person", Person.class, null, Company.class))
                    .isInstanceOf(Person.class)
                    .isEqualTo(new Person(
                            1,
                            "Ahmed",
                            new Company(
                                    "Test Company",
                                    10
                            )
                    ));
        }

        @Test
        void getCustomObjectWithNestedCustomObjectAndUsingCustomParser() {
            var json = new JSONObject("""
                    {
                       "person": {
                         "id": 1,
                         "name": "Ahmed",
                         "dateOfBirth2": "1986-04-08 12:30",
                         "workAt": {
                           "name": "Test Company",
                           "numOfEmployees": 10
                         }
                       }
                     }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            var person = new Person();
            Function<Object, LocalDateTime> pLocalDateTime =
                    obj -> {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        return LocalDateTime.parse(obj.toString(), formatter);
                    };

            person.setId(1);
            person.setName("Ahmed");
            person.setDateOfBirth2(pLocalDateTime.apply("1986-04-08 12:30"));
            person.setWorkAt(new Company("Test Company", 10));

            assertThat(new JSONReader(jsonLoader).getCustomObject("person",
                    Person.class,
                    Map.of("dateOfBirth2", pLocalDateTime),
                    Company.class))
                    .isInstanceOf(Person.class)
                    .isEqualTo(person);
        }

        @Test
        void getCustomObjectWithNestedCustomObjectAtList() {
            var json = new JSONObject("""
                    {
                        "person": {
                          "id": 1,
                          "name": "Ahmed",
                          "previousCompanies": [
                            {
                              "name": "Company 1",
                              "numOfEmployees": 1
                            },
                            {
                              "name": "Company 2",
                              "numOfEmployees": 2
                            }
                          ]
                        }
                      }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            var person = new Person();
            person.setId(1);
            person.setName("Ahmed");
            person.setPreviousCompanies(List.of(
                    new Company("Company 1", 1),
                    new Company("Company 2", 2)
            ));

            assertThat(new JSONReader(jsonLoader).getCustomObject("person",
                    Person.class,
                    null,
                    Company.class))
                    .isInstanceOf(Person.class)
                    .isEqualTo(person);
        }

        @Test
        void getCustomObjectUsingWithNestedCustomObjectAtMap() {
            var json = new JSONObject("""
                    {
                         "person": {
                           "id": 1,
                           "name": "Ahmed",
                           "companiesOrder": {
                             "first-one": {
                               "name": "Company 1",
                               "numOfEmployees": 1
                             },
                             "second-one": {
                               "name": "Company 2",
                               "numOfEmployees": 2
                             }
                           }
                         }
                       }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            var person = new Person();
            person.setId(1);
            person.setName("Ahmed");
            person.setCompaniesOrder(Map.of(
                    "first-one", new Company("Company 1", 1),
                    "second-one", new Company("Company 2", 2)
            ));

            assertThat(new JSONReader(jsonLoader).getCustomObject("person",
                    Person.class,
                    null,
                    Company.class))
                    .isInstanceOf(Person.class)
                    .isEqualTo(person);
        }

        @Test
        void getCustomObjectWithNestedCustomObjectAtListAndMap() {
            var json = new JSONObject("""
                    {
                         "person": {
                           "id": 1,
                           "name": "Ahmed",
                           "previousCompanies": [
                            {
                              "name": "Company 1",
                              "numOfEmployees": 1
                            },
                            {
                              "name": "Company 2",
                              "numOfEmployees": 2
                            }
                          ],
                           "companiesOrder": {
                             "first-one": {
                               "name": "Company 1",
                               "numOfEmployees": 1
                             },
                             "second-one": {
                               "name": "Company 2",
                               "numOfEmployees": 2
                             }
                           }
                         }
                       }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            var person = new Person();
            person.setId(1);
            person.setName("Ahmed");
            person.setPreviousCompanies(List.of(
                    new Company("Company 1", 1),
                    new Company("Company 2", 2)
            ));
            person.setCompaniesOrder(Map.of(
                    "first-one", new Company("Company 1", 1),
                    "second-one", new Company("Company 2", 2)
            ));

            assertThat(new JSONReader(jsonLoader).getCustomObject("person",
                    Person.class,
                    null,
                    Company.class))
                    .isInstanceOf(Person.class)
                    .isEqualTo(person);
        }

        @Test
        void getCustomObjectUsingCustomParserWithNestedCustomObjectAtListAndMap() {
            var json = new JSONObject("""
                    {
                         "person": {
                           "id": 1,
                           "name": "Ahmed",
                           "dateOfBirth2": "1986-04-08 12:30",
                           "previousCompanies": [
                            {
                              "name": "Company 1",
                              "numOfEmployees": 1,
                              "establishDate": "1970-04-08 12:30"
                            },
                            {
                              "name": "Company 2",
                              "numOfEmployees": 2,
                              "establishDate": "1988-04-08 12:30"
                            }
                          ],
                           "companiesOrder": {
                             "first-one": {
                               "name": "Company 1",
                               "numOfEmployees": 1,
                               "establishDate": "1970-04-08 12:30"
                             },
                             "second-one": {
                               "name": "Company 2",
                               "numOfEmployees": 2,
                               "establishDate": "1988-04-08 12:30"
                             }
                           }
                         }
                       }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            var person = new Person();
            Function<Object, LocalDateTime> pLocalDateTime =
                    obj -> {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        return LocalDateTime.parse(obj.toString(), formatter);
                    };

            person.setId(1);
            person.setName("Ahmed");
            person.setDateOfBirth2(pLocalDateTime.apply("1986-04-08 12:30"));
            person.setPreviousCompanies(List.of(
                    new Company("Company 1", 1, pLocalDateTime.apply("1970-04-08 12:30")),
                    new Company("Company 2", 2, pLocalDateTime.apply("1988-04-08 12:30"))
            ));
            person.setCompaniesOrder(Map.of(
                    "first-one", new Company("Company 1", 1, pLocalDateTime.apply("1970-04-08 12:30")),
                    "second-one", new Company("Company 2", 2, pLocalDateTime.apply("1988-04-08 12:30"))
            ));

            assertThat(new JSONReader(jsonLoader).getCustomObject("person",
                    Person.class,
                    Map.of("dateOfBirth2", pLocalDateTime, "establishDate", pLocalDateTime),
                    Company.class))
                    .isInstanceOf(Person.class)
                    .isEqualTo(person);
        }

        @Test
        void getCustomObjectContainsVariables() {
            var json = new JSONObject("""
                    {
                      "variables": {
                        "last-name": "Ali"
                      },
                      "person": {
                        "id": 1,
                        "name": "Ahmed ${last-name}",
                        "email": "ahmed@email.com",
                        "age": 35
                      }
                    }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThat(new JSONReader(jsonLoader).getCustomObject("person", Person.class))
                    .isInstanceOf(Person.class)
                    .isEqualTo(new Person(
                            1,
                            "Ahmed Ali",
                            "ahmed@email.com",
                            35
                    ));
        }

        @Test
        void getCustomObjectWithNestedCustomObjectNotPassed() {
            var json = new JSONObject("""
                    {
                       "person": {
                         "id": 1,
                         "name": "Ahmed",
                         "workAt": {
                           "name": "Test Company",
                           "numOfEmployees": 10
                         }
                       }
                     }
                    """);
            when(jsonLoader.getData()).thenReturn(json.toMap());

            assertThatThrownBy(
                    () -> new JSONReader(jsonLoader).getCustomObject("person", Person.class)
            ).isInstanceOf(ClassCastException.class);
        }
    }

    @Nested
    class ReadFromObjectCases {
        @Test
        void getAll() {
            var json = new JSONObject("""
                    {
                      "object": "object"
                    }
                    """);

            assertThat(new JSONReader(json.toMap()).getAll())
                    .isInstanceOf(Object.class)
                    .isEqualTo(Map.of("object", "object"));
        }

        @Test
        void getValueFromMap() {
            var map = Map.of("key1", "01", "key2", "02");

            assertThat(new JSONReader(map).get("key1"))
                    .isEqualTo("01");
        }

        @Test
        void getValueFromList() {
            var list = List.of(0, 1, 2);

            assertThat(new JSONReader(list).get("[0]"))
                    .isEqualTo(0);
        }

        @Test
        void getObjectFromNestedMap() {
            var json = new JSONObject("""
                    {
                      "map": {
                        "sub-map": {
                            "value": "I'm here"
                        }
                      }
                    }
                    """);

            assertThat(new JSONReader(json.toMap()).get("map.sub-map.value"))
                    .isEqualTo("I'm here");
        }

        @Test
        void getValidObjectFromJSONArray() {
            var json = new JSONArray("""
                    [
                       "object1",
                       "object2"
                     ]
                    """);

            assertThat(new JSONReader(json.toList()).get("[1]"))
                    .isInstanceOf(Object.class)
                    .isEqualTo("object2");
        }

        @Test
        void getStringContainsVariablePlaceHolder() {
            var json = new JSONObject("""
                    {
                        "variables": {
                             "don't-change-me": ", I found YOU"
                        },
                       "map": [
                         {
                           "sub-map": [
                             {
                               "value": "I'm here${don't-change-me}"
                             }
                           ]
                         }
                       ]
                     }
                    """);

            assertThat(new JSONReader(json.toMap()).getString("map[0].sub-map[0].value"))
                    .isEqualTo("I'm here, I found YOU");
        }
    }
}