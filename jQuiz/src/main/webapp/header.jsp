<%@ page import="models.User" %>
<header>
    <div class="logo">
        <img src="logo.png" alt="Website Logo">
    </div>
    <nav class="main-nav">
        <ul>
            <li><a href="/">Home</a></li>
            <li><a href="/users.jsp">Users</a></li>
            <li><a href="/achievements.jsp">Achievements</a></li>
            <li><a href="/categories.jsp">Categories</a></li>
            <li><a href="/createquiz.jsp">Create quiz</a></li>
            <li><a href="/historySummary.jsp">History</a></li>
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
        <% } else { %>
        <ul>
            <li><a href="#"><%= curUser.getUsername() %></a></li>
            <li><a onclick="submitLogOut()">Log out</a></li>
            <form id="log-out-form" style="display: none" action="Login" method="get"></form>
        </ul>
        <% } %>
    </nav>
</header>
