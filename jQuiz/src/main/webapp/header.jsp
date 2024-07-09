<%@ page import="models.User" %>
<header>
    <% UserDatabase statisticsUserDB = getDatabase(Database.USER_DB,request);%>
    <div class="logo">
        <img src="logo.png" alt="Website Logo">
    </div>
    <nav class="main-nav">
        <ul>
            <li><a href="/">Home</a></li>
            <% if(curUser != null && statisticsUserDB.isUserAdmin(curUser.getId())) { %>
                <li><a href="/statistics.jsp">Statistics</a></li>
            <% } %>
            <li><a href="/achievements.jsp">Achievements</a></li>
            <li><a href="/categories.jsp">Categories</a></li>
            <li><a href=<%=curUser == null ? "/login.jsp" : "/createquiz.jsp"%>>Create quiz</a></li>
            <li><a href=<%=curUser == null ? "/login.jsp" : "/HistorySummary"%>>History</a></li>
            <li><a href=<%=curUser == null ? "/login.jsp" : "/FriendsPage"%>>Friends</a></li>
        </ul>
    </nav>
    <nav class="mail-nav">
        <ul>
            <li><a onclick="togglePanel()">Show Messages</a></li>
        </ul>
    </nav>
    <div class="search-box">
        <form action="Search" method="get">
            <input type="search" name="searchString" placeholder="Search..." required>
            <button type="submit"><i class="fa fa-search"></i></button>
        </form>
    </div>
    <nav class="auth-nav">
        <% if (curUser == null) { %>
        <ul>
            <li><a href="login.jsp">Login</a></li>
            <li><a href="register.jsp">Register</a></li>
        </ul>
        <% } else {
            String loggedInAs = curUser.getUsername();%>
        <ul>
            <li><a href="profile.jsp?username=<%=loggedInAs%>"><%=loggedInAs%></a></li>
            <li><a onclick="submitLogOut()">Log out</a></li>
        </ul>
        <form id="log-out-form" style="display: none" action="Login" method="post">
            <input type="hidden" name="logInAttempted" value="false">
        </form>
        <% } %>
    </nav>
</header>
