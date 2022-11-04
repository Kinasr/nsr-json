package nsr_json;

import exception.JSONFileException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JSONFileLoaderTest {
    private final String filePath = "src/test/resources/json_test";

    @Test
    void loadFileWithTheExtension() {
        assertThat(JSONFileLoader.getInstance(filePath + ".json"))
                .isInstanceOf(JSONFileLoader.class);
    }

    @Test
    void loadFileWithoutTheExtension() {
        assertThat(JSONFileLoader.getInstance(filePath))
                .isInstanceOf(JSONFileLoader.class);
    }

    @Test
    void getDataFromJSONFile() {
        assertThat(JSONFileLoader.getInstance(filePath).getData())
                .isInstanceOf(Object.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void loadFileWithNullPath(String path) {
        assertThatThrownBy(
                () -> JSONFileLoader.getInstance(path)
        ).isInstanceOf(JSONFileException.class);
    }

    @Test
    void loadNotExistedFile() {
        assertThatThrownBy(
                () -> JSONFileLoader.getInstance("src/test/resources/wrong_file")
        ).isInstanceOf(JSONFileException.class);
    }
}