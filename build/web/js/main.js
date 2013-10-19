function showPage() {
    $("#questionSection").hide();
    $("#pageSection").show();
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
    if (type == "text") {
        $("#textSection").show();
        showHideValidateTextSection();
    } else if (type == "wholeNumber") {
        $("#wholeNumberSection").show();
        showHideWholeNumberValidation();
    } else if (type == "decimalNumber") {
        $("#decimalNumberSection").show();
        showHideDecimalNumberValidation();
    //    } else if (type == "date") {
    //        $("#dateSection").show();
    } else if (type == "multipleChoice") {
        $("#multipleChoiceSection").show();
        showCorrectMultipleChoice();
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
}

function showCorrectMultipleChoice() {
    showCorrectMultipleChoiceAmount();
}

function showHideMultipleChoiceFieldOther() {
    if (document.getElementById("multipleChoiceAddOther").checked) {
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

function showCorrectMultipleChoiceValidationOptions() {
    $("#multipleChoiceOtherValidateSpecificLength").hide();
    $("#multipleChoiceOtherValidateWholeNumber").hide();
    $("#multipleChoiceOtherValidateDecimalNumber").hide();
    $("#multipleChoiceOtherValidateDate").hide();
    $("#multipleChoiceOtherValidateErrorMessageSection").show();
    var type = document.getElementById('multipleChoiceOtherValidate').value;
    if (type == "specificLength") {
        $("#multipleChoiceOtherValidateSpecificLength").show();
    } else if (type == "wholeNumber") {
        $("#multipleChoiceOtherValidateWholeNumber").show();
    } else if (type == "decimalNumber") {
        $("#multipleChoiceOtherValidateDecimalNumber").show();
    } else if (type == "date") {
        $("#multipleChoiceOtherValidateDate").show();
    } else {
        $("#multipleChoiceOtherValidateErrorMessageSection").hide();
    }
}

function showCorrectMultipleChoiceAmount() {
    var type = document.getElementById('multipleChoiceAmount').value;
    if (type == "1") {
        document.getElementById('multipleChoiceDisplayType').innerHTML = "<option value=\"radio\">Radio Buttons</option><option value=\"dropdown\">Drop-down List</option>"
    } else {
        document.getElementById('multipleChoiceDisplayType').innerHTML = "<option value=\"dropdown\">Drop-down List</option><option value=\"checkbox\">Checkboxes</option>"
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
    if ($("#ddlProject").val() == "none") {
        if ($("#txtNewProject").val() == "") {
            $("#lblSelectProjectError").text("You must enter the name of the new project").show();
        } else {
            $("#lblSelectProjectError").hide();
            switchProject(-1, $("#txtNewProject").val());
            $("#txtNewProject").val("");
            $('#myModal').modal('hide');
        }
    } else if ($("#rbtnExistingProject").is(':checked')) {
        $("#lblSelectProjectError").hide();
        switchProject($("#ddlProject").val(), $("#ddlProject option:selected").text());
        $("#txtNewProject").val("");
        $('#myModal').modal('hide');
    } else {
        if ($("#txtNewProject").val() == "") {
            $("#lblSelectProjectError").text("You must enter the name of the new project OR use an existing project").show();
            return;
        } else {
            $("#lblSelectProjectError").hide();
            switchProject(-1, $("#txtNewProject").val());
            $("#txtNewProject").val("");
            $('#myModal').modal('hide');
        }
    }
}