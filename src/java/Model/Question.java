package Model;

import java.io.Serializable;

public class Question implements Serializable {

    public String name = "";
    public int questionIndex;
    public int pageIndex;

    public String questionText = "";//all
    public boolean isRequired = false;//all
    public String questionType = "none";//all
    public String min = "";//textbox, whole number, decimal number
    public String max = "";//textbox, whole number, decimal number
    public String validCharacters = "";//textbox
    public String decimalPlaces = "";//decimal number
    public String validationType = "none";//whole number, decimal number
//    public boolean validateFormat;//textbox
//    public boolean validateContents;//textbox

    public Question(String name, int pageIndex, int questionIndex) {
        this.name = name;
        this.questionIndex = questionIndex;
        this.pageIndex = pageIndex;
    }

    public void clearAll() {
        questionText = "";
        isRequired = false;
        min = "";
        max = "";
        validCharacters = "";
        decimalPlaces = "";
        validationType = "none";
    }
}
