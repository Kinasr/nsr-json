package nsr_json;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JSONTest {

    @Test
    void readFromFile() {
        var json = new JSON("src/test/resources/json_test.json");

        assertThat(json.read().get("test"))
                .isEqualTo("test");
    }
}