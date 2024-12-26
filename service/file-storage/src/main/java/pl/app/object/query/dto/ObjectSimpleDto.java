package pl.app.object.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ObjectSimpleDto implements Serializable {
    private ObjectId objectId;
    private String key;
    private ObjectId containerId;
    private Map<String, Object> metadata;

    private Integer size;
    private String storageId;
}