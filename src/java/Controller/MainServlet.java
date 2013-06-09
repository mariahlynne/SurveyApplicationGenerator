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

public class MainServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sFunction = request.getParameter("func");
        PrintWriter out = response.getWriter();
        switch (sFunction) {
            // <editor-fold defaultstate="collapsed" desc="Add Page">
            case "addPage":
                HttpSession session = request.getSession();
                ArrayList<Page> pages = (ArrayList<Page>) session.getAttribute("pages");
                int count = pages.size();
                pages.add(new Page("Page " + count + 1, count));
                session.setAttribute("pages", pages);
                int pageIndex;
                for (Page p : pages) {
                    pageIndex = p.pageIndex;
                    System.out.println("pageIndex=" + pageIndex);
                    out.println("<li><i class=\"icon-li icon-minus collapsible clickable\"></i><a href=\"javascript:switchToPage(" + pageIndex + ");\"> Page " + (pageIndex + 1) + "</a>");
                    out.println("<ul>");
                    int questionIndex;
                    for (Question q : p.questions) {
                        questionIndex = q.questionIndex;
                        System.out.println("questionIndex=" + questionIndex);
                        out.println("<li><a href=\"javascript:switchToQuestion(" + pageIndex + ", " + questionIndex + ");\">Question " + (questionIndex + 1) + "</a></li>");
                    }
                    out.println("</ul>");
                    out.println("</li>");
                }
                break;
            // </editor-fold>
                
            // <editor-fold defaultstate="collapsed" desc="Add Question">
            case "addQuestion":
                break;
            // </editor-fold>
                
            // <editor-fold defaultstate="collapsed" desc="Remove Page">
            case "removePage":
                break;
            // </editor-fold>
                
            // <editor-fold defaultstate="collapsed" desc="Remove Question">
            case "removeQuestion":
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
                System.out.println("page: " + request.getParameter("pageIndex"));
                System.out.println("question: " + request.getParameter("questionIndex"));
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
}
