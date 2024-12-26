package pl.app.object.query;

import lombok.Getter;
import pl.app.object.query.dto.ObjectFullDto;
import pl.app.object.query.dto.ObjectSimpleDto;

import java.util.Objects;

public enum ObjectDtoName {
    SIMPLE("simple", ObjectSimpleDto.class),
    FULL("full", ObjectFullDto.class);

    @Getter
    private final String name;
    @Getter
    private final Class<?> dtoClass;

    public static final ObjectDtoName DEFAULT_DTO = SIMPLE;

    ObjectDtoName(String name, Class<?> dtoClass) {
        this.name = name;
        this.dtoClass = dtoClass;
    }

    /**
     * @param input the string to match
     * @return the matched DtoType or the default
     */
    public static ObjectDtoName fromString(String input) {
        if (Objects.isNull(input) || input.isBlank()) {
            return DEFAULT_DTO;
        }
        for (ObjectDtoName dtoType : values()) {
            if (dtoType.name.equalsIgnoreCase(input)) {
                return dtoType;
            }
        }
        return DEFAULT_DTO;
    }
}
