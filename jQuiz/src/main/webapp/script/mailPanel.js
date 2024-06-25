function ajaxCall(form,formData){
    $.ajax({
        url: form.attr('action'),
        type: form.attr('method'),
        data: formData,
        success: function(response) {
            console.log('Ajax request successful');
            console.log(response);
            // Optionally, update UI or handle response
        },
        error: function(xhr, status, error) {
            console.error('Ajax request failed');
            console.error(error);
        }
    });
}

$(document).ready(function() {
    // Ajax request on clicking the div with class 'clickableDiv'
    $('.message-box').click(function() {
        $(this).removeClass('active-message');
        var form = $(this).closest('form');
        var formData = form.serializeArray();
        formData.push({ name: 'seen', value: 'true' });

        ajaxCall(form,formData);
    });

    // Ajax request on form submit button click
    $('.friend-acpt-submit').click(function(e) {
        e.preventDefault(); // Prevent default form submission

        var form = $(this).closest('form');
        var formData = form.serializeArray();
        formData.push({ name: 'seen', value: 'false' });
        formData.push({ name: 'accept', value: 'true' });
        $('input', form).prop('disabled', true);

        ajaxCall(form,formData);
    });

    // Ajax request on form submit button click
    $('.friend-rjct-submit').click(function(e) {
        e.preventDefault(); // Prevent default form submission

        var form = $(this).closest('form');
        var formData = form.serializeArray();
        formData.push({ name: 'seen', value: 'false' });
        formData.push({ name: 'accept', value: 'false' });
        $('input', form).prop('disabled', true);

        ajaxCall(form,formData);
    });
});
function togglePanel() {
    var panel = document.getElementById("mail-panel");
    panel.style.display = (panel.style.display === "block") ? "none" : "block";
}