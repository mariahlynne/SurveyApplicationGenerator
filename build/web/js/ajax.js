function switchProject(projectID, title) {
    //calling switch node will effectively save the node they're currently on - all others will already be saved
    if ($("#hfIsSessionEmpty").val() == "false") {
        switchNode($("#currentPageIndex").val(), $("#currentQuestionIndex").val());
    }
    $.ajax({
        async: false,
        url: "MainServlet",
        data: {
            func:"switchProject",
            projectID:projectID,
            title:title
        },
        success: function(json) {
            $("#hfIsSessionEmpty").val("true");
            $("#projectName").text(json.projectTitle);
            if ($("#ddlProject option[value='" + json.projectID + "']").length == 0) {
                $("#ddlProject").append('<option value="' + json.projectID + '">' + json.projectTitle + '</option>');
            }
            document.getElementById("navigationTree").innerHTML = json.treeHTML;
            var currentNode;
            var currentNodeType = $("#currentNodeType");
            var currentPageIndex = $("#currentPageIndex");
            var currentQuestionIndex = $("#currentQuestionIndex");
            if (currentNodeType.val() == "page") {
                currentNode = $("#navigationTree > li:nth-child(" + (parseInt(currentPageIndex.val()) + 1) + ") > a");
                currentNode.attr('href', "javascript:switchNode(" + currentPageIndex.val() + ", -1);");
                currentNode.removeClass("nodeSelected");
            } else if (currentNodeType.val() == "question") {
                currentNode = $("#navigationTree > li:nth-child(" + (parseInt(currentPageIndex.val()) + 1) + ") > ul > li:nth-child(" +
                    (parseInt(currentQuestionIndex.val()) + 1) + ") > a");
                currentNode.attr('href', "javascript:switchNode(" + currentPageIndex.val() + ", " + currentQuestionIndex.val() + ");");
                currentNode.removeClass("nodeSelected");
            }

            setSettingsFromJSON(json);
            currentPageIndex.val(0);
            currentQuestionIndex.val(0);
            showQuestion();
            currentNodeType.val("question");
            currentNode = $("#navigationTree > li:nth-child(1) > ul > li:nth-child(1) > a");
            currentNode.addClass("nodeSelected");
            currentNode.attr('href', "javascript:doNothing()");
            validate();
        }
    });
}

function switchNode(pageIndex, questionIndex) {
    var currentNodeType = $("#currentNodeType");
    var currentPageIndex = $("#currentPageIndex");
    var currentQuestionIndex = $("#currentQuestionIndex");
    var settingsJSON = getSettingsJSON();
    var validated = false;
    $.ajax({
        async: false,
        url: "MainServlet",
        data: {
            func:"switch",
            pageIndex:pageIndex,
            questionIndex:questionIndex,
            currentPageIndex:currentPageIndex.val(),
            currentQuestionIndex:currentQuestionIndex.val(),
            settingsJSON:settingsJSON
        },
        success: function(json) {
            var currentNode;
            if (currentNodeType.val() == "page") {
                currentNode = $("#navigationTree > li:nth-child(" + (parseInt(currentPageIndex.val()) + 1) + ") > a");
                currentNode.attr('href', "javascript:switchNode(" + currentPageIndex.val() + ", -1);");
                currentNode.removeClass("nodeSelected");
            } else if (currentNodeType.val() == "question") {
                currentNode = $("#navigationTree > li:nth-child(" + (parseInt(currentPageIndex.val()) + 1) + ") > ul > li:nth-child(" +
                    (parseInt(currentQuestionIndex.val()) + 1) + ") > a");
                currentNode.attr('href', "javascript:switchNode(" + currentPageIndex.val() + ", " + currentQuestionIndex.val() + ");");
                currentNode.removeClass("nodeSelected");
            }

            setSettingsFromJSON(json);
            currentPageIndex.val(pageIndex);
            currentQuestionIndex.val(questionIndex);
            if (questionIndex == -1) {
                currentNodeType.val("page");
                showPage();
                currentNode = $("#navigationTree > li:nth-child(" + (pageIndex + 1) + ") > a");
            } else {
                showQuestion();
                currentNodeType.val("question");
                currentNode = $("#navigationTree > li:nth-child(" + (pageIndex + 1) + ") > ul > li:nth-child(" + (questionIndex + 1) + ") > a");
            }
            currentNode.addClass("nodeSelected");
            currentNode.attr('href', "javascript:doNothing()");
            validated = validate();
        }
    });
    return validated;
}

function getSettingsJSON() {
    var json = new Object();

    json.questionName = $("#questionName").val();
    json.questionText = $("#questionText").val();
    json.isRequired = $("#isRequired").is(':checked');
    json.questionType = $("#questionType").val();
    switch (json.questionType) {
        case "text":
            json.min = $("#textValidateSpecificLengthMin").val();
            json.max = $("#textValidateSpecificLengthMax").val();
            json.validateText = $("#validateText").is(":checked");
            json.allowUpper = $("#validCharsUppercase").is(":checked");
            json.allowLower = $("#validCharsLowercase").is(":checked");
            json.allowDigits = $("#validCharsDigits").is(":checked");
            json.allowSpecial = $("#validCharsSpecial").is(":checked");
            json.validSpecialCharacters = $("#validCharsSpecialText").val();
            break;
        case "wholeNumber":
            json.min = $("#txtWholeNumberMin").val();
            json.max = $("#txtWholeNumberMax").val();
            json.validationType = $("#wholeNumberValidation").val();
            break;
        case "decimalNumber":
            json.decimalPlaces = $("#decimalPlaces").val();
            json.min = $("#txtDecimalNumberMin").val();
            json.max = $("#txtDecimalNumberMax").val();
            json.validationType = $("#decimalNumberValidation").val();
            break;
        case "multipleChoice":
            json.numberOfAnswers = $("#multipleChoiceAmount").val();
            json.displayType = $("#multipleChoiceDisplayType").val();
            json.answerChoices = "";
            $("#multipleChoiceChoices option").each(function()
            {
                json.answerChoices += $(this).val() + ";;";
            });
            if ($("#multipleChoiceAddOther").is(":checked")) {
                json.otherChoice = $("#multipleChoiceOtherAnswerText").val();
                json.min = $("#textValidateSpecificLengthMinMC").val();
                json.max = $("#textValidateSpecificLengthMaxMC").val();
                json.validateText = $("#validateTextMC").is(":checked");
                json.allowUpper = $("#validCharsUppercaseMC").is(":checked");
                json.allowLower = $("#validCharsLowercaseMC").is(":checked");
                json.allowDigits = $("#validCharsDigitsMC").is(":checked");
                json.allowSpecial = $("#validCharsSpecialMC").is(":checked");
                json.validSpecialCharacters = $("#validCharsSpecialTextMC").val();
            } else {
                json.otherChoice = "";
            }
            break;
    }

    return JSON.stringify(json);
}

function setSettingsFromJSON(json) {
    clearAllFields();
    $("#questionName").val(json.questionName);
    $("#questionText").val(json.questionText);
    $("#isRequired").prop('checked', json.isRequired);
    $("#questionType").val(json.questionType).attr('selected', 'selected');
    switch (json.questionType) {
        case "text":
            $("#textValidateSpecificLengthMin").val(json.min);
            $("#textValidateSpecificLengthMax").val(json.max);
            $("#textValidateErrorMessage").text(json.validationErrorMessage);
            $("#validateText").prop('checked', json.validateText);
            showHideValidateTextSection();
            $("#validCharsUppercase").prop('checked', json.allowUpper == 'true');
            $("#validCharsLowercase").prop('checked', json.allowLower == 'true');
            $("#validCharsDigits").prop('checked', json.allowDigits == 'true');
            $("#validCharsSpecial").prop('checked', json.allowSpecial == 'true');
            $("#validCharsSpecialText").val(json.validSpecialCharacters);
            break;
        case "wholeNumber":
            $("#txtWholeNumberMin").val(json.min);
            $("#txtWholeNumberMax").val(json.max);
            $("#wholeNumberValidation").val(json.validationType);
            break;
        case "decimalNumber":
            $("#decimalPlaces").val(json.decimalPlaces);
            $("#txtDecimalNumberMin").val(json.min);
            $("#txtDecimalNumberMax").val(json.max);
            $("#decimalNumberValidation").val(json.validationType);
            break;
        case "multipleChoice":
            $("#multipleChoiceAmount").val(json.numberOfAnswers);
            showCorrectMultipleChoiceAmount();
            $("#multipleChoiceDisplayType").val(json.displayType);
            var answers = json.answerChoices.split(";;");
            var choices = document.getElementById('multipleChoiceChoices');
            for (var i = 0; i < answers.length; i++) {
                if (answers[i] != "") {
                    choices.options[choices.options.length] = new Option(answers[i], answers[i]);
                }
            }
            if (json.otherChoice == "") {
                $("#multipleChoiceAddOther").prop('checked', false);
            } else {
                $("#multipleChoiceOtherAnswerText").val(json.otherChoice);
                $("#multipleChoiceAddOther").prop('checked', true);
                $("#textValidateSpecificLengthMinMC").val(json.min);
                $("#textValidateSpecificLengthMaxMC").val(json.max);
                $("#validateTextMC").prop('checked', json.validateText);
                showHideValidateTextMCSection();
                $("#validCharsUppercaseMC").prop('checked', json.allowUpper == 'true');
                $("#validCharsLowercaseMC").prop('checked', json.allowLower == 'true');
                $("#validCharsDigitsMC").prop('checked', json.allowDigits == 'true');
                $("#validCharsSpecialMC").prop('checked', json.allowSpecial == 'true');
                $("#validCharsSpecialTextMC").val(json.validSpecialCharacters);
            }
            break;
    }

}

function clearAllFields() {
    $("#questionErrorSection").hide();

    $("#questionText").val("");
    $("#isRequired").prop('checked', false);
    $("#questionType").val("none").attr('selected', 'selected');

    //text section
    $("#textValidateSpecificLengthMin").val("");
    $("#textValidateSpecificLengthMax").val("");
    $("#validateText").prop("checked", false);
    $("#validCharsUppercase").prop('checked', true);
    $("#validCharsLowercase").prop('checked', true);
    $("#validCharsDigits").prop('checked', true);
    $("#validCharsSpecial").prop('checked', true);
    $("#validCharsSpecialText").val("~!@#$%^&*()-_=+|\\[]{};:' \",./?<>");

    //whole number section
    $("#txtWholeNumberMin").val("");
    $("#txtWholeNumberMax").val("");
    $("#wholeNumberValidation").val("none");

    //decimal section
    $("#decimalPlaces").val("1");
    $("#txtDecimalNumberMin").val("");
    $("#txtDecimalNumberMax").val("");
    $("#decimalNumberValidation").val("none");

    //multiple choice section
    $("#multipleChoiceChoice").val("");
    $("#multipleChoiceAmount").val("1");
    $("#multipleChoiceChoices").html("");
    showCorrectMultipleChoiceAmount();
    $("#multipleChoiceDisplayType").val("radio");
    $("#multipleChoiceAddOther").prop('checked', false);
    $("#multipleChoiceOtherAnswerText").val("Other (please specify)");
    $("#textValidateSpecificLengthMinMC").val("");
    $("#textValidateSpecificLengthMaxMC").val("");
    $("#validateTextMC").prop("checked", false);
    $("#validCharsUppercaseMC").prop('checked', true);
    $("#validCharsLowercaseMC").prop('checked', true);
    $("#validCharsDigitsMC").prop('checked', true);
    $("#validCharsSpecialMC").prop('checked', true);
    $("#validCharsSpecialTextMC").val("~!@#$%^&*()-_=+|\\[]{};:' \",./?<>");

}

function addPage() {
    $.ajax({
        async: false,
        url: 'MainServlet',
        data: {
            func:"addPage"
        },
        success: function() {
            var pageIndex = $("#navigationTree > li").length;
            $("#navigationTree > li:last").after(
                '<li>' +
                '    <i class="icon-li icon-minus collapsible clickable"></i>' +
                '    <a href="javascript:switchNode(' + pageIndex + ', -1);"> Page ' + (pageIndex + 1) + '</a>' +
                '    <ul> ' +
                '        <li>' +
                '            <a href="javascript:switchNode(' + pageIndex + ', 0);">Question 1</a>' +
                '        </li>' +
                '    </ul>' +
                '</li>'
                );
        }
    });
}

function addQuestion() {
    var errorMessage = $("#sidebarErrorMessage");
    var currentPageIndex = parseInt($("#currentPageIndex").val());

    if (currentPageIndex == -1) {
        errorMessage.text("You must first select a page to add a question to.");
        errorMessage.show(0).delay(5000).hide(0);
    } else {
        $.ajax({
            async: false,
            url: 'MainServlet',
            data: {
                func:"addQuestion",
                pageIndex:currentPageIndex
            },
            success: function() {
                var questionCount = $("#navigationTree > li:nth-child(" + (currentPageIndex + 1) + ") > ul > li").length;
                $("#navigationTree > li:nth-child(" + (currentPageIndex + 1) + ") > ul > li:last").after(
                    '<li>' +
                    '    <a href="javascript:switchNode(' + currentPageIndex + ', ' + questionCount + ');">Question ' + (questionCount + 1) + '</a>' +
                    '</li>'
                    );
            }
        });
    }
}

function removeNode() {
    var currentNodeType = $("#currentNodeType");
    var currentPageIndex = $("#currentPageIndex");
    var currentQuestionIndex = $("#currentQuestionIndex");
    var errorMessage = $("#sidebarErrorMessage");
    var confirmationText;

    switch (currentNodeType.val()) {
        case "page":
            if ($("#navigationTree > li").length == 1) {
                //can't remove if it's the only page
                errorMessage.text("This is the only page so it cannot be deleted.");
                errorMessage.show(0).delay(5000).hide(0);
                return;
            } else {
                confirmationText = "Are you sure you want to delete this page? All questions on it will also be deleted.";
            }
            break;

        case "question":
            if ($("#navigationTree > li:nth-child(" + (parseInt(currentPageIndex.val()) + 1) + ") > ul > li").length == 1) {
                //can't remove it if it's the only question on the only page
                if ($("#navigationTree > li").length == 1) {
                    errorMessage.text("This is the only question on the only page so it cannot be deleted.");
                    errorMessage.show(0).delay(5000).hide(0);
                    return;
                } else {
                    confirmationText = "This is the only question on the page. Deleting it will also delete the page. " +
                "Are you sure you wish to delete this page?";
                }
            } else {
                confirmationText = "Are you sure you want to delete this question?";
            }
            break;

        case "":
            errorMessage.text("You must first select a page or question to delete.");
            errorMessage.show(0).delay(5000).hide(0);
            return;
    }
    if (confirm(confirmationText)) {
        $.ajax({
            async: false,
            url: 'MainServlet',
            data: {
                func:"remove",
                pageIndex:currentPageIndex.val(),
                questionIndex:currentQuestionIndex.val()
            },
            success: function(treeHTML) {
                errorMessage.hide();
                currentNodeType.val("");
                currentPageIndex.val("-1");
                currentQuestionIndex.val("-1");
                document.getElementById("navigationTree").innerHTML = treeHTML;
            }
        });
    }
}

function doNothing() {

}

function generateApplication() {
    //calling switch node will effectively save the node they're currently on - all others will already be saved
    var validated = switchNode($("#currentPageIndex").val(), $("#currentQuestionIndex").val());
    if (validated) {
        $.ajax({
            url:'MainServlet',
            data: {
                func:"generateApplication"
            },
            success: function() {
                alert('Your survey has been generated successfully!');
            },
            async: false
        });
    } else {
        alert('You must resolve all errors before the survey can be generated.')
    }
}

function validate() {
    var currentPageIndex = $("#currentPageIndex");
    var currentQuestionIndex = $("#currentQuestionIndex");
    var validated = false;
    $.ajax({
        async: false,
        url: "MainServlet",
        data: {
            func:"validate",
            currentPageIndex:currentPageIndex.val(),
            currentQuestionIndex:currentQuestionIndex.val()
        },
        success: function(json) {
            clearAllErrorIcons();
            var invalidNodes = json.invalidNodes.split(";");
            var pages = "";
            var currentNode;
            for (var ndx = 0; ndx < invalidNodes.length; ndx++) {
                if (invalidNodes[ndx].length > 0){
                    var pageIndex = invalidNodes[ndx].split(",")[0];
                    var questionIndex = invalidNodes[ndx].split(",")[1];
                    if (pages.indexOf(pageIndex) == -1) {
                        currentNode = $("#navigationTree > li:nth-child(" + (parseInt(pageIndex) + 1) + ") > a");
                        currentNode.after("<i class=\"icon-warning-sign nodeErrorIcon\" style=\"color: red; margin-left: 10px;\"></i>");
                        pages += pageIndex + ";";
                    }
                    currentNode = $("#navigationTree > li:nth-child(" + (parseInt(pageIndex) + 1) + ") > ul > li:nth-child(" +
                        (parseInt(questionIndex) + 1) + ") > a");
                    currentNode.after("<i class=\"icon-warning-sign nodeErrorIcon\" style=\"color: red; margin-left: 10px;\"></i>");
                }
            }
            if (json.errorMessage != undefined && json.errorMessage.length > 0) {
                $("#questionErrorMessages").html(json.errorMessage);
                $("#questionErrorSection").show();
            }
            validated = json.invalidNodes.length == 0;
        }
    });
    //returns whether or not ANY errors are present
    return validated;
}

function clearAllErrorIcons() {
    $(".nodeErrorIcon").remove();
}

function launchRenameModal() {
    $('#projectNameModal').modal({
        backdrop: "static"
    });
    $("#btnCopyProject").hide();
    $("#btnRenameProject").show();
}

function launchCopyModal() {
    $('#projectNameModal').modal({
        backdrop: "static"
    });
    $("#btnRenameProject").hide();
    $("#btnCopyProject").show();
}

function renameProject() {

}

function copyProject() {

}