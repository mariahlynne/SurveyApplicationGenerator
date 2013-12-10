package Model;

import java.util.ArrayList;
import java.util.HashMap;

public class CodeGenerator {

    public static String spaces = "                                                                                                                       ";
    public int spaceCount = 0;
    public String code = "";

    public enum DIR {

        F, S, B, FB
    }

    // <editor-fold defaultstate="collapsed" desc="MISC">
    public void getPageHeader(int pageCount, String surveyTitle) {
        addLine(CodeGenerator.DIR.S, "<%@page contentType=\"text/html\" pageEncoding=\"UTF-8\"%>\n");
        addLine(CodeGenerator.DIR.S, "<!DOCTYPE html>\n");
        addLine(CodeGenerator.DIR.S, "<html>\n");
        addLine(CodeGenerator.DIR.F, "<head>\n");
        addLine(CodeGenerator.DIR.F, "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
        addLine(CodeGenerator.DIR.S, "<title>" + surveyTitle + "</title>\n");
        addLine(CodeGenerator.DIR.S, "<link type=\"text/css\" rel=\"stylesheet\" href=\"css/bootstrap.css\"/>\n");
        addLine(CodeGenerator.DIR.S, "<link type=\"text/css\" rel=\"stylesheet\" href=\"css/font-awesome.css\"/>\n");
        addLine(CodeGenerator.DIR.S, "<link type=\"text/css\" rel=\"stylesheet\" href=\"css/main.css\"/>\n");
        addLine(CodeGenerator.DIR.S, "<script type=\"text/javascript\" src=\"js/jquery-1.10.1.js\"></script>\n");
        addLine(CodeGenerator.DIR.S, "<script type=\"text/javascript\" src=\"js/bootstrap.js\"></script>\n");
        addLine(CodeGenerator.DIR.S, "<script type=\"text/javascript\" src=\"js/static.js\"></script>\n");
        addLine(CodeGenerator.DIR.S, "<script type=\"text/javascript\" src=\"js/main.js\"></script>\n");
        addLine(CodeGenerator.DIR.B, "</head>\n");
        addLine(CodeGenerator.DIR.S, "<body>\n");
        addLine(CodeGenerator.DIR.F, "<header id=\"primary\">\n");
        addLine(CodeGenerator.DIR.F, "<h1>\n");
        addLine(CodeGenerator.DIR.F, surveyTitle + "\n");
        addLine(CodeGenerator.DIR.B, "</h1>\n");
        addLine(CodeGenerator.DIR.B, "</header>\n");
        addLine(CodeGenerator.DIR.S, "<form action=\"Servlet?Page=Page" + pageCount + "\" id=\"form\" onsubmit=\"return validatePage" + pageCount + "()\" method=\"POST\">\n");
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="HTML Elements (with bootstrap)">
    public void getMultipleChoiceCode(ArrayList<String> answers, Question q) {
        //incorporate the "other" answer & its validation
        switch (q.getDisplayType()) {
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
            if (q.getOtherChoice().length() > 0) {
                addLine(DIR.F, "<input type=\"radio\" id=\"" + q.getQuestionID() + "_" + ndx++ + "\" name=\"" + q.getQuestionID() + "\" value=\"" + answer + "\" onchange=\"showHideOtherChoiceTextbox('" + q.getQuestionID() + "', '" + q.getQuestionID() + "OtherChoice', 'radio');\" />\n");
            } else {
                addLine(DIR.F, "<input type=\"radio\" id=\"" + q.getQuestionID() + "_" + ndx++ + "\" name=\"" + q.getQuestionID() + "\" value=\"" + answer + "\" />\n");
            }
            addLine(DIR.S, answer + "\n");
            addLine(DIR.B, "</label>\n");
            addLine(DIR.B, "</div>\n");
        }
        if (q.getOtherChoice().length() > 0) {
            addLine(DIR.S, "<div class=\"radio\">\n");
            addLine(DIR.F, "<label>\n");
            addLine(DIR.F, "<input type=\"radio\" id=\"" + q.getQuestionID() + "OtherChoiceSpecify\" name=\"" + q.getQuestionID() + "\" value=\"" + q.getOtherChoice() + "\" onchange=\"showHideOtherChoiceTextbox('" + q.getQuestionID() + "', '" + q.getQuestionID() + "OtherChoice', 'radio');\">\n");
            addLine(DIR.S, q.getOtherChoice() + "\n");
            addLine(DIR.B, "</label>\n");
            addLine(DIR.B, "</div>\n");
            addLine(DIR.S, "<p id=\"" + q.getQuestionID() + "OtherChoiceErrorMessage\" class=\"errorText\"></p>\n");
            getTextBoxCode(q.getQuestionID() + "OtherChoice", Integer.parseInt(q.getMax()), false);
        }
    }

    public void getDropDownListCode(ArrayList<String> answers, Question q) {
        addLine(DIR.S, "<select class=\"form-control\" id=\"" + q.getQuestionID() + "\" onchange=\"showHideOtherChoiceTextbox('" + q.getQuestionID() + "', '" + q.getQuestionID() + "OtherChoice', 'dropdown');\">\n");
        for (String answer : answers) {
            addLine(DIR.FB, "<option>" + answer + "</option>\n");
        }
        if (q.getOtherChoice().length() > 0) {
            addLine(DIR.FB, "<option value=\"otherChoiceSpecify\">" + q.getOtherChoice() + "</option>\n");
        }
        addLine(DIR.S, "</select><br />\n");
        if (q.getOtherChoice().length() > 0) {
            addLine(DIR.S, "<p id=\"" + q.getQuestionID() + "OtherChoiceErrorMessage\" class=\"errorText\"></p>\n");
            getTextBoxCode(q.getQuestionID() + "OtherChoice", Integer.parseInt(q.getMax()), false);
        }
    }

    public void getCheckBoxListCode(ArrayList<String> answers, Question q) {
        int ndx = 0;
        for (String answer : answers) {
            addLine(DIR.S, "<div class=\"checkbox\">\n");
            addLine(DIR.F, "<label>\n");
            addLine(DIR.F, "<input type=\"checkbox\" id=\"" + q.getQuestionID() + "_" + ndx++ + "\" value=\"" + answer + "\">\n");
            addLine(DIR.S, answer + "\n");
            addLine(DIR.B, "</label>\n");
            addLine(DIR.B, "</div>\n");
        }
        if (q.getOtherChoice().length() > 0) {
            addLine(DIR.S, "<div class=\"checkbox\">\n");
            addLine(DIR.F, "<label>\n");
            addLine(DIR.F, "<input type=\"checkbox\" id=\"" + q.getQuestionID() + "OtherChoiceSpecify\" value=\"otherChoice\" onchange=\"showHideOtherChoiceTextbox('" + q.getQuestionID() + "OtherChoiceSpecify', '" + q.getQuestionID() + "OtherChoice', 'checkbox');\">\n");
            addLine(DIR.S, q.getOtherChoice() + "\n");
            addLine(DIR.B, "</label>\n");
            addLine(DIR.S, "<p id=\"" + q.getQuestionID() + "OtherChoiceErrorMessage\" class=\"errorText\"></p>\n");
            getTextBoxCode(q.getQuestionID() + "OtherChoice", Integer.parseInt(q.getMax()), false);
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
        addLine(CodeGenerator.DIR.S, "result = meetsLengthRequirements('" + id + "', 'text', " + min + ", " + max + ") && result;\n");
    }

    public void getTextValidationCode(Question q) {
        if (q.isValidateText()) {
            String validChars = "";
            String[] allowTypes = q.getAllowTypes().split(", ");
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
                validChars += q.getValidSpecialCharacters().replace("\\", "\\\\").replace("'", "\\'");
            }
            String qID = q.getQuestionID();
            if (q.getQuestionType().equals("multipleChoice")) {
                qID += "OtherChoice";
            }
            addLine(CodeGenerator.DIR.S, "result = containsOnlyValidChars('" + qID + "', 'text', '" + validChars + "') && result;\n");
        }
    }

    public void getDecimalValidationCode(Question q) {
        switch (q.getValidationType()) {
            case "setMin":
                addLine(CodeGenerator.DIR.S, "result = meetsDecimalNumberRequirements('" + q.getQuestionID() + "', " + q.getMin() + ", '') && result;\n");
                break;
            case "setMax":
                addLine(CodeGenerator.DIR.S, "result = meetsDecimalNumberRequirements('" + q.getQuestionID() + "', '', " + q.getMax() + ") && result;\n");
                break;
            case "setMinMax":
                addLine(CodeGenerator.DIR.S, "result = meetsDecimalNumberRequirements('" + q.getQuestionID() + "', " + q.getMin() + ", " + q.getMax() + ") && result;\n");
                break;
        }
    }

    public void getWholeNumberValidationCode(Question q) {
        switch (q.getValidationType()) {
            case "setMin":
                addLine(CodeGenerator.DIR.S, "result = meetsWholeNumberRequirements('" + q.getQuestionID() + "', " + q.getMin() + ", '') && result;\n");
                break;
            case "setMax":
                addLine(CodeGenerator.DIR.S, "result = meetsWholeNumberRequirements('" + q.getQuestionID() + "', '', " + q.getMax() + ") && result;\n");
                break;
            case "setMinMax":
                addLine(CodeGenerator.DIR.S, "result = meetsWholeNumberRequirements('" + q.getQuestionID() + "', " + q.getMin() + ", " + q.getMax() + ") && result;\n");
                break;
        }
    }

    public void getMultipleChoiceValidationCode(Question q) {
        if (q.getOtherChoice().length() > 0) {
            switch (q.getDisplayType()) {
                case "checkbox":
                    addLine(CodeGenerator.DIR.S, "result = validateAnswerLimit('" + q.getQuestionID() + "', '" + q.getNumberOfAnswers() + "') && result;\n");
                case "radio":
                    addLine(CodeGenerator.DIR.S, "if ($(\"#" + q.getQuestionID() + "OtherChoiceSpecify\").is(':checked')) {\n");
                    break;
                case "dropdown":
                    addLine(CodeGenerator.DIR.S, "if ($(\"#" + q.getQuestionID() + "\").val() == \"otherChoiceSpecify\") {\n");
                    break;
            }
            spaceCount += 4;
            getMeetsLengthRequirementsCode(q.getQuestionID() + "OtherChoice", q.getMin(), q.getMax());
            getTextValidationCode(q);
            addLine(CodeGenerator.DIR.B, "}\n");
        }
    }

// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Navigation">
    public void getNextButtonCode() {
        addLine(CodeGenerator.DIR.F, "<button id=\"nextButton\" type=\"submit\" class=\"btn btn-info\" style=\"margin-bottom: 50px\">Next</button>\n");
    }

    public void getSubmitButtonCode() {
        addLine(CodeGenerator.DIR.F, "<button id=\"submitButton\" type=\"submit\" class=\"btn btn-info\" style=\"margin-bottom: 50px\">Submit</button>\n");
    }

    public void getAJAX(CodeGenerator json, boolean last) {
        addLine(CodeGenerator.DIR.F, json.code);
        addLine(CodeGenerator.DIR.S, "$.ajax({\n");
        addLine(CodeGenerator.DIR.F, "async: false,\n");
        addLine(CodeGenerator.DIR.S, "url: \"Servlet\",\n");
        addLine(CodeGenerator.DIR.S, "data: {\n");
        addLine(CodeGenerator.DIR.F, "requestType:\"" + (last ? "submit" : "nextPage") + "\",\n");
        addLine(CodeGenerator.DIR.F, "appName:$(\"header#primary > h1\").text(),\n");
        addLine(CodeGenerator.DIR.S, "columnsJSON:JSON.stringify(json)\n");
        addLine(CodeGenerator.DIR.B, "},\n");
        addLine(CodeGenerator.DIR.S, "success: function(json) {\n");
        spaceCount += 4;
        if (last) {
            addLine(CodeGenerator.DIR.S, "alert('Your answers have been recorded successfully.');\n");
        }
        addLine(CodeGenerator.DIR.S, "window.location.assign(json.redirect);\n");
        addLine(CodeGenerator.DIR.B, "}\n");
        addLine(CodeGenerator.DIR.B, "});\n");
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Database">

    public void getSaveColumnsCode(Question q) {
        String type = q.getQuestionType();
        if (type.equals("multipleChoice")) {
            type = q.getDisplayType();
        }
        addLine(CodeGenerator.DIR.S, "json." + q.getQuestionID() + " = getValueByTypeAndIDForDB('" + q.getQuestionID() + "', '" + type + "');\n");
    }

    public static void getSQLColumnDeclaration(Question q, HashMap<String, String> dbColumns) {
        String colName = q.getQuestionID();
        String colDec = "";
        switch (q.getQuestionType()) {
            case "text":
                colDec += "VARCHAR(" + q.getMax() + ")";
                break;
            case "wholeNumber":
                switch (q.getValidationType()) {
                    case "setMax":
                    case "setMinMax":
                        colDec += "DECIMAL(" + q.getMin().replace("-", "").length() + ", 0)";
                        break;
                    case "setMin":
                    default:
                        colDec += "DECIMAL(64, 0)";
                        break;
                }
                break;
            case "decimalNumber":
                switch (q.getValidationType()) {
                    case "setMax":
                    case "setMinMax":
                        int decimalPlaces = Integer.parseInt(q.getDecimalPlaces());
                        int max = Math.max(q.getMin().replace("-", "").length(), q.getMax().replace("-", "").length()) - 1;
                        colDec += "DECIMAL(" + max + ", " + decimalPlaces + ")";
                        break;
                    case "setMin":
                    default:
                        colDec += "DECIMAL(64, " + q.getDecimalPlaces() + ")";
                        break;
                }
                break;
            case "multipleChoice":
                colDec += "VARCHAR(" + q.longestAnswerChoiceLength() + ")";
                if (q.getOtherChoice().length() > 0) {
                    dbColumns.put(q.getQuestionID() + "OtherChoice", "VARCHAR(" + q.getMax() + ")");
                }
                break;
        }
        if (q.isRequired()) {
            colDec += " NOT NULL";
        }
        dbColumns.put(colName, colDec);
    }
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