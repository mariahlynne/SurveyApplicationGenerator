package Model;

import java.io.Serializable;

public class Question implements Serializable {
    
    public String name;
    public int questionIndex;
    public int pageIndex;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(int questionIndex) {
        this.questionIndex = questionIndex;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public boolean isIsRequired() {
        return isRequired;
    }

    public void setIsRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getToolTip() {
        return toolTip;
    }

    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public String getValidCharacters() {
        return validCharacters;
    }

    public void setValidCharacters(String validCharacters) {
        this.validCharacters = validCharacters;
    }

    public boolean isValidateFormat() {
        return validateFormat;
    }

    public void setValidateFormat(boolean validateFormat) {
        this.validateFormat = validateFormat;
    }

    public boolean isValidateContents() {
        return validateContents;
    }

    public void setValidateContents(boolean validateContents) {
        this.validateContents = validateContents;
    }
    
    //Attributes must also be added to the backing bean
    public String questionText;
    public boolean isRequired = false;
    public String errorMessage = "This question is required.";
    public String toolTip;
    public String type = "none";
    public int minLength;//textbox
    public int maxLength;//textbox
    public String validCharacters;//textbox
    public boolean validateFormat;//textbox
    public boolean validateContents;//textbox   
    
    public Question(String name, int pageIndex, int questionIndex) {
        this.name = name;
        this.questionIndex = questionIndex;
        this.pageIndex = pageIndex;
    }
}
