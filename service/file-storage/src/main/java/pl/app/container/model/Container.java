package pl.app.container.model;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "container")
public class Container {
    @Id
    private ObjectId containerId;
    private String name;
    private ContainerType type;
    private RevisionPolicyType revisionPolicyType;

    public Container() {
    }

    public Container(String name, ContainerType type, RevisionPolicyType revisionPolicyType) {
        this.containerId = ObjectId.get();
        this.name = name;
        this.type = type;
        this.revisionPolicyType = revisionPolicyType;
    }

    public static enum ContainerType {
        PUBLIC,
        PROTECTED,
        PRIVATE
    }
}
