package test_helper;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Accessors(chain = true)
@Data
public class Pet {
    private String name;
    private Integer age;

    public static Pet valueOf(Object obj) {
        var pet = new Pet();

        if (obj instanceof Map<?, ?> map) {
            pet.name = String.valueOf(map.get("name"));
            pet.age = Integer.valueOf(map.get("age").toString());
        }

        return pet;
    }
}
