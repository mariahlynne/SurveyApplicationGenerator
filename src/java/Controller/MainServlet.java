package Controller;

import Model.CodeGen;
import Model.Page;
import Model.Question;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        int pageIndex;
        int questionIndex;
        ArrayList<Page> pages;
        int count;
        Page page;
        Question question;
        int currentQuestionIndex;
        int currentPageIndex;

        switch (sFunction) {
            // <editor-fold defaultstate="collapsed" desc="Add Page">
            case "addPage":
                pages = (ArrayList<Page>) session.getAttribute("pages");
                count = pages.size();
                pages.add(new Page("Page " + count + 1, count));
                session.setAttribute("pages", pages);
                break;
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Add Question">
            case "addQuestion":
                pageIndex = Integer.parseInt(request.getParameter("pageIndex"));
                pages = (ArrayList<Page>) session.getAttribute("pages");
                page = pages.get(pageIndex);
                count = page.questions.size();
                page.addQuestion("Question " + (count + 1), count);
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
                    pages.remove(pageIndex);
                } else {
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
                    JSONParser p = new JSONParser();
                    JSONObject o = (JSONObject) p.parse(request.getParameter("settingsJSON").toString());
                    question.clearAll();
                    question.questionText = o.get("questionText").toString();
                    question.questionID = o.get("questionName").toString();
                    question.isRequired = (Boolean) o.get("isRequired");
                    question.questionType = o.get("questionType").toString();
                    switch (question.questionType) {
                        case "text":
                            question.min = o.get("min").toString();
                            question.max = o.get("max").toString();
                            question.validCharacters = o.get("validCharacters").toString();
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
                            question.otherChoice = o.get("otherChoice").toString();
                            question.numberOfAnswers = o.get("numberOfAnswers").toString();
                            question.validationType = o.get("validationType").toString();
                    }

                    session.setAttribute("pages", pages);
                }

                if (questionIndex != -1) {
                    page = pages.get(pageIndex);
                    question = page.questions.get(questionIndex);
                    JSONObject o = new JSONObject();
                    o.put("questionText", question.questionText);
                    o.put("questionName", question.questionID);
                    o.put("isRequired", question.isRequired);
                    o.put("questionType", question.questionType);
                    o.put("min", question.min);
                    o.put("max", question.max);
                    o.put("validCharacters", question.validCharacters);
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

            // <editor-fold defaultstate="collapsed" desc="Validate">
            case "validate":
                pages = (ArrayList<Page>) session.getAttribute("pages");
                currentQuestionIndex = Integer.parseInt(request.getParameter("currentQuestionIndex"));
                currentPageIndex = Integer.parseInt(request.getParameter("currentPageIndex"));
                System.out.println(currentPageIndex + "," + currentQuestionIndex);
                String invalidNodes = "";
                String errorMessage = "";
                JSONObject o = new JSONObject();
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
                                    if (Integer.parseInt(q.min) < Integer.parseInt(q.max)) {
                                        errorMessage += "<li>Character minimum cannot be less than character maximum</li>";
                                    } else if (Integer.parseInt(q.max) == 0) {
                                        errorMessage += "<li>Character maximum must be greater than 0</li>";
                                    }
                                } catch (Exception ex) {
                                }
                                //TODO characters allowed validation
                                break;
                            case "wholeNumber":
                                switch (q.validationType) {
                                    case "none":
                                        break;
                                    case "setMin":
                                        if (q.min.length() == 0) {
                                            errorMessage += "<li>Minimum is required</li>";
                                        } else if (!q.min.matches("^[0-9]+$")) {
                                            errorMessage += "<li>Minimum must be a whole number</li>";
                                        }

                                        break;
                                    case "setMax":
                                        if (q.max.length() == 0) {
                                            errorMessage += "<li>Maximum is required</li>";
                                        } else if (!q.max.matches("^[0-9]+$")) {
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
                                        } else if (!q.min.matches("^[0-9]+$")) {
                                            errorMessage += "<li>Minimum must be a whole number</li>";
                                        }
                                        if (q.max.length() == 0) {
                                            errorMessage += "<li>Maximum is required</li>";
                                        } else if (!q.max.matches("^[0-9]+$")) {
                                            errorMessage += "<li>Maximum must be a whole number</li>";
                                        }
                                        try {
                                            if (Integer.parseInt(q.min) < Integer.parseInt(q.max)) {
                                                errorMessage += "<li>Minimum cannot be less than maximum</li>";
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
                                        } else if (!q.min.matches("^[0-9]+(\\.[0-9]*)?$")) {
                                            errorMessage += "<li>Minimum must be a decimal number</li>";
                                        }

                                        break;
                                    case "setMax":
                                        if (q.max.length() == 0) {
                                            errorMessage += "<li>Maximum is required</li>";
                                        } else if (!q.max.matches("^[0-9]+(\\.[0-9]*)?$")) {
                                            errorMessage += "<li>Maximum must be a decimal number</li>";
                                        }
                                        break;
                                    case "setMinMax":
                                        if (q.min.length() == 0) {
                                            errorMessage += "<li>Minimum is required</li>";
                                        } else if (!q.min.matches("^[0-9]+(\\.[0-9]*)?$")) {
                                            errorMessage += "<li>Minimum must be a decimal number</li>";
                                        }
                                        if (q.max.length() == 0) {
                                            errorMessage += "<li>Maximum is required</li>";
                                        } else if (!q.max.matches("^[0-9]+(\\.[0-9]*)?$")) {
                                            errorMessage += "<li>Maximum must be a decimal number</li>";
                                        }
                                        try {
                                            if (Integer.parseInt(q.min) < Integer.parseInt(q.max)) {
                                                errorMessage += "<li>Minimum cannot be less than maximum</li>";
                                            }
                                        } catch (Exception ex) {
                                        }
                                        break;
                                }
                                break;
                            case "multipleChoice":
                                break;
                        }
                        if (errorMessage.length() > 0) {
                            invalidNodes += p.pageIndex + "," + q.questionIndex + ";";
                            if (currentQuestionIndex == q.questionIndex && currentPageIndex == p.pageIndex) {
                                o.put("errorMessage", errorMessage);

                            }
                            errorMessage = "";
                        }
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
                for (Page p : pages) {
                    body = new CodeGen();
                    partialJS = new CodeGen();
                    body.addLine(CodeGen.DIR.S, "<%@page contentType=\"text/html\" pageEncoding=\"UTF-8\"%>\n");
                    body.addLine(CodeGen.DIR.S, "<!DOCTYPE html>\n");
                    body.addLine(CodeGen.DIR.S, "<html>\n");
                    body.addLine(CodeGen.DIR.F, "<head>\n");
                    body.addLine(CodeGen.DIR.F, "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
                    body.addLine(CodeGen.DIR.S, "<title>Generated Survey</title>\n");
                    body.addLine(CodeGen.DIR.S, "<link type=\"text/css\" rel=\"stylesheet\" href=\"css/bootstrap.css\"/>\n");
                    body.addLine(CodeGen.DIR.S, "<link type=\"text/css\" rel=\"stylesheet\" href=\"css/font-awesome.css\"/>\n");
                    body.addLine(CodeGen.DIR.S, "<link type=\"text/css\" rel=\"stylesheet\" href=\"css/main.css\"/>\n");
                    body.addLine(CodeGen.DIR.S, "<script type=\"text/javascript\" src=\"js/jquery-1.10.1.js\"></script>\n");
                    body.addLine(CodeGen.DIR.S, "<script type=\"text/javascript\" src=\"js/bootstrap.js\"></script>\n");
                    body.addLine(CodeGen.DIR.S, "<script type=\"text/javascript\" src=\"js/static.js\"></script>\n");
                    body.addLine(CodeGen.DIR.S, "<script type=\"text/javascript\" src=\"js/main.js\"></script>\n");
                    body.addLine(CodeGen.DIR.B, "</head>\n");
                    body.addLine(CodeGen.DIR.S, "<body>\n");
                    body.addLine(CodeGen.DIR.F, "<header id=\"primary\">\n");
                    body.addLine(CodeGen.DIR.F, "<h1>\n");
                    body.addLine(CodeGen.DIR.F, "Survey Title\n");
                    body.addLine(CodeGen.DIR.B, "</h1>\n");
                    body.addLine(CodeGen.DIR.B, "</header>\n");
                    body.addLine(CodeGen.DIR.S, "<form action=\"Servlet?Page=Page" + pageCount + "\" id=\"form\" onsubmit=\"return validatePage" + pageCount + "()\" method=\"POST\">\n");
                    partialJS.addLine(CodeGen.DIR.S, "function validatePage" + pageCount + "() {\n");
                    partialJS.addLine(CodeGen.DIR.F, "var result = true;\n");
                    body.addLine(CodeGen.DIR.F, "<table id=\"mainContent\">\n");
                    body.spaceCount += 4;
                    for (Question q : p.questions) {
                        body.addLine(CodeGen.DIR.S, "<tr>\n");
                        body.addLine(CodeGen.DIR.F, "<td>\n");
                        body.addLine(CodeGen.DIR.F, "<p id=\"" + q.questionID + "ErrorMessage\" class=\"errorText\"></p>\n");
                        body.addLine(CodeGen.DIR.S, "<span class=\"questionText\">" + questionCount++ + ". " + q.questionText + "</span>\n");
                        body.addLine(CodeGen.DIR.S, "<div class=\"question\">\n");
                        body.spaceCount += 4;
                        if (q.isRequired) {
                            validationCount++;
                            partialJS.addLine(CodeGen.DIR.S, "var v" + validationCount + " = isNotEmpty('" + q.questionID + "', '" + q.questionType + "');\n");
                            partialJS.addLine(CodeGen.DIR.S, "result = v" + validationCount + " && result;\n");
                            partialJS.addLine(CodeGen.DIR.S, "if (v" + validationCount + ") {\n");
                            partialJS.spaceCount += 4;
                        }
                        switch (q.questionType) {
                            case "text":
                                body.getTextBoxCode(q.questionID, Integer.parseInt(q.max));
                                partialJS.addLine(CodeGen.DIR.S, "result = meetsLengthRequirements('" + q.questionID + "', 'text', " + q.min + ", " + q.max + ") && result;\n");
                                //todo validate that there are no invalid characters
                                break;
                            case "multipleChoice":
                                ArrayList<String> answers = new ArrayList<String>();
                                for (String answer : q.answerChoices.split(";;")) {
                                    if (!answer.equals("")) {
                                        answers.add(answer);
                                    }
                                }
                                body.getMultipleChoiceCode(answers, q.questionID, q.displayType);
                                break;
                            case "wholeNumber":
                                body.getWholeNumberCode(q.questionID);
                                switch (q.validationType) {
                                    case "min":
                                        partialJS.addLine(CodeGen.DIR.S, "result = meetsWholeNumberRequirements('" + q.questionID + "', " + q.min + ", '') && result;\n");
                                        break;
                                    case "max":
                                        partialJS.addLine(CodeGen.DIR.S, "result = meetsWholeNumberRequirements('" + q.questionID + "', '', " + q.max + ") && result;\n");
                                        break;
                                    case "minMax":
                                        partialJS.addLine(CodeGen.DIR.S, "result = meetsWholeNumberRequirements('" + q.questionID + "', " + q.min + ", " + q.max + ") && result;\n");
                                        break;
                                }
                                break;
                            case "decimalNumber":
                                body.getDecimalNumberCode(q.questionID, Integer.parseInt(q.decimalPlaces));
                                switch (q.validationType) {
                                    case "min":
                                        partialJS.addLine(CodeGen.DIR.S, "result = meetsDecimalNumberRequirements('" + q.questionID + "', " + q.min + ", '') && result;\n");
                                        break;
                                    case "max":
                                        partialJS.addLine(CodeGen.DIR.S, "result = meetsDecimalNumberRequirements('" + q.questionID + "', '', " + q.max + ") && result;\n");
                                        break;
                                    case "minMax":
                                        partialJS.addLine(CodeGen.DIR.S, "result = meetsDecimalNumberRequirements('" + q.questionID + "', " + q.min + ", " + q.max + ") && result;\n");
                                        break;
                                }
                                break;
                        }
                        if (q.isRequired) {
                            partialJS.addLine(CodeGen.DIR.B, "}\n");
                        }
                        body.addLine(CodeGen.DIR.B, "</div>\n");
                        body.addLine(CodeGen.DIR.B, "</td>\n");
                        body.addLine(CodeGen.DIR.B, "</tr>\n");
                    }
                    partialJS.addLine(CodeGen.DIR.S, "return result;\n");
                    partialJS.addLine(CodeGen.DIR.B, "}\n");
                    javascript += partialJS.code + "\n";
                    body.addLine(CodeGen.DIR.S, "<tr>\n");
                    body.addLine(CodeGen.DIR.F, "<td align=\"center\">\n");
                    if (pageCount < pages.size()) {
                        body.addLine(CodeGen.DIR.F, "<button id=\"nextButton\" type=\"submit\" class=\"btn btn-info\">Next</button>\n");
                    } else {
                        body.addLine(CodeGen.DIR.F, "<button id=\"submitButton\" type=\"submit\" class=\"btn btn-info\">Submit</button>\n");
                    }
                    body.addLine(CodeGen.DIR.B, "</td>\n");
                    body.addLine(CodeGen.DIR.B, "</tr>\n");
                    body.addLine(CodeGen.DIR.B, "</table>\n");
                    body.addLine(CodeGen.DIR.B, "</form>\n");
                    body.addLine(CodeGen.DIR.B, "</body>\n");
                    body.addLine(CodeGen.DIR.B, "</html>");
                    bw = new BufferedWriter(new FileWriter("C:\\Users\\Mariah\\desktop\\Page" + pageCount + ".jsp"));
                    bw.write(body.code);
                    bw.flush();
                    bw.close();
                    pageCount++;
                }
                bw = new BufferedWriter(new FileWriter("C:\\Users\\Mariah\\desktop\\javascript.txt"));
                bw.write(javascript);
                bw.flush();
                bw.close();
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
        System.out.println("in do get");
        try {
            processRequest(request, response);
        } catch (ParseException ex) {
            Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (ParseException ex) {
            Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
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
            for (Question q : p.questions) {
                q.questionIndex = questionIndex++;
            }
        }
    }
}