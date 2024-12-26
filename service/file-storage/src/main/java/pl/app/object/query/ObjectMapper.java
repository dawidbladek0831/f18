package pl.app.object.query;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.app.common.mapper.BaseMapper;
import pl.app.object.application.domain.ObjectAggregate;
import pl.app.object.query.dto.ObjectFullDto;
import pl.app.object.query.dto.ObjectSimpleDto;

@Component
@RequiredArgsConstructor
public class ObjectMapper extends BaseMapper {
    private final ModelMapper modelMapper;

    @PostConstruct
    void init() {
        addMapper(ObjectAggregate.class, ObjectSimpleDto.class, this::mapToObjectDto);
        addMapper(ObjectAggregateQuery.class, ObjectSimpleDto.class, this::mapToObjectDto);
        addMapper(ObjectAggregateQuery.class, ObjectFullDto.class, e -> modelMapper.map(e, ObjectFullDto.class));
    }
    ObjectSimpleDto mapToObjectDto(ObjectAggregate domain) {
        return ObjectSimpleDto.builder()
                .objectId(domain.getObjectId())
                .key(domain.getKey())
                .containerId(domain.getContainerId())
                .metadata(domain.getMetadata())
                .size(domain.getLeadRevision().isPresent() ? domain.getLeadRevision().get().getSize() : 0)
                .storageId(domain.getLeadRevision().isPresent() ? domain.getLeadRevision().get().getStorageId() : "")
                .build();
    }
    ObjectSimpleDto mapToObjectDto(ObjectAggregateQuery domain) {
        return ObjectSimpleDto.builder()
                .objectId(domain.getObjectId())
                .key(domain.getKey())
                .containerId(domain.getContainerId())
                .metadata(domain.getMetadata())
                .size(domain.getLeadRevision().isPresent() ? domain.getLeadRevision().get().getSize() : 0)
                .storageId(domain.getLeadRevision().isPresent() ? domain.getLeadRevision().get().getStorageId() : "")
                .build();
    }

}