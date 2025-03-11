window.onload = function() {

    $("#submit").click(function(){
        if ($('input[type="radio"]').is(":checked")) {
            var checkedValue = $('input[type="radio"]:checked').val();
            ga('send', 'event', 'radio - select', 'Do you get Universal Credit?', getCheckedGAValue(checkedValue));
        }
    });


    function getCheckedGAValue value {
        if(value === true) {
            return "Universal Credit)"
        else (value === false) {
            return "None of these"
        }
    }

};
