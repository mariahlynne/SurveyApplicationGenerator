package Model;

import java.io.Serializable;

public class Question implements Serializable {

    private String name = "";
    private int ID, questionIndex;
    private String questionID = "";
    private String questionText = "";//all
    private boolean required = false;//all
    private String questionType = "none";//all
    private String min = "";//textbox, whole number, decimal number
    private String max = "";//textbox, whole number, decimal number
    private boolean validateText = false;//textbox
    private String allowTypes = "true, true, true, true";
    private String validSpecialCharacters = "~!@#$%^&*()-_=+|\\[]{};:' \",./?<>";//textbox
    private String decimalPlaces = "";//decimal number
    private String validationType = "none";//whole number, decimal number, multiple choice with other
    private String answerChoices = "";//multiple choice
    private String otherChoice = "";//multiple choice
    private String displayType = "radioButtons";//multiple choice
    private String numberOfAnswers = "1";//multiple choice

    public Question(int id, String name, int questionIndex) {
        this.ID = id;
        this.name = name;
        this.questionIndex = questionIndex;
    }

    public void clearAll() {
        questionText = "";
        questionID = "";
        required = false;
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

    public int longestAnswerChoiceLength() {
        int iLongest = 0;
        if (displayType.equals("checkbox")) {
            return answerChoices.length() + otherChoice.length() + 2;
        } else {
            for (String s : answerChoices.split(";;")) {
                if (s.length() > iLongest) {
                    iLongest = s.length();
                }
            }
            if (otherChoice.length() > iLongest) {
                iLongest = otherChoice.length();
            }
        }
        return iLongest;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(int questionIndex) {
        this.questionIndex = questionIndex;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public boolean isValidateText() {
        return validateText;
    }

    public void setValidateText(boolean validateText) {
        this.validateText = validateText;
    }

    public String getAllowTypes() {
        return allowTypes;
    }

    public void setAllowTypes(String allowTypes) {
        this.allowTypes = allowTypes;
    }

    public String getValidSpecialCharacters() {
        return validSpecialCharacters;
    }

    public void setValidSpecialCharacters(String validSpecialCharacters) {
        this.validSpecialCharacters = validSpecialCharacters;
    }

    public String getDecimalPlaces() {
        return decimalPlaces;
    }

    public void setDecimalPlaces(String decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    public String getValidationType() {
        return validationType;
    }

    public void setValidationType(String validationType) {
        this.validationType = validationType;
    }

    public String getAnswerChoices() {
        return answerChoices;
    }

    public void setAnswerChoices(String answerChoices) {
        this.answerChoices = answerChoices;
    }

    public String getOtherChoice() {
        return otherChoice;
    }

    public void setOtherChoice(String otherChoice) {
        this.otherChoice = otherChoice;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public String getNumberOfAnswers() {
        return numberOfAnswers;
    }

    public void setNumberOfAnswers(String numberOfAnswers) {
        this.numberOfAnswers = numberOfAnswers;
    }
}
