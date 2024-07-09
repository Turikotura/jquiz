<%@ page import="models.*" %>
<%@ page import="java.util.*" %>
<%@ page import="database.*" %>
<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User curUser = (User) request.getSession().getAttribute("curUser");

    MailDatabase mailDB = getDatabase(Database.MAIL_DB,request);
    UserDatabase userDB = getDatabase(Database.USER_DB,request);
    HistoryDatabase historyDB = getDatabase(Database.HISTORY_DB,request);
    QuizDatabase quizDB = getDatabase(Database.QUIZ_DB,request);
    AchievementDatabase achievementDB = getDatabase(Database.ACHIEVEMENT_DB,request);

    // Mail variables
    List<Mail> mails = new ArrayList<Mail>();
    List<String> senderNames = new ArrayList<String>();
    Map<Integer,Integer> maxGrades = new HashMap<Integer,Integer>();

    if(curUser != null){
        // Get mails received by user
        mails = mailDB.getMailsByUserId(curUser.getId(),"RECEIVE");
        for(Mail mail : mails){
            // Get names of senders
            senderNames.add(userDB.getById(mail.getSenderId()).getUsername());
            if(mail.getType() == MailTypes.CHALLENGE){
                // Get max grade of senders for challenges
                History history = historyDB.getBestHistoryByUserAndQuizId(mail.getSenderId(),mail.getQuizId());
                int grade = (history == null) ? 0 : history.getGrade();
                maxGrades.put(mail.getId(),grade);
            }
        }
    }

    String match = request.getParameter("match");
    String category = request.getParameter("category");
    String tag = request.getParameter("tag");
    String sortBy = request.getParameter("sortBy");

    String title = "Quizzes";
    if(match != null && !match.isEmpty()){
        title += " matching string: \"" + match + "\"";
    }
    if(category != null && !category.isEmpty()){
        title += " in category: \"" + category + "\"";
    }
    if(tag != null && !tag.isEmpty()){
        title += " tagged with: \"" + tag + "\"";
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Quizzes</title>
    <link href="style/general.css" rel="stylesheet" type="text/css">
    <link href="style/quizzes.css" rel="stylesheet" type="text/css">
</head>
<body>

<%@ include file="header.jsp" %>

<main>
    <h1><%= title%></h1>
    <form method="get" action="quizzes" class="search-form">
        <label>
            Match:
            <input type="text" name="match" value="<%= request.getParameter("match") != null ? request.getParameter("match") : "" %>">
        </label>
        <label>
            Tag:
            <input type="text" name="tag" value="<%= request.getParameter("tag") != null ? request.getParameter("tag") : "" %>">
        </label>
        <label>
            Category:
            <select name="category">
                <option value="">All</option>
                <option value="Science" <%= "Science".equals(request.getParameter("category")) ? "selected" : "" %>>Science</option>
                <option value="Sports" <%= "Sports".equals(request.getParameter("category")) ? "selected" : "" %>>Sports</option>
                <option value="History" <%= "History".equals(request.getParameter("category")) ? "selected" : "" %>>History</option>
                <option value="Music" <%= "Music".equals(request.getParameter("category")) ? "selected" : "" %>>Music</option>
                <option value="Geography" <%= "Geography".equals(request.getParameter("category")) ? "selected" : "" %>>Geography</option>
                <option value="Literature" <%= "Literature".equals(request.getParameter("category")) ? "selected" : "" %>>Literature</option>
                <option value="Movies" <%= "Movies".equals(request.getParameter("category")) ? "selected" : "" %>>Movies</option>
                <option value="Gaming" <%= "Gaming".equals(request.getParameter("category")) ? "selected" : "" %>>Gaming</option>
                <option value="Arts" <%= "Arts".equals(request.getParameter("category")) ? "selected" : "" %>>Arts</option>
                <option value="Other" <%= "Other".equals(request.getParameter("category")) ? "selected" : "" %>>Other</option>
            </select>
        </label>
        <label>
            Sort By:
            <select name="sortBy">
                <option value="NEWEST" <%= "NEWEST".equals(request.getParameter("sortBy")) ? "selected" : "" %>>Newest</option>
                <option value="TOTAL" <%= "TOTAL".equals(request.getParameter("sortBy")) ? "selected" : "" %>>Most Played</option>
                <option value="LAST_MONTH" <%= "LAST_MONTH".equals(request.getParameter("sortBy")) ? "selected" : "" %>>Most Played (Last Month)</option>
            </select>
        </label>
        <button type="submit">Search</button>
    </form>
    <div class="quiz-list-wrapper">
        <div class="quiz-boxes">
            <% List<Quiz> quizzes = (List<Quiz>) request.getAttribute("quizzes");
                for (Quiz quiz : quizzes) { %>
            <div class="quiz-box">
                <a href="quizInfo.jsp?quizId=<%= quiz.getId() %>">
                    <div class="quiz-box-top" style="background-size: cover; background-image: url('image?type=quiz&quizId=<%= quiz.getId() %>');">
                        <p class="quiz-box-name"><%= quiz.getTitle() %></p>
                    </div>
                    <div class="quiz-box-bot">
                        <p><%= quiz.getTotalPlayCount() %></p>
                    </div>
                </a>
            </div>
            <% } %>
        </div>
    </div>

    <div class="pagination">
        <%
            Integer currentPageObj = (Integer) request.getAttribute("currentPage");
            Integer totalPagesObj = (Integer) request.getAttribute("totalPages");

            int currentPage = currentPageObj != null ? currentPageObj : 1;
            int totalPages = totalPagesObj != null ? totalPagesObj : 1;

            String parameters = "";
            if(match != null){
                parameters += "match=" + match + "&";
            }
            if(category != null){
                parameters += "category=" + category + "&";
            }
            if(tag != null){
                parameters += "tag=" + tag + "&";
            }
            if(sortBy != null){
                parameters += "sortBy=" + sortBy + "&";
            }

            if (currentPage > 1) { %>
        <a href="quizzes?<%= parameters%>page=<%= currentPage - 1 %>">Previous</a>
        <% }

            for (int i = 1; i <= totalPages; i++) {
                if (i == currentPage) { %>
        <span class="current-page"><%= i %></span>
        <% } else { %>
        <a href="quizzes?<%= parameters%>page=<%= i %>"><%= i %></a>
        <% }
        }

            if (currentPage < totalPages) { %>
        <a href="quizzes?<%= parameters%>page=<%= currentPage + 1 %>">Next</a>
        <% } %>
    </div>
</main>

</body>
</html>
