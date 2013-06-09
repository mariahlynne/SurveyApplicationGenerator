function switchToPage(pageIndex) {
    alert("switching to page " + pageIndex);
    ajaxCall("MainServlet?function=switch&pageIndex=" + pageIndex + "&questionIndex=-1");
}

function switchToQuestion(pageIndex, questionIndex) {
    alert("switching to page " + pageIndex + ", question " + questionIndex);
    ajaxCall("MainServlet?function=switch&pageIndex=" + pageIndex + "&questionIndex=" + questionIndex);
}

function addPage() {
    $.post('MainServlet', {func:"addPage"}, function(responseText) { 
        document.getElementById('tree').innerHTML = responseText;         
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