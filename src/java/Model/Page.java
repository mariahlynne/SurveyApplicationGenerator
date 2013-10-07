package Model;

import java.io.Serializable;
import java.util.ArrayList;

public class Page implements Serializable {

    public String name;
    public ArrayList<Question> questions;
    public int pageIndex, ID;

    public Page(int id, String name, int pageIndex) {
        this.ID = id;
        this.name = name;
        this.pageIndex = pageIndex;
        questions = new ArrayList<Question>();
    }

    public void addQuestion(int id, String name, int questionIndex) {
        questions.add(new Question(id, name, questionIndex));
    }

    public int getQuestionIDByIndex(int index) {
        return questions.get(index).ID;
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
