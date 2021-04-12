package qa.util.dao.query.builder;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Date;

public class QueryBuilder {

    private final SessionFactory sessionFactory;
    private Session session;

    private final UserQueryBuilder userQueryBuilder = new UserQueryBuilder();
    private final QuestionQueryBuilder questionQueryBuilder = new QuestionQueryBuilder();
    private final AnswerQueryBuilder answerQueryBuilder = new AnswerQueryBuilder();
    private final CommentQuestionQueryBuilder commentQuestionQueryBuilder = new CommentQuestionQueryBuilder();
    private final CommentAnswerQueryBuilder commentAnswerQueryBuilder = new CommentAnswerQueryBuilder();


    public QueryBuilder(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public QueryBuilder openSession() {
        this.session = sessionFactory.openSession();
        this.session.beginTransaction();
        return this;
    }

    public void closeSession() {
        this.session.getTransaction().commit();
        this.session.close();
    }

    public QueryBuilder flushAndClear() {
        
        this.session.flush();
        this.session.clear();
        return this;
    }

    public QueryBuilder user(Long id, String username) {
        userQueryBuilder
                .with(session)
                .user(id, username);
        return this;
    }

    public QueryBuilder user() {
        userQueryBuilder
                .with(session)
                .user();
        return this;
    }

    public QueryBuilder question(Long id,
                                 Date date,
                                 String tags,
                                 String text,
                                 String title) {
        questionQueryBuilder
                .with(session)
                .question(id, date, tags, text, title);
        return this;
    }

    public QueryBuilder question(Long id,
                                 Date date) {
        questionQueryBuilder
                .with(session)
                .question(id, date);
        return this;
    }

    public QueryBuilder question(Long id) {
        questionQueryBuilder
                .with(session)
                .question(id);
        return this;
    }

    public QueryBuilder question() {
        questionQueryBuilder
                .with(session)
                .question();
        return this;
    }

    public QueryBuilder answer(Long id,
                               Boolean answered,
                               Date date,
                               String text,
                               Long questionId) {
        answerQueryBuilder
                .with(session)
                .answer(id, answered, date, text, questionId);
        return this;
    }

    public QueryBuilder answer(Long id,
                               Boolean answered,
                               Date date) {
        answerQueryBuilder
                .with(session)
                .answer(id, answered, date);
        return this;
    }

    public QueryBuilder answer(Long id,
                               Boolean answered) {
        answerQueryBuilder
                .with(session)
                .answer(id, answered);
        return this;
    }

    public QueryBuilder answer(Long id,
                               Long questionId,
                               Date date) {
        answerQueryBuilder
                .with(session)
                .answer(id, questionId, date);
        return this;
    }

    public QueryBuilder answer(Long id,
                               Date date) {
        answerQueryBuilder
                .with(session)
                .answer(id, date);
        return this;
    }

    public QueryBuilder answer(Long id) {
        answerQueryBuilder
                .with(session)
                .answer(id);
        return this;
    }

    public QueryBuilder answer() {
        answerQueryBuilder
                .with(session)
                .answer();
        return this;
    }

    public QueryBuilder commentQuestion(Long id,
                                        String text,
                                        Long questionId,
                                        Date date) {
        commentQuestionQueryBuilder
                .with(session)
                .commentQuestion(id, text, questionId, date);
        return this;
    }

    public QueryBuilder commentQuestion(Long id,
                                        Date date) {
        commentQuestionQueryBuilder
                .with(session)
                .commentQuestion(id, date);
        return this;
    }

    public QueryBuilder commentAnswer(Long id,
                                      String text,
                                      Long answerId,
                                      Date date) {
        commentAnswerQueryBuilder
                .with(session)
                .commentAnswer(id, text, answerId, date);
        return this;
    }

    public QueryBuilder commentAnswer(Long id,
                              Long answerId,
                              Date date) {
        commentAnswerQueryBuilder
                .with(session)
                .commentAnswer(id, date, answerId);
        return this;
    }

    public QueryBuilder commentAnswer(Long id,
                              Date date) {
        commentAnswerQueryBuilder
                .with(session)
                .commentAnswer(id, date);
        return this;
    }
}
