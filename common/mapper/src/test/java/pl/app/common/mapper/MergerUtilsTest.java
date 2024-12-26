package pl.app.common.mapper;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;

class MergerUtilsTest {

    private final BiFunction<TestClass, TestClass, TestClass> mergeFunction = (target, source) -> {
        if (source == null || target == null) {
            return target;
        }

        if (source.getName() != null) {
            target.setName(source.getName());
        }
        return target;
    };

    @Test
    void testLeftInclusiveMerge() {
        List<TestClass> r1 = MergerUtils.mergeCollectionsInNew(Join.LEFT_INCLUSIVE,
                List.of(new TestClass(11L, "Ala"), new TestClass(12L, "Zosia")),
                List.of(new TestClass(12L, "Zosia2"), new TestClass(13L, "Aga")),
                mergeFunction
        );
        assertThat(r1).hasSize(2);
        assertThat(r1.stream()
                .filter(e -> Objects.equals(e.getId(), 12L))
                .findAny().get().getName())
                .isEqualTo("Zosia");
    }

    @Test
    void testLeftInclusiveMergeByField() {
        List<TestClass> r1 = MergerUtils.mergeCollectionsInNew(Join.LEFT_INCLUSIVE,
                List.of(new TestClass(11L, "Ala"), new TestClass(12L, "Zosia")),
                List.of(new TestClass(12L, "Zosia2"), new TestClass(13L, "Aga")),
                mergeFunction,
                TestClass::getId
        );
        assertThat(r1).hasSize(2);
        assertThat(r1.stream()
                .filter(e -> Objects.equals(e.getId(), 12L))
                .findAny().get().getName())
                .isEqualTo("Zosia2");
    }

    @Test
    void testLeftExclusiveMerge() {
        List<TestClass> r1 = MergerUtils.mergeCollectionsInNew(Join.LEFT_EXCLUSIVE,
                List.of(new TestClass(11L, "Ala"), new TestClass(12L, "Zosia")),
                List.of(new TestClass(12L, "Zosia2"), new TestClass(13L, "Aga")),
                mergeFunction
        );
        assertThat(r1).hasSize(2);
        assertThat(r1.stream()
                .filter(e -> Objects.equals(e.getId(), 12L))
                .findAny().get().getName())
                .isEqualTo("Zosia");
    }

    @Test
    void testLeftExclusiveMergeByField() {
        List<TestClass> r1 = MergerUtils.mergeCollectionsInNew(Join.LEFT_EXCLUSIVE,
                List.of(new TestClass(11L, "Ala"), new TestClass(12L, "Zosia")),
                List.of(new TestClass(12L, "Zosia2"), new TestClass(13L, "Aga")),
                mergeFunction,
                TestClass::getId
        );
        assertThat(r1).hasSize(1);
    }

    @Test
    void testRightInclusiveMerge() {
        List<TestClass> r1 = MergerUtils.mergeCollectionsInNew(Join.RIGHT_INCLUSIVE,
                List.of(new TestClass(11L, "Ala"), new TestClass(12L, "Zosia")),
                List.of(new TestClass(12L, "Zosia2"), new TestClass(13L, "Aga")),
                mergeFunction
        );
        assertThat(r1).hasSize(2);
        assertThat(r1.stream()
                .filter(e -> Objects.equals(e.getId(), 12L))
                .findAny().get().getName())
                .isEqualTo("Zosia2");
    }

    @Test
    void testRightInclusiveMergeByField() {
        List<TestClass> r1 = MergerUtils.mergeCollectionsInNew(Join.RIGHT_INCLUSIVE,
                List.of(new TestClass(11L, "Ala"), new TestClass(12L, "Zosia")),
                List.of(new TestClass(12L, "Zosia2"), new TestClass(13L, "Aga")),
                mergeFunction,
                TestClass::getId
        );
        assertThat(r1).hasSize(2);
        assertThat(r1.stream()
                .filter(e -> Objects.equals(e.getId(), 12L))
                .findAny().get().getName())
                .isEqualTo("Zosia2");
        assertThat(r1.stream()
                .filter(e -> Objects.equals(e.getId(), 13L))
                .findAny().get().getName())
                .isEqualTo("Aga");
    }

    @Test
    void testRightInclusiveMergeByNullField() {
        List<TestClass> r1 = MergerUtils.mergeCollectionsInNew(Join.RIGHT_INCLUSIVE,
                List.of(new TestClass(11L, "Ala"), new TestClass(12L, "Zosia")),
                List.of(new TestClass(12L, "Zosia2"), new TestClass(13L, "Aga"),
                        new TestClass(null, "Aga2"), new TestClass(null, "Aga2")),
                mergeFunction,
                TestClass::getId
        );
        assertThat(r1).hasSize(4);
        assertThat(r1.stream()
                .filter(e -> Objects.equals(e.getId(), 12L))
                .findAny().get().getName())
                .isEqualTo("Zosia2");
    }

    @Test
    void testInnerMergeByField() {
        List<TestClass> r1 = MergerUtils.mergeCollectionsInNew(Join.INNER,
                List.of(new TestClass(11L, "Ala"), new TestClass(12L, "Zosia")),
                List.of(new TestClass(12L, "Zosia2"), new TestClass(13L, "Aga")),
                mergeFunction,
                TestClass::getId
        );
        assertThat(r1).hasSize(1);
        assertThat(r1.stream()
                .filter(e -> Objects.equals(e.getId(), 12L))
                .findAny().get().getName())
                .isEqualTo("Zosia2");
    }
}