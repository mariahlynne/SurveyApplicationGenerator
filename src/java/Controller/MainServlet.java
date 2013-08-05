package Controller;

import Model.CodeGenerator;
import Model.Page;
import Model.Question;
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
        String currentNodeType;
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
                int currentQuestionIndex = Integer.parseInt(request.getParameter("currentQuestionIndex"));
                int currentPageIndex = Integer.parseInt(request.getParameter("currentPageIndex"));
                pages = (ArrayList<Page>) session.getAttribute("pages");

                if (currentQuestionIndex != -1) {
                    page = pages.get(currentPageIndex);
                    question = page.questions.get(currentQuestionIndex);
                    JSONParser p = new JSONParser();
                    JSONObject o = (JSONObject) p.parse(request.getParameter("settingsJSON").toString());
                    question.clearAll();
                    question.questionText = o.get("questionText").toString();
                    question.questionName = o.get("questionName").toString();
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
                    o.put("questionName", question.questionName);
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

                    currentNodeType = (questionIndex == -1) ? "page" : "question";
                    response.setContentType("application/json");
                    response.getWriter().write(o.toJSONString());
                }
                break;

            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Generate Application">

            case "generateApplication":
                pages = (ArrayList<Page>) session.getAttribute("pages");
                for (Page p : pages) {
                    for (Question q : p.questions) {
                        switch (q.questionType) {
                            case "text":
                                System.out.println(CodeGenerator.getTextBoxCode(q.questionName, Integer.parseInt(q.max)));
                                break;
                            case "multipleChoice":
                                ArrayList<String> answers = new ArrayList<String>();
                                for (String answer : q.answerChoices.split(";;")) {
                                    if (!answer.equals("")) {
                                        answers.add(answer);
                                    }
                                }
                                System.out.println(CodeGenerator.getMultipleChoiceCode(answers, q.questionName, q.displayType));
                                break;
                        }
                    }
                }
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
