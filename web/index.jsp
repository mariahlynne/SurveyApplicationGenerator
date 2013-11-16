<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="Model.DatabaseAccess"%>
<%@page import="Model.Question"%>
<%@page import="Model.Page"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <%  session = request.getSession(true);
        ArrayList<Page> pages = (ArrayList<Page>) session.getAttribute("pages");
        boolean bNoProjects = false;
        ResultSet rs = DatabaseAccess.ListSurveyApplications();
        HashMap<Integer, String> projects = new HashMap<Integer, String>();
        while (rs.next()) {
            projects.put(rs.getInt("iApplicationID"), rs.getString("vchTitle"));
        }
        if (projects.size() == 0) {
            bNoProjects = true;
        }
        if (pages == null || pages.size() == 0) {
            pages = new ArrayList<Page>();
            Page p = new Page(-1, "Page 1", 0);
            p.addQuestion(-1, "Question 1", 0);
            pages.add(p);%>
    <input id="hfIsSessionEmpty" type="hidden" value="true" />
    <%  } else {%>
    <input id="hfIsSessionEmpty" type="hidden" value="false" />
    <%  }
    %>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Survey Application Designer</title>
        <link type="text/css" rel="stylesheet" href="css/bootstrap.css"/>
        <link type="text/css" rel="stylesheet" href="css/font-awesome.css"/>
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
                    class="btn btn-info ">Generate Survey</button>
            <div class="btn-toolbar" style="float: right; height: 30px; margin-top: 35px;">
                <div class="btn-group">
                    <button class="btn btn-info dropdown-toggle" data-toggle="dropdown">Project <span class="caret"></span></button>
                    <ul class="dropdown-menu">
                        <li><a href="javascript:switchApplication(false)">Switch Project</a></li>
                        <li><a href="javascript:launchRenameModal()">Rename Project</a></li>
                        <li><a href="javascript:launchCopyModal()">Copy Project</a></li>
                    </ul>
                </div>
            </div>
            <h1 id="projectName" style="width: 500px; float: left;"></h1>
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
                        <!--                        <li><a href="#">Reorder Pages</a></li>
                                                <li><a href="#">Reorder Questions</a></li>-->
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
                            pageIndex = p.getPageIndex();
                    %>
                    <li>
                        <i class="icon-li icon-minus collapsible clickable"></i>
                        <a href="javascript:switchNode(<%= pageIndex%>, -1);"> Page <%= pageIndex + 1%></a>
                        <ul>
                            <% int questionIndex;
                                for (Question q : p.getQuestions()) {
                                    questionIndex = q.getQuestionIndex();
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
                <div id="questionErrorSection" style="width: 60%; margin: 20px auto; background-color: transparent; border: 1px solid red; color: red; font-weight: bold;">
                    <table>
                        <tr>
                            <td valign="middle">
                                <i class="icon-warning-sign icon-5x" style="display: inline-block; margin: 7px 10px 0px 10px"></i>
                            </td>
                            <td>
                                <div style="padding: 5px 25px 5px 5px; font-size: 14px;">
                                    Please correct the following:
                                    <ul id="questionErrorMessages" style="padding-left: 15px; list-style-type: circle !important; padding-bottom: 0; margin-bottom: 0;">
                                        <li>Question Text is required</li>
                                        <li>Question ID is required</li>
                                        <li>Question Type is required</li>
                                    </ul>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
                <label id="questionTextLabel" for="questionText" class="boldLabel">Question Text:</label>
                <textarea id="questionText" rows="3" style="width: 97%"></textarea>
                <br /><br />
                <label id="questionNameLabel" for="questionName" class="boldLabel">Question ID:</label>
                <input id="questionName" type="text" maxlength="50" />
                <i class="icon-question-sign icon-2x" style="color: #424242" title="The question ID will be the name of the column in the database table so it should be a short reminder of what question was asked. It will not be displayed on the screen and it must be alphanumeric."></i>
                <br /><br />
                <div class="checkbox">
                    <label for="isRequired" class="boldLabel">
                        <input type="checkbox" id="isRequired" />
                        <span>Require an answer for this question</span>
                    </label>
                </div>
                <br />
                <div class="well well-small" id="questionTypeSection">
                    <label id="questionTypeLabel" for="questionType" class="boldLabel">Question Type:</label>
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
                        <div class="checkbox">
                            <label for="validateText" class="boldLabel">
                                <input type="checkbox" id="validateText" onchange="showHideValidateTextSection()" />
                                <span>Validate text</span>
                            </label>
                        </div>
                        <div id="validateTextSection" style="padding-left: 20px">
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
                                </label>
                                <input type="text" id="validCharsSpecialText" value="~!@#$%^&*()-_=+|\[]{};:' &quot;,./?<>" style="margin-left: 20px;"/>
                            </div>
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
                        <select id="decimalPlaces" style="width: 50px;" onchange="changeDecimalPlacePadding();">
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
                            <input type="text" id="multipleChoiceChoice" style="width: 94%" />
                            <button type="button" id="addChoiceButton" class="btn btn-info pull-right" style="margin-top:3px" onclick="addChoice()">
                                <i class="icon-plus" style="color: white;"></i>
                            </button>
                            <br />
                            <select size="5" id="multipleChoiceChoices" style="width: 85%">
                            </select><br />
                            <div class="btn-group" style="float: left; clear: both;">
                                <button type="button" class="btn btn-info" onclick="moveChoiceUp()">
                                    <i class="icon-arrow-up" style="color: white;"></i>
                                </button>
                                <button type="button" class="btn btn-info" onclick="moveChoiceDown()">
                                    <i class="icon-arrow-down" style="color: white;"></i>
                                </button>
                                <button type="button" class="btn btn-danger" onclick="removeChoice()">
                                    <i class="icon-remove" style="color: white;"></i>
                                </button>
                            </div><br /><br /><br />
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
                                    <table>
                                        <tr>
                                            <td><label class ="boldLabel" style="margin-right: 10px">At least </label></td>
                                            <td><input type="text" id="textValidateSpecificLengthMinMC"style="width: 40px" maxlength="5" /></td>
                                            <td><label class="boldLabel" style="margin-right: 10px; margin-left: 10px"> characters but no more than </label></td>
                                            <td><input type="text" id="textValidateSpecificLengthMaxMC" style="width: 40px" maxlength="5" /></td>
                                            <td><label class="boldLabel" style="margin-left: 10px"> characters</label></td>
                                        </tr>
                                    </table>
                                    <div class="checkbox">
                                        <label for="validateTextMC" class="boldLabel">
                                            <input type="checkbox" id="validateTextMC" onchange="showHideValidateTextMCSection()" />
                                            <span>Validate text</span>
                                        </label>
                                    </div>
                                    <div id="validateTextSectionMC" style="padding-left: 20px">
                                        <label class="boldLabel">Characters Allowed:</label>
                                        <div class="checkbox">
                                            <label for="validCharsUppercaseMC">
                                                <input type="checkbox" id="validCharsUppercaseMC" checked="checked" />
                                                <span>Uppercase Letters</span>
                                            </label>
                                        </div>
                                        <div class="checkbox">
                                            <label for="validCharsLowercaseMC">
                                                <input type="checkbox" id="validCharsLowercaseMC" checked="checked" />
                                                <span>Lowercase Letters</span>
                                            </label>
                                        </div>
                                        <div class="checkbox">
                                            <label for="validCharsDigitsMC">
                                                <input type="checkbox" id="validCharsDigitsMC" checked="checked" />
                                                <span>Digits</span>
                                            </label>
                                        </div>
                                        <div class="checkbox">
                                            <label for="validCharsSpecialMC">
                                                <input type="checkbox" id="validCharsSpecialMC" checked="checked" />
                                                <span>Special Characters:</span>
                                                <br />
                                            </label>
                                            <input type="text" id="validCharsSpecialTextMC" value="~!@#$%^&*()-_=+|\[]{};:' &quot;,./?<>" style="margin-left: 20px;"/>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- Modal -->
        <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button id="btnCancelSwitchX" type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title">Select a Project</h4>
                    </div>
                    <div class="modal-body">
                        <label>
                            <input id="rbtnExistingProject" type="radio" name="rbgProjectType" value="existing"/> Existing Project:</label>
                        <select id="ddlProject" style="margin-left: 15px;">
                            <% if (!bNoProjects) {%>
                            <% for (Entry<Integer, String> entry : projects.entrySet()) {%>
                            <option value="<%= entry.getKey()%>"><%= entry.getValue()%></option>
                            <%}
                            } else {%>
                            <option value="none" selected>None</option>
                            <%}%>
                        </select>
                        <label><input id="rbtnNewProject" type="radio" name="rbgProjectType" value="new"/> New Project:
                        </label>
                        <input type="text" id="txtNewProject" maxlength="100" style="margin-left: 15px;" />
                        <p id="lblSelectProjectError" style="color: red; font-weight: bold; text-align: center; margin: 10px 0;"></p>
                    </div>
                    <div class="modal-footer">
                        <button id="btnSelectProject" type="button" class="btn btn-primary" onclick="selectProject()">Select Project</button>
                        <button id="btnCancelSwitch" type="button" class="btn btn-primary" data-dismiss="modal">Cancel</button>
                    </div>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div><!-- /.modal -->
        <div class="modal fade" id="projectNameModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title">New Project Name</h4>
                    </div>
                    <div class="modal-body">
                        <p>New Project Name:</p>
                        <input type="text" id="txtNewProjectName" maxlength="100" style="margin-left: 15px;" />
                        <p id="lblNewProjectNameError" style="color: red; font-weight: bold; text-align: center; margin: 10px 0; display: none;">You must enter the New Project Name</p>
                    </div>
                    <div class="modal-footer">
                        <button id="btnRenameProject" type="button" class="btn btn-primary" onclick="renameProject()">Rename Project</button>
                        <button id="btnCopyProject" type="button" class="btn btn-primary" onclick="copyProject()">Copy Project</button>
                        <button id="btnCancelRename" type="button" class="btn btn-primary" data-dismiss="modal">Cancel</button>
                    </div>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div><!-- /.modal -->
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
            switchApplication(true, <%= bNoProjects%>);
        });
    </script>
</html>
