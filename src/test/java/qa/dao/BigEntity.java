package qa.dao;

import qa.dao.databasecomponents.Field;
import qa.dao.databasecomponents.FieldDataSetterExtractor;
import qa.dao.databasecomponents.FieldExtractor;
import qa.domain.setters.SetterField;

@SuppressWarnings("unused")
public class BigEntity implements FieldExtractor, FieldDataSetterExtractor {

    private Long l1;
    private Long l2;
    private Long l3;
    private Long l4;
    private Long l5;
    private Long l6;
    private Long l7;
    private Long l8;
    private Long l9;

    private String s1;
    private String s2;
    private String s3;
    private String s4;
    private String s5;
    private String s6;
    private String s7;
    private String s8;
    private String s9;

    private Boolean b1;
    private Boolean b2;
    private Boolean b3;
    private Boolean b4;
    private Boolean b5;
    private Boolean b6;
    private Boolean b7;
    private Boolean b8;
    private Boolean b9;

    public BigEntity(Long l1,
                     Long l2,
                     Long l3,
                     Long l4,
                     Long l5,
                     Long l6,
                     Long l7,
                     Long l8,
                     Long l9,
                     String s1,
                     String s2,
                     String s3,
                     String s4,
                     String s5,
                     String s6,
                     String s7,
                     String s8,
                     String s9,
                     Boolean b1,
                     Boolean b2,
                     Boolean b3,
                     Boolean b4,
                     Boolean b5,
                     Boolean b6,
                     Boolean b7,
                     Boolean b8,
                     Boolean b9) {
        this.l1 = l1;
        this.l2 = l2;
        this.l3 = l3;
        this.l4 = l4;
        this.l5 = l5;
        this.l6 = l6;
        this.l7 = l7;
        this.l8 = l8;
        this.l9 = l9;
        this.s1 = s1;
        this.s2 = s2;
        this.s3 = s3;
        this.s4 = s4;
        this.s5 = s5;
        this.s6 = s6;
        this.s7 = s7;
        this.s8 = s8;
        this.s9 = s9;
        this.b1 = b1;
        this.b2 = b2;
        this.b3 = b3;
        this.b4 = b4;
        this.b5 = b5;
        this.b6 = b6;
        this.b7 = b7;
        this.b8 = b8;
        this.b9 = b9;
    }

    public Long getL1() {
        return l1;
    }

    public void setL1(Long l1) {
        this.l1 = l1;
    }

    public Long getL2() {
        return l2;
    }

    public void setL2(Long l2) {
        this.l2 = l2;
    }

    public Long getL3() {
        return l3;
    }

    public void setL3(Long l3) {
        this.l3 = l3;
    }

    public Long getL4() {
        return l4;
    }

    public void setL4(Long l4) {
        this.l4 = l4;
    }

    public Long getL5() {
        return l5;
    }

    public void setL5(Long l5) {
        this.l5 = l5;
    }

    public Long getL6() {
        return l6;
    }

    public void setL6(Long l6) {
        this.l6 = l6;
    }

    public Long getL7() {
        return l7;
    }

    public void setL7(Long l7) {
        this.l7 = l7;
    }

    public Long getL8() {
        return l8;
    }

    public void setL8(Long l8) {
        this.l8 = l8;
    }

    public Long getL9() {
        return l9;
    }

    public void setL9(Long l9) {
        this.l9 = l9;
    }

    public String getS1() {
        return s1;
    }

    public void setS1(String s1) {
        this.s1 = s1;
    }

    public String getS2() {
        return s2;
    }

    public void setS2(String s2) {
        this.s2 = s2;
    }

    public String getS3() {
        return s3;
    }

    public void setS3(String s3) {
        this.s3 = s3;
    }

    public String getS4() {
        return s4;
    }

    public void setS4(String s4) {
        this.s4 = s4;
    }

    public String getS5() {
        return s5;
    }

    public void setS5(String s5) {
        this.s5 = s5;
    }

    public String getS6() {
        return s6;
    }

    public void setS6(String s6) {
        this.s6 = s6;
    }

    public String getS7() {
        return s7;
    }

    public void setS7(String s7) {
        this.s7 = s7;
    }

    public String getS8() {
        return s8;
    }

    public void setS8(String s8) {
        this.s8 = s8;
    }

    public String getS9() {
        return s9;
    }

    public void setS9(String s9) {
        this.s9 = s9;
    }

    public Boolean getB1() {
        return b1;
    }

    public void setB1(Boolean b1) {
        this.b1 = b1;
    }

    public Boolean getB2() {
        return b2;
    }

    public void setB2(Boolean b2) {
        this.b2 = b2;
    }

    public Boolean getB3() {
        return b3;
    }

    public void setB3(Boolean b3) {
        this.b3 = b3;
    }

    public Boolean getB4() {
        return b4;
    }

    public void setB4(Boolean b4) {
        this.b4 = b4;
    }

    public Boolean getB5() {
        return b5;
    }

    public void setB5(Boolean b5) {
        this.b5 = b5;
    }

    public Boolean getB6() {
        return b6;
    }

    public void setB6(Boolean b6) {
        this.b6 = b6;
    }

    public Boolean getB7() {
        return b7;
    }

    public void setB7(Boolean b7) {
        this.b7 = b7;
    }

    public Boolean getB8() {
        return b8;
    }

    public void setB8(Boolean b8) {
        this.b8 = b8;
    }

    public Boolean getB9() {
        return b9;
    }

    public void setB9(Boolean b9) {
        this.b9 = b9;
    }

    @Override
    public Field[] extract() {
        return new Field[]{
                new Field("l1", l1),
                new Field("l2", l2),
                new Field("l3", l3),
                new Field("l4", l4),
                new Field("l5", l5),
                new Field("l6", l6),
                new Field("l7", l7),
                new Field("l8", l8),
                new Field("l9", l9),
                new Field("s1", s1),
                new Field("s2", s2),
                new Field("s3", s3),
                new Field("s4", s4),
                new Field("s5", s5),
                new Field("s6", s6),
                new Field("s7", s7),
                new Field("s8", s8),
                new Field("s9", s9),
                new Field("b1", b1),
                new Field("b2", b2),
                new Field("b3", b3),
                new Field("b4", b4),
                new Field("b5", b5),
                new Field("b6", b6),
                new Field("b7", b7),
                new Field("b8", b8),
                new Field("b9", b9),
        };
    }

    @Override
    public SetterField[] extractSettersField() {
        return new SetterField[0];
    }
}
