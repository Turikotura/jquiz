<%@ page import="java.util.*" %>
<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="models.*" %>
<%@ page import="database.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    MailDatabase mailDB = getDatabase(Database.MAIL_DB,request);
    UserDatabase userDB = getDatabase(Database.USER_DB,request);
    HistoryDatabase historyDB = getDatabase(Database.HISTORY_DB,request);
    QuizDatabase quizDB = getDatabase(Database.QUIZ_DB,request);
    User curUser = (User) request.getSession().getAttribute("curUser");
    // Mail variables
    List<Mail> mails = new ArrayList<Mail>();
    List<String> senderNames = new ArrayList<String>();
    Map<Integer,Integer> maxGrades = new HashMap<Integer,Integer>();

    if(curUser != null) {
        // Get mails received by user
        mails = mailDB.getMailsByUserId(curUser.getId(), "RECEIVE");
        for (Mail mail : mails) {
            // Get names of senders
            senderNames.add(userDB.getById(mail.getSenderId()).getUsername());
            if (mail.getType() == MailTypes.CHALLENGE) {
                // Get max grade of senders for challenges
                History history = historyDB.getBestHistoryByUserAndQuizId(mail.getSenderId(), mail.getQuizId());
                int grade = (history == null) ? 0 : history.getGrade();
                maxGrades.put(mail.getId(), grade);
            }
        }
    }

    List<Category> categories = new ArrayList<Category>();
    categories.add(new Category("Science", "https://cdn-icons-png.flaticon.com/512/3749/3749768.png"));
    categories.add(new Category("Sports", "https://cdn-icons-png.flaticon.com/512/4344/4344985.png"));
    categories.add(new Category("History", "https://static.thenounproject.com/png/1441452-200.png"));
    categories.add(new Category("Music", "https://cdn-icons-png.flaticon.com/512/4472/4472606.png"));
    categories.add(new Category("Geography", "https://cdn-icons-png.flaticon.com/512/2234/2234665.png"));
    categories.add(new Category("Literature", "https://cdn-icons-png.flaticon.com/512/3749/3749830.png"));
    categories.add(new Category("Movies", "https://cdn-icons-png.flaticon.com/512/3938/3938627.png"));
    categories.add(new Category("Gaming", "https://cdn-icons-png.freepik.com/512/7708/7708371.png"));
    categories.add(new Category("Arts", "https://cdn-icons-png.freepik.com/512/5259/5259910.png"));
    categories.add(new Category("Other", "https://simpleicon.com/wp-content/uploads/smile.png"));

    TagDatabase tagDB = getDatabase(Database.TAG_DB,request);
    List<Tag> tags = tagDB.getAllTags();

    Map<Character, List<Tag>> tagGroups = new TreeMap<Character, List<Tag>>();
    for (Tag tag : tags) {
        char firstLetter = Character.toUpperCase(tag.getName().charAt(0));
        if (!tagGroups.containsKey(firstLetter)) {
            tagGroups.put(firstLetter, new ArrayList<Tag>());
        }
        tagGroups.get(firstLetter).add(tag);
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Categories</title>
    <link href="style/general.css" rel="stylesheet" type="text/css">
    <link href="style/categories.css" rel="stylesheet" type="text/css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet" type="text/css">
</head>
<body>
<%@ include file="header.jsp" %>
<%@ include file="mail.jsp" %>

<main>
    <div class="category-list-wrapper">
        <h1>Categories</h1>
        <div class="category-boxes">
            <%
                for (Category category : categories) {
            %>
            <a href="category.jsp?category=<%= category.getName() %>">
                <div class="category-box">
                    <div class="category-box-image" style="background-image: url(<%= category.getImageUrl() %>);"></div>
                    <div class="category-box-content"><%= category.getName() %></div>
                </div>
            </a>
            <%
                }
            %>
        </div>
        <h1>Tags</h1>
        <div class="tag-lexicon">
            <% for (Map.Entry<Character, List<Tag>> entry : tagGroups.entrySet()) { %>
            <div class="tag-group">
                <h2><%= entry.getKey() %></h2>
                <ul>
                    <% for (Tag tag : entry.getValue()) { %>
                    <li><a href="quizzes?tag=<%= tag.getName()%>"><%= tag.getName()%></a></li>
                    <% } %>
                </ul>
            </div>
            <% } %>
        </div>
    </div>
</main>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script> <!-- jQuery for AJAX -->
<script src="script/general.js"></script>

</body>
</html>
