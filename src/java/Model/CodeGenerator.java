package Model;

import java.util.ArrayList;

public class CodeGenerator {

    // <editor-fold defaultstate="collapsed" desc="HTML Elements (with bootstrap)">
    public static String getMultipleChoiceCode(ArrayList<String> answers, String id, String type) {
        //incorporate the "other" answer & its validation
        String result = "";
        switch (result) {
            case "radio":
                result += getRadioButtonListCode(answers, id);
                break;
            case "dropdown":
                result += getDropDownListCode(answers, id);
                break;
            case "checkbox":
                result += getCheckBoxListCode(answers, id);
                break;
        }
        return result;
    }

    public static String getRadioButtonListCode(ArrayList<String> answers, String id) {
        String result = "";
        for (String answer : answers) {
            result += "<div class=\"radio\">"
                    + "    <label>"
                    + "        <input type=\"radio\" name=\"" + id + "\" value=\"" + answer + "\">"
                    + answer
                    + "      </label>"
                    + "</div>";
        }
        return result;
    }

    public static String getDropDownListCode(ArrayList<String> answers, String id) {
        String result = "<select class=\"form-control\" id=\"" + id + "\">";
        for (String answer : answers) {
            result += "<option>" + answer + "</option>";
        }
        result += "</select>";
        return result;
    }

    public static String getCheckBoxListCode(ArrayList<String> answers, String id) {
        String result = "";
        int ndx = 0;
        for (String answer : answers) {
            result += "<div class=\"checkbox\">"
                    + "    <label>"
                    + "        <input type=\"checkbox\" id=\"" + ndx++ + "\" value=\"" + answer + "\">"
                    + answer
                    + "    </label>"
                    + "</div>";
        }
        return result;
    }

    public static String getTextBoxCode(String id) {
        String result = "<input type=\"text\" id=\"" + id + "\" class=\"form-control\">";
        return result;
    }

    public static String getTextAreaCode(String id) {
        String result = "<textarea id=\"" + id + "\" class=\"form-control\" rows=\"3\"></textarea>";
        return result;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Validation">
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Navigation">
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Database">
    // </editor-fold>
}
