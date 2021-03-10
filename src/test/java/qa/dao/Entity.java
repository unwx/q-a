package qa.dao;

import qa.dao.databasecomponents.Field;
import qa.dao.databasecomponents.FieldExtractor;

import java.time.LocalDateTime;

public class Entity implements FieldExtractor {

    private Long id;
    private String str;
    private Boolean bool;
    private LocalDateTime date;
    private NestedEntity nested1;
    private NestedEntity nested2;

    public Entity(Long id, String str, Boolean bool, LocalDateTime date) {
        this.id = id;
        this.str = str;
        this.bool = bool;
        this.date = date;
    }

    public Entity(Long id, String str, Boolean bool, LocalDateTime date, NestedEntity nested1, NestedEntity nested2) {
        this.id = id;
        this.str = str;
        this.bool = bool;
        this.date = date;
        this.nested1 = nested1;
        this.nested2 = nested2;
    }

    public NestedEntity getNested1() {
        return nested1;
    }

    public void setNested1(NestedEntity nested1) {
        this.nested1 = nested1;
    }

    public NestedEntity getNested2() {
        return nested2;
    }

    public void setNested2(NestedEntity nested2) {
        this.nested2 = nested2;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public Boolean getBool() {
        return bool;
    }

    public void setBool(Boolean bool) {
        this.bool = bool;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public Field[] extract() {
        return new Field[]{
                new Field("id", id),
                new Field("str", str),
                new Field("bool", bool),
                new Field("date", date)
        };
    }
}
