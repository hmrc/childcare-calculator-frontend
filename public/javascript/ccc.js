window.onload = function() {

    $("#submit").click(function(){
        if ($('input[type="radio"]').is(":checked")) {
            var checkedValue = $('input[type="radio"]:checked').val();
            ga('send', 'event', 'decision', 'submit', getCheckedGAValue(checkedValue));
        }
    });


    function getCheckedGAValue (value) {
        if(value === "true") {
            return "yes"
        } else if (value === "false") {
            return "no"
        } else {
            return value
        }
    }

};