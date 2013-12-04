package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Page implements Serializable {

    private String name;
    private ArrayList<Question> questions;
    private int pageIndex, ID;

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
        return questions.get(index).getID();
    }

    public Question getQuestion(String name) {
        for (Question q : questions) {
            if (q.getName().equals(name)) {
                return q;
            }
        }
        return null;
    }
    
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

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
