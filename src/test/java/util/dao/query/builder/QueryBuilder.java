package util.dao.query.builder;

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

    public QueryBuilder user(long id, String username) {
        this.userQueryBuilder
                .with(session)
                .user(id, username);
        return this;
    }

    public QueryBuilder user() {
        this.userQueryBuilder
                .with(session)
                .user();
        return this;
    }

    public QueryBuilder question(long id,
                                 Date date,
                                 String tags,
                                 String text,
                                 String title) {
        this.questionQueryBuilder
                .with(session)
                .question(id, date, tags, text, title);
        return this;
    }

    public QueryBuilder question(long id,
                                 long authorId,
                                 Date date,
                                 String tags,
                                 String text,
                                 String title) {
        this.questionQueryBuilder
                .with(session)
                .question(id, authorId, date, tags, text, title);
        return this;
    }

    public QueryBuilder question(Long id,
                                 String tags,
                                 String text,
                                 String title) {
        this.questionQueryBuilder
                .with(session)
                .question(id, new Date(), tags, text, title);
        return this;
    }

    public QueryBuilder question(Long id,
                                 Date date) {
        this.questionQueryBuilder
                .with(session)
                .question(id, date);
        return this;
    }

    public QueryBuilder question(Long id) {
        this.questionQueryBuilder
                .with(session)
                .question(id);
        return this;
    }

    public QueryBuilder question() {
        this.questionQueryBuilder
                .with(session)
                .question();
        return this;
    }

    public QueryBuilder answer(Long id,
                               Boolean answered,
                               Date date,
                               String text,
                               Long questionId) {
        this.answerQueryBuilder
                .with(session)
                .answer(id, answered, date, text, questionId);
        return this;
    }

    public QueryBuilder answer(Long id,
                               Boolean answered,
                               Date date) {
        this.answerQueryBuilder
                .with(session)
                .answer(id, answered, date);
        return this;
    }

    public QueryBuilder answer(Long id,
                               Boolean answered) {
        this.answerQueryBuilder
                .with(session)
                .answer(id, answered);
        return this;
    }

    public QueryBuilder answer(Long id,
                               Long questionId,
                               Date date) {
        this.answerQueryBuilder
                .with(session)
                .answer(id, questionId, date);
        return this;
    }

    public QueryBuilder answer(long id,
                               long authorId,
                               long questionId,
                               String text,
                               Date date) {
        this.answerQueryBuilder
                .with(session)
                .answer(id, authorId, questionId, false, date, text);
        return this;
    }

    public QueryBuilder answer(Long id,
                               Date date) {
        this.answerQueryBuilder
                .with(session)
                .answer(id, date);
        return this;
    }

    public QueryBuilder answer(Long id) {
        this.answerQueryBuilder
                .with(session)
                .answer(id);
        return this;
    }

    public QueryBuilder answer() {
        this.answerQueryBuilder
                .with(session)
                .answer();
        return this;
    }

    public void commentQuestion(int id,
                                long authorId,
                                long questionId,
                                String text,
                                Date date) {

        this.commentQuestionQueryBuilder
                .with(session)
                .commentQuestion(id, authorId, questionId, text, date);
    }

    public QueryBuilder commentQuestion(Long id,
                                        String text,
                                        Long questionId,
                                        Date date) {
        this.commentQuestionQueryBuilder
                .with(session)
                .commentQuestion(id, text, questionId, date);
        return this;
    }

    public QueryBuilder commentQuestion(Long id,
                                        Date date) {
        this.commentQuestionQueryBuilder
                .with(session)
                .commentQuestion(id, date);
        return this;
    }

    public QueryBuilder commentQuestion() {
        this.commentQuestionQueryBuilder
                .with(session)
                .commentQuestion();
        return this;
    }

    public QueryBuilder commentAnswer(Long id,
                                      String text,
                                      Long answerId,
                                      Date date) {
        this.commentAnswerQueryBuilder
                .with(session)
                .commentAnswer(id, text, answerId, date);
        return this;
    }

    public void commentAnswer(long id,
                              long userId,
                              long answerId,
                              String text,
                              Date date) {
        this.commentAnswerQueryBuilder
                .with(session)
                .commentAnswer(id, userId, answerId, text, date);
    }

    public QueryBuilder commentAnswer(Long id,
                              Long answerId,
                              Date date) {
        this.commentAnswerQueryBuilder
                .with(session)
                .commentAnswer(id, date, answerId);
        return this;
    }

    public QueryBuilder commentAnswer(Long id,
                              Date date) {
        this.commentAnswerQueryBuilder
                .with(session)
                .commentAnswer(id, date);
        return this;
    }

    public QueryBuilder commentAnswer() {
        this.commentAnswerQueryBuilder
                .with(session)
                .commentAnswer();
        return this;
    }
}
