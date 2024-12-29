package pl.app.common.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static pl.app.common.mapper.CollectionsUtils.contains;
import static pl.app.common.mapper.CollectionsUtils.createCollectionOfClass;

public class JoinUtils {
    public static <E, C extends Collection<E>> C joinCollections(Join type, C target, C source) {
        if (Objects.isNull(type)) {
            return target;
        }
        switch (type) {
            case LEFT, LEFT_INCLUSIVE -> {
            }
            case LEFT_EXCLUSIVE -> {
                target.removeIf(e1 -> contains(source, e1)); // removeMid
            }
            case RIGHT, RIGHT_INCLUSIVE -> {
                target.removeIf(e1 -> !contains(source, e1)); // removeLeft
                source.stream()
                        .filter(e2 -> !contains(target, e2))
                        .toList()
                        .forEach(target::add); // addRight
            }
            case RIGHT_EXCLUSIVE -> {
                List<E> temp = new ArrayList<>(source.size());
                source.stream()
                        .filter(e2 -> !contains(target, e2))
                        .toList()
                        .forEach(temp::add); // addRight
                target.clear();
                target.addAll(temp);
            }
            case INNER -> {
                target.removeIf(e1 -> !contains(source, e1)); // removeLeft
            }
            case FULL, FULL_OUTER_INCLUSIVE -> {
                source.stream()
                        .filter(e2 -> !contains(target, e2))
                        .toList()
                        .forEach(target::add); // addRight
            }
            case FULL_OUTER_EXCLUSIVE -> {
                source.stream()
                        .filter(e2 -> !contains(target, e2))
                        .toList()
                        .forEach(target::add); // addRight
                target.removeIf(e1 -> contains(source, e1)); // removeMid
            }
        }
        return target;
    }

    public static <E, C extends Collection<E>, V> C joinCollections(Join type, C target, C source, Function<E, V> fieldProvider) {
        if (Objects.isNull(type)) {
            return target;
        }
        if (Objects.isNull(fieldProvider)) {
            return joinCollectionsInNew(type, target, source);
        }
        switch (type) {
            case LEFT, LEFT_INCLUSIVE -> {
            }
            case LEFT_EXCLUSIVE -> {
                target.removeIf(e1 -> contains(source, e1, fieldProvider)); // removeMid
            }
            case RIGHT, RIGHT_INCLUSIVE -> {
                target.removeIf(e1 -> !contains(source, e1, fieldProvider)); // removeLeft
                source.stream()
                        .filter(e2 -> !contains(target, e2, fieldProvider))
                        .toList()
                        .forEach(target::add); // addRight
            }
            case RIGHT_EXCLUSIVE -> {
                List<E> temp = new ArrayList<>(source.size());
                source.stream()
                        .filter(e2 -> !contains(target, e2, fieldProvider))
                        .toList()
                        .forEach(temp::add); // addRight
                target.clear();
                target.addAll(temp);
            }
            case INNER -> {
                target.removeIf(e1 -> !contains(source, e1, fieldProvider)); // removeLeft
            }
            case FULL, FULL_OUTER_INCLUSIVE -> {
                source.stream()
                        .filter(e2 -> !contains(target, e2, fieldProvider))
                        .toList()
                        .forEach(target::add); // addRight
            }
            case FULL_OUTER_EXCLUSIVE -> {
                source.stream()
                        .filter(e2 -> !contains(target, e2, fieldProvider))
                        .toList()
                        .forEach(target::add); // addRight
                target.removeIf(e1 -> contains(source, e1, fieldProvider)); // removeMid
            }
        }
        return target;
    }

    @SuppressWarnings("unchecked")
    public static <E, C extends Collection<E>> C joinCollectionsInNew(Join type, C c1, C c2) {
        C result = createCollectionOfClass((Class<C>) c1.getClass());
        if (Objects.isNull(type)) {
            return result;
        }
        switch (type) {
            case LEFT, LEFT_INCLUSIVE -> {
                addLeft(result, c1, c2);
                addMid(result, c1, c2);
            }
            case LEFT_EXCLUSIVE -> addLeft(result, c1, c2);
            case RIGHT, RIGHT_INCLUSIVE -> {
                addMid(result, c1, c2);
                addRight(result, c1, c2);
            }
            case RIGHT_EXCLUSIVE -> addRight(result, c1, c2);
            case INNER -> addMid(result, c1, c2);
            case FULL, FULL_OUTER_INCLUSIVE -> {
                addLeft(result, c1, c2);
                addMid(result, c1, c2);
                addRight(result, c1, c2);
            }
            case FULL_OUTER_EXCLUSIVE -> {
                addLeft(result, c1, c2);
                addRight(result, c1, c2);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <E, C extends Collection<E>, V> C joinCollectionsInNew(Join type, C c1, C c2, Function<E, V> fieldProvider) {
        if (Objects.isNull(fieldProvider)) {
            return joinCollectionsInNew(type, c1, c2);
        }
        C result = createCollectionOfClass((Class<C>) c1.getClass());
        if (Objects.isNull(type)) {
            return result;
        }
        switch (type) {
            case LEFT, LEFT_INCLUSIVE -> {
                addLeft(result, c1, c2, fieldProvider);
                addMid(result, c1, c2, fieldProvider);
            }
            case LEFT_EXCLUSIVE -> addLeft(result, c1, c2, fieldProvider);
            case RIGHT, RIGHT_INCLUSIVE -> {
                addMid(result, c1, c2, fieldProvider);
                addRight(result, c1, c2, fieldProvider);
            }
            case RIGHT_EXCLUSIVE -> addRight(result, c1, c2, fieldProvider);
            case INNER -> addMid(result, c1, c2, fieldProvider);
            case FULL, FULL_OUTER_INCLUSIVE -> {
                addLeft(result, c1, c2, fieldProvider);
                addMid(result, c1, c2, fieldProvider);
                addRight(result, c1, c2, fieldProvider);
            }
            case FULL_OUTER_EXCLUSIVE -> {
                addLeft(result, c1, c2, fieldProvider);
                addRight(result, c1, c2, fieldProvider);
            }
        }
        return result;
    }

    static <E, C extends Collection<E>> void addLeft(C result, C c1, C c2) {
        c1.stream()
                .filter(e1 -> !contains(c2, e1))
                .forEach(result::add);
    }

    static <E, C extends Collection<E>, V> void addLeft(C result, C c1, C c2, Function<E, V> fieldProvider) {
        c1.stream()
                .filter(e1 -> !contains(c2, e1, fieldProvider))
                .forEach(result::add);
    }

    static <E, C extends Collection<E>> void addMid(C result, C c1, C c2) {
        c1.stream()
                .filter(e1 -> contains(c2, e1))
                .forEach(result::add);
    }

    static <E, C extends Collection<E>, V> void addMid(C result, C c1, C c2, Function<E, V> fieldProvider) {
        c1.stream()
                .filter(e1 -> contains(c2, e1, fieldProvider))
                .forEach(result::add);
    }

    static <E, C extends Collection<E>> void addRight(C result, C c1, C c2) {
        c2.stream()
                .filter(e2 -> !contains(c1, e2))
                .forEach(result::add);
    }

    static <E, C extends Collection<E>, V> void addRight(C result, C c1, C c2, Function<E, V> fieldProvider) {
        c2.stream()
                .filter(e2 -> !contains(c1, e2, fieldProvider))
                .forEach(result::add);
    }
}
