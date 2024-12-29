package pl.app.common.mapper;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static pl.app.common.mapper.CollectionsUtils.*;

public class MergerUtils {
    public static <E, C extends Collection<E>> C mergeCollections(Join type,
                                                                  C target, C source,
                                                                  BiFunction<E, E, E> merger) {
        if (Objects.isNull(type)) {
            return target;
        }
        switch (type) {
            case LEFT, LEFT_INCLUSIVE -> {
                mergeMid(target, source, merger);
            }
            case LEFT_EXCLUSIVE -> {
                target.removeIf(targetElement -> contains(source, targetElement)); // removeMid
            }
            case RIGHT, RIGHT_INCLUSIVE, FULL, FULL_OUTER_INCLUSIVE -> {
                mergeMid(target, source, merger);
                target.addAll(source.stream()
                        .filter(sourceElement -> !contains(target, sourceElement))
                        .toList()); // addRight
            }
            case RIGHT_EXCLUSIVE -> {
                List<E> temp = new ArrayList<>(source.size());
                source.stream()
                        .filter(sourceElement -> !contains(target, sourceElement))
                        .forEach(temp::add); // addRight
                target.clear();
                target.addAll(temp);
            }
            case INNER -> {
                mergeMid(target, source, merger);
                target.removeIf(targetElement -> !contains(source, targetElement)); // removeLeft
            }
            case FULL_OUTER_EXCLUSIVE -> {
                target.addAll(source.stream()
                        .filter(sourceElement -> !contains(target, sourceElement))
                        .toList()); // addRight
                target.removeIf(targetElement -> contains(source, targetElement)); // removeMid
            }
        }
        return target;
    }

    public static <E, C extends Collection<E>, V> C mergeCollections(Join type,
                                                                     C target, C source,
                                                                     BiFunction<E, E, E> merger,
                                                                     Function<E, V> fieldProvider) {
        if (Objects.isNull(type)) {
            return target;
        }
        if (Objects.isNull(fieldProvider)) {
            return mergeCollections(type, target, source, merger);
        }
        switch (type) {
            case LEFT, LEFT_INCLUSIVE -> {
                mergeMid(target, source, merger, fieldProvider);
            }
            case LEFT_EXCLUSIVE -> {
                target.removeIf(targetElement -> contains(source, targetElement, fieldProvider)); // removeMid
            }
            case RIGHT, RIGHT_INCLUSIVE,
                    FULL, FULL_OUTER_INCLUSIVE -> {
                target.removeIf(targetElement -> !contains(source, targetElement, fieldProvider)); // removeLeft
                mergeMid(target, source, merger, fieldProvider);
                target.addAll(source.stream()
                        .filter(sourceElement -> !contains(target, sourceElement, fieldProvider))
                        .toList()); // addRight
            }
            case RIGHT_EXCLUSIVE -> {
                List<E> temp = new ArrayList<>(source.size());
                temp.addAll(source.stream()
                        .filter(sourceElement -> !contains(target, sourceElement, fieldProvider))
                        .toList()); // addRight to temp
                target.clear();
                target.addAll(temp);
            }
            case INNER -> {
                mergeMid(target, source, merger, fieldProvider);
                target.removeIf(targetElement -> !contains(source, targetElement, fieldProvider)); // removeLeft
            }
            case FULL_OUTER_EXCLUSIVE -> {
                target.addAll(source.stream()
                        .filter(sourceElement -> !contains(target, sourceElement, fieldProvider))
                        .toList()); // addRight
                target.removeIf(targetElement -> contains(source, targetElement, fieldProvider)); // removeMid
            }
        }
        return target;
    }

    public static <E, C extends Collection<E>, E2, C2 extends Collection<E2>, V> C mergeCollections(Join type, C target, C2 source,
                                                                                                    BiFunction<E, E2, E> merger, Supplier<E> targetNewInstance,
                                                                                                    Function<E, V> targetFieldProvider, Function<E2, V> sourceFieldProvider) {
        if (Objects.isNull(type) || Objects.isNull(targetFieldProvider) || Objects.isNull(sourceFieldProvider)) {
            return target;
        }
        switch (type) {
            case LEFT, LEFT_INCLUSIVE -> {
                mergeMid(target, source, merger, targetFieldProvider, sourceFieldProvider);
            }
            case LEFT_EXCLUSIVE -> {
                target.removeIf(targetElement -> contains(source, targetElement, sourceFieldProvider, targetFieldProvider)); // removeMid
            }
            case RIGHT, RIGHT_INCLUSIVE,
                    FULL, FULL_OUTER_INCLUSIVE -> {
                target.removeIf(targetElement -> !contains(source, targetElement, sourceFieldProvider, targetFieldProvider)); // removeLeft
                mergeMid(target, source, merger, targetFieldProvider, sourceFieldProvider);
                target.addAll(source.stream()
                        .filter(sourceElement -> !contains(target, sourceElement, targetFieldProvider, sourceFieldProvider))
                        .map(sourceElement -> merger.apply(targetNewInstance.get(), sourceElement))
                        .toList()); // addRight
            }
            case RIGHT_EXCLUSIVE -> {
                List<E> temp = new ArrayList<>(source.size());
                source.stream()
                        .filter(sourceElement -> !contains(target, sourceElement, targetFieldProvider, sourceFieldProvider))
                        .map(sourceElement -> merger.apply(targetNewInstance.get(), sourceElement))
                        .forEach(temp::add); // addRight
                target.clear();
                target.addAll(temp);
            }
            case INNER -> {
                mergeMid(target, source, merger, targetFieldProvider, sourceFieldProvider);
                target.removeIf(targetElement -> !contains(source, targetElement, sourceFieldProvider, targetFieldProvider)); // removeLeft
            }
            case FULL_OUTER_EXCLUSIVE -> {
                target.addAll(source.stream()
                        .filter(sourceElement -> !contains(target, sourceElement, targetFieldProvider, sourceFieldProvider))
                        .map(sourceElement -> merger.apply(targetNewInstance.get(), sourceElement))
                        .toList()); // addRight
                target.removeIf(targetElement -> contains(source, targetElement, sourceFieldProvider, targetFieldProvider)); // removeMid
            }
        }
        return target;
    }

    @SuppressWarnings("unchecked")
    public static <E, C extends Collection<E>> C mergeCollectionsInNew(Join type,
                                                                       C c1, C c2,
                                                                       BiFunction<E, E, E> merger) {
        C result = createCollectionOfClass((Class<C>) c1.getClass());
        if (Objects.isNull(type)) {
            return result;
        }
        switch (type) {
            case LEFT, LEFT_INCLUSIVE -> {
                JoinUtils.addLeft(result, c1, c2);
                mergeMid(result, c1, c2, merger);
            }
            case LEFT_EXCLUSIVE -> JoinUtils.addLeft(result, c1, c2);
            case RIGHT, RIGHT_INCLUSIVE -> {
                mergeMid(result, c1, c2, merger);
                JoinUtils.addRight(result, c1, c2);
            }
            case RIGHT_EXCLUSIVE -> JoinUtils.addRight(result, c1, c2);
            case INNER -> mergeMid(result, c1, c2, merger);
            case FULL, FULL_OUTER_INCLUSIVE -> {
                JoinUtils.addLeft(result, c1, c2);
                mergeMid(result, c1, c2, merger);
                JoinUtils.addRight(result, c1, c2);
            }
            case FULL_OUTER_EXCLUSIVE -> {
                JoinUtils.addLeft(result, c1, c2);
                JoinUtils.addRight(result, c1, c2);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <E, C extends Collection<E>, V> C mergeCollectionsInNew(Join type,
                                                                          C c1, C c2,
                                                                          BiFunction<E, E, E> merger,
                                                                          Function<E, V> fieldProvider) {
        if (Objects.isNull(fieldProvider)) {
            return mergeCollectionsInNew(type, c1, c2, merger);
        }
        C result = createCollectionOfClass((Class<C>) c1.getClass());
        if (Objects.isNull(type)) {
            return result;
        }
        switch (type) {
            case LEFT, LEFT_INCLUSIVE -> {
                JoinUtils.addLeft(result, c1, c2, fieldProvider);
                mergeMid(result, c1, c2, merger, fieldProvider);
            }
            case LEFT_EXCLUSIVE -> JoinUtils.addLeft(result, c1, c2, fieldProvider);
            case RIGHT, RIGHT_INCLUSIVE -> {
                mergeMid(result, c1, c2, merger, fieldProvider);
                JoinUtils.addRight(result, c1, c2, fieldProvider);
            }
            case RIGHT_EXCLUSIVE -> JoinUtils.addRight(result, c1, c2, fieldProvider);
            case INNER -> mergeMid(result, c1, c2, merger, fieldProvider);
            case FULL, FULL_OUTER_INCLUSIVE -> {
                JoinUtils.addLeft(result, c1, c2, fieldProvider);
                mergeMid(result, c1, c2, merger, fieldProvider);
                JoinUtils.addRight(result, c1, c2, fieldProvider);
            }
            case FULL_OUTER_EXCLUSIVE -> {
                JoinUtils.addLeft(result, c1, c2, fieldProvider);
                JoinUtils.addRight(result, c1, c2, fieldProvider);
            }
        }
        return result;
    }

    private static <E, C extends Collection<E>> void mergeMid(C result,
                                                              C c1, C c2,
                                                              BiFunction<E, E, E> merger) {
        c1.stream().filter(e1 -> contains(c2, e1))
                .map(e1 -> {
                    Optional<E> e2 = getElement(c2, e1);
                    return e2.map(e -> merger.apply(e1, e)).orElse(e1);
                }).forEach(result::add);
    }

    private static <E, C extends Collection<E>, V> void mergeMid(C result,
                                                                 C c1, C c2,
                                                                 BiFunction<E, E, E> merger,
                                                                 Function<E, V> fieldProvider) {
        c1.stream().filter(e1 -> contains(c2, e1, fieldProvider))
                .map(e1 -> {
                    Optional<E> e2 = getElement(c2, fieldProvider.apply(e1), fieldProvider);
                    return e2.map(e -> merger.apply(e1, e)).orElse(e1);
                }).forEach(result::add);
    }

    private static <E, C extends Collection<E>> void mergeMid(C target, C source,
                                                              BiFunction<E, E, E> merger) {
        target.stream().filter(targetElement -> contains(source, targetElement))
                .forEach(targetElement -> {
                    Optional<E> sourceElement = getElement(source, targetElement);
                    sourceElement.map(e -> merger.apply(targetElement, e));
                });
    }

    private static <E, C extends Collection<E>, V> void mergeMid(C target, C source,
                                                                 BiFunction<E, E, E> merger,
                                                                 Function<E, V> fieldProvider) {
        target.stream().filter(targetElement -> contains(source, targetElement, fieldProvider))
                .forEach(targetElement -> {
                    Optional<E> sourceElement = getElement(source, fieldProvider.apply(targetElement), fieldProvider);
                    sourceElement.map(e -> merger.apply(targetElement, e));
                });
    }

    private static <E, C extends Collection<E>, E2, C2 extends Collection<E2>, V> void mergeMid(C target, C2 source,
                                                                                                BiFunction<E, E2, E> merger,
                                                                                                Function<E, V> targetFieldProvider, Function<E2, V> sourceFieldProvider) {
        target.stream().filter(targetElement -> contains(source, targetElement, sourceFieldProvider, targetFieldProvider))
                .forEach(targetElement -> {
                    Optional<E2> sourceElement = getElement(source, targetFieldProvider.apply(targetElement), sourceFieldProvider);
                    sourceElement.map(e -> merger.apply(targetElement, e));
                });
    }
}
