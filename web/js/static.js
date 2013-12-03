$(document).ready(function() {
    onlyAllowNumbers();
    onlyAllowDecimalNumbers();
});

function displayErrorMessage(id, message) {
    $("#" + id + "ErrorMessage").text(message);
    $("#" + id + "ErrorMessage").show();
}

function isNotEmpty(id, type, isRequired) {
    var value = getValueByTypeAndID(id, type);
    if (value == null || value == "") {
        if (isRequired) {
            displayErrorMessage(id, "* This question is required");
        }
        return false;
    }
    return true;
}

function getValueByTypeAndID(id, type) {
    var value = "";
    switch (type) {
        case "radio":
            value = $('input[name=' + id + ']:checked').val();
            break;
        case "textarea":
            value = $("#" + id).text();
            break;
        case "checkbox":
            value = $("input[type=checkbox][id^=" + id + "]:checked").map(function() {
                return this.value;
            }).get().join(';;');
            break;
        default:
            value = $("#" + id).val();
            break;
    }
    return value;
}

function getValueByTypeAndIDForDB(id, type) {
    var value = getValueByTypeAndID(id, type);
    if (type != "decimalNumber" && type != "wholeNumber") {
        value = "'" + value + "'";
    }
    return value;
}

function meetsLengthRequirements(id, type, min, max) {
    var value = getValueByTypeAndID(id, type);
    if (value.length < min) {
        displayErrorMessage(id, "* must be at least " + min + " characters");
        return false;
    } else if (value.length > max) {
        displayErrorMessage(id, "* cannot be more than " + max + " characters");
        return false;
    }
    return true;
}

function containsOnlyValidChars(id, type, validChars) {
    var value = getValueByTypeAndID(id, type);
    if (value != undefined && value != null) {
        for (var i = 0; i < value.length; i++) {
            if (validChars.indexOf(value.charAt(i)) < 0) {
                displayErrorMessage(id, "* cannot contain '" + value.charAt(i) + "'");
                return false;
            }
        }
    }
    return true;
}

function meetsWholeNumberRequirements(id, min, max) {
    var val = getValueByTypeAndID(id, "wholeNumber");
    val = parseInt(val);
    if (isNaN(val)) {
        displayErrorMessage(id, "* must be a whole number");
        return false;
    } else {
        if (min != "") {
            if (val < min) {
                displayErrorMessage(id, "* must be at least " + min);
                return false;
            }
        }
        if (max != "") {
            if (val > max) {
                displayErrorMessage(id, "* cannot be more than " + max);
                return false;
            }
        }
    }
    return true;
}

function meetsDecimalNumberRequirements(id, min, max) {
    var val = getValueByTypeAndID(id, "decimalNumber");
    val = parseFloat(val);
    if (isNaN(val)) {
        displayErrorMessage(id, "* must be a number");
        return false;
    } else {
        if (min != "") {
            if (val < min) {
                displayErrorMessage(id, "* must be at least " + min);
                return false;
            }
        }
        if (max != "") {
            if (val > max) {
                displayErrorMessage(id, "* cannot be more than " + max);
                return false;
            }
        }
    }
    return true;
}

function padDecimalPointPlaces(id, places) {
    var padded = parseFloat($("#" + id).val());
    if (!isNaN(padded)) {
        $("#" + id).val(padded.toFixed(places));
    } else {
        $("#" + id).val("");
    }
}

function showHideOtherChoiceTextbox(selectID, textID, type) {
    var otherSelected = false;
    switch (type) {
        case "radio":
            otherSelected = $("#" + selectID + "OtherChoiceSpecify").is(':checked');
            break;
        case "checkbox":
            otherSelected = $("#" + selectID).is(':checked');
            break;
        case "dropdown":
            otherSelected = $("#" + selectID).val() == "otherChoiceSpecify";
            break;
    }
    if (otherSelected) {
        $("#" + textID).show();
    } else {
        $("#" + textID).hide();
    }
}

function validateAnswerLimit(id, limit) {
    if (limit == "unlimited") {
        return true;
    } else {
        if ($("input[type=checkbox][id^=" + id + "]:checked").length > limit) {
            displayErrorMessage(id, "* No more than " + limit + " answers can be selected")
            return false;
        } else {
            return true;
        }
    }
}

function onlyAllowNumbers() {
    $(".onlyAllowNumbers").keydown(function (event) {
        // Allow: backspace, delete, tab, escape, and enter
        if (event.keyCode == 46 || event.keyCode == 8 || event.keyCode == 9 || event.keyCode == 27 || event.keyCode == 13 ||
            // Allow: Ctrl+A
            (event.keyCode == 65 && event.ctrlKey === true) ||
            // Allow: Ctrl+C
            (event.keyCode == 67 && event.ctrlKey === true) ||
            // Allow: Ctrl+V
            (event.keyCode == 86 && event.ctrlKey === true) ||
            // Allow: Ctrl+X
            (event.keyCode == 88 && event.ctrlKey === true) ||
            // Allow: home, end, left, right
            (event.keyCode >= 35 && event.keyCode <= 39) ||
            //Allow -
            (event.keyCode == 189)) {
            // let it happen, don't do anything
            return;
        }
        else {
            // Ensure that it is a number and stop the keypress
            if (event.shiftKey || (event.keyCode < 48 || event.keyCode > 57) && (event.keyCode < 96 || event.keyCode > 105)) {
                event.preventDefault();
            }
        }
    });
}

function onlyAllowDecimalNumbers() {
    $(".onlyAllowDecimalNumbers").keydown(function (event) {
        // Allow: backspace, delete, tab, escape, and enter
        if (event.keyCode == 46 || event.keyCode == 8 || event.keyCode == 9 || event.keyCode == 27 || event.keyCode == 13 ||
            // Allow: Ctrl+A
            (event.keyCode == 65 && event.ctrlKey === true) ||
            // Allow: Ctrl+C
            (event.keyCode == 67 && event.ctrlKey === true) ||
            // Allow: Ctrl+V
            (event.keyCode == 86 && event.ctrlKey === true) ||
            // Allow: Ctrl+X
            (event.keyCode == 88 && event.ctrlKey === true) ||
            // Allow: home, end, left, right
            (event.keyCode >= 35 && event.keyCode <= 39) ||
            // Allow: decimal point, period, & -
            (event.KeyCode == 110) || (event.keyCode == 190) || (event.keyCode == 189)) {
            // let it happen, don't do anything
            return;
        } else {
            // Ensure that it is a number and stop the keypress
            if (event.shiftKey || (event.keyCode < 48 || event.keyCode > 57) && (event.keyCode < 96 || event.keyCode > 105)) {
                event.preventDefault();
            }
        }
    });
}