package Model;

import java.util.ArrayList;

public class CodeGen {

    static String spaces = "                                                                                                                       ";
    public int spaceCount = 0;
    public String code = "";

    public enum DIR {

        F, S, B, FB
    }

    // <editor-fold defaultstate="collapsed" desc="MISC">
    public void getPageHeader(int pageCount, String surveyTitle) {
        addLine(CodeGen.DIR.S, "<%@page contentType=\"text/html\" pageEncoding=\"UTF-8\"%>\n");
        addLine(CodeGen.DIR.S, "<!DOCTYPE html>\n");
        addLine(CodeGen.DIR.S, "<html>\n");
        addLine(CodeGen.DIR.F, "<head>\n");
        addLine(CodeGen.DIR.F, "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
        addLine(CodeGen.DIR.S, "<title>" + surveyTitle + "</title>\n");
        addLine(CodeGen.DIR.S, "<link type=\"text/css\" rel=\"stylesheet\" href=\"css/bootstrap.css\"/>\n");
        addLine(CodeGen.DIR.S, "<link type=\"text/css\" rel=\"stylesheet\" href=\"css/font-awesome.css\"/>\n");
        addLine(CodeGen.DIR.S, "<link type=\"text/css\" rel=\"stylesheet\" href=\"css/main.css\"/>\n");
        addLine(CodeGen.DIR.S, "<script type=\"text/javascript\" src=\"js/jquery-1.10.1.js\"></script>\n");
        addLine(CodeGen.DIR.S, "<script type=\"text/javascript\" src=\"js/bootstrap.js\"></script>\n");
        addLine(CodeGen.DIR.S, "<script type=\"text/javascript\" src=\"js/static.js\"></script>\n");
        addLine(CodeGen.DIR.S, "<script type=\"text/javascript\" src=\"js/main.js\"></script>\n");
        addLine(CodeGen.DIR.B, "</head>\n");
        addLine(CodeGen.DIR.S, "<body>\n");
        addLine(CodeGen.DIR.F, "<header id=\"primary\">\n");
        addLine(CodeGen.DIR.F, "<h1>\n");
        addLine(CodeGen.DIR.F, surveyTitle + "\n");
        addLine(CodeGen.DIR.B, "</h1>\n");
        addLine(CodeGen.DIR.B, "</header>\n");
        addLine(CodeGen.DIR.S, "<form action=\"Servlet?Page=Page" + pageCount + "\" id=\"form\" onsubmit=\"return validatePage" + pageCount + "()\" method=\"POST\">\n");
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HTML Elements (with bootstrap)">
    public void getMultipleChoiceCode(ArrayList<String> answers, Question q) {
        //incorporate the "other" answer & its validation
        switch (q.displayType) {
            case "radio":
                getRadioButtonListCode(answers, q);
                break;
            case "dropdown":
                getDropDownListCode(answers, q);
                break;
            case "checkbox":
                getCheckBoxListCode(answers, q);
                break;
        }
    }

    public void getRadioButtonListCode(ArrayList<String> answers, Question q) {
        int ndx = 0;
        for (String answer : answers) {
            addLine(DIR.S, "<div class=\"radio\">\n");
            addLine(DIR.F, "<label>\n");
            if (q.otherChoice.length() > 0) {
                addLine(DIR.F, "<input type=\"radio\" id=\"" + q.questionID + "_" + ndx++ + "\" name=\"" + q.questionID + "\" value=\"" + answer + "\" onchange=\"showHideOtherChoiceTextbox('" + q.questionID + "', '" + q.questionID + "OtherChoice', 'radio');\" />\n");
            } else {
                addLine(DIR.F, "<input type=\"radio\" id=\"" + q.questionID + "_" + ndx++ + "\" name=\"" + q.questionID + "\" value=\"" + answer + "\" />\n");
            }
            addLine(DIR.S, answer + "\n");
            addLine(DIR.B, "</label>\n");
            addLine(DIR.B, "</div>\n");
        }
        if (q.otherChoice.length() > 0) {
            addLine(DIR.S, "<div class=\"radio\">\n");
            addLine(DIR.F, "<label>\n");
            addLine(DIR.F, "<input type=\"radio\" id=\"" + q.questionID + "OtherChoiceSpecify\" name=\"" + q.questionID + "\" value=\"" + q.otherChoice + "\" onchange=\"showHideOtherChoiceTextbox('" + q.questionID + "', '" + q.questionID + "OtherChoice', 'radio');\">\n");
            addLine(DIR.S, q.otherChoice + "\n");
            addLine(DIR.B, "</label>\n");
            addLine(DIR.B, "</div>\n");
            addLine(DIR.S, "<p id=\"" + q.questionID + "OtherChoiceErrorMessage\" class=\"errorText\"></p>\n");
            getTextBoxCode(q.questionID + "OtherChoice", Integer.parseInt(q.max), false);
        }
    }

    public void getDropDownListCode(ArrayList<String> answers, Question q) {
        addLine(DIR.S, "<select class=\"form-control\" id=\"" + q.questionID + "\" onchange=\"showHideOtherChoiceTextbox('" + q.questionID + "', '" + q.questionID + "OtherChoice', 'dropdown');\">\n");
        for (String answer : answers) {
            addLine(DIR.FB, "<option>" + answer + "</option>\n");
        }
        if (q.otherChoice.length() > 0) {
            addLine(DIR.FB, "<option value=\"otherChoiceSpecify\">" + q.otherChoice + "</option>\n");
        }
        addLine(DIR.S, "</select><br />\n");
        if (q.otherChoice.length() > 0) {
            addLine(DIR.S, "<p id=\"" + q.questionID + "OtherChoiceErrorMessage\" class=\"errorText\"></p>\n");
            getTextBoxCode(q.questionID + "OtherChoice", Integer.parseInt(q.max), false);
        }
    }

    public void getCheckBoxListCode(ArrayList<String> answers, Question q) {
        int ndx = 0;
        for (String answer : answers) {
            addLine(DIR.S, "<div class=\"checkbox\">\n");
            addLine(DIR.F, "<label>\n");
            addLine(DIR.F, "<input type=\"checkbox\" id=\"" + q.questionID + "_" + ndx++ + "\" value=\"" + answer + "\">\n");
            addLine(DIR.S, answer + "\n");
            addLine(DIR.B, "</label>\n");
            addLine(DIR.B, "</div>\n");
        }
        if (q.otherChoice.length() > 0) {
            addLine(DIR.S, "<div class=\"checkbox\">\n");
            addLine(DIR.F, "<label>\n");
            addLine(DIR.F, "<input type=\"checkbox\" id=\"" + q.questionID + "OtherChoiceSpecify\" value=\"otherChoice\" onchange=\"showHideOtherChoiceTextbox('" + q.questionID + "OtherChoiceSpecify', '" + q.questionID + "OtherChoice', 'checkbox');\">\n");
            addLine(DIR.S, q.otherChoice + "\n");
            addLine(DIR.B, "</label>\n");
            addLine(DIR.S, "<p id=\"" + q.questionID + "OtherChoiceErrorMessage\" class=\"errorText\"></p>\n");
            getTextBoxCode(q.questionID + "OtherChoice", Integer.parseInt(q.max), false);
            addLine(DIR.B, "</div>\n");
        }
    }

    public void getTextBoxCode(String id, int maxlength, boolean display) {
        String style = display ? "" : " style=\"display: none;\"";
        if (maxlength > 100) {
            addLine(DIR.S, "<textarea id=\"" + id + "\" rows=\"3\" class=\"textarea\" maxlength=\"" + maxlength + "\"" + style + "></textarea>\n");
        } else {
            addLine(DIR.S, "<input type=\"text\" id=\"" + id + "\" class=\"form-control\" maxlength=\"" + maxlength + "\"" + style + " />\n");
        }
    }

    public void getWholeNumberCode(String id) {
        addLine(DIR.S, "<input type=\"text\" id=\"" + id + "\" class=\"form-control onlyAllowNumbers\" />\n");
    }

    public void getDecimalNumberCode(String id, int decimalPlaces) {
        addLine(DIR.S, "<input type=\"text\" id=\"" + id + "\" class=\"form-control onlyAllowDecimalNumbers\" onblur=\"padDecimalPointPlaces('" + id + "', " + decimalPlaces + ")\" />\n");
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Validation">
    public void getMeetsLengthRequirementsCode(String id, String min, String max) {
        addLine(CodeGen.DIR.S, "result = meetsLengthRequirements('" + id + "', 'text', " + min + ", " + max + ") && result;\n");
    }

    public void getTextValidationCode(Question q) {
        if (q.validateText) {
            String validChars = "";
            String[] allowTypes = q.allowTypes.split(", ");
            if (allowTypes[0].equals("true")) {
                validChars += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            }
            if (allowTypes[1].equals("true")) {
                validChars += "abcdefghijklmnopqrstuvwxyz";
            }
            if (allowTypes[2].equals("true")) {
                validChars += "0123456789";
            }
            if (allowTypes[3].equals("true")) {
                validChars += q.validSpecialCharacters.replace("\\", "\\\\").replace("'", "\\'");
            }
            if (q.questionType.equals("multipleChoice")) {
                q.questionID += "OtherChoice";
            }
            addLine(CodeGen.DIR.S, "result = containsOnlyValidChars('" + q.questionID + "', 'text', '" + validChars + "') && result;\n");
        }
    }

    public void getDecimalValidationCode(Question q) {
        switch (q.validationType) {
            case "setMin":
                addLine(CodeGen.DIR.S, "result = meetsDecimalNumberRequirements('" + q.questionID + "', " + q.min + ", '') && result;\n");
                break;
            case "setMax":
                addLine(CodeGen.DIR.S, "result = meetsDecimalNumberRequirements('" + q.questionID + "', '', " + q.max + ") && result;\n");
                break;
            case "setMinMax":
                addLine(CodeGen.DIR.S, "result = meetsDecimalNumberRequirements('" + q.questionID + "', " + q.min + ", " + q.max + ") && result;\n");
                break;
        }
    }

    public void getWholeNumberValidationCode(Question q) {
        switch (q.validationType) {
            case "setMin":
                addLine(CodeGen.DIR.S, "result = meetsWholeNumberRequirements('" + q.questionID + "', " + q.min + ", '') && result;\n");
                break;
            case "setMax":
                addLine(CodeGen.DIR.S, "result = meetsWholeNumberRequirements('" + q.questionID + "', '', " + q.max + ") && result;\n");
                break;
            case "setMinMax":
                addLine(CodeGen.DIR.S, "result = meetsWholeNumberRequirements('" + q.questionID + "', " + q.min + ", " + q.max + ") && result;\n");
                break;
        }
    }

    public void getMultipleChoiceValidationCode(Question q) {
        if (q.otherChoice.length() > 0) {
            switch (q.displayType) {
                case "checkbox":
                    addLine(CodeGen.DIR.S, "result = validateAnswerLimit('" + q.questionID + "', " + q.numberOfAnswers + ") && result;\n");
                case "radio":
                    addLine(CodeGen.DIR.S, "if ($(\"#" + q.questionID + "OtherChoiceSpecify\").is(':checked')) {\n");
                    break;
                case "dropdown":
                    addLine(CodeGen.DIR.S, "if ($(\"#" + q.questionID + "\").val() == \"otherChoiceSpecify\") {\n");
                    break;
            }
            spaceCount += 4;
            getMeetsLengthRequirementsCode(q.questionID + "OtherChoice", q.min, q.max);
            getTextValidationCode(q);
            addLine(CodeGen.DIR.B, "}\n");
        }
    }

// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Navigation">
    public void getNextButtonCode() {
        addLine(CodeGen.DIR.F, "<button id=\"nextButton\" type=\"submit\" class=\"btn btn-info\" style=\"margin-bottom: 50px\">Next</button>\n");
    }

    public void getSubmitButtonCode() {
        addLine(CodeGen.DIR.F, "<button id=\"submitButton\" type=\"submit\" class=\"btn btn-info\" style=\"margin-bottom: 50px\">Submit</button>\n");
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Database">
    // </editor-fold>

    public void addLine(DIR direction, String newHTML) {
        switch (direction) {
            case F:
            case FB:
                spaceCount += 4;
                break;
            case S:
                //do nothing
                break;
            case B:
                spaceCount -= 4;
                break;

        }
        if (spaceCount == -4) {
            spaceCount = 0;
        }
        code += spaces.substring(0, spaceCount) + newHTML;
        if (direction == DIR.FB) {
            spaceCount -= 4;
        }
    }
}