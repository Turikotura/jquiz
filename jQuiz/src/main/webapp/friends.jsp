<%@ page import="java.util.List" %>
<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="database.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="models.*" %>
<%@ page import="java.text.SimpleDateFormat" %><%--
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
    // Mail variables
    List<Mail> mails = (List<Mail>) request.getAttribute("mails");
    List<String> senderNames = (List<String>) request.getAttribute("senderNames");
    Map<Integer,Integer> maxGrades = (Map<Integer, Integer>) request.getAttribute("maxGrades");

    List<Activity> activities = (List<Activity>) request.getAttribute("activities");
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
<%@ include file="mail.jsp" %>
<main>
    <div class="activities-panel">
        <%
            for(Activity activity : activities){
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDateString = dateFormat.format(activity.getActivityTime());
        %>
        <div class="activity-box">
            <h3><%=formattedDateString%></h3>
            <h3><a href="profile.jsp?username=<%=activity.getUsername()%>"><%=activity.getUsername()%></a></h3>
            <div class="details">
                <p><%=activity.getText()%></p><br>
                <%
                    ActivityTypes type = activity.getType();
                    String link = "";
                    if(type == ActivityTypes.QUIZ_CREATE || type == ActivityTypes.QUIZ_WRITE) {
                        link = "quizInfo.jsp?quizId=" + activity.getNextId();
                    }
                %>
                <a href="<%=link%>"><%=activity.getNextText()%></a>
            </div>
        </div>
        <%
            }
        %>
    </div>

    <%
        if(curUser != null){
            for(User friend : friends){
    %>
    <%--row for friend info--%>
    <div id="friend-<%=friend.getId()%>" class="friend-box">
        <%--friend info--%>
        <div class="friend-info">
            <div class="friend-img" style="background-size: cover; background-image: url('image?type=user&userId=<%=friend.getId()%>');"></div>
            <div>
                <div class="friend-name"><strong>Name: </strong><%=friend.getUsername()%></div>
                <div class="friend-email"><strong>Email: </strong><%=friend.getEmail()%></div>
            </div>

            <a href="profile.jsp?username=<%=friend.getUsername()%>">Go to profile</a>
        </div>
        <%--send mail--%>
        <form class="mail-form hidden" id="m-f-<%=friend.getId()%>" action="FriendsPage" method="post">
            <input hidden="hidden" type="number" name="receiverId" value="<%=friend.getId()%>">
            <textarea class="message-input" name="message" form="m-f-<%=friend.getId()%>" required></textarea><br>
            <button class="send-mail" type="submit">Send</button>
        </form>
        <%--friend button--%>
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
<script src="script/mailPanel.js"></script>
<script>
    // Display text to send mail
    function displayTextForm(friendId) {
        var form = $("#m-f-"+friendId);
        if(form.hasClass('hidden')){
            form.removeClass('hidden');
        }else{
            form.addClass('hidden');
        }
    }

    // Remove friend
    function removeFriend(toRemoveId){
        $.ajax({
            url: 'FriendsPage',
            type: 'POST',
            data: {
                toRemoveId: toRemoveId
            },
            success: function(response) {
                console.log('Removed friend');
                $('#friend-'+toRemoveId).remove();
            },
            error: function(xhr, status, error) {
                console.error('Could not remove friend');
            }
        })
    }

    // Send mail
    $(document).ready(function() {
        $('.send-mail').click(function (e) {
           e.preventDefault();

            var form = $(this).closest('form');
            var formData = form.serializeArray();

            $(this.parentNode).find('.message-input').val('');

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
        });
    });
</script>

</body>
</html>