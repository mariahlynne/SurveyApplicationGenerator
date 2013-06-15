<%@page import="Model.Question"%>
<%@page import="Model.Page"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <%  session = request.getSession(true);
        ArrayList<Page> pages = (ArrayList<Page>) session.getAttribute("pages");
        if (pages == null || pages.size() == 0) {
            System.out.println("creating pages");
            pages = new ArrayList<Page>();
            pages.add(new Page("Page 1", 0));
            session.setAttribute("pages", pages);
            session.setAttribute("currentNodeType", "question");
            session.setAttribute("currentPageIndex", 0);
            session.setAttribute("currentQuestionIndex", 0);
        } else {
            System.out.println("session persisted, " + pages.size() + " pages");
        }

    %>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link type="text/css" rel="stylesheet" href="css/bootstrap.min.css"/>
        <link type="text/css" rel="stylesheet" href="css/main.css"/>
        <link type="text/css" rel="stylesheet" href="css/tree.css"/>
        <script type="text/javascript" src="js/jquery-1.10.1.js"></script>
        <script type="text/javascript" src="js/bootstrap.min.js"></script>
        <script type="text/javascript" src="js/main.js"></script>
        <script type="text/javascript" src="js/ajax.js"></script>

    </head>
    <body>
        <header id="primary">
            <h1>Header</h1>
        </header>
        <div id="sidebar">
            <!--Sidebar content-->
            <div class="btn-toolbar" style="margin-left: 10px;">
                <div class="btn-group">
                    <button class="btn btn-info dropdown-toggle" data-toggle="dropdown">Pages <span class="caret"></span></button>
                    <ul class="dropdown-menu">
                        <li><a href="javascript:addPage()">Add Page</a></li>
                        <li><a href="javascript:removeNode()">Remove Page</a></li>
                        <li><a href="#">Reorder Pages</a></li>
                    </ul>
                </div>
                <div class="btn-group" style="float: right; margin-right: 10px;">
                    <button class="btn btn-info dropdown-toggle" data-toggle="dropdown">Questions <span class="caret"></span></button>
                    <ul class="dropdown-menu">
                        <li><a href="javascript:addQuestion()">Add Question</a></li>
                        <li><a href="javascript:removeNode()">Remove Question</a></li>
                        <li><a href="#">Reorder Questions</a></li>
                    </ul>
                </div>
            </div>
            <!-- tree -->
            <div>
                <input type="hidden" id="currentNodeType" value="question"/>
                <input type="hidden" id="currentPageIndex" value="0"/>
                <input type="hidden" id="currentQuestionIndex" value="0"/>
                <ul class="icons-ul tree" id="navigationTree">
                    <%  int pageIndex = -1;
                        for (Page p : pages) {
                            pageIndex = p.pageIndex;
                    %>
                    <li>
                        <i class="icon-li icon-minus collapsible clickable"></i>
                        <a href="javascript:switchNode(<%= pageIndex%>, -1);"> Page <%= pageIndex + 1%></a>
                        <ul>
                            <% int questionIndex;
                                for (Question q : p.questions) {
                                    questionIndex = q.questionIndex;
                            %>
                            <li><a href="javascript:switchNode(<%= pageIndex%>, <%= questionIndex%>);">Question <%= questionIndex + 1%></a></li>
                            <%}%>
                        </ul>
                    </li>
                    <%}%>
                </ul>
            </div>
        </div>
        <div id="mainContent">
            <!--Body content-->
            <h1>Body Content</h1>
        </div>
    </body>
    <script>
        $(document).ready(function() {
            $("#sidebar").height(Math.max($("#mainContent").height(),$("#sidebar").height()));
            $("#navigationTree > li:nth-child(1) > ul > li:nth-child(1) > a").addClass("nodeSelected");
            $("#navigationTree > li:nth-child(1) > ul > li:nth-child(1) > a").attr('href', "javascript:doNothing()");
            $(function () {
                $('.collapsible').on('click', function (e) {
                    var parent = $(this).parent();
                    var children = parent.find('> ul > li');
                    if (children.is(":visible")) {
                        children.hide('fast');
                        $(this).removeClass("icon-minus");
                        $(this).addClass("icon-plus");
                    }
                    else {
                        children.show('fast');
                        $(this).removeClass("icon-plus");
                        $(this).addClass("icon-minus");
                    }
                    e.stopPropagation();
                });
            });
        });
    </script>
</html>
