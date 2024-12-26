package pl.app.common.mapper;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;

class CollectionsUtilsTest {

    @Test
    void test_contains() {
        // given
        TestClass t1 = new TestClass(0L,"Ala");
        TestClass t2 = new TestClass(1L,"Zosia");
        TestClass t3 = new TestClass(2L,"Aga");
        List<TestClass> c1 = List.of(t1, t2, t3);

        TestClass t4 = new TestClass(4L,"Aga");
        // when
        // then
        assertThat(CollectionsUtils.contains(c1, t1)).isTrue();
        assertThat(CollectionsUtils.contains(c1, t4)).isFalse();
    }
    @Test
    void test_contains_byField() {
        // given
        TestClass t1 = new TestClass(0L,"Ala");
        TestClass t2 = new TestClass(1L,"Zosia");
        TestClass t3 = new TestClass(2L,"Aga");
        List<TestClass> c1 = List.of(t1, t2, t3);

        TestClass t4 = new TestClass(4L,"Aga");
        // when
        // then
        assertThat(CollectionsUtils.contains(c1, t1, TestClass::getId)).isTrue();
        assertThat(CollectionsUtils.contains(c1, t4, TestClass::getId)).isFalse();
        assertThat(CollectionsUtils.contains(c1, t4, TestClass::getName)).isTrue();
    }
}