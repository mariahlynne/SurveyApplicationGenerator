package Controller;

import Model.Page;
import Model.Question;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONObject;

public class MainServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sFunction = request.getParameter("func");
        PrintWriter out;
        HttpSession session = request.getSession();
        int pageIndex;
        int questionIndex;
        ArrayList<Page> pages;
        int count;
        Page page;
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

            // <editor-fold defaultstate="collapsed" desc="Switch">
            case "switch":
                questionIndex = Integer.parseInt(request.getParameter("questionIndex"));
                pageIndex = Integer.parseInt(request.getParameter("pageIndex"));

                JSONObject jObject = new JSONObject();
                jObject.put("previousNodeType", session.getAttribute("currentNodeType").toString());
                jObject.put("previousPageIndex", new Integer(session.getAttribute("currentPageIndex").toString()));
                jObject.put("previousQuestionIndex", new Integer(session.getAttribute("currentQuestionIndex").toString()));

//                System.out.println("switching from page: " + session.getAttribute("currentPageIndex").toString() + ", question: " + session.getAttribute("currentQuestionIndex").toString());
//                System.out.println("to page: " + request.getParameter("pageIndex") + ", question: " + request.getParameter("questionIndex"));
                currentNodeType = (questionIndex == -1) ? "page" : "question";
                session.setAttribute("currentNodeType", currentNodeType);
                session.setAttribute("currentPageIndex", pageIndex);
                session.setAttribute("currentQuestionIndex", questionIndex);
                response.setContentType("application/json");
                response.getWriter().write(jObject.toJSONString());
                break;
            // </editor-fold>
        }
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
    }
// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("in do get");
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
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
                q.setQuestionIndex(questionIndex++);
            }
        }
    }
}
