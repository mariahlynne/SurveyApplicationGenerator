function switchNode(pageIndex, questionIndex) {
    var currentNodeType = $("#currentNodeType");
    var currentPageIndex = $("#currentPageIndex");
    var currentQuestionIndex = $("#currentQuestionIndex");
    $.post("MainServlet", {
        func:"switch", 
        pageIndex:pageIndex, 
        questionIndex:questionIndex
    }, function(json) {
        var currentNode;
        if (currentNodeType.val() == "page") {
            currentNode = $("#navigationTree > li:nth-child(" + (parseInt(currentPageIndex.val()) + 1) + ") > a");
            currentNode.attr('href', "javascript:switchNode(" + currentPageIndex.val() + ", -1);");
        } else {
            currentNode = $("#navigationTree > li:nth-child(" + (parseInt(currentPageIndex.val()) + 1) + ") > ul > li:nth-child(" + 
                (parseInt(currentQuestionIndex.val()) + 1) + ") > a");
            currentNode.attr('href', "javascript:switchNode(" + currentPageIndex.val() + ", " + currentQuestionIndex.val() + ");");
        } 
        currentNode.removeClass("nodeSelected");
        if (questionIndex == -1) {
            currentNodeType.val("page");
            currentNode = $("#navigationTree > li:nth-child(" + (pageIndex + 1) + ") > a");
        } else {
            currentNodeType.val("question");
            currentNode = $("#navigationTree > li:nth-child(" + (pageIndex + 1) + ") > ul > li:nth-child(" + (questionIndex + 1) + ") > a");
        }
        currentPageIndex.val(pageIndex);
        currentQuestionIndex.val(questionIndex);
        currentNode.addClass("nodeSelected");
        currentNode.attr('href', "javascript:doNothing()");            
    });
}

function doNothing() {
    
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
    var currentPageIndex = parseInt($("#currentPageIndex").val());
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

function removeNode() {
    var currentNodeType = $("#currentNodeType");
    var currentPageIndex = $("#currentPageIndex");
    var currentQuestionIndex = $("#currentQuestionIndex");
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

function ajaxCall(url) {
    var xmlhttp = GetXmlHttpObject();
    if (xmlhttp == null)
    {
        alert ("Your browser does not support Ajax HTTP");
        return;
    }
    xmlhttp.onreadystatechange = getOutput(xmlhttp);
    xmlhttp.open("GET", url, true);
    xmlhttp.send(null);
}

function getOutput(xmlhttp) {
    if (xmlhttp.readyState == 4) {
        return xmlhttp.responseText;
    }
    return "";
}

function GetXmlHttpObject() {
    if (window.XMLHttpRequest) {
        return new XMLHttpRequest();
    }
    if (window.ActiveXObject) {
        return new ActiveXObject("Microsoft.XMLHTTP");
    }
    return null;
}