// Default server call
function ajaxCall(form,formData){
    $.ajax({
        url: form.attr('action'),
        type: form.attr('method'),
        data: formData,
        success: function(response) {
            console.log('Ajax request successful');
            console.log(response);
        },
        error: function(xhr, status, error) {
            console.error('Ajax request failed');
            console.error(error);
        }
    });
}

// Mail behaviour
$(document).ready(function() {
    // Clicking box - save mail as seen
    $('.message-box').click(function() {
        $(this).removeClass('active-message');
        var form = $(this).closest('form');
        var formData = form.serializeArray();
        formData.push({ name: 'seen', value: 'true' });

        ajaxCall(form,formData);
    });

    // Accept - add friend and delete mail
    $('.friend-acpt-submit').click(function(e) {
        e.preventDefault();

        var form = $(this).closest('form');
        var formData = form.serializeArray();
        formData.push({ name: 'seen', value: 'false' });
        formData.push({ name: 'accept', value: 'true' });
        $('input', form).prop('disabled', true);

        ajaxCall(form,formData);
    });

    // Reject - reject friend request and delete mail
    $('.friend-rjct-submit').click(function(e) {
        e.preventDefault();

        var form = $(this).closest('form');
        var formData = form.serializeArray();
        formData.push({ name: 'seen', value: 'false' });
        formData.push({ name: 'accept', value: 'false' });
        $('input', form).prop('disabled', true);

        ajaxCall(form,formData);
    });
});
// Show mail panel
function togglePanel() {
    var panel = document.getElementById("mail-panel");
    panel.style.display = (panel.style.display === "block") ? "none" : "block";
}