package Model;

import java.io.Serializable;
import java.util.ArrayList;

public class Page implements Serializable {

    public String name;
    public ArrayList<Question> questions;
    public int pageIndex;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Page(String name, int index) {
        this.name = name;
        pageIndex = index;
        questions = new ArrayList<Question>();
        addQuestion("Question 1", 0);
    }

    public void addQuestion(String name, int qIndex) {
        questions.add(new Question(name, pageIndex, qIndex));
    }

    public Question getQuestion(String name) {
        for (Question q : questions) {
            if (q.name.equals(name)) {
                return q;
            }
        }
        return null;
    }
}
