package Model;

import java.io.Serializable;
import java.util.ArrayList;

public class Page implements Serializable {

    public String name;
    public ArrayList<Question> questions;
    public int pageIndex;

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
