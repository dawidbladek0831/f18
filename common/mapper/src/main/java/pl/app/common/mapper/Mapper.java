package pl.app.common.mapper;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public interface Mapper {
    default <T, R> R map(T source, Class<R> destinationClass) {
        if (Objects.isNull(source)) {
            return null;
        }
        if (Objects.equals(source.getClass(), destinationClass)) {
            return (R) source;
        }
        Function<T, R> mapper = getMapper((Class<T>) source.getClass(), destinationClass)
                .orElseThrow(() -> new RuntimeException("Mapper " + getClass().getName() + " has no mapper from " + source.getClass().getName() + " to " + destinationClass.getName()));
        return mapper.apply(source);
    }

    default <T, T2, R> R map(T source, T2 source2, Class<R> destinationClass) {
        if (Objects.isNull(source)) {
            return null;
        }
        if (Objects.equals(source.getClass(), destinationClass)) {
            return (R) source;
        }
        BiFunction<T, T2, R> mapper = getMapper((Class<T>) source.getClass(), (Class<T2>) source2.getClass(), destinationClass)
                .orElseThrow(() -> new RuntimeException("Mapper " + getClass().getName() + " has no mapper from " + source.getClass().getName() + "," + source2.getClass().getName() + " to " + destinationClass.getName()));
        return mapper.apply(source, source2);
    }

    default <T, T2, T3, R> R map(T source, T2 source2, T3 source3, Class<R> destinationClass) {
        if (Objects.isNull(source)) {
            return null;
        }
        if (Objects.equals(source.getClass(), destinationClass)) {
            return (R) source;
        }
        MapperObject4.TriFunction<T, T2, T3, R> mapper = getMapper((Class<T>) source.getClass(), (Class<T2>) source2.getClass(), (Class<T3>) source3.getClass(), destinationClass)
                .orElseThrow(() -> new RuntimeException("Mapper " + getClass().getName() + " has no mapper from " + source.getClass().getName() + "," + source2.getClass().getName() + "," + source3.getClass().getName() + " to " + destinationClass.getName()));
        return mapper.apply(source, source2, source3);
    }

    default <T, R> Optional<Function<T, R>> getMapper(Class<T> sourceClass, Class<R> destinationClass) {
        return getMapperObject2().stream().filter(e ->
                        e.getSourceClass().equals(sourceClass)
                                && e.getDestinationClass().equals(destinationClass)
                ).findAny()
                .map(e -> (Function<T, R>) e.getMapFunction());
    }

    default <T, T2, R> Optional<BiFunction<T, T2, R>> getMapper(Class<T> sourceClass, Class<T2> sourceClass2, Class<R> destinationClass) {
        return getMapperObject3().stream().filter(e ->
                        e.getSourceClass().equals(sourceClass)
                                && e.getSourceClass2().equals(sourceClass2)
                                && e.getDestinationClass().equals(destinationClass)
                ).findAny()
                .map(e -> (BiFunction<T, T2, R>) e.getMapFunction());
    }

    default <T, T2, T3, R> Optional<MapperObject4.TriFunction<T, T2, T3, R>> getMapper(Class<T> sourceClass, Class<T2> sourceClass2, Class<T3> sourceClass3, Class<R> destinationClass) {
        return getMapperObject4().stream().filter(e ->
                        e.getSourceClass().equals(sourceClass)
                                && e.getSourceClass2().equals(sourceClass2)
                                && e.getSourceClass3().equals(sourceClass3)
                                && e.getDestinationClass().equals(destinationClass)
                ).findAny()
                .map(e -> (MapperObject4.TriFunction<T, T2, T3, R>) e.getMapFunction());
    }

    default <T, R> void addMapper(Class<T> source, Class<R> destination, Function<T, R> function) {
        getMapperObject2().add(new MapperObject2<>(source, destination, function));
    }

    default <T, T2, R> void addMapper(Class<T> source, Class<T2> source2, Class<R> destination, BiFunction<T, T2, R> function) {
        getMapperObject3().add(new MapperObject3<>(source, source2, destination, function));
    }

    default <T, T2, T3, R> void addMapper(Class<T> source, Class<T2> source2, Class<T3> source3, Class<R> destination, MapperObject4.TriFunction<T, T2, T3, R> function) {
        getMapperObject4().add(new MapperObject4<>(source, source2, source3, destination, function));
    }

    Set<MapperObject2<?, ?>> getMapperObject2();

    Set<MapperObject3<?, ?, ?>> getMapperObject3();

    Set<MapperObject4<?, ?, ?, ?>> getMapperObject4();

    class MapperObject2<T, R> {
        Class<T> sourceClass;
        Class<R> destinationClass;
        Function<T, R> mapFunction;

        public MapperObject2(Class<T> sourceClass, Class<R> destinationClass, Function<T, R> mapFunction) {
            this.sourceClass = sourceClass;
            this.destinationClass = destinationClass;
            this.mapFunction = mapFunction;
        }

        public Class<T> getSourceClass() {
            return sourceClass;
        }

        public Class<R> getDestinationClass() {
            return destinationClass;
        }

        public Function<T, R> getMapFunction() {
            return mapFunction;
        }
    }

    class MapperObject3<T, T2, R> {
        Class<T> sourceClass;
        Class<T2> sourceClass2;
        Class<R> destinationClass;
        BiFunction<T, T2, R> mapFunction;

        public MapperObject3(Class<T> sourceClass, Class<T2> sourceClass2, Class<R> destinationClass, BiFunction<T, T2, R> mapFunction) {
            this.sourceClass = sourceClass;
            this.sourceClass2 = sourceClass2;
            this.destinationClass = destinationClass;
            this.mapFunction = mapFunction;
        }

        public Class<T> getSourceClass() {
            return sourceClass;
        }

        public Class<T2> getSourceClass2() {
            return sourceClass2;
        }

        public Class<R> getDestinationClass() {
            return destinationClass;
        }

        public BiFunction<T, T2, R> getMapFunction() {
            return mapFunction;
        }
    }

    class MapperObject4<T, T2, T3, R> {
        Class<T> sourceClass;
        Class<T2> sourceClass2;
        Class<T3> sourceClass3;
        Class<R> destinationClass;
        TriFunction<T, T2, T3, R> mapFunction;

        @FunctionalInterface
        public interface TriFunction<T, T2, T3, R> {
            R apply(T t1, T2 t2, T3 t3);

            default <V> TriFunction<T, T2, T3, V> andThen(
                    Function<? super R, ? extends V> after) {
                Objects.requireNonNull(after);
                return (T t1, T2 t2, T3 t3) -> after.apply(apply(t1, t2, t3));
            }
        }

        public MapperObject4(Class<T> sourceClass, Class<T2> sourceClass2, Class<T3> sourceClass3, Class<R> destinationClass, TriFunction<T, T2, T3, R> mapFunction) {
            this.sourceClass = sourceClass;
            this.sourceClass2 = sourceClass2;
            this.sourceClass3 = sourceClass3;
            this.destinationClass = destinationClass;
            this.mapFunction = mapFunction;
        }

        public Class<T> getSourceClass() {
            return sourceClass;
        }

        public Class<T2> getSourceClass2() {
            return sourceClass2;
        }

        public Class<T3> getSourceClass3() {
            return sourceClass3;
        }

        public Class<R> getDestinationClass() {
            return destinationClass;
        }

        public TriFunction<T, T2, T3, R> getMapFunction() {
            return mapFunction;
        }
    }
}
