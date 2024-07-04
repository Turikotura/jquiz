<%@ page import="models.*" %>
<%@ page import="database.*" %>
<%@ page import="javax.servlet.jsp.tagext.TagData" %>
<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="database.Database" %>
<%@ page import="java.util.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User curUser = (User) request.getSession().getAttribute("curUser");

    MailDatabase mailDB = getDatabase(Database.MAIL_DB,request);
    UserDatabase userDB = getDatabase(Database.USER_DB,request);
    HistoryDatabase historyDB = getDatabase(Database.HISTORY_DB,request);

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
    <title>Tag Lexicon</title>
    <link href="style/general.css" rel="stylesheet" type="text/css">
    <link href="style/tags.css" rel="stylesheet" type="text/css">
</head>
<body>
<%@ include file="header.jsp" %>
<%@ include file="mail.jsp" %>
<main>
    <h1>Tags</h1>
    <div class="tag-lexicon">
        <% for (Map.Entry<Character, List<Tag>> entry : tagGroups.entrySet()) { %>
        <div class="tag-group">
            <h2><%= entry.getKey() %></h2>
            <ul>
                <% for (Tag tag : entry.getValue()) { %>
                <li><a href="tag.jsp?name=<%= tag.getName() %>"><%= tag.getName() %></a></li>
                <% } %>
            </ul>
        </div>
        <% } %>
    </div>
</main>
</body>
</html>
