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

function showHideRequiredSection() {
    if (document.getElementById("isRequired").checked) {
        $("#requiredSection").show();
    } else {
        $("#requiredSection").hide();
    }
}

function showHideWholeNumberValidation() {
    $("#wholeNumberMinSection").hide();
    $("#wholeNumberMaxSection").hide();
    $("#wholeNumberErrorMessageSection").show();

    var type = document.getElementById("wholeNumberValidation").value;
    if (type == "setMin") {
        $("#wholeNumberMinSection").show();
        $("#wholeNumberErrorMessage").text("The number must be at least <minimum>");
    } else if (type == "setMax") {
        $("#wholeNumberMaxSection").show();
        $("#wholeNumberErrorMessage").text("The number must be no more than <maximum>");
    } else if (type == "setMinMax") {
        $("#wholeNumberMinSection").show();
        $("#wholeNumberMaxSection").show();
        $("#wholeNumberErrorMessage").text("The number must be at least <minimum> and no more than <maximum>");
    } else {
        $("#wholeNumberErrorMessageSection").hide();
    }
}

function showHideDecimalNumberValidation() {
    $("#decimalNumberMinSection").hide();
    $("#decimalNumberMaxSection").hide();
    $("#decimalNumberErrorMessageSection").show();

    var type = document.getElementById("decimalNumberValidation").value;
    if (type == "setMin") {
        $("#decimalNumberMinSection").show();
        $("#decimalNumberErrorMessage").text("The number must be at least <minimum>");
    } else if (type == "setMax") {
        $("#decimalNumberMaxSection").show();
        $("#decimalNumberErrorMessage").text("The number must be no more than <maximum>");
    } else if (type == "setMinMax") {
        $("#decimalNumberMinSection").show();
        $("#decimalNumberMaxSection").show();
        $("#decimalNumberErrorMessage").text("The number must be at least <minimum> and no more than <maximum>");
    } else {
        $("#decimalNumberErrorMessageSection").hide();
    }
}

function showHideMultipleChoiceFieldOther() {
    if (document.getElementById("multipleChoiceAddOther").checked) {
        $("#multipleChoiceAddOtherFieldSection").show();
    } else {
        $("#multipleChoiceAddOtherFieldSection").hide();
    }
}

function showCorrectMultipleChoiceFieldType() {
    $("#multipleChoiceOtherLineSection").hide();
    $("#multipleChoiceOtherParagraphSection").hide();
    var type = document.getElementById('multipleChoiceOtherFieldType').value;
    if (type == "line") {
        $("#multipleChoiceOtherLineSection").show();
    } else {
        $("#multipleChoiceOtherParagraphSection").show();
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
    $('#multipleChoiceChoices option')[index].selected = true;//todo this throws an arrow when last one is removed
    $('#multipleChoiceChoices').focus();
}

function addChoice() {
    var choices = document.getElementById('multipleChoiceChoices');
    var newChoice = document.getElementById('multipleChoiceChoice').value;
    choices.options[choices.options.length] = new Option(newChoice, '1');
    document.getElementById('multipleChoiceChoice').value = "";
    document.getElementById('multipleChoiceChoice').focus();
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