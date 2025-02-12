window.onload = function() {

    $("#submit").click(function(){
        if ($('input[type="radio"]').is(":checked")) {
            var checkedValue = $('input[type="radio"]:checked').val();
            ga('send', 'event', 'radio - select', 'Do you get tax credits or Universal Credit?', getCheckedGAValue(checkedValue));
        }
    });


    function getCheckedGAValue value {
        if(value === true) {
            return "Universal Credit)"
        else (value === "none") {
            return "None of these"
        }
    }

};