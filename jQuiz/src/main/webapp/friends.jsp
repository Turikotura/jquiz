<%@ page import="java.util.List" %>
<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="database.*" %>
<%@ page import="models.Mail" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %><%--
  Created by IntelliJ IDEA.
  User: Dachi
  Date: 02.07.2024
  Time: 15:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User curUser = (User) request.getSession().getAttribute("curUser");

    List<User> friends = (List<User>) request.getAttribute("friends");
%>
<html>
<head>
    <title>Friends</title>
    <link href="style/general.css" rel="stylesheet" type="text/css">
    <link href="style/friends.css" rel="stylesheet" type="text/css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet" type="text/css">
</head>
<body>
<%@ include file="header.jsp" %>
<%--<%@ include file="mail.jsp" %>--%>
<main>
    <%
        if(curUser != null){
            for(User friend : friends){
    %>
    <div id="friend-<%=friend.getId()%>" class="friend-box">
        <div class="friend-info">
            <div class="friend-img" style="background-size: cover; background-image: url('image?type=user&userId=<%=friend.getId()%>');"></div>
            <div>
                <div class="friend-name"><strong>Name: </strong><%=friend.getUsername()%></div>
                <div class="friend-email"><strong>Email: </strong><%=friend.getEmail()%></div>
            </div>

            <a href="profile.jsp?username=<%=friend.getUsername()%>">Go to profile</a>
        </div>
        <form class="mail-form hidden" id="m-f-<%=friend.getId()%>" action="FriendsPage" method="post">
            <input hidden="hidden" type="number" name="receiverId" value="<%=friend.getId()%>">
            <input class="message-input" type="text" name="message" required>
            <button class="send-mail" type="submit">Send</button>
        </form>
        <div class="friend-buttons">
            <button class="friend-text-reveal-button" onclick="displayTextForm(<%=friend.getId()%>)">Text</button>
            <button class="friend-remove-button" onclick="removeFriend(<%=friend.getId()%>)">Remove</button>
        </div>
    </div>
    <%
            }
        }else{
    %>
    <h1>Log in first</h1>
    <%
        }
    %>
</main>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script> <!-- jQuery for AJAX -->
<script src="script/general.js"></script>
<script>
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

    function displayTextForm(friendId) {
        var form = document.getElementById("m-f-"+friendId);
        if(form.classList.contains('hidden')){
            form.classList.remove('hidden');
        }else{
            form.classList.add('hidden');
        }
    }

    function removeFriend(toRemoveId){
        $.ajax({
            url: 'FriendsPage',
            type: 'POST',
            data: {
                toRemoveId: toRemoveId
            },
            success: function(response) {
                console.log('Ajax request successful');
                console.log(response);
                // Optionally, update UI or handle response
                $('#friend-'+toRemoveId).remove();
            },
            error: function(xhr, status, error) {
                console.error('Ajax request failed');
                console.error(error);
            }
        })
    }

    $(document).ready(function() {
        $('.send-mail').click(function (e) {
           e.preventDefault();

            var form = $(this).closest('form');
            var formData = form.serializeArray();

            $(this.parentNode).find('.message-input').val('');

            ajaxCall(form,formData);
        });
    });
</script>

</body>
</html>
