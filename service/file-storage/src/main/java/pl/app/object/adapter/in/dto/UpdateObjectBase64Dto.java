package pl.app.object.adapter.in.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateObjectBase64Dto implements Serializable {
    @NotNull
    private String content;
    private Map<String, Object> metadata = new HashMap<>();
}
