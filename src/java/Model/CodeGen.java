package Model;

import java.util.ArrayList;

public class CodeGen {

    static String spaces = "                                                                                                                       ";
    public int spaceCount = 0;
    public String code = "";

    public enum DIR {

        F, S, B, FB
    }

    // <editor-fold defaultstate="collapsed" desc="HTML Elements (with bootstrap)">
    public void getMultipleChoiceCode(ArrayList<String> answers, String id, String type, String otherChoice) {
        //incorporate the "other" answer & its validation
        switch (type) {
            case "radio":
                getRadioButtonListCode(answers, id, otherChoice);
                break;
            case "dropdown":
                getDropDownListCode(answers, id, otherChoice);
                break;
            case "checkbox":
                getCheckBoxListCode(answers, id, otherChoice);
                break;
        }
    }

    public void getRadioButtonListCode(ArrayList<String> answers, String id, String otherChoice) {
        int ndx = 0;
        for (String answer : answers) {
            addLine(DIR.S, "<div class=\"radio\">\n");
            addLine(DIR.F, "<label>\n");
            addLine(DIR.F, "<input type=\"radio\" id=\"" + id + "_" + ndx + "\" name=\"" + id + "\" value=\"" + answer + "\" onchange=\"showHideOtherChoiceTextbox('" + id + "_" + ndx + "', '" + id + "_" + ndx++ + "OtherChoice', 'radio');\">\n");
            addLine(DIR.S, answer + "\n");
            addLine(DIR.B, "</label>\n");
            addLine(DIR.B, "</div>\n");
        }
        if (otherChoice.length() > 0) {
            addLine(DIR.S, "<div class=\"radio\">\n");
            addLine(DIR.F, "<label>\n");
            addLine(DIR.F, "<input type=\"radio\" id=\"" + id + "otherChoice(specify)\" name=\"" + id + "\" value=\"" + otherChoice + "\" onchange=\"showHideOtherChoiceTextbox('" + id + "_" + ndx + "', '" + id + "_" + ndx + "OtherChoice', 'radio');\">\n");
            addLine(DIR.S, otherChoice + "\n");
            addLine(DIR.B, "</label>\n");
            addLine(DIR.B, "</div>\n");
            addLine(DIR.S, "<input type=\"text\" id=\"" + id + "OtherChoice\" style=\"display: none;\" />\n");
        }
    }

    public void getDropDownListCode(ArrayList<String> answers, String id, String otherChoice) {
        addLine(DIR.S, "<select class=\"form-control\" id=\"" + id + "\" onchange=\"showHideOtherChoiceTextbox('" + id + "', '" + id + "OtherChoice', 'checkbox');\">\n");
        for (String answer : answers) {
            addLine(DIR.FB, "<option>" + answer + "</option>\n");
        }
        if (otherChoice.length() > 0) {
            addLine(DIR.FB, "<option value=\"otherChoice(specify)\">" + otherChoice + "</option>");
        }
        addLine(DIR.S, "</select>\n");
        if (otherChoice.length() > 0) {
            addLine(DIR.S, "<input type=\"text\" id=\"" + id + "OtherChoice\" style=\"display: none;\" />\n");
        }
    }

    public void getCheckBoxListCode(ArrayList<String> answers, String id, String otherChoice) {
        int ndx = 0;
        for (String answer : answers) {
            addLine(DIR.S, "<div class=\"checkbox\">\n");
            addLine(DIR.F, "<label>\n");
            if (otherChoice.length() > 0) {
                addLine(DIR.F, "<input type=\"checkbox\" id=\"" + id + "_" + ndx + "\" value=\"" + answer + "\" onchange=\"showHideOtherChoiceTextbox('" + id + "_" + ndx + "', '" + id + "_" + ndx + "OtherChoice', 'checkbox');\">\n");
            } else {
                addLine(DIR.F, "<input type=\"checkbox\" id=\"" + id + "_" + ndx++ + "\" value=\"" + answer + "\">\n");
            }
            addLine(DIR.S, answer + "\n");
            addLine(DIR.B, "</label>\n");
            if (otherChoice.length() > 0) {
                addLine(DIR.S, "<option value=\"otherChoice(specify)\">" + otherChoice + "</option>");
                addLine(DIR.S, "<input type=\"text\" id=\"" + id + "OtherChoice\" style=\"display: none;\" />\n");
            }
            addLine(DIR.B, "</div>\n");
        }
    }

    public void getTextBoxCode(String id, int maxlength) {
        if (maxlength > 100) {
            addLine(DIR.S, "<textarea id=\"" + id + "\" rows=\"3\" maxlength=\"" + maxlength + "\"></textarea>\n");
        } else {
            addLine(DIR.S, "<input type=\"text\" id=\"" + id + "\" class=\"form-control\" maxlength=\"" + maxlength + "\" />\n");
        }
    }

    public void getWholeNumberCode(String id) {
        addLine(DIR.S, "<input type=\"text\" id=\"" + id + "\" class=\"form-control onlyAllowNumbers\" />\n");
    }

    public void getDecimalNumberCode(String id, int decimalPlaces) {
        addLine(DIR.S, "<input type=\"text\" id=\"" + id + "\" class=\"form-control onlyAllowDecimalNumbers\" onblur=\"padDecimalPointPlaces('" + id + "', " + decimalPlaces + ")\" />\n");
    }

    public void getTextAreaCode(String id) {
        addLine(DIR.S, "<textarea id=\"" + id + "\" class=\"form-control\" rows=\"3\"></textarea>\n");
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Validation">
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Navigation">
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