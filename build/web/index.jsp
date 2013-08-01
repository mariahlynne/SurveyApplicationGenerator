<%@page import="Model.Question"%>
<%@page import="Model.Page"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <%  session = request.getSession(true);
        ArrayList<Page> pages = (ArrayList<Page>) session.getAttribute("pages");
        if (pages == null || pages.size() == 0) {
            //System.out.println("creating pages");
            pages = new ArrayList<Page>();
            pages.add(new Page("Page 1", 0));
            session.setAttribute("pages", pages);
            session.setAttribute("currentNodeType", "question");
            session.setAttribute("currentPageIndex", 0);
            session.setAttribute("currentQuestionIndex", 0);
        } else {
            //System.out.println("session persisted, " + pages.size() + " pages");
        }

    %>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Survey Application Designer</title>
        <link type="text/css" rel="stylesheet" href="css/bootstrap.css"/>
        <link type="text/css" rel="stylesheet" href="css/main.css"/>
        <link type="text/css" rel="stylesheet" href="css/tree.css"/>
        <script type="text/javascript" src="js/jquery-1.10.1.js"></script>
        <script type="text/javascript" src="js/bootstrap.js"></script>
        <script type="text/javascript" src="js/main.js"></script>
        <script type="text/javascript" src="js/ajax.js"></script>

    </head>
    <body>
        <header id="primary">
            <button type="button" onclick="generateApplication();" style="float: right; height: 30px; margin: 35px;"
                    class="btn btn-info ">Generate Application</button>
            <h1 style="width: 500px; float: left;">Header</h1>
        </header>
        <!--Sidebar content-->
        <div id="sidebar">
            <div id="sidebarErrorMessage" class="alert alert-error" style="display: none; font-weight: bold;">
            </div>
            <div class="btn-toolbar" style="margin-left: 10px;">
                <div class="btn-group">
                    <button class="btn btn-info dropdown-toggle" data-toggle="dropdown">Action <span class="caret"></span></button>
                    <ul class="dropdown-menu">
                        <li><a href="javascript:addPage()">Add Page</a></li>
                        <li><a href="javascript:addQuestion()">Add Question</a></li>
                        <li><a href="javascript:removeNode()">Delete Selected</a></li>
                        <li><a href="#">Reorder Pages</a></li>
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
        <!--Body content-->
        <div id="mainContent" class="well well-small">
            <div id="errorMessages" style="color: red; font-weight: bold;">
                
            </div>
            <div id="pageSection">
                This will show how all of the questions for this page will appear.
            </div>
            <div id="questionSection" style="margin-top: 10px;">
                <label id="questionTextLabel" for="questionText" class="boldLabel">Question Text:</label>
                <textarea id="questionText" rows="3" style="width: 97%"></textarea>
                <br /><br />
                <div class="checkbox">
                    <label for="isRequired" class="boldLabel">
                        <input type="checkbox" id="isRequired" />
                        <span>Require an answer for this question</span>
                    </label>
                </div>
                <br />
                <div class="well well-small" id="questionTypeSection">
                    <label id="questionTypeLabel" for="questionType" class="boldLabel">Type:</label>
                    <select id="questionType" onchange="showCorrectQuestionSettings()" style="width: auto">
                        <option label="Choose Question Type" value="none"/>
                        <option label="Text" value="text"/>
                        <option label="Whole Number" value="wholeNumber"/>
                        <option label="Decimal Number" value="decimalNumber"/>
                        <!--                        <option label="Date (MM/DD/YYYY)" value="date"/>-->
                        <option label="Multiple Choice" value="multipleChoice"/>
                    </select>

                    <!-- Text Section -->
                    <div id="textSection" class="well well-small">
                        <br />
                        <table>
                            <tr>
                                <td><label class ="boldLabel" style="margin-right: 10px">At least </label></td>
                                <td><input type="text" id="textValidateSpecificLengthMin"style="width: 40px" maxlength="5" /></td>
                                <td><label class="boldLabel" style="margin-right: 10px; margin-left: 10px"> characters but no more than </label></td>
                                <td><input type="text" id="textValidateSpecificLengthMax" style="width: 40px" maxlength="5" /></td>
                                <td><label class="boldLabel" style="margin-left: 10px"> characters</label></td>
                            </tr>
                        </table>
                        <br />
                        <label class="boldLabel">Characters Allowed:</label>
                        <div class="checkbox">
                            <label for="validCharsUppercase">
                                <input type="checkbox" id="validCharsUppercase" checked="checked" />
                                <span>Uppercase Letters</span>
                            </label>
                        </div>
                        <div class="checkbox">
                            <label for="validCharsLowercase">
                                <input type="checkbox" id="validCharsLowercase" checked="checked" />
                                <span>Lowercase Letters</span>
                            </label>
                        </div>
                        <div class="checkbox">
                            <label for="validCharsDigits">
                                <input type="checkbox" id="validCharsDigits" checked="checked" />
                                <span>Digits</span>
                            </label>
                        </div>
                        <div class="checkbox">
                            <label for="validCharsSpecial">
                                <input type="checkbox" id="validCharsSpecial" checked="checked" />
                                <span>Special Characters:</span>
                                <br />
                                <input type="text" id="validCharsSpecialText" value="~!@#$%^&*()-_=+|\[]{};:' &quot;,./?<>" style="margin-left: 20px;"/>
                            </label>
                        </div>
                    </div>

                    <!-- Whole Number Section -->
                    <div id="wholeNumberSection">
                        <div class="well well-small">
                            <label for="wholeNumberValidation" class="boldLabel">Validation:</label>
                            <select id="wholeNumberValidation" onchange="showHideWholeNumberValidation()">
                                <option value="none">None</option>
                                <option value="setMin">Set minimum</option>
                                <option value="setMax">Set maximum</option>
                                <option value="setMinMax">Set minimum & maximum</option>
                            </select>
                            <div id="wholeNumberMinSection">
                                <label id="lblWholeNumberMin" for="wholeNumberMin" class="boldLabel">Minimum:</label>
                                <input type="text" id="txtWholeNumberMin" style="width: 160px" maxlength="20" />
                            </div>
                            <div id="wholeNumberMaxSection">
                                <label id="lblWholeNumberMax" for="wholeNumberMax" class="boldLabel">Maximum:</label>
                                <input type="text" id="txtWholeNumberMax" style="width: 160px" maxlength="19" />
                            </div>
                        </div>
                    </div>

                    <!-- Decimal Number Section -->
                    <div id="decimalNumberSection">
                        <br />
                        <label for="decimalPlaces" class="boldLabel">Number of Decimal Places:</label>
                        <select id="decimalPlaces" style="width: 50px;">
                            <option value="1">1</option>
                            <option value="2">2</option>
                            <option value="3">3</option>
                            <option value="4">4</option>
                            <option value="5">5</option>
                        </select>
                        <div class="well well-small">
                            <label for="decimalNumberValidation" class="boldLabel">Validation:</label>
                            <select id="decimalNumberValidation" onchange="showHideDecimalNumberValidation()">
                                <option value="none" selected>None</option>
                                <option value="setMin">Set minimum</option>
                                <option value="setMax">Set maximum</option>
                                <option value="setMinMax">Set minimum & maximum</option>
                            </select>
                            <div id="decimalNumberMinSection">
                                <label id="lblDecimalNumberMin" for="decimalNumberMin" class="boldLabel">Minimum:</label>
                                <input type="text" id="txtDecimalNumberMin" style="width: 160px" maxlength="20" />
                            </div>
                            <div id="decimalNumberMaxSection">
                                <label id="lblDecimalNumberMax" for="decimalNumberMax" class="boldLabel">Maximum:</label>
                                <input type="text" id="txtDecimalNumberMax" style="width: 160px" maxlength="19" />
                            </div>
                        </div>
                    </div>

                    <!-- Date Section -->
                    <!--                    <div id="dateSection" class="well well-small">
                                            <span>Date Section</span>
                                        </div>-->

                    <!-- Multiple Choice Section -->
                    <div id="multipleChoiceSection">
                        <div id="multipleChoiceAmountSection">
                            <label for="multipleChoiceAmount" class="boldLabel">Number of answers the user may select:</label>
                            <select id="multipleChoiceAmount" onchange="showCorrectMultipleChoiceAmount()" style="width: auto">
                                <option label="1 Answer" value="1"/>
                                <option label="2 Answers" value="2"/>
                                <option label="3 Answers" value="3"/>
                                <option label="4 Answers" value="4"/>
                                <option label="5 Answers" value="5"/>
                                <option label="6 Answers" value="6"/>
                                <option label="7 Answers" value="7"/>
                                <option label="8 Answers" value="8"/>
                                <option label="9 Answers" value="9"/>
                                <option label="10 Answers" value="10"/>
                                <option label="Unlimited Answers" value="unlimited"/>
                            </select>
                        </div>
                        <div id="multipleChoiceTypeSection">
                            <label id="multipleChoiceDisplayTypeLabel" for="multipleChoiceDisplayType" class="boldLabel">Display Type:</label>
                            <select id="multipleChoiceDisplayType" style="width: auto">
                                <option label="Radio Buttons" value="radio"/>
                                <option label="Drop-down List" value="dropdown"/>
                            </select>
                        </div>

                        <div class="well well-small">
                            <label id="multipleChoiceChoicesLabel" for="multipleChoiceChoice" class="boldLabel">Answer choices:</label>
                            <input type="text" id="multipleChoiceChoice" style="width: 95%" />
                            <a id="addChoiceButton" class="btn btn-info pull-right" style="margin-top:3px" onclick="addChoice()">+
                                <!--                          <i class="icon-white icon-plus"/>-->
                            </a>
                            <br />
                            <select size="5" id="multipleChoiceChoices" style="width: 85%">
                            </select>
                            <div class="btn-group" style="float: right">
                                <a class="btn btn-info" onclick="moveChoiceUp()">&lt;
                                    <!--                                <i class="icon-arrow-up icon-white" />-->
                                </a>
                                <a class="btn btn-info" onclick="moveChoiceDown()">&gt;
                                    <!--<i class="icon-arrow-down icon-white"/>-->
                                </a>
                                <a class="btn btn-danger" onclick="removeChoice()">x
                                    <!--<i class="icon-remove icon-white"/>-->
                                </a>
                            </div>
                            <div class="well well-small">
                                <div class="checkbox">
                                    <label for="multipleChoiceAddOther" class="boldLabel">
                                        <input type="checkbox" id="multipleChoiceAddOther" onchange="showHideMultipleChoiceFieldOther()"/>
                                        <span>Add "Other" choice (with textbox)</span>
                                    </label>
                                </div>
                                <div id="multipleChoiceAddOtherFieldSection">
                                    <label id="multipleChoiceOtherAnswerTextLabel" for="multipleChoiceOtherAnswerText" class="boldLabel">Answer Label:</label>
                                    <input type="text" id="multipleChoiceOtherAnswerText" value="Other (please specify)" style="width: 97%" />
                                    <p>***Put the text validation options here***</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
<script>
    $(document).ready(function() {
        $("#sidebar").height(Math.max($("#mainContent").height(),$("#sidebar").height()));
        $("#mainContent").width($("#primary").width() - ($("#sidebar").width() + 40));
        $("#navigationTree > li:nth-child(1) > ul > li:nth-child(1) > a").addClass("nodeSelected");
        $("#navigationTree > li:nth-child(1) > ul > li:nth-child(1) > a").attr('href', "javascript:doNothing()");
        showQuestion();
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
        showCorrectQuestionSettings();
        showHideMultipleChoiceFieldOther();
        $("#multipleChoiceChoice").keypress(function (event) {
            if (event.keyCode == 13) {
                event.preventDefault();
                $("#addChoiceButton").click();
            }
        });

    });
</script>
</html>
