package pl.app.container.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.container.model.Container;
import pl.app.container.model.RevisionPolicyType;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContainerDto implements Serializable {
    private ObjectId containerId;
    private String name;
    private Container.ContainerType type;
    private RevisionPolicyType revisionPolicyType;
}