<%@ page import="models.Quiz" %>
<%@ page import="java.util.List" %>
<%@ page import="database.UserDatabase" %>
<%@ page import="models.User" %>
<%@ page import="database.Database" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="models.Mail" %>
<%@ page import="database.MailDatabase" %>
<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="models.MailTypes" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <%
        MailDatabase maildb = getDatabase(Database.MAIL_DB,request);
        UserDatabase userdb = getDatabase(Database.USER_DB,request);

        String username = (String) request.getSession().getAttribute("curUser");
        User curUser = null;
        List<Mail> mails = new ArrayList<Mail>();

        if(username != null){
            try {
                curUser = userdb.getByUsername(username);
                if(curUser != null){
                    mails = maildb.getMailsByUserId(curUser.getId(),"RECEIVE");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    %>
    <title>Quiz List</title>
    <link href="index.css" rel="stylesheet" type="text/css">
</head>
<body>
<div class="main">
    <header>
        <div class="logo">
            <img src="logo.png" alt="Website Logo">
        </div>
        <nav class="main-nav">
            <ul>
                <li><a href="/index.jsp">Home</a></li>
                <li><a href="/users.jsp">Users</a></li>
                <li><a href="/achievements.jsp">Achievements</a></li>
                <li><a href="/categories.jsp">Categories</a></li>
                <li><a href="/createquiz.jsp">Create quiz</a></li>
            </ul>
        </nav>
        <nav class="mail-nav">
            <button onclick="togglePanel()">Show Messages</button>
        </nav>
        <nav class="auth-nav">
            <%if(request.getSession().getAttribute("curUser") == null) { %>
            <ul>
                <li><a href="login.jsp">Login</a></li>
                <li><a href="register.jsp">Register</a></li>
            </ul>
            <%} else { %>
            <ul>
                <li><a href="#"><%=(String)request.getSession().getAttribute("curUser")%></a></li>
                <li><form action="Login" method="get">
                    <input type="submit" value="Log out">
                </form></li>
            </ul>
            <%}%>

        </nav>
    </header>

    <div id="mail-panel">
        <%
            for(Mail mail : mails){
                String active = "";
                if(!mail.getSeen()){
                    active = "active-message";
                }
        %>
        <form id="mail-form-<%=mail.getId()%>" method="post" action="MailPanel">
            <input type="hidden" name="mailId" value="<%=mail.getId()%>">
            <div class="message-box <%=active%>">
                <p class="message-date"><%=mail.getTimeSent()%></p>
                <h3 class="message-text"><%=mail.getText()%></h3>
                <h4 class="message-author">From: <%=mail.getSenderId()%></h4>

            <%
                if(mail.getType() == MailTypes.CHALLENGE){
            %>
                <p>Best Score: //TODO</p>
                <a href="quizInfo.jsp?quizId=<%=mail.getQuizId()%>">Accept</a>
            <%
                }else if(mail.getType() == MailTypes.FRIEND_REQUEST){
            %>
                <input type="submit" value="accept">
            <%
                }
            %>
            </div>
        </form>
        <%
            }
        %>
        <div class="message-box">Message 2</div>
        <div class="message-box">Message 3</div>
        <!-- Add more message boxes here -->
    </div>

    <div class="quiz-list-wrapper">
        <h2>Recently added quizzes</h2>
        <div class="quiz-boxes">
            <%
                try {
                    UserDatabase userDB = (UserDatabase) application.getAttribute(Database.USER_DB);
                    List<Quiz> recentQuizzes = (List<Quiz>) request.getAttribute("recentQuizzes");
                    List<Quiz> popularQuizzes = (List<Quiz>) request.getAttribute("popularQuizzes");
                    List<Quiz> lastMonthPopularQuizzes = (List<Quiz>) request.getAttribute("lastMonthPopularQuizzes");

                    if (userDB == null) {
                        throw new Exception("User database not found in application.");
                    }
                    if (recentQuizzes == null || popularQuizzes == null) {
                        throw new Exception("Quizzes not found in request.");
                    }
                    for (Quiz quiz : recentQuizzes) {
                        User author = userDB.getById(quiz.getAuthorId());
            %>
            <div class="quiz-box">
                <a href="quizInfo.jsp?quizId=<%=quiz.getId()%>">
                    <div class="quiz-box-top">
                        <p class="quiz-box-name"><%= quiz.getTitle() %></p>
                    </div>
                    <div class="quiz-box-bot">
                        <p><%= quiz.getTotalPlayCount()%></p>
                        <p><%= author.getUsername()%></p>
                    </div>
                </a>
            </div>
            <%
                    }
            %>

        </div>
    </div>
    <div class="quiz-list-wrapper">
        <h2>All-time popular quizzes</h2>
        <div class="quiz-boxes">
        <%
                    for (Quiz quiz : popularQuizzes) {
                        User author = userDB.getById(quiz.getAuthorId());
                %>
                <div class="quiz-box">
                    <a href="quizInfo.jsp?quizId=<%=quiz.getId()%>">
                        <div class="quiz-box-top">
                            <p class="quiz-box-name"><%= quiz.getTitle() %></p>
                        </div>
                        <div class="quiz-box-bot">
                            <p><%= quiz.getTotalPlayCount()%></p>
                            <p><%= author.getUsername()%></p>
                        </div>
                    </a>
                </div>
                <%
                        }
                %>
        </div>
    </div>
    <div class="quiz-list-wrapper">
        <h2>Popular quizzes in the last month</h2>
        <div class="quiz-boxes">
            <%
                for (Quiz quiz : lastMonthPopularQuizzes) {
                    User author = userDB.getById(quiz.getAuthorId());
            %>
            <div class="quiz-box">
                <a href="quizInfo.jsp?quizId=<%=quiz.getId()%>">
                    <div class="quiz-box-top">
                        <p class="quiz-box-name"><%= quiz.getTitle() %></p>
                    </div>
                    <div class="quiz-box-bot">
                        <p><%= quiz.getTotalPlayCount()%></p>
                        <p><%= author.getUsername()%></p>
                    </div>
                </a>
            </div>
            <%
                }
            %>
        </div>
    </div>

    <%
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<p>Error loading quizzes: " + e.getMessage() + "</p>");
        }
    %>
</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script> <!-- jQuery for AJAX -->
<script>
    // Function to handle form submission
    function submitForm(formId) {
        $('#' + formId).submit(function (event) {
            // Prevent default form submission
            event.preventDefault();

            // Serialize form data
            var formData = $('#' + formId).serialize();

            // Send AJAX request
            $.ajax({
                type: 'POST',
                url: '/MailPanel', // Replace with your servlet URL
                data: formData,
                success: function (response) {
                    // Optionally, you can update the current page content based on the response
                },
                error: function () {
                }
            });
        });
    }

    document.addEventListener("DOMContentLoaded", function() {
        var messageBoxes = document.querySelectorAll(".message-box");

        messageBoxes.forEach(function (div) {
            div.addEventListener("click", function () {
                var form = div.parentElement;
                if (form) {
                    var hiddenInput = document.createElement('input');
                    hiddenInput.type = 'hidden';
                    hiddenInput.name = 'submittedBy';
                    hiddenInput.value = 'div';
                    form.appendChild(hiddenInput);
                    div.classList.remove("active-message");
                    submitForm(form.id);
                }
            });
        });
    });
    function togglePanel() {
        <%
        if(request.getSession().getAttribute("curUser") == null){
        %>
        window.location.href = "login.jsp";
        <%
        }else{
        %>
        var panel = document.getElementById("mail-panel");
        panel.style.display = (panel.style.display === "block") ? "none" : "block";
        <%
        }
        %>
    }
</script>
</body>
</html>