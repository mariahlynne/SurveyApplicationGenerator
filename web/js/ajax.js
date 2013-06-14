function switchNode(pageIndex, questionIndex) {
    $.post("MainServlet", {
        func:"switch", 
        pageIndex:pageIndex, 
        questionIndex:questionIndex
    }, function(json) {
        var previousNode;
        var node;
        if (json.previousNodeType == "page") {
            previousNode = $("#navigationTree > li:nth-child(" + (json.previousPageIndex + 1) + ") > a");
            previousNode.attr('href', "javascript:switchNode(" + json.previousPageIndex + ", -1);");
        } else {
            previousNode = $("#navigationTree > li:nth-child(" + (json.previousPageIndex + 1) + ") > ul > li:nth-child(" + 
                (json.previousQuestionIndex + 1) + ") > a");
            previousNode.attr('href', "javascript:switchNode(" + json.previousPageIndex + ", " + json.previousQuestionIndex + ");");
        } 
        previousNode.removeClass("nodeSelected");
        if (questionIndex == -1) {
            node = $("#navigationTree > li:nth-child(" + (pageIndex + 1) + ") > a");
        } else {
            node = $("#navigationTree > li:nth-child(" + (pageIndex + 1) + ") > ul > li:nth-child(" + (questionIndex + 1) + ") > a");
        }
        node.addClass("nodeSelected");
        node.attr('href', "javascript:doNothing()");            
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

function addQuestion(pageIndex) {
    ajaxCall("MainServlet?function=addQuestion&pageIndex=" + pageIndex)
}

function removePage(pageIndex) {
    ajaxCall("MainServlet?function=removePage&pageIndex=" + pageIndex);
}

function removeQuestion(pageIndex, questionIndex) {
    ajaxCall("MainServlet?function=removeQuestion&pageIndex=" + pageIndex + "&questionIndex=" + questionIndex);
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