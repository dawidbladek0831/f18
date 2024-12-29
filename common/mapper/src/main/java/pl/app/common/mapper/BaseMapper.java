package pl.app.common.mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public abstract class BaseMapper implements
        Mapper,
        Merger {
    private static final Map<Class<?>, BiFunction<?, ?, ?>> mergers = new HashMap<>();
    private static final Set<MapperObject2<?, ?>> mapperObject2s = ConcurrentHashMap.newKeySet();
    private static final Set<MapperObject3<?, ?, ?>> mapperObject3s = ConcurrentHashMap.newKeySet();
    private static final Set<MapperObject4<?, ?, ?, ?>> mapperObject4s = ConcurrentHashMap.newKeySet();

    @Override
    public Set<MapperObject2<?, ?>> getMapperObject2() {
        return mapperObject2s;
    }

    @Override
    public Set<MapperObject3<?, ?, ?>> getMapperObject3() {
        return mapperObject3s;
    }

    @Override
    public Set<MapperObject4<?, ?, ?, ?>> getMapperObject4() {
        return mapperObject4s;
    }

    @Override
    public Map<Class<?>, BiFunction<?, ?, ?>> getMergers() {
        return mergers;
    }
}
