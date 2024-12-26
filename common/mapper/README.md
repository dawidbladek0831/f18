# Mapper

## Table of Contents

* [General Info](#general-information)
* [Usage](#usage)

## General Information

Jar contains classes to map objects, such as: JPA entities, DTO, etc..

## Usage

### Example of [Mapper.java](src%2Fmain%2Fjava%2Fpl%2Fapp%2Fcommon%2Fmapper%2FMapper.java)

1. Defining mapper.

```java
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.app.common.mapper.Mapper;
import pl.app.common.shared.dto.BaseDto;
import pl.app.learning.organization.dto.OrganizationDto;
import pl.app.learning.organization.model.OrganizationEntity;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Getter
@Component
@RequiredArgsConstructor
public class OrganizationMapper implements Mapper {
    private final ModelMapper modelMapper;

    private final Map<AbstractMap.SimpleEntry<Class<?>, Class<?>>, Function<?, ?>> mappers = new HashMap<>();

    @PostConstruct
    void init() {
        addMapper(OrganizationEntity.class, BaseDto.class, e -> modelMapper.map(e, BaseDto.class));
        addMapper(OrganizationEntity.class, OrganizationDto.class, e -> modelMapper.map(e, OrganizationDto.class));
        addMapper(OrganizationDto.class, OrganizationEntity.class, e -> modelMapper.map(e, OrganizationEntity.class));
    }
}
```

2. Usage

```java
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.app.learning.organization.dto.OrganizationDto;
import pl.app.learning.organization.mapper.OrganizationMapper;
import pl.app.learning.organization.model.OrganizationEntity;
import pl.app.learning.organization.persistence.OrganizationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Getter
class OrganizationQueryServiceImpl {
    private final OrganizationRepository repository;
    private final OrganizationMapper mapper;
    
    public List<OrganizationDto> fetchAll() {
        List<OrganizationEntity> entities = getRepository().findAll();
        return entities.stream()
                .map(entity -> mapper.map(entity, OrganizationDto.class))
                .collect(Collectors.toList());
    }
}
```
