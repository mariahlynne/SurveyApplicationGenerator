package Model;

import java.io.Serializable;

public class Question implements Serializable {

    public String name = "";
    public int questionIndex;
    public int pageIndex;

    public String questionText = "";//all
    public boolean isRequired = false;//all
    public String errorMessage = "This question is required";//all
    public String questionType = "none";//all
    public String min = "";//textbox
    public String max = "";//textbox
    public String validationErrorMessage = "";
    public String validCharacters = "";//textbox
//    public boolean validateFormat;//textbox
//    public boolean validateContents;//textbox

    public Question(String name, int pageIndex, int questionIndex) {
        this.name = name;
        this.questionIndex = questionIndex;
        this.pageIndex = pageIndex;
    }
}
