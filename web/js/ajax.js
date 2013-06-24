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

        if (questionIndex == -1) {
            currentNodeType.val("page");
            showPage();
            currentNode = $("#navigationTree > li:nth-child(" + (pageIndex + 1) + ") > a");
        } else {
            showQuestion();
            currentNodeType.val("question");
            currentNode = $("#navigationTree > li:nth-child(" + (pageIndex + 1) + ") > ul > li:nth-child(" + (questionIndex + 1) + ") > a");
        }
        setSettingsFromJSON(json);
        currentPageIndex.val(pageIndex);
        currentQuestionIndex.val(questionIndex);
        currentNode.addClass("nodeSelected");
        currentNode.attr('href', "javascript:doNothing()");
    });
}

function getSettingsJSON() {
    var json = new Object();

    json.questionText = $("#questionText").val();
    json.isRequired = $("#isRequired").is(':checked');
    json.errorMessage = $("#requiredErrorMessage").text();
    json.questionType = $("#questionType").val();
    json.min = -1;
    json.max = -1;
    json.validCharacters = "a-zA-Z";

    return JSON.stringify(json);
}

function setSettingsFromJSON(json) {
    $("#questionText").val(json.questionText);
    $("#isRequired").prop('checked', json.isRequired);
    $("#requiredErrorMessage").text(json.errorMessage);
    $("#questionType").val(json.questionType).attr('selected', 'selected');

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