window.onload = function() {

    $("#submit").click(function(){
        if ($('input[type="radio"]').is(":checked")) {
            var checkedValue = $('input[type="radio"]:checked').val();
            ga('send', 'event', 'radio - select', 'Do you get tax credits or Universal Credit?', getCheckedGAValue(checkedValue));
        }
    });


    function getCheckedGAValue (value) {
        if(value === "tc") {
            return "Tax credits (includes Working and Child Tax Credit)"
        } else if (value === "uc") {
            return "Universal Credit"
        } else if (value === "none") {
            return "None of these"
        }
    }

};