package pl.app.common.mapper;

import java.util.Map;
import java.util.function.BiFunction;

@SuppressWarnings("unchecked")
public interface Merger {
    default <T> T merge(T target, T source) {
        Class<T> targetClass = (Class<T>) target.getClass();
        BiFunction<T, T, T> mapper = getMerger(targetClass);
        if (mapper == null) {
            throw new RuntimeException("Mapper " + getClass().getName() + " has no merger to " + targetClass.getName() + " class");
        }
        return mapper.apply(target, source);
    }

    default <T> BiFunction<T, T, T> getMerger(Class<T> targetClass) {
        return (BiFunction<T, T, T>) getMergers().get(targetClass);
    }

    default <T> void addMerger(Class<T> targetClass, BiFunction<T, T, T> function) {
        getMergers().put(targetClass, function);
    }

    Map<Class<?>, BiFunction<?, ?, ?>> getMergers();
}
