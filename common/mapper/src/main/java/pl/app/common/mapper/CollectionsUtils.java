package pl.app.common.mapper;

import org.hibernate.collection.spi.*;

import java.util.*;
import java.util.function.Function;

public class CollectionsUtils {
    public static <T, V> Optional<T> getElement(Collection<T> c1, V targetValue) {
        return c1.stream()
                .filter(e -> Objects.equals(e, targetValue))
                .findFirst();
    }

    public static <T, V> Optional<T> getElement(Collection<T> c1, V targetValue, Function<T, V> fieldProvider) {
        return c1.stream()
                .filter(e -> Objects.equals(targetValue, fieldProvider.apply(e)))
                .findFirst();
    }

    public static <T> boolean contains(Collection<T> collection, T target) {
        return collection.stream()
                .anyMatch(e -> Objects.equals(e, target));
    }

    public static <T> boolean contains(Collection<T> collection, T target, Function<T, ?> fieldProvider) {
        return collection.stream()
                .anyMatch(e -> Objects.equals(fieldProvider.apply(e), fieldProvider.apply(target)));
    }

    public static <T1, T2> boolean contains(Collection<T1> collection, T2 target, Function<T1, ?> fieldProvider1, Function<T2, ?> fieldProvider2) {
        return collection.stream()
                .anyMatch(e -> Objects.equals(fieldProvider1.apply(e), fieldProvider2.apply(target)));
    }

    @SuppressWarnings("unchecked")
    public static <E, C extends Collection<E>> C createCollectionOfClass(Class<C> collectionType) {
        try {
            // Hibernate collections
            if (collectionType.isAssignableFrom(PersistentBag.class)) {
                return (C) new PersistentBag<E>();
            } else if (collectionType.isAssignableFrom(PersistentList.class)) {
                return (C) new PersistentList<E>();
            } else if (collectionType.isAssignableFrom(PersistentSortedSet.class)) {
                return (C) new PersistentSortedSet<E>();
            } else if (collectionType.isAssignableFrom(PersistentSet.class)) {
                return (C) new PersistentSet<E>();
            } else if (collectionType.isAssignableFrom(PersistentIdentifierBag.class)) {
                return (C) new PersistentIdentifierBag<E>();
            }
            // Java collections
            else if (List.class.isAssignableFrom(collectionType)) {
                return (C) new ArrayList<E>();
            } else if (Set.class.isAssignableFrom(collectionType)) {
                return (C) new LinkedHashSet<>();
            }
            return collectionType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return (C) new ArrayList<E>();
        }
    }
}
