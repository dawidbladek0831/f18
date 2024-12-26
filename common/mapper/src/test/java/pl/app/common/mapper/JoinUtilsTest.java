package pl.app.common.mapper;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class JoinUtilsTest {

    @Test
    void testLeftInclusiveJoin() {
        List<TestClass> r1 = JoinUtils.joinCollectionsInNew(Join.LEFT_INCLUSIVE,
                List.of(new TestClass(11L, "Ala"), new TestClass(12L, "Zosia")),
                List.of(new TestClass(12L, "Zosia2"), new TestClass(13L, "Aga"))
        );
        assertThat(r1).hasSize(2);
        assertThat(r1.stream()
                .filter(e -> Objects.equals(e.getId(), 12L))
                .findAny().get().getName())
                .isEqualTo("Zosia");
    }
    @Test
    void testLeftInclusiveJoinByField() {
        List<TestClass> r1 = JoinUtils.joinCollectionsInNew(Join.LEFT_INCLUSIVE,
                List.of(new TestClass(11L, "Ala"), new TestClass(12L, "Zosia")),
                List.of(new TestClass(12L, "Zosia2"), new TestClass(13L, "Aga")),
                TestClass::getId
        );
        assertThat(r1).hasSize(2);
        assertThat(r1.stream()
                .filter(e -> Objects.equals(e.getId(), 12L))
                .findAny().get().getName())
                .isEqualTo("Zosia");
    }
    @Test
    void testLeftExclusiveJoin() {
        List<TestClass> r1 = JoinUtils.joinCollectionsInNew(Join.LEFT_EXCLUSIVE,
                List.of(new TestClass(11L, "Ala"), new TestClass(12L, "Zosia")),
                List.of(new TestClass(12L, "Zosia2"), new TestClass(13L, "Aga"))
        );
        assertThat(r1).hasSize(2);
        assertThat(r1.stream()
                .filter(e -> Objects.equals(e.getId(), 12L))
                .findAny().get().getName())
                .isEqualTo("Zosia");

        List<TestClass> r2 = JoinUtils.joinCollectionsInNew(Join.LEFT_EXCLUSIVE,
                List.of(new TestClass(11L, "Ala"), new TestClass(12L, "Zosia")),
                List.of(new TestClass(12L, "Zosia"), new TestClass(13L, "Aga"))
        );
        assertThat(r2).hasSize(1);
    }
    @Test
    void testLeftExclusiveJoinByField() {
        List<TestClass> r1 = JoinUtils.joinCollectionsInNew(Join.LEFT_EXCLUSIVE,
                List.of(new TestClass(11L, "Ala"), new TestClass(12L, "Zosia")),
                List.of(new TestClass(12L, "Zosia2"), new TestClass(13L, "Aga")),
                TestClass::getId
        );
        assertThat(r1).hasSize(1);
        assertThat(r1.get(0).getName() )
                .isEqualTo("Ala");
    }


    @Test
    void testRightInclusiveJoin() {
        List<TestClass> r1 = JoinUtils.joinCollectionsInNew(Join.RIGHT_INCLUSIVE,
                List.of(new TestClass(11L, "Ala"), new TestClass(12L, "Zosia")),
                List.of(new TestClass(12L, "Zosia2"), new TestClass(13L, "Aga"))
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
    void testRightInclusiveJoinByField() {
        List<TestClass> r1 = JoinUtils.joinCollectionsInNew(Join.RIGHT_INCLUSIVE,
                List.of(new TestClass(11L, "Ala"), new TestClass(12L, "Zosia")),
                List.of(new TestClass(12L, "Zosia2"), new TestClass(13L, "Aga")),
                TestClass::getId
        );
        assertThat(r1).hasSize(2);
        assertThat(r1.stream()
                .filter(e -> Objects.equals(e.getId(), 12L))
                .findAny().get().getName())
                .isEqualTo("Zosia");
        assertThat(r1.stream()
                .filter(e -> Objects.equals(e.getId(), 13L))
                .findAny().get().getName())
                .isEqualTo("Aga");
    }
    @Test
    void testRightExclusiveJoin() {
        List<TestClass> r1 = JoinUtils.joinCollectionsInNew(Join.RIGHT_EXCLUSIVE,
                List.of(new TestClass(11L, "Ala"), new TestClass(12L, "Zosia")),
                List.of(new TestClass(12L, "Zosia2"), new TestClass(13L, "Aga"))
        );
        assertThat(r1).hasSize(2);
    }
    @Test
    void testRightExclusiveJoinByField() {
        List<TestClass> r1 = JoinUtils.joinCollectionsInNew(Join.RIGHT_EXCLUSIVE,
                List.of(new TestClass(11L, "Ala"), new TestClass(12L, "Zosia")),
                List.of(new TestClass(12L, "Zosia2"), new TestClass(13L, "Aga")),
                TestClass::getId
        );
        assertThat(r1).hasSize(1);
        assertThat(r1.get(0).getName())
                .isEqualTo("Aga");
    }

    @Test
    void testInnerJoin() {
        List<TestClass> r1 = JoinUtils.joinCollectionsInNew(Join.INNER,
                List.of(new TestClass(11L, "Ala"), new TestClass(12L, "Zosia")),
                List.of(new TestClass(12L, "Zosia2"), new TestClass(13L, "Aga"))
        );
        assertThat(r1).hasSize(0);
    }
    @Test
    void testInnerJoinByField() {
        List<TestClass> r1 = JoinUtils.joinCollectionsInNew(Join.INNER,
                List.of(new TestClass(11L, "Ala"), new TestClass(12L, "Zosia")),
                List.of(new TestClass(12L, "Zosia2"), new TestClass(13L, "Aga")),
                TestClass::getId
        );
        assertThat(r1).hasSize(1);
        assertThat(r1.get(0).getName())
                .isEqualTo("Zosia");
    }
    @Test
    void testFullJoin() {
        List<TestClass> r1 = JoinUtils.joinCollectionsInNew(Join.FULL,
                List.of(new TestClass(11L, "Ala"), new TestClass(12L, "Zosia")),
                List.of(new TestClass(12L, "Zosia2"), new TestClass(13L, "Aga"))
        );
        assertThat(r1).hasSize(4);
    }
    @Test
    void testFullJoinByField() {
        List<TestClass> r1 = JoinUtils.joinCollectionsInNew(Join.FULL,
                List.of(new TestClass(11L, "Ala"), new TestClass(12L, "Zosia")),
                List.of(new TestClass(12L, "Zosia2"), new TestClass(13L, "Aga")),
                TestClass::getId
        );
        assertThat(r1).hasSize(3);
    }
    @Test
    void testFullExclusiveJoin() {
        List<TestClass> r1 = JoinUtils.joinCollectionsInNew(Join.FULL_OUTER_EXCLUSIVE,
                List.of(new TestClass(11L, "Ala"), new TestClass(12L, "Zosia")),
                List.of(new TestClass(12L, "Zosia2"), new TestClass(13L, "Aga"))
        );
        assertThat(r1).hasSize(4);
    }
    @Test
    void testFullExclusiveJoinByField() {
        List<TestClass> r1 = JoinUtils.joinCollectionsInNew(Join.FULL_OUTER_EXCLUSIVE,
                List.of(new TestClass(11L, "Ala"), new TestClass(12L, "Zosia")),
                List.of(new TestClass(12L, "Zosia2"), new TestClass(13L, "Aga")),
                TestClass::getId
        );
        assertThat(r1).hasSize(2);
    }
}