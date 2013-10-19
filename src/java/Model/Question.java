package Model;

import java.io.Serializable;

public class Question implements Serializable {

    public String name = "";
    public int ID, questionIndex;

    public String questionID = "";
    public String questionText = "";//all
    public boolean isRequired = false;//all
    public String questionType = "none";//all
    public String min = "";//textbox, whole number, decimal number
    public String max = "";//textbox, whole number, decimal number
    public boolean validateText = false;//textbox
    public String allowTypes = "true, true, true, true";
    public String validSpecialCharacters = "~!@#$%^&*()-_=+|\\[]{};:' \",./?<>";//textbox
    public String decimalPlaces = "";//decimal number
    public String validationType = "none";//whole number, decimal number, multiple choice with other
    public String answerChoices = "";//multiple choice
    public String otherChoice = "";//multiple choice
    public String displayType = "radioButtons";//multiple choice
    public String numberOfAnswers = "1";//multiple choice
//    public boolean validateFormat;//textbox
//    public boolean validateContents;//textbox

    public Question(int id, String name, int questionIndex) {
        this.ID = id;
        this.name = name;
        this.questionIndex = questionIndex;
    }

    public void clearAll() {
        questionText = "";
        questionID = "";
        isRequired = false;
        min = "";
        max = "";
        validateText = false;
        allowTypes = "true, true, true, true";
        validSpecialCharacters = "~!@#$%^&*()-_=+|\\[]{};:' \",./?<>";
        decimalPlaces = "";
        validationType = "none";
        answerChoices = "";
        otherChoice = "";
        displayType = "radioButtons";
        numberOfAnswers = "1";
    }
}
