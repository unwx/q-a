package qa.domain;

import javax.persistence.*;

@Entity
@DiscriminatorValue("answer")
public class AnswerComment extends Comment {}
