package pl.app.container.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.app.common.mapper.BaseMapper;
import pl.app.container.model.Container;
import pl.app.container.service.dto.ContainerDto;

@Component
@RequiredArgsConstructor
public class ContainerMapper extends BaseMapper {
    private final ModelMapper modelMapper;

    @PostConstruct
    void init() {
        addMapper(Container.class, ContainerDto.class, e -> modelMapper.map(e, ContainerDto.class));
    }

}