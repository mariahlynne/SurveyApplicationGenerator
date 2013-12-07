function showPage() {
    $("#questionSection").hide();
    $("#pageSection").show();
    onlyAllowNumbers();
    onlyAllowDecimalNumbers();
}

function showQuestion() {
    $("#questionSection").show();
    $("#pageSection").hide();
    showCorrectQuestionSettings();
}

function showCorrectQuestionSettings() {
    //hide all sections
    $("#textSection").hide();
    $("#wholeNumberSection").hide();
    $("#decimalNumberSection").hide();
    $("#multipleChoiceSection").hide();

    //show selected section
    var type = document.getElementById("questionType").value;
    switch (type) {
        case "text":
            $("#textSection").show();
            showHideValidateTextSection();
            break;
        case "wholeNumber":
            $("#wholeNumberSection").show();
            showHideWholeNumberValidation();
            break;
        case "decimalNumber":
            $("#decimalNumberSection").show();
            showHideDecimalNumberValidation();
            break;
        case "date":
            $("#dateSection").show();
            break;
        case "multipleChoice":
            $("#multipleChoiceSection").show();
            showCorrectMultipleChoice();
            break;
    }
}

function showHideValidateTextSection() {
    if ($("#validateText").is(":checked")) {
        $("#validateTextSection").show();
    } else {
        $("#validateTextSection").hide();
    }
}

function showHideWholeNumberValidation() {
    $("#wholeNumberMinSection").hide();
    $("#wholeNumberMaxSection").hide();
    $("#wholeNumberErrorMessageSection").show();

    var type = document.getElementById("wholeNumberValidation").value;
    if (type == "setMin") {
        $("#wholeNumberMinSection").show();
    } else if (type == "setMax") {
        $("#wholeNumberMaxSection").show();
    } else if (type == "setMinMax") {
        $("#wholeNumberMinSection").show();
        $("#wholeNumberMaxSection").show();
    }
}

function showHideDecimalNumberValidation() {
    $("#decimalNumberMinSection").hide();
    $("#decimalNumberMaxSection").hide();
    $("#decimalNumberErrorMessageSection").show();

    var type = document.getElementById("decimalNumberValidation").value;
    if (type == "setMin") {
        $("#decimalNumberMinSection").show();
    } else if (type == "setMax") {
        $("#decimalNumberMaxSection").show();
    } else if (type == "setMinMax") {
        $("#decimalNumberMinSection").show();
        $("#decimalNumberMaxSection").show();
    }
    changeDecimalPlacePadding();
}

function showCorrectMultipleChoice() {
    showCorrectMultipleChoiceAmount();
    showHideMultipleChoiceFieldOther();
    showHideValidateTextMCSection();
}

function showHideMultipleChoiceFieldOther() {
    if ($("#multipleChoiceAddOther").is(':checked')) {
        $("#multipleChoiceAddOtherFieldSection").show();
    } else {
        $("#multipleChoiceAddOtherFieldSection").hide();
    }
}

function moveChoiceUp() {
    var selected = $("#multipleChoiceChoices option:selected");
    var text = selected.text();
    var index = $("#multipleChoiceChoices option").index(selected);
    if (index != 0) {
        selected.remove();
        $("#multipleChoiceChoices option:eq(" + (index - 1) + ")").before("<option value='1'>" + text + "</option>");
        $('#multipleChoiceChoices option')[index - 1].selected = true;
    }
    $('#multipleChoiceChoices').focus();
}

function moveChoiceDown() {
    var selected = $("#multipleChoiceChoices option:selected");
    var text = selected.text();
    var index = $("#multipleChoiceChoices option").index(selected);
    var size = $("#multipleChoiceChoices option").size();
    if (index != size - 1) {
        selected.remove();
        $("#multipleChoiceChoices option:eq(" + index + ")").after("<option value='1'>" + text + "</option>");
        $('#multipleChoiceChoices option')[index + 1].selected = true;
    }
    $('#multipleChoiceChoices').focus();
}

function removeChoice() {
    var index = $("#multipleChoiceChoices option:selected").index();
    var size = $("#multipleChoiceChoices option").size();
    $("#multipleChoiceChoices option:selected").remove();
    if (index == size - 1 && size > 1)
        index--;
    if (size != 1) {
        $('#multipleChoiceChoices option')[index].selected = true;
    }
    $('#multipleChoiceChoices').focus();
}

function addChoice() {
    var choices = document.getElementById('multipleChoiceChoices');
    var newChoice = document.getElementById('multipleChoiceChoice').value;
    if (newChoice.trim() != "") {
        choices.options[choices.options.length] = new Option(newChoice, newChoice);
        document.getElementById('multipleChoiceChoice').value = "";
        document.getElementById('multipleChoiceChoice').focus();
    }
}

function showCorrectMultipleChoiceAmount() {
    var type = document.getElementById('multipleChoiceAmount').value;
    if (type == "1" && $("#multipleChoiceDisplayType option[value='checkbox']").length > 0) {
        document.getElementById('multipleChoiceDisplayType').innerHTML = "<option value=\"radio\">Radio Buttons</option><option value=\"dropdown\">Drop-down List</option>"
    } else if (type != "1" && $("#multipleChoiceDisplayType option[value='radio']").length > 0) {
        document.getElementById('multipleChoiceDisplayType').innerHTML = "<option value=\"checkbox\">Checkboxes</option>"
    }
}

function showHideValidateTextMCSection() {
    if ($("#validateTextMC").is(":checked")) {
        $("#validateTextSectionMC").show();
    } else {
        $("#validateTextSectionMC").hide();
    }
}

function switchApplication(bForce, bNoProjects) {
    $('#myModal').modal({
        backdrop: "static"
    });
    if (bForce) {
        $("#btnCancelSwitch").attr('disabled', 'disabled');
        $("#btnCancelSwitchX").attr('disabled', 'disabled');
        $("#rbtnExistingProject").attr('checked', 'checked');
        if (bNoProjects) {
            $("#rbtnExistingProject").attr('disabled', 'disabled');
            $("#rbtnExistingProject").parent().css('color', 'lightgray');
            $("#rbtnNewProject").attr('checked', 'checked');
            $("#ddlProject").attr('disabled', 'disabled');
        }
    } else {
        $("#btnCancelSwitch").removeAttr('disabled');
        $("#btnCancelSwitchX").removeAttr('disabled');
        $("#rbtnExistingProject").attr('checked', 'checked');
        $("#rbtnExistingProject").removeAttr('disabled');
        $("#rbtnExistingProject").parent().css('color', 'black');
        $("#rbtnNewProject").removeAttr('disabled');
        $("#ddlProject").removeAttr('disabled');
        $("#ddlProject option[value='none']").remove();
    }
    $('#myModal').modal('show');
}

function selectProject() {
    var newName = $("#txtNewProject").val();
    if ($("#ddlProject").val() == "none") {
        if (newName == "") {
            $("#lblSelectProjectError").text("You must enter the name of the new project").show();
        } else if (!/^[a-zA-Z_ 0-9]+$/.test(newName)) {
            $("#lblSelectProjectError").text("The project name must contain only alphanumeric characters").show();
        } else if (!isProjectNameUnique(newName)) {
            $("#lblSelectProjectError").text("A project named '" + newName + "' already exists").show();
        } else {
            $("#lblSelectProjectError").hide();
            switchProject(-1, newName);
            $("#txtNewProject").val("");
            $('#myModal').modal('hide');
        }
    } else if ($("#rbtnExistingProject").is(':checked')) {
        $("#lblSelectProjectError").hide();
        switchProject($("#ddlProject").val(), $("#ddlProject option:selected").text());
        $("#txtNewProject").val("");
        $('#myModal').modal('hide');
    } else {
        if (newName == "") {
            $("#lblSelectProjectError").text("You must enter the name of the new project OR use an existing project").show();
            return;
        } else if (!/^[a-zA-Z_ 0-9]+$/.test(newName)) {
            $("#lblSelectProjectError").text("The project name must contain only alphanumeric characters").show();
            return;
        } else if (!isProjectNameUnique(newName)) {
            $("#lblSelectProjectError").text("A project named '" + newName + "' already exists").show();
            return;
        } else {
            $("#lblSelectProjectError").hide();
            switchProject(-1, newName);
            $("#txtNewProject").val("");
            $('#myModal').modal('hide');
        }
    }
}

function changeDecimalPlacePadding() {
    $("#txtDecimalNumberMin").blur(function () {
        padDecimalPointPlaces("txtDecimalNumberMin", $("#decimalPlaces").val());
    });
    $("#txtDecimalNumberMax").blur(function () {
        padDecimalPointPlaces("txtDecimalNumberMax", $("#decimalPlaces").val());
    });
}

function padDecimalPointPlaces(id, places) {
    var padded = parseFloat($("#" + id).val());
    if (!isNaN(padded)) {
        $("#" + id).val(padded.toFixed(places));
    } else {
        $("#" + id).val("");
    }
}

function enableCollapsibleTree() {
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
}