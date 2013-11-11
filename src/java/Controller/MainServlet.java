package Controller;

import Model.CodeGen;
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

public class MainServlet extends HttpServlet {

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
                count = page.questions.size();
                iQuestionID = DatabaseAccess.InsertQuestion(page.ID, "Question " + (count + 1), count);
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
                if ((questionIndex == -1) || (page.questions.size() == 1)) {
                    DatabaseAccess.DeletePage(page.ID);
                    pages.remove(pageIndex);
                } else {
                    DatabaseAccess.DeleteQuestion(page.getQuestionIDByIndex(questionIndex));
                    page.questions.remove(questionIndex);
                    pages.set(pageIndex, page);
                }
                reindex(pages);
                session.setAttribute("pages", pages);

                //send back full (inner) html of tree
                response.setContentType("text/html;charset=UTF-8");
                out = response.getWriter();
                for (Page p : pages) {
                    pageIndex = p.pageIndex;
                    out.println("<li><i class=\"icon-li icon-minus collapsible clickable\"></i><a href=\"javascript:switchNode(" + pageIndex + ", -1);\"> Page " + (pageIndex + 1) + "</a>");
                    out.println("<ul>");
                    for (Question q : p.questions) {
                        questionIndex = q.questionIndex;
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
                    question = page.questions.get(currentQuestionIndex);
                    if (page.ID != -1 && question.ID != -1) {
                        Question oldQuestion = question;
                        JSONParser p = new JSONParser();
                        o = (JSONObject) p.parse(request.getParameter("settingsJSON").toString());
                        question.clearAll();
                        question.ID = oldQuestion.ID;
                        question.name = oldQuestion.name;
                        question.questionIndex = oldQuestion.questionIndex;
                        question.questionText = o.get("questionText").toString();
                        question.questionID = o.get("questionName").toString();
                        question.isRequired = (Boolean) o.get("isRequired");
                        question.questionType = o.get("questionType").toString();
                        switch (question.questionType) {
                            case "text":
                                question.min = o.get("min").toString();
                                question.max = o.get("max").toString();
                                question.validateText = (Boolean) o.get("validateText");
                                if (question.validateText) {
                                    question.allowTypes = o.get("allowUpper").toString() + ", " + o.get("allowLower").toString() + ", " + o.get("allowDigits").toString() + ", " + o.get("allowSpecial").toString();
                                    question.validSpecialCharacters = o.get("validSpecialCharacters").toString();
                                }
                                break;
                            case "wholeNumber":
                                question.min = o.get("min").toString();
                                question.max = o.get("max").toString();
                                question.validationType = o.get("validationType").toString();
                                break;
                            case "decimalNumber":
                                question.min = o.get("min").toString();
                                question.max = o.get("max").toString();
                                question.decimalPlaces = o.get("decimalPlaces").toString();
                                question.validationType = o.get("validationType").toString();
                                break;
                            case "multipleChoice":
                                question.answerChoices = o.get("answerChoices").toString();
                                question.displayType = o.get("displayType").toString();
                                question.numberOfAnswers = o.get("numberOfAnswers").toString();
                                question.otherChoice = o.get("otherChoice").toString();
                                if (question.otherChoice.length() > 0) {
                                    question.min = o.get("min").toString();
                                    question.max = o.get("max").toString();
                                    question.validateText = (Boolean) o.get("validateText");
                                    if (question.validateText) {
                                        question.allowTypes = o.get("allowUpper").toString() + ", " + o.get("allowLower").toString() + ", " + o.get("allowDigits").toString() + ", " + o.get("allowSpecial").toString();
                                        question.validSpecialCharacters = o.get("validSpecialCharacters").toString();
                                    }
                                }
                        }
                        DatabaseAccess.UpdateQuestion(question);
                        session.setAttribute("pages", pages);
                    }
                }

                if (questionIndex != -1) {
                    page = pages.get(pageIndex);
                    question = page.questions.get(questionIndex);
                    o = new JSONObject();
                    o.put("questionText", question.questionText);
                    o.put("questionName", question.questionID);
                    o.put("isRequired", question.isRequired);
                    o.put("questionType", question.questionType);
                    o.put("min", question.min);
                    o.put("max", question.max);
                    o.put("validateText", question.validateText);
                    o.put("allowUpper", question.allowTypes.split(", ")[0]);
                    o.put("allowLower", question.allowTypes.split(", ")[1]);
                    o.put("allowDigits", question.allowTypes.split(", ")[2]);
                    o.put("allowSpecial", question.allowTypes.split(", ")[3]);
                    o.put("validSpecialCharacters", question.validSpecialCharacters);
                    o.put("validationType", question.validationType);
                    o.put("decimalPlaces", question.decimalPlaces);
                    o.put("answerChoices", question.answerChoices);
                    o.put("displayType", question.displayType);
                    o.put("numberOfAnswers", question.numberOfAnswers);
                    o.put("otherChoice", question.otherChoice);

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
                            rsQuestions = DatabaseAccess.ListQuestions(page.ID);
                            while (rsQuestions.next()) {
                                question = new Question(rsQuestions.getInt("iQuestionID"), getNullSafeString(rsQuestions.getString("vchName")),
                                        rsQuestions.getInt("iIndex"));
                                question.questionText = getNullSafeString(rsQuestions.getString("vchQuestionText"));
                                question.questionID = getNullSafeString(rsQuestions.getString("vchQuestionID"));
                                question.questionType = getNullSafeString(rsQuestions.getString("vchQuestionType"));
                                question.isRequired = rsQuestions.getBoolean("bRequired");
                                question.min = getNullSafeString(rsQuestions.getString("vchMin"));
                                question.max = getNullSafeString(rsQuestions.getString("vchMax"));
                                question.validateText = rsQuestions.getBoolean("bValidateText");
                                question.allowTypes = rsQuestions.getString("vchAllowTypes");
                                question.validSpecialCharacters = getNullSafeString(rsQuestions.getString("vchValidSpecialCharacters"));
                                question.decimalPlaces = getNullSafeString(rsQuestions.getString("vchDecimalPlaces"));
                                question.validationType = getNullSafeString(rsQuestions.getString("vchValidationType"));
                                question.answerChoices = getNullSafeString(rsQuestions.getString("vchAnswerChoices"));
                                question.otherChoice = getNullSafeString(rsQuestions.getString("vchOtherChoice"));
                                question.displayType = getNullSafeString(rsQuestions.getString("vchDisplayType"));
                                question.numberOfAnswers = getNullSafeString(rsQuestions.getString("vchNumberOfAnswers"));
                                page.questions.add(question);
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
                question = page.questions.get(0);
                o.put("questionText", question.questionText);
                o.put("questionName", question.questionID);
                o.put("isRequired", question.isRequired);
                o.put("questionType", question.questionType);
                o.put("min", question.min);
                o.put("max", question.max);
                o.put("validateText", question.validateText);
                o.put("allowUpper", question.allowTypes.split(", ")[0]);
                o.put("allowLower", question.allowTypes.split(", ")[1]);
                o.put("allowDigits", question.allowTypes.split(", ")[2]);
                o.put("allowSpecial", question.allowTypes.split(", ")[3]);
                o.put("validSpecialCharacters", question.validSpecialCharacters);
                o.put("validationType", question.validationType);
                o.put("decimalPlaces", question.decimalPlaces);
                o.put("answerChoices", question.answerChoices);
                o.put("displayType", question.displayType);
                o.put("numberOfAnswers", question.numberOfAnswers);
                o.put("otherChoice", question.otherChoice);

                //send back full (inner) html of tree
                String treeHTML = "";
                for (Page p : pages) {
                    pageIndex = p.pageIndex;
                    treeHTML += "<li><i class=\"icon-li icon-minus collapsible clickable\"></i><a href=\"javascript:switchNode(" + pageIndex + ", -1);\"> Page " + (pageIndex + 1) + "</a>\n";
                    treeHTML += "<ul>\n";
                    for (Question q : p.questions) {
                        questionIndex = q.questionIndex;
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
                    for (Question q : p.questions) {
                        if (q.questionText.trim().length() == 0) {
                            errorMessage += "<li>Question Text is required</li>";
                        }
                        if (q.questionID.trim().length() == 0) {
                            errorMessage += "<li>Question ID is required</li>";
                        } else if (!q.questionID.matches("^[a-zA-Z0-9_]+$")) {
                            errorMessage += "<li>Question ID can only contain letters, digits, and underscores</li>";
                        }
                        switch (q.questionType) {
                            case "none":
                                errorMessage += "<li>Question Type is required</li>";
                                break;
                            case "text":
                                if (q.min.length() == 0) {
                                    errorMessage += "<li>Character minimum is required</li>";
                                } else if (!q.min.matches("^[0-9]+$")) {
                                    errorMessage += "<li>Character minimum must be a whole number</li>";
                                }
                                if (q.max.length() == 0) {
                                    errorMessage += "<li>Character maximum is required</li>";
                                } else if (!q.max.matches("^[0-9]+$")) {
                                    errorMessage += "<li>Character maximum must be a whole number</li>";
                                }
                                try {
                                    if (Integer.parseInt(q.min) > Integer.parseInt(q.max)) {
                                        errorMessage += "<li>Character minimum cannot be more than character maximum</li>";
                                    } else if (Integer.parseInt(q.max) == 0) {
                                        errorMessage += "<li>Character maximum must be greater than 0</li>";
                                    } else if (Integer.parseInt(q.min) == 0 && q.isRequired) {
                                        errorMessage += "<li>Character minimum cannot be 0 when the question is required</li>";
                                    }
                                } catch (Exception ex) {
                                }
                                if (q.validateText) {
                                    if (!q.allowTypes.contains("true")) {
                                        errorMessage += "<li>Because validate text is selected, at least one character type to allow must be selected</li>";
                                    } else if (q.allowTypes.split(", ")[3].equals("true") && q.validSpecialCharacters.length() == 0) {
                                        errorMessage += "<li>The special characters textbox cannot be empty when allow special characters is checked";
                                    }
                                }
                                break;
                            case "wholeNumber":
                                switch (q.validationType) {
                                    case "none":
                                        break;
                                    case "setMin":
                                        if (q.min.length() == 0) {
                                            errorMessage += "<li>Minimum is required</li>";
                                        } else if (!q.min.matches("^-?[0-9]+$")) {
                                            errorMessage += "<li>Minimum must be a whole number</li>";
                                        }

                                        break;
                                    case "setMax":
                                        if (q.max.length() == 0) {
                                            errorMessage += "<li>Maximum is required</li>";
                                        } else if (!q.max.matches("^-?[0-9]+$")) {
                                            errorMessage += "<li>Maximum must be a whole number</li>";
                                        }
                                        try {
                                            if (Integer.parseInt(q.max) == 0) {
                                                errorMessage += "<li>Maximum must be greater than 0</li>";
                                            }
                                        } catch (Exception ex) {
                                        }
                                        break;
                                    case "setMinMax":
                                        if (q.min.length() == 0) {
                                            errorMessage += "<li>Minimum is required</li>";
                                        } else if (!q.min.matches("^-?[0-9]+$")) {
                                            errorMessage += "<li>Minimum must be a whole number</li>";
                                        }
                                        if (q.max.length() == 0) {
                                            errorMessage += "<li>Maximum is required</li>";
                                        } else if (!q.max.matches("^-?[0-9]+$")) {
                                            errorMessage += "<li>Maximum must be a whole number</li>";
                                        }
                                        try {
                                            if (Integer.parseInt(q.min) > Integer.parseInt(q.max)) {
                                                errorMessage += "<li>Minimum cannot be more than maximum</li>";
                                            }
                                        } catch (Exception ex) {
                                        }
                                        break;
                                }
                                break;
                            case "decimalNumber":
                                switch (q.validationType) {
                                    case "none":
                                        break;
                                    case "setMin":
                                        if (q.min.length() == 0) {
                                            errorMessage += "<li>Minimum is required</li>";
                                        } else if (!q.min.matches("^-?[0-9]+(\\.[0-9]*)?$")) {
                                            errorMessage += "<li>Minimum must be a decimal number</li>";
                                        }

                                        break;
                                    case "setMax":
                                        if (q.max.length() == 0) {
                                            errorMessage += "<li>Maximum is required</li>";
                                        } else if (!q.max.matches("^-?[0-9]+(\\.[0-9]*)?$")) {
                                            errorMessage += "<li>Maximum must be a decimal number</li>";
                                        }
                                        break;
                                    case "setMinMax":
                                        if (q.min.length() == 0) {
                                            errorMessage += "<li>Minimum is required</li>";
                                        } else if (!q.min.matches("^-?[0-9]+(\\.[0-9]*)?$")) {
                                            errorMessage += "<li>Minimum must be a decimal number</li>";
                                        }
                                        if (q.max.length() == 0) {
                                            errorMessage += "<li>Maximum is required</li>";
                                        } else if (!q.max.matches("^-?[0-9]+(\\.[0-9]*)?$")) {
                                            errorMessage += "<li>Maximum must be a decimal number</li>";
                                        }
                                        try {
                                            if (Double.parseDouble(q.min) > Double.parseDouble(q.max)) {
                                                errorMessage += "<li>Minimum cannot be more than maximum</li>";
                                            }
                                        } catch (Exception ex) {
                                        }
                                        break;
                                }
                                break;
                            case "multipleChoice":
                                if (q.answerChoices.length() == 0) {
                                    errorMessage += "<li>There must be at least one answer choice</li>";
                                }
                                if (q.otherChoice.length() > 0) {
                                    if (q.min.length() == 0) {
                                        errorMessage += "<li>Character minimum is required</li>";
                                    } else if (!q.min.matches("^[0-9]+$")) {
                                        errorMessage += "<li>Character minimum must be a whole number</li>";
                                    }
                                    if (q.max.length() == 0) {
                                        errorMessage += "<li>Character maximum is required</li>";
                                    } else if (!q.max.matches("^[0-9]+$")) {
                                        errorMessage += "<li>Character maximum must be a whole number</li>";
                                    }
                                    try {
                                        if (Integer.parseInt(q.min) > Integer.parseInt(q.max)) {
                                            errorMessage += "<li>Character minimum cannot be more than character maximum</li>";
                                        } else if (Integer.parseInt(q.max) == 0) {
                                            errorMessage += "<li>Character maximum must be greater than 0</li>";
                                        } else if (Integer.parseInt(q.min) == 0 && q.isRequired) {
                                            errorMessage += "<li>Character minimum cannot be 0</li>";
                                        }
                                    } catch (Exception ex) {
                                    }
                                    if (q.validateText) {
                                        if (!q.allowTypes.contains("true")) {
                                            errorMessage += "<li>Because validate text is selected, at least one character type to allow must be selected</li>";
                                        } else if (q.allowTypes.split(", ")[3].equals("true") && q.validSpecialCharacters.length() == 0) {
                                            errorMessage += "<li>The special characters textbox cannot be empty when allow special characters is checked";
                                        }
                                    }
                                }
                                break;
                        }
                        if (errorMessage.length() > 0) {
                            invalidNodes += p.pageIndex + "," + q.questionIndex + ";";
                        }
                        if (currentQuestionIndex == q.questionIndex && currentPageIndex == p.pageIndex) {
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
                CodeGen body;
                CodeGen partialJS;
                CodeGen json;
                HashMap<String, String> dbColumns = new HashMap<String, String>();
                for (Page p : pages) {
                    body = new CodeGen();
                    partialJS = new CodeGen();
                    json = new CodeGen();
                    json.spaceCount -= 4;
                    json.addLine(CodeGen.DIR.F, "var json = new Object();\n");
                    json.spaceCount += 8;
                    body.getPageHeader(pageCount, session.getAttribute("sProjectTitle").toString());
                    partialJS.addLine(CodeGen.DIR.S, "function validatePage" + pageCount + "() {\n");
                    partialJS.addLine(CodeGen.DIR.F, "$(\".errorText\").hide();\n");
                    partialJS.addLine(CodeGen.DIR.S, "var result = true;\n");
                    body.addLine(CodeGen.DIR.F, "<table id=\"mainContent\">\n");
                    body.spaceCount += 4;
                    for (Question q : p.questions) {
                        CodeGen.getSQLColumnDeclaration(q, dbColumns);
                        json.getSaveColumnsCode(q);
                        body.addLine(CodeGen.DIR.S, "<tr>\n");
                        body.addLine(CodeGen.DIR.F, "<td>\n");
                        body.addLine(CodeGen.DIR.F, "<p id=\"" + q.questionID + "ErrorMessage\" class=\"errorText\"></p>\n");
                        body.addLine(CodeGen.DIR.S, "<span class=\"questionText\">" + questionCount++ + ". " + q.questionText + "</span>\n");
                        body.addLine(CodeGen.DIR.S, "<div class=\"question\">\n");
                        body.spaceCount += 4;
                        if (q.isRequired) {
                            String type = q.questionType;
                            if (type.equals("multipleChoice")) {
                                type = q.displayType;
                            }
                            partialJS.addLine(CodeGen.DIR.S, "var v" + ++validationCount + " = isNotEmpty('" + q.questionID + "', '" + type + "', true);\n");
                            partialJS.addLine(CodeGen.DIR.S, "result = v" + validationCount + " && result;\n");
                            partialJS.addLine(CodeGen.DIR.S, "if (v" + validationCount + ") {\n");
                            partialJS.spaceCount += 4;
                        } else {
                            partialJS.addLine(CodeGen.DIR.S, "var v" + ++validationCount + " = isNotEmpty('" + q.questionID + "', '" + q.questionType + "', false);\n");
                            partialJS.addLine(CodeGen.DIR.S, "if (v" + validationCount + ") {\n");
                            partialJS.spaceCount += 4;
                        }
                        switch (q.questionType) {
                            case "text":
                                body.getTextBoxCode(q.questionID, Integer.parseInt(q.max), true);
                                partialJS.getMeetsLengthRequirementsCode(q.questionID, q.min, q.max);
                                partialJS.getTextValidationCode(q);
                                break;
                            case "multipleChoice":
                                ArrayList<String> answers = new ArrayList<String>();
                                for (String answer : q.answerChoices.split(";;")) {
                                    if (!answer.equals("")) {
                                        answers.add(answer);
                                    }
                                }
                                body.getMultipleChoiceCode(answers, q);
                                partialJS.getMultipleChoiceValidationCode(q);
                                break;
                            case "wholeNumber":
                                body.getWholeNumberCode(q.questionID);
                                partialJS.getWholeNumberValidationCode(q);
                                break;
                            case "decimalNumber":
                                body.getDecimalNumberCode(q.questionID, Integer.parseInt(q.decimalPlaces));
                                partialJS.getDecimalValidationCode(q);
                                break;
                        }
                        partialJS.addLine(CodeGen.DIR.B, "}\n");
                        body.addLine(CodeGen.DIR.B, "</div>\n");
                        body.addLine(CodeGen.DIR.B, "</td>\n");
                        body.addLine(CodeGen.DIR.B, "</tr>\n");
                    }
                    partialJS.addLine(CodeGen.DIR.S, "if (result) {\n");
                    partialJS.getAJAX(json, pageCount == pages.size());
                    partialJS.addLine(CodeGen.DIR.B, "}\n");
                    partialJS.addLine(CodeGen.DIR.S, "return false;\n");
                    partialJS.addLine(CodeGen.DIR.B, "}\n");
                    javascript += partialJS.code + "\n";
                    body.addLine(CodeGen.DIR.S, "<tr>\n");
                    body.addLine(CodeGen.DIR.F, "<td align=\"center\">\n");
                    if (pageCount < pages.size()) {
                        body.getNextButtonCode();
                    } else {
                        body.getSubmitButtonCode();
                    }
                    body.addLine(CodeGen.DIR.B, "</td>\n");
                    body.addLine(CodeGen.DIR.B, "</tr>\n");
                    body.addLine(CodeGen.DIR.B, "</table>\n");
                    body.addLine(CodeGen.DIR.B, "</form>\n");
                    body.addLine(CodeGen.DIR.B, "</body>\n");
                    body.addLine(CodeGen.DIR.B, "</html>");
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
//            out.println("<title>Servlet MainServlet</title>");
//            out.println("</head>");
//            out.println("<body>");
//            out.println("<h1>Servlet MainServlet at " + request.getContextPath() + "</h1>");
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
            p.pageIndex = pageIndex++;
            questionIndex = 0;
            for (Question q : p.questions) {
                q.questionIndex = questionIndex++;
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
