function switchNode(pageIndex, questionIndex) {
    var currentNodeType = $("#currentNodeType");
    var currentPageIndex = $("#currentPageIndex");
    var currentQuestionIndex = $("#currentQuestionIndex");
    var settingsJSON = getSettingsJSON();
    $.post("MainServlet", {
        func:"switch",
        pageIndex:pageIndex,
        questionIndex:questionIndex,
        currentPageIndex:currentPageIndex.val(),
        currentQuestionIndex:currentQuestionIndex.val(),
        settingsJSON:settingsJSON
    }, function(json) {
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
    });
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
            json.validCharacters = getValidCharacters();
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
            if ($("#multipleChoiceAddOther").is(":checked")) {
                json.otherChoice = $("#multipleChoiceOtherAnswerText").val();
            } else {
                json.otherChoice = "";
            }
            json.validationType = "";
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
            setValidCharacters(json.validCharacters);
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
            $("#multipleChoiceDisplayType").val(json.displayType);
            json.answerChoices = "";
            if (json.otherChoice == "") {
                $("#multipleChoiceOtherAnswerText").val("Other");
                $("#multipleChoiceAddOther").prop('checked', false);
            } else {
                $("#multipleChoiceOtherAnswerText").val(json.otherChoice);
                $("#multipleChoiceAddOther").prop('checked', true);
            }
            break;
    }

}

function clearAllFields() {
    $("#questionText").val("");
    $("#isRequired").prop('checked', false);
    $("#questionType").val("none").attr('selected', 'selected');

    //text section
    $("#textValidateSpecificLengthMin").val("");
    $("#textValidateSpecificLengthMax").val("");
    setValidCharacters("default");

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
    showCorrectMultipleChoiceAmount();
    $("#multipleChoiceDisplayType").val("radioButtons");
}

function getValidCharacters() {
    var result = "";
    if ($("#validCharsUppercase").is(':checked')) {
        result += "A-Z";
    }
    if ($("#validCharsLowercase").is(':checked')) {
        result += "a-z";
    }
    if ($("#validCharsDigits").is(':checked')) {
        result += "0-9";
    }
    if ($("#validCharsSpecial").is(':checked')) {
        result += ";;;" + $("#validCharsSpecialText").val();
    }
    return result;
}

function setValidCharacters(characters) {
    if (characters == "default") {
        $("#validCharsUppercase").prop('checked', true);
        $("#validCharsLowercase").prop('checked', true);
        $("#validCharsDigits").prop('checked', true);
        $("#validCharsSpecial").prop('checked', true);
        $("#validCharsSpecialText").val("~!@#$%^&*()-_=+|\\[]{};:' \",./?<>");
    } else {
        $("#validCharsUppercase").prop('checked', characters.indexOf("A-Z") != -1);
        $("#validCharsLowercase").prop('checked', characters.indexOf("a-z") != -1);
        $("#validCharsDigits").prop('checked', characters.indexOf("0-9") != -1);
        $("#validCharsSpecial").prop('checked', characters.indexOf(";;;") != -1);
        $("#validCharsSpecialText").val(characters.substring(characters.indexOf(";;;") + 3));
    }
}

function addPage() {
    $.post('MainServlet', {
        func:"addPage"
    }, function() {
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
    });
}

function addQuestion() {
    var errorMessage = $("#sidebarErrorMessage");
    var currentPageIndex = parseInt($("#currentPageIndex").val());

    if (currentPageIndex == -1) {
        errorMessage.text("You must first select a page to add a question to.");
        errorMessage.show(0).delay(5000).hide(0);
    } else {
        $.post('MainServlet', {
            func:"addQuestion",
            pageIndex:currentPageIndex
        }, function() {
            var questionCount = $("#navigationTree > li:nth-child(" + (currentPageIndex + 1) + ") > ul > li").length;
            $("#navigationTree > li:nth-child(" + (currentPageIndex + 1) + ") > ul > li:last").after(
                '<li>' +
                '    <a href="javascript:switchNode(' + currentPageIndex + ', ' + questionCount + ');">Question ' + (questionCount + 1) + '</a>' +
                '</li>'
                );
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
        $.post('MainServlet', {
            func:"remove",
            pageIndex:currentPageIndex.val(),
            questionIndex:currentQuestionIndex.val()
        }, function(treeHTML) {
            errorMessage.hide();
            currentNodeType.val("");
            currentPageIndex.val("-1");
            currentQuestionIndex.val("-1");
            document.getElementById("navigationTree").innerHTML = treeHTML;
        });
    }
}

function doNothing() {

}