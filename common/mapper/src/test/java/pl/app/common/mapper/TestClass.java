package pl.app.common.mapper;

import java.util.Objects;

class TestClass {
    private Long id;
    private String name;

    public TestClass() {
        this.id = 0L;
        this.name = "";
    }

    public TestClass(String name) {
        this();
        this.name = name;
    }

    public TestClass(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestClass testClass)) return false;
        return Objects.equals(id, testClass.id)
                && Objects.equals(name, testClass.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
