package pl.app.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "app.kafka.topic")
@Setter
@Getter
@NoArgsConstructor
public class KafkaTopicConfigurationProperties {
    private Topic containerCreated;
    private Topic containerUpdated;
    private Topic containerDeleted;

    private Topic objectCreated;
    private Topic objectUpdated;
    private Topic objectRemoved;
    private Topic objectDeleted;
    private Topic objectRevisionCreated;
    private Topic objectRevisionDeleted;
    private Topic objectRevisionRestored;

    private Topic storageInitialized;
    private Topic fileStored;
    private Topic fileDeleted;
    private Topic fileCopied;



    public List<Topic> getAllTopics() {
        return List.of(
                containerCreated,
                containerUpdated,
                containerDeleted,

                objectCreated,
                objectUpdated,
                objectRemoved,
                objectDeleted,
                objectRevisionCreated,
                objectRevisionDeleted,
                objectRevisionRestored,

                storageInitialized,
                fileStored,
                fileDeleted,
                fileCopied
        );
    }

    public List<String> getAllTopicNames() {
        return getAllTopics().stream()
                .map(Topic::getTopicNames)
                .flatMap(List::stream)
                .toList();
    }

    @Setter
    @Getter
    public static class Topic {
        private String name;
        private Integer partitions;
        private Boolean dtlTopic;

        public Topic() {
            this.name = "NAME_NOT_CONFIGURED";
            this.partitions = 1;
            this.dtlTopic = true;
        }

        public List<String> getTopicNames() {
            return dtlTopic ? List.of(name, name + ".DTL") : List.of(name);
        }
    }
}
