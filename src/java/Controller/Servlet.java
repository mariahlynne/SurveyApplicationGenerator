package Controller;

import Model.CodeGenerator;
import Model.DatabaseAccess;
import Model.Page;
import Model.Question;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Servlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        String sFunction = request.getParameter("func");
        PrintWriter out;
        HttpSession session = request.getSession();
        int pageIndex, questionIndex, count, currentQuestionIndex, currentPageIndex, iProjectID, iPageID, iQuestionID;
        ArrayList<Page> pages;
        Page page;
        Question question;
        ResultSet rs;
        JSONObject o;

        switch (sFunction) {
            // <editor-fold defaultstate="collapsed" desc="Add Page">
            case "addPage":
                pages = (ArrayList<Page>) session.getAttribute("pages");
                count = pages.size();
                iProjectID = (int) session.getAttribute("iProjectID");
                iPageID = DatabaseAccess.InsertPage(iProjectID, "Page " + (count + 1), count);
                page = new Page(iPageID, "Page " + (count + 1), count);
                iQuestionID = DatabaseAccess.InsertQuestion(iPageID, "Question 1", 0);
                page.addQuestion(iQuestionID, "Question 1", 0);
                pages.add(page);
                session.setAttribute("pages", pages);
                break;
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Add Question">
            case "addQuestion":
                pageIndex = Integer.parseInt(request.getParameter("pageIndex"));
                pages = (ArrayList<Page>) session.getAttribute("pages");
                page = pages.get(pageIndex);
                count = page.getQuestions().size();
                iQuestionID = DatabaseAccess.InsertQuestion(page.getID(), "Question " + (count + 1), count);
                page.addQuestion(iQuestionID, "Question " + (count + 1), count);
                session.setAttribute("pages", pages);
                break;
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Remove Page">
            case "remove":
                questionIndex = Integer.parseInt(request.getParameter("questionIndex"));
                pageIndex = Integer.parseInt(request.getParameter("pageIndex"));
                pages = (ArrayList<Page>) session.getAttribute("pages");
                page = pages.get(pageIndex);
                if ((questionIndex == -1) || (page.getQuestions().size() == 1)) {
                    DatabaseAccess.DeletePage(page.getID());
                    pages.remove(pageIndex);
                } else {
                    DatabaseAccess.DeleteQuestion(page.getQuestionIDByIndex(questionIndex));
                    page.getQuestions().remove(questionIndex);
                    pages.set(pageIndex, page);
                }
                reindex(pages);
                session.setAttribute("pages", pages);

                //send back full (inner) html of tree
                response.setContentType("text/html;charset=UTF-8");
                out = response.getWriter();
                for (Page p : pages) {
                    pageIndex = p.getPageIndex();
                    out.println("<li><i class=\"icon-li icon-minus collapsible clickable\"></i><a href=\"javascript:switchNode(" + pageIndex + ", -1);\"> Page " + (pageIndex + 1) + "</a>");
                    out.println("<ul>");
                    for (Question q : p.getQuestions()) {
                        questionIndex = q.getQuestionIndex();
                        out.println("<li><a href=\"javascript:switchNode(" + pageIndex + ", " + questionIndex + ");\">Question " + (questionIndex + 1) + "</a></li>");
                    }
                    out.println("</ul>");
                    out.println("</li>");
                }
                break;
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Reorder Pages">
            case "reorderPages":
                break;
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Reorder Questions">
            case "reorderQuestions":
                break;
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Switch Node">
            case "switch":
                questionIndex = Integer.parseInt(request.getParameter("questionIndex"));
                pageIndex = Integer.parseInt(request.getParameter("pageIndex"));
                currentQuestionIndex = Integer.parseInt(request.getParameter("currentQuestionIndex"));
                currentPageIndex = Integer.parseInt(request.getParameter("currentPageIndex"));
                pages = (ArrayList<Page>) session.getAttribute("pages");

                if (currentQuestionIndex != -1) {
                    page = pages.get(currentPageIndex);
                    question = page.getQuestions().get(currentQuestionIndex);
                    if (page.getID() != -1 && question.getID() != -1) {
                        Question oldQuestion = question;
                        JSONParser p = new JSONParser();
                        o = (JSONObject) p.parse(request.getParameter("settingsJSON").toString());
                        question.clearAll();
                        question.setID(oldQuestion.getID());
                        question.setName(oldQuestion.getName());
                        question.setQuestionIndex(oldQuestion.getQuestionIndex());
                        question.setQuestionText(o.get("questionText").toString());
                        question.setQuestionID(o.get("questionName").toString());
                        question.setRequired((Boolean) o.get("isRequired"));
                        question.setQuestionType(o.get("questionType").toString());
                        switch (question.getQuestionType()) {
                            case "text":
                                question.setMin(o.get("min").toString());
                                question.setMax(o.get("max").toString());
                                question.setValidateText((Boolean) o.get("validateText"));
                                if (question.isValidateText()) {
                                    question.setAllowTypes(o.get("allowUpper").toString() + ", " + o.get("allowLower").toString() + ", " + o.get("allowDigits").toString() + ", " + o.get("allowSpecial").toString());
                                    question.setValidSpecialCharacters(o.get("validSpecialCharacters").toString());
                                }
                                break;
                            case "decimalNumber":
                                question.setDecimalPlaces(o.get("decimalPlaces").toString());
                                case "wholeNumber":
                                question.setValidationType(o.get("validationType").toString());
                                question.setMin(o.get("min").toString());
                                question.setMax(o.get("max").toString());
                                break;
                            case "multipleChoice":
                                question.setAnswerChoices(o.get("answerChoices").toString());
                                question.setDisplayType(o.get("displayType").toString());
                                question.setNumberOfAnswers(o.get("numberOfAnswers").toString());
                                question.setOtherChoice(o.get("otherChoice").toString());
                                if (question.getOtherChoice().length() > 0) {
                                    question.setMin(o.get("min").toString());
                                    question.setMax(o.get("max").toString());
                                    question.setValidateText((Boolean) o.get("validateText"));
                                    if (question.isValidateText()) {
                                        question.setAllowTypes(o.get("allowUpper").toString() + ", " + o.get("allowLower").toString() + ", " + o.get("allowDigits").toString() + ", " + o.get("allowSpecial").toString());
                                        question.setValidSpecialCharacters(o.get("validSpecialCharacters").toString());
                                    }
                                }
                        }
                        DatabaseAccess.UpdateQuestion(question);
                        session.setAttribute("pages", pages);
                    }
                }

                if (questionIndex != -1) {
                    page = pages.get(pageIndex);
                    question = page.getQuestions().get(questionIndex);
                    o = new JSONObject();
                    o.put("questionText", question.getQuestionText());
                    o.put("questionName", question.getQuestionID());
                    o.put("isRequired", question.isRequired());
                    o.put("questionType", question.getQuestionType());
                    o.put("min", question.getMin());
                    o.put("max", question.getMax());
                    o.put("validateText", question.isValidateText());
                    o.put("allowUpper", question.getAllowTypes().split(", ")[0]);
                    o.put("allowLower", question.getAllowTypes().split(", ")[1]);
                    o.put("allowDigits", question.getAllowTypes().split(", ")[2]);
                    o.put("allowSpecial", question.getAllowTypes().split(", ")[3]);
                    o.put("validSpecialCharacters", question.getValidSpecialCharacters());
                    o.put("validationType", question.getValidationType());
                    o.put("decimalPlaces", question.getDecimalPlaces());
                    o.put("answerChoices", question.getAnswerChoices());
                    o.put("displayType", question.getDisplayType());
                    o.put("numberOfAnswers", question.getNumberOfAnswers());
                    o.put("otherChoice", question.getOtherChoice());

                    response.setContentType("application/json");
                    response.getWriter().write(o.toJSONString());
                }
                break;

            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Switch Project">
            case "switchProject":
                iProjectID = Integer.parseInt(request.getParameter("projectID"));
                String title = request.getParameter("title");
                pages = new ArrayList<Page>();
                if (iProjectID == -1) {
                    iProjectID = DatabaseAccess.InsertSurveyApplication(title);
                    iPageID = DatabaseAccess.InsertPage(iProjectID, "Page 1", 0);
                    page = new Page(iPageID, "Page 1", 0);
                    iQuestionID = DatabaseAccess.InsertQuestion(iPageID, "Question 1", 0);
                    page.addQuestion(iQuestionID, "Question 1", 0);
                    pages.add(page);
                } else {
                    ResultSet rsQuestions;
                    rs = DatabaseAccess.ListPages(iProjectID);
                    try {
                        while (rs.next()) {
                            page = new Page(rs.getInt("iPageID"), getNullSafeString(rs.getString("vchName")), rs.getInt("iIndex"));
                            rsQuestions = DatabaseAccess.ListQuestions(page.getID());
                            while (rsQuestions.next()) {
                                question = new Question(rsQuestions.getInt("iQuestionID"), getNullSafeString(rsQuestions.getString("vchName")),
                                        rsQuestions.getInt("iIndex"));
                                question.setQuestionText(getNullSafeString(rsQuestions.getString("vchQuestionText")));
                                question.setQuestionID(getNullSafeString(rsQuestions.getString("vchQuestionID")));
                                question.setQuestionType(getNullSafeString(rsQuestions.getString("vchQuestionType")));
                                question.setRequired(rsQuestions.getBoolean("bRequired"));
                                question.setMin(getNullSafeString(rsQuestions.getString("vchMin")));
                                question.setMax(getNullSafeString(rsQuestions.getString("vchMax")));
                                question.setValidateText(rsQuestions.getBoolean("bValidateText"));
                                question.setAllowTypes(rsQuestions.getString("vchAllowTypes"));
                                question.setValidSpecialCharacters(getNullSafeString(rsQuestions.getString("vchValidSpecialCharacters")));
                                question.setDecimalPlaces(getNullSafeString(rsQuestions.getString("vchDecimalPlaces")));
                                question.setValidationType(getNullSafeString(rsQuestions.getString("vchValidationType")));
                                question.setAnswerChoices(getNullSafeString(rsQuestions.getString("vchAnswerChoices")));
                                question.setOtherChoice(getNullSafeString(rsQuestions.getString("vchOtherChoice")));
                                question.setDisplayType(getNullSafeString(rsQuestions.getString("vchDisplayType")));
                                question.setNumberOfAnswers(getNullSafeString(rsQuestions.getString("vchNumberOfAnswers")));
                                page.getQuestions().add(question);
                            }
                            pages.add(page);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                session.setAttribute("pages", pages);
                session.setAttribute("iProjectID", iProjectID);
                session.setAttribute("sProjectTitle", title);
                o = new JSONObject();
                o.put("projectTitle", title);
                o.put("projectID", iProjectID);
                page = pages.get(0);
                question = page.getQuestions().get(0);
                o.put("questionText", question.getQuestionText());
                o.put("questionName", question.getQuestionID());
                o.put("isRequired", question.isRequired());
                o.put("questionType", question.getQuestionType());
                o.put("min", question.getMin());
                o.put("max", question.getMax());
                o.put("validateText", question.isValidateText());
                o.put("allowUpper", question.getAllowTypes().split(", ")[0]);
                o.put("allowLower", question.getAllowTypes().split(", ")[1]);
                o.put("allowDigits", question.getAllowTypes().split(", ")[2]);
                o.put("allowSpecial", question.getAllowTypes().split(", ")[3]);
                o.put("validSpecialCharacters", question.getValidSpecialCharacters());
                o.put("validationType", question.getValidationType());
                o.put("decimalPlaces", question.getDecimalPlaces());
                o.put("answerChoices", question.getAnswerChoices());
                o.put("displayType", question.getDisplayType());
                o.put("numberOfAnswers", question.getNumberOfAnswers());
                o.put("otherChoice", question.getOtherChoice());

                //send back full (inner) html of tree
                String treeHTML = "";
                for (Page p : pages) {
                    pageIndex = p.getPageIndex();
                    treeHTML += "<li><i class=\"icon-li icon-minus collapsible clickable\"></i><a href=\"javascript:switchNode(" + pageIndex + ", -1);\"> Page " + (pageIndex + 1) + "</a>\n";
                    treeHTML += "<ul>\n";
                    for (Question q : p.getQuestions()) {
                        questionIndex = q.getQuestionIndex();
                        treeHTML += "<li><a href=\"javascript:switchNode(" + pageIndex + ", " + questionIndex + ");\">Question " + (questionIndex + 1) + "</a></li>\n";
                    }
                    treeHTML += "</ul>\n";
                    treeHTML += "</li>\n";
                }
                o.put("treeHTML", treeHTML);

                response.setContentType("application/json");
                response.getWriter().write(o.toJSONString());
                break;

            //</editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Validate">
            case "validate":
                pages = (ArrayList<Page>) session.getAttribute("pages");
                currentQuestionIndex = Integer.parseInt(request.getParameter("currentQuestionIndex"));
                currentPageIndex = Integer.parseInt(request.getParameter("currentPageIndex"));
                String invalidNodes = "";
                String errorMessage = "";
                o = new JSONObject();
                for (Page p : pages) {
                    for (Question q : p.getQuestions()) {
                        if (q.getQuestionText().trim().length() == 0) {
                            errorMessage += "<li>Question Text is required</li>";
                        }
                        if (q.getQuestionID().trim().length() == 0) {
                            errorMessage += "<li>Question ID is required</li>";
                        } else if (!q.getQuestionID().matches("^[a-zA-Z0-9_]+$")) {
                            errorMessage += "<li>Question ID can only contain letters, digits, and underscores</li>";
                        }
                        switch (q.getQuestionType()) {
                            case "none":
                                errorMessage += "<li>Question Type is required</li>";
                                break;
                            case "text":
                                if (q.getMin().length() == 0) {
                                    errorMessage += "<li>Character minimum is required</li>";
                                } else if (!q.getMin().matches("^[0-9]+$")) {
                                    errorMessage += "<li>Character minimum must be a whole number</li>";
                                }
                                if (q.getMax().length() == 0) {
                                    errorMessage += "<li>Character maximum is required</li>";
                                } else if (!q.getMax().matches("^[0-9]+$")) {
                                    errorMessage += "<li>Character maximum must be a whole number</li>";
                                }
                                try {
                                    if (Integer.parseInt(q.getMin()) > Integer.parseInt(q.getMax())) {
                                        errorMessage += "<li>Character minimum cannot be more than character maximum</li>";
                                    } else if (Integer.parseInt(q.getMax()) == 0) {
                                        errorMessage += "<li>Character maximum must be greater than 0</li>";
                                    } else if (Integer.parseInt(q.getMin()) == 0 && q.isRequired()) {
                                        errorMessage += "<li>Character minimum cannot be 0 when the question is required</li>";
                                    }
                                } catch (Exception ex) {
                                }
                                if (q.isValidateText()) {
                                    if (!q.getAllowTypes().contains("true")) {
                                        errorMessage += "<li>Because validate text is selected, at least one character type to allow must be selected</li>";
                                    } else if (q.getAllowTypes().split(", ")[3].equals("true") && q.getValidSpecialCharacters().length() == 0) {
                                        errorMessage += "<li>The special characters textbox cannot be empty when allow special characters is checked";
                                    }
                                }
                                break;
                            case "wholeNumber":
                                switch (q.getValidationType()) {
                                    case "none":
                                        break;
                                    case "setMin":
                                        if (q.getMin().length() == 0) {
                                            errorMessage += "<li>Minimum is required</li>";
                                        } else if (!q.getMin().matches("^-?[0-9]+$")) {
                                            errorMessage += "<li>Minimum must be a whole number</li>";
                                        }

                                        break;
                                    case "setMax":
                                        if (q.getMax().length() == 0) {
                                            errorMessage += "<li>Maximum is required</li>";
                                        } else if (!q.getMax().matches("^-?[0-9]+$")) {
                                            errorMessage += "<li>Maximum must be a whole number</li>";
                                        }
                                        try {
                                            if (Integer.parseInt(q.getMax()) == 0) {
                                                errorMessage += "<li>Maximum must be greater than 0</li>";
                                            }
                                        } catch (Exception ex) {
                                        }
                                        break;
                                    case "setMinMax":
                                        if (q.getMin().length() == 0) {
                                            errorMessage += "<li>Minimum is required</li>";
                                        } else if (!q.getMin().matches("^-?[0-9]+$")) {
                                            errorMessage += "<li>Minimum must be a whole number</li>";
                                        }
                                        if (q.getMax().length() == 0) {
                                            errorMessage += "<li>Maximum is required</li>";
                                        } else if (!q.getMax().matches("^-?[0-9]+$")) {
                                            errorMessage += "<li>Maximum must be a whole number</li>";
                                        }
                                        try {
                                            if (Integer.parseInt(q.getMin()) > Integer.parseInt(q.getMax())) {
                                                errorMessage += "<li>Minimum cannot be more than maximum</li>";
                                            }
                                        } catch (Exception ex) {
                                        }
                                        break;
                                }
                                break;
                            case "decimalNumber":
                                switch (q.getValidationType()) {
                                    case "none":
                                        break;
                                    case "setMin":
                                        if (q.getMin().length() == 0) {
                                            errorMessage += "<li>Minimum is required</li>";
                                        } else if (!q.getMin().matches("^-?[0-9]+(\\.[0-9]*)?$")) {
                                            errorMessage += "<li>Minimum must be a decimal number</li>";
                                        }

                                        break;
                                    case "setMax":
                                        if (q.getMax().length() == 0) {
                                            errorMessage += "<li>Maximum is required</li>";
                                        } else if (!q.getMax().matches("^-?[0-9]+(\\.[0-9]*)?$")) {
                                            errorMessage += "<li>Maximum must be a decimal number</li>";
                                        }
                                        break;
                                    case "setMinMax":
                                        if (q.getMin().length() == 0) {
                                            errorMessage += "<li>Minimum is required</li>";
                                        } else if (!q.getMin().matches("^-?[0-9]+(\\.[0-9]*)?$")) {
                                            errorMessage += "<li>Minimum must be a decimal number</li>";
                                        }
                                        if (q.getMax().length() == 0) {
                                            errorMessage += "<li>Maximum is required</li>";
                                        } else if (!q.getMax().matches("^-?[0-9]+(\\.[0-9]*)?$")) {
                                            errorMessage += "<li>Maximum must be a decimal number</li>";
                                        }
                                        try {
                                            if (Double.parseDouble(q.getMin()) > Double.parseDouble(q.getMax())) {
                                                errorMessage += "<li>Minimum cannot be more than maximum</li>";
                                            }
                                        } catch (Exception ex) {
                                        }
                                        break;
                                }
                                break;
                            case "multipleChoice":
                                if (q.getAnswerChoices().length() == 0) {
                                    errorMessage += "<li>There must be at least one answer choice</li>";
                                }
                                if (q.getOtherChoice().length() > 0) {
                                    if (q.getMin().length() == 0) {
                                        errorMessage += "<li>Character minimum is required</li>";
                                    } else if (!q.getMin().matches("^[0-9]+$")) {
                                        errorMessage += "<li>Character minimum must be a whole number</li>";
                                    }
                                    if (q.getMax().length() == 0) {
                                        errorMessage += "<li>Character maximum is required</li>";
                                    } else if (!q.getMax().matches("^[0-9]+$")) {
                                        errorMessage += "<li>Character maximum must be a whole number</li>";
                                    }
                                    try {
                                        if (Integer.parseInt(q.getMin()) > Integer.parseInt(q.getMax())) {
                                            errorMessage += "<li>Character minimum cannot be more than character maximum</li>";
                                        } else if (Integer.parseInt(q.getMax()) == 0) {
                                            errorMessage += "<li>Character maximum must be greater than 0</li>";
                                        } else if (Integer.parseInt(q.getMin()) == 0 && q.isRequired()) {
                                            errorMessage += "<li>Character minimum cannot be 0</li>";
                                        }
                                    } catch (Exception ex) {
                                    }
                                    if (q.isValidateText()) {
                                        if (!q.getAllowTypes().contains("true")) {
                                            errorMessage += "<li>Because validate text is selected, at least one character type to allow must be selected</li>";
                                        } else if (q.getAllowTypes().split(", ")[3].equals("true") && q.getValidSpecialCharacters().length() == 0) {
                                            errorMessage += "<li>The special characters textbox cannot be empty when allow special characters is checked";
                                        }
                                    }
                                }
                                break;
                        }
                        if (errorMessage.length() > 0) {
                            invalidNodes += p.getPageIndex() + "," + q.getQuestionIndex() + ";";
                        }
                        if (currentQuestionIndex == q.getQuestionIndex() && currentPageIndex == p.getPageIndex()) {
                            o.put("errorMessage", errorMessage);

                        }
                        errorMessage = "";
                    }
                }

                o.put("invalidNodes", invalidNodes);
                response.setContentType("application/json");
                response.getWriter().write(o.toJSONString());
                break;
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Generate Application">

            case "generateApplication":
                String javascript = "";
                int pageCount = 1;
                int questionCount = 1;
                int validationCount = 0;
                pages = (ArrayList<Page>) session.getAttribute("pages");
                BufferedWriter bw;
                CodeGenerator body;
                CodeGenerator partialJS;
                CodeGenerator json;
                HashMap<String, String> dbColumns = new HashMap<String, String>();
                for (Page p : pages) {
                    body = new CodeGenerator();
                    partialJS = new CodeGenerator();
                    json = new CodeGenerator();
                    json.spaceCount -= 4;
                    json.addLine(CodeGenerator.DIR.F, "var json = new Object();\n");
                    json.spaceCount += 8;
                    body.getPageHeader(pageCount, session.getAttribute("sProjectTitle").toString());
                    partialJS.addLine(CodeGenerator.DIR.S, "function validatePage" + pageCount + "() {\n");
                    partialJS.addLine(CodeGenerator.DIR.F, "$(\".errorText\").hide();\n");
                    partialJS.addLine(CodeGenerator.DIR.S, "var result = true;\n");
                    body.addLine(CodeGenerator.DIR.F, "<table id=\"mainContent\">\n");
                    body.spaceCount += 4;
                    for (Question q : p.getQuestions()) {
                        CodeGenerator.getSQLColumnDeclaration(q, dbColumns);
                        json.getSaveColumnsCode(q);
                        body.addLine(CodeGenerator.DIR.S, "<tr>\n");
                        body.addLine(CodeGenerator.DIR.F, "<td>\n");
                        body.addLine(CodeGenerator.DIR.F, "<p id=\"" + q.getQuestionID() + "ErrorMessage\" class=\"errorText\"></p>\n");
                        body.addLine(CodeGenerator.DIR.S, "<span class=\"questionText\">" + questionCount++ + ". " + q.getQuestionText() + "</span>\n");
                        body.addLine(CodeGenerator.DIR.S, "<div class=\"question\">\n");
                        body.spaceCount += 4;
                        if (q.isRequired()) {
                            String type = q.getQuestionType();
                            if (type.equals("multipleChoice")) {
                                type = q.getDisplayType();
                            }
                            partialJS.addLine(CodeGenerator.DIR.S, "var v" + ++validationCount + " = isNotEmpty('" + q.getQuestionID() + "', '" + type + "', true);\n");
                            partialJS.addLine(CodeGenerator.DIR.S, "result = v" + validationCount + " && result;\n");
                            partialJS.addLine(CodeGenerator.DIR.S, "if (v" + validationCount + ") {\n");
                            partialJS.spaceCount += 4;
                        } else {
                            partialJS.addLine(CodeGenerator.DIR.S, "var v" + ++validationCount + " = isNotEmpty('" + q.getQuestionID() + "', '" + q.getQuestionType() + "', false);\n");
                            partialJS.addLine(CodeGenerator.DIR.S, "if (v" + validationCount + ") {\n");
                            partialJS.spaceCount += 4;
                        }
                        switch (q.getQuestionType()) {
                            case "text":
                                body.getTextBoxCode(q.getQuestionID(), Integer.parseInt(q.getMax()), true);
                                partialJS.getMeetsLengthRequirementsCode(q.getQuestionID(), q.getMin(), q.getMax());
                                partialJS.getTextValidationCode(q);
                                break;
                            case "multipleChoice":
                                ArrayList<String> answers = new ArrayList<String>();
                                for (String answer : q.getAnswerChoices().split(";;")) {
                                    if (!answer.equals("")) {
                                        answers.add(answer);
                                    }
                                }
                                body.getMultipleChoiceCode(answers, q);
                                partialJS.getMultipleChoiceValidationCode(q);
                                break;
                            case "wholeNumber":
                                body.getWholeNumberCode(q.getQuestionID());
                                partialJS.getWholeNumberValidationCode(q);
                                break;
                            case "decimalNumber":
                                body.getDecimalNumberCode(q.getQuestionID(), Integer.parseInt(q.getDecimalPlaces()));
                                partialJS.getDecimalValidationCode(q);
                                break;
                        }
                        partialJS.addLine(CodeGenerator.DIR.B, "}\n");
                        body.addLine(CodeGenerator.DIR.B, "</div>\n");
                        body.addLine(CodeGenerator.DIR.B, "</td>\n");
                        body.addLine(CodeGenerator.DIR.B, "</tr>\n");
                    }
                    partialJS.addLine(CodeGenerator.DIR.S, "if (result) {\n");
                    partialJS.getAJAX(json, pageCount == pages.size());
                    partialJS.addLine(CodeGenerator.DIR.B, "}\n");
                    partialJS.addLine(CodeGenerator.DIR.S, "return false;\n");
                    partialJS.addLine(CodeGenerator.DIR.B, "}\n");
                    javascript += partialJS.code + "\n";
                    body.addLine(CodeGenerator.DIR.S, "<tr>\n");
                    body.addLine(CodeGenerator.DIR.F, "<td align=\"center\">\n");
                    if (pageCount < pages.size()) {
                        body.getNextButtonCode();
                    } else {
                        body.getSubmitButtonCode();
                    }
                    body.addLine(CodeGenerator.DIR.B, "</td>\n");
                    body.addLine(CodeGenerator.DIR.B, "</tr>\n");
                    body.addLine(CodeGenerator.DIR.B, "</table>\n");
                    body.addLine(CodeGenerator.DIR.B, "</form>\n");
                    body.addLine(CodeGenerator.DIR.B, "</body>\n");
                    body.addLine(CodeGenerator.DIR.B, "</html>");
                    bw = new BufferedWriter(new FileWriter("C:\\Users\\Mariah\\Documents\\NetBeansProjects\\PracticeApplication\\web\\Page" + pageCount + ".jsp"));
                    bw.write(body.code);
                    bw.flush();
                    bw.close();
                    pageCount++;
                }
                bw = new BufferedWriter(new FileWriter("C:\\Users\\Mariah\\Documents\\NetBeansProjects\\PracticeApplication\\web\\js\\main.js"));
                bw.write(javascript);
                bw.flush();
                bw.close();
                DatabaseAccess.CreateTable(session.getAttribute("sProjectTitle").toString(), dbColumns);
                break;
            //</editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Rename Project">
            case "renameProject":
                DatabaseAccess.renameProject(Integer.parseInt(session.getAttribute("iProjectID").toString()),
                        request.getParameter("newProjectName").toString());
                session.setAttribute("sProjectTitle", request.getParameter("newProjectName").toString());
                break;
            //</editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Copy Project">
            case "copyProject":
                iProjectID = DatabaseAccess.copyProject(Integer.parseInt(session.getAttribute("iProjectID").toString()),
                        request.getParameter("newProjectName").toString());
                o = new JSONObject();
                o.put("projectID", iProjectID);
                response.setContentType("application/json");
                response.getWriter().write(o.toJSONString());
                break;
            //</editor-fold>
        }
    }
//                jObject.put("previousNodeType", session.getAttribute("currentNodeType").toString());
//                jObject.put("previousPageIndex", new Integer(session.getAttribute("currentPageIndex").toString()));
//                jObject.put("previousQuestionIndex", new Integer(session.getAttribute("currentQuestionIndex").toString()));

//                System.out.println("switching from page: " + session.getAttribute("currentPageIndex").toString() + ", question: " + session.getAttribute("currentQuestionIndex").toString());
//                System.out.println("to page: " + request.getParameter("pageIndex") + ", question: " + request.getParameter("questionIndex"));
    //        response.setContentType("text/html;charset=UTF-8");
//        PrintWriter out = response.getWriter();
//        try {
//            /* TODO output your page here. You may use following sample code. */
//            out.println("<html>");
//            out.println("<head>");
//            out.println("<title>Servlet Servlet</title>");
//            out.println("</head>");
//            out.println("<body>");
//            out.println("<h1>Servlet Servlet at " + request.getContextPath() + "</h1>");
//            out.println("</body>");
//            out.println("</html>");
//        } finally {
//            out.close();
//        }
// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);


        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);


        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void reindex(ArrayList<Page> pages) {
        int pageIndex = 0;
        int questionIndex;
        for (Page p : pages) {
            p.setPageIndex(pageIndex++);
            questionIndex = 0;
            for (Question q : p.getQuestions()) {
                q.setQuestionIndex(questionIndex++);
            }
        }
    }

    private String getNullSafeString(String s) {
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }
}
