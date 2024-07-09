<%@ page import="java.sql.SQLException" %>
<%@ page import="models.*" %>
<%@ page import="java.util.List" %>
<%@ page import="models.Mail" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="database.*" %>
<%@ page import="models.History" %>
<%@ page import="database.HistoryDatabase" %>
<%@ page import="database.UserDatabase" %>
<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="Statistics.Statistics" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<%
    User curUser = (User) request.getSession().getAttribute("curUser");

    MailDatabase mailDB = getDatabase(Database.MAIL_DB,request);
    UserDatabase userDB = getDatabase(Database.USER_DB,request);
    HistoryDatabase historyDB = getDatabase(Database.HISTORY_DB,request);
    QuizDatabase quizDB = getDatabase(Database.QUIZ_DB,request);
    List<History> prevAttempts, bestAttempts, fastestAttempts = new ArrayList<History>();

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

    int quizId = Integer.parseInt(request.getParameter("quizId"));
    Quiz curQuiz = null;
    User author = null;

    CommentDatabase commentdb = getDatabase(Database.COMMENT_DB,request);
    List<Comment> comments = new ArrayList<Comment>();
    Map<Integer,String> commentUsernames = new HashMap<Integer,String>();

    RatingDatabase ratingdb = getDatabase(Database.RATING_DB,request);
    Rating rating = null;
    double averageRating = 0;

    try {
        curQuiz = quizDB.getById(quizId);
        author = userDB.getById(curQuiz.getAuthorId());

        comments = commentdb.getCommentsByQuizId(quizId);
        for(Comment comment : comments){
            commentUsernames.put(comment.getId(),userDB.getById(comment.getUserId()).getUsername());
        }

        if(curUser != null){
            rating = ratingdb.getRatingByQuizAndUserId(quizId,curUser.getId());
        }
        List<Rating> quizRatings = ratingdb.getRatingsByQuizId(quizId);
        int sum = 0;
        int am = 0;
        for(Rating r : quizRatings){
            sum += r.getRating();
            am++;
        }
        if(am > 0){
            averageRating = (int) Math.rint((double) sum / am);
        }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
    }

    List<History> allAttempts = historyDB.getHistoryByQuizId(quizId);
    ArrayList<Integer> allGrades = new ArrayList();
    ArrayList<Integer> allTimes = new ArrayList();
    for(History curAttempt : allAttempts) {
        allGrades.add(curAttempt.getGrade());
        allTimes.add(curAttempt.getWritingTime());
    }
    double gradeAv = Statistics.getAverage(allGrades), gradeVar = Statistics.getVariance(allGrades),
            timeAv = Statistics.getAverage(allTimes), timeVar = Statistics.getVariance(allTimes);

    timeAv /= 1000.0;
    timeVar /= 1000000.0;
%>
<head>
    <title><%=curQuiz.getTitle()%></title>
    <link href="style/general.css" rel="stylesheet" type="text/css">
    <link href="style/quizInfo.css" rel="stylesheet" type="text/css">
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const ids = ['recent','fastest','best'];
            document.getElementById('fastest').style.display = 'none';
            document.getElementById('best').style.display = 'none';

            const sortSelect = document.getElementById('sort');

            sortSelect.addEventListener('change', function() {
                const selectedValue = sortSelect.value;
                for (const id of ids) {
                    if(id === selectedValue) {
                        document.getElementById(id).style.display = 'block';
                    } else {
                        document.getElementById(id).style.display = 'none';
                    }
                }

                console.log('Selected sort option:', selectedValue);
            });
        });
    </script>
</head>
<body>
<%@ include file="header.jsp" %>
<%@ include file="mail.jsp" %>
<main>
    <h1><%=curQuiz.getTitle()%></h1>
    <h2 id="avg-quiz-rating">
        <%
            for(int i = 1; i <= 5; i++){
                String checkedRating = "";
                if(i <= averageRating){
                    checkedRating = "check-star";
                }
        %>
        <a class="star <%=checkedRating%>">&#9733;</a>
        <%
            }
        %>
    </h2>
    <h3><%="Author: " + author.getUsername()%></h3>
    <h3><%="Created at: " + curQuiz.getCreatedAt().toString()%></h3>
    <h3><%="Time Limit: " + curQuiz.getMaxTime() + " seconds"%></h3>
    <p><%=curQuiz.getDescription()%></p>
    <%
        if(curUser != null) {
            if(curQuiz.getAllowPractice()){
    %>
    <form action="QuizInfo" method="post">
        <input name="practice" type="hidden" value="true">
        <input name="quizId" type="hidden" value="<%=curQuiz.getId()%>">
        <input type="submit" value="Practice">
    </form>
    <%
            }
    %>
    <form action="QuizInfo" method="post">
        <input name="practice" type="hidden" value="false">
        <input name="quizId" type="hidden" value="<%=curQuiz.getId()%>">
        <input type="submit" value="Start Quiz">
    </form>
    <button id="challenge-button">Challenge</button>
    <form id="challenge-form" class="hidden" action="SendChallenge" method="post">
        <input hidden="hidden" name="quizId" type="hidden" value="<%=quizId%>">
        <label>Send to :</label><br>
        <input name="toUsername" type="text"><br>
        <label>Text :</label><br>
        <input name="message" type="text"><br>
        <label id="send-challenge-error-label"></label><br>
        <input id="send-challenge-button" type="submit" value="send">
    </form>
    <%
        }
    %>

    <%
        try {
            if(curUser != null && (userDB.isUserAdmin(curUser.getId()) || curQuiz.getAuthorId() == curUser.getId())) { %>
            <form action="RemoveQuiz" method="post">
                <input type="hidden" name="deleteQuiz" value="true">
                <input name="quizId" type="hidden" value="<%=curQuiz.getId()%>">
                <input type="submit" value="Remove Quiz">
            </form>
            <form action="RemoveQuiz" method="post">
                <input type="hidden" name="deleteQuiz" value="false">
                <input name="quizId" type="hidden" value="<%=curQuiz.getId()%>">
                <input type="submit" value="Clear Quiz History">
            </form>
                <% }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } %>

    <h4>This quiz has been taken <%=allAttempts.size()%> times.</h4>
    <h4>Average grade for all users on this quiz is <%=Math.round(gradeAv*100)/100.0%> with variance of <%=Math.round(gradeVar*100)/100.0%>.</h4>
    <h4>Average time spent on all attempts of this quiz is <%=Math.round(timeAv*100)/100.0%>s with variance of <%=Math.round(timeVar*100)/100.0%>s.</h4>

    <h3>Your previous attempts</h3>
    <hr>
    <% if(curUser == null) { %>
        <p><a href="login.jsp">Log in</a> to track your attempts.</p>
    <% } else { %>
        <label for="sort">Sort by:</label>
        <select id="sort">
            <option value="recent">Most Recent</option>
            <option value="fastest">Fastest Completion</option>
            <option value="best">Highest Score</option>
        </select>
        <div class="scroll-container" id="recent">
            <% int userId = ((User) request.getSession().getAttribute("curUser")).getId();
                prevAttempts = historyDB.getLatestHistoriesByUserAndQuizId(userId, quizId);
                for(int i = 0; i < prevAttempts.size(); i++){ %>
            <div class="box">
                <h3><%=prevAttempts.get(i).getGrade()%></h3>
                <h3><%=prevAttempts.get(i).getWritingTime()%> Seconds</h3>
                <h4><%=prevAttempts.get(i).getCompletedAt()%></h4>
            </div>
            <%}%>
        </div>

        <div class="scroll-container" id="fastest">
            <% fastestAttempts = historyDB.getFastestByUserAndQuizId(userId, quizId);
                for(int i = 0; i < fastestAttempts.size(); i++){ %>
            <div class="box">
                <h3><%=fastestAttempts.get(i).getGrade()%></h3>
                <h3><%=fastestAttempts.get(i).getWritingTime()%> Seconds</h3>
                <h4><%=fastestAttempts.get(i).getCompletedAt()%></h4>
            </div>
            <%}%>
        </div>

        <div class="scroll-container" id="best">
            <% bestAttempts = historyDB.getBestScoresByUserAndQuizId(userId, quizId);
                for(int i = 0; i < bestAttempts.size(); i++){ %>
            <div class="box">
                <h3><%=bestAttempts.get(i).getGrade()%></h3>
                <h3><%=bestAttempts.get(i).getWritingTime()%> Seconds</h3>
                <h4><%=bestAttempts.get(i).getCompletedAt()%></h4>
            </div>
            <%}%>
        </div>
    <%}%>

    <h3>Hall of fame</h3>
    <% try {
        List<History> topOfAllTime = historyDB.getTopPerformersByQuizId(curQuiz.getId(),10);
        if(topOfAllTime.isEmpty()) { %>
            <p>No one has taken this quiz yet. Be the first!</p>
        <% } else { %>
            <ol>
                <% for(History curTry : topOfAllTime) {
                    User topScorer = userDB.getById(curTry.getUserId());
                    String scorerName = topScorer.getUsername();%>
                <li><a href="profile.jsp?username=<%=scorerName%>"><%=scorerName%></a> <%=": " + curTry.getGrade() + " in " + curTry.getWritingTime()/1000.0 + "sec"%></li>
                <% } %>
            </ol>
        <% }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
    } %>


    <h3>Top performers of the last day</h3>
    <% try {
        List<History> topOfLastDay = historyDB.getTopInLastDayByQuizId(curQuiz.getId(),10);
        if(topOfLastDay.isEmpty()) { %>
            <p>No one has taken this quiz in the last day. Be the first!</p>
        <% } else { %>
            <ol>
                <% for(History curTry : topOfLastDay) {
                    User topScorer = userDB.getById(curTry.getUserId());
                    String scorerName = topScorer.getUsername();%>
                <li><a href="profile.jsp?username=<%=scorerName%>"><%=scorerName%></a><%=": " + curTry.getGrade() + " in " + curTry.getWritingTime()/1000.0 + "sec"%></li>
                <% } %>
            </ol>
        <% }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
    } %>

    <h3>Recent attempts</h3>
    <% try {
        List<History> recentAttempts = historyDB.getLatestHistoriesByQuizId(curQuiz.getId(),10);
        if(recentAttempts.isEmpty()) { %>
            <p>No one has taken this quiz yet. Be the first!</p>
        <% } else { %>
            <ol>
                <% for(History curTry : recentAttempts) {
                    User topScorer = userDB.getById(curTry.getUserId());
                    String scorerName = topScorer.getUsername();%>
                <li><a href="profile.jsp?username=<%=scorerName%>"><%=scorerName%></a> <%=": " + curTry.getGrade() + " at " + curTry.getCompletedAt().toString()%></li>
                <% } %>
            </ol>
    <% }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
    } %>

    <div class="quiz-list-wrapper">
        <h2>Quizzes by the same author:</h2>
        <div class="quiz-boxes">
            <% try {
                    List<Quiz> quizzesBySameAuthor = quizDB.getQuizzesByAuthorId(author.getId());
                    if (quizzesBySameAuthor == null) {
                        throw new Exception("Quizzes not found in request.");
                    }

                    for (Quiz quiz : quizzesBySameAuthor) {
                        User curAuthor = userDB.getById(quiz.getAuthorId());
                        if(quiz.getId() == curQuiz.getId()) continue;
            %>
            <div class="quiz-box">
                <a href="quizInfo.jsp?quizId=<%=quiz.getId()%>">
                    <div class="quiz-box-top" style="background-size: cover; background-image: url('image?type=quiz&quizId=<%=quiz.getId()%>');">
                        <p class="quiz-box-name"><%= quiz.getTitle() %></p>
                    </div>
                    <div class="quiz-box-bot">
                        <p><%= quiz.getTotalPlayCount()%></p>
                        <p><a href="profile.jsp?username=<%= author.getUsername()%>"><%= author.getUsername()%></a></p>
                    </div>
                </a>
            </div>
            <% }
            } catch (Exception e) {
                e.printStackTrace();
                out.println("<p>Error loading quizzes: " + e.getMessage() + "</p>");
            } %>
        </div>
    </div>

    <% try {
        if(curUser != null && !userDB.isUserAdmin(curUser.getId())) { %>
            <div id="report">
                <h3>Report quiz as inappropriate?</h3>
                <form id="report-form" method="post" action="Report">
                    <input hidden="hidden" type="number" name="quizId" value="<%=quizId%>">
                    <textarea placeholder="Describe your issue here" id="report-text" name="report-text"></textarea><br>
                    <input id="report-submit" type="submit" value="Report">
                </form>
            </div>
        <% }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
    } %>

    <h2 id="user-rating">
        <%
            for(int i = 1; i <= 5; i++){
                String checkedRating = "";
                String onClick = "";
                if(curUser != null){
                    onClick = "onclick=\"checkStar(" + i + ")\"";
                }
                if(rating != null && i <= rating.getRating()){
                    checkedRating = "check-star";
                }
        %>
        <a class="star <%=checkedRating%>" id="star-<%=i%>" <%=onClick%>>&#9733;</a>
        <%
            }
        %>
    </h2>
    <hr>
    <%
        if(curUser != null){
    %>
    <form id="comment-form" method="post" action="AddComment">
        <input hidden="hidden" type="number" name="quizId" value="<%=quizId%>">
        <textarea id="comment-text" name="comment-text" form="comment-form"></textarea><br>
        <input id="comment-submit" type="submit" value="Comment">
    </form>
    <%
        }else{
    %>
    <p>log in first to comment</p>
    <%
        }
    %>
    <div id="comment-section">
        <%
            for (Comment comment : comments){
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDateString = dateFormat.format(comment.getWrittenTime());
        %>
        <div class="comment-box">
            <p><a href="profile.jsp?username=<%=commentUsernames.get(comment.getId())%>"><%=commentUsernames.get(comment.getId())%> : </a><%=comment.getText()%></p>
            <p><%=formattedDateString%></p>
        </div>
        <%
            }
        %>
    </div>
</main>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script> <!-- jQuery for AJAX -->
<script src="script/mailPanel.js"></script>
<script src="script/general.js"></script>

<script>
    function checkStar(x){
        $.ajax({
            url: 'Rating',
            method: 'POST',
            data: {
                rating: x,
                quizId: <%=quizId%>,
                userId: <%=curUser == null ? -1 : curUser.getId()%>
            },
            success: function(response) {
                console.log('Rated successfully');
                for(var i = 1; i <= 5; i++){
                    $('#star-'+i).removeClass('check-star');
                }
                for(var i = 1; i <= x; i++){
                    $('#star-'+i).addClass('check-star');
                }
            },
            error: function(xhr, status, error) {
                console.error('Rating failed');
                console.error(error);
                alert('Could not rate');
            }
        })
    }

    function addComment(resp){
        $('#comment-section').prepend(
            '<div class="comment-box">' +
            '<p><a href="' + resp.username + '">' + resp.username + ' : </a>' + resp.text + '</p>' +
            '<p>' + resp.time + '</p>' +
            '</div>');
    }

    $(document).ready(function () {
        $('#comment-submit').click(function (e) {
            e.preventDefault();

            var form = $(this).closest('form');
            var formData = form.serializeArray();

            $.ajax({
                url: form.attr('action'),
                type: form.attr('method'),
                data: formData,
                success: function(response) {
                    window.postMessage("Success");
                    console.log('Comment submitted');
                    $("#comment-text").val('');
                    addComment(response);
                },
                error: function(xhr, status, error) {
                    console.error('Could not submit comment');
                    console.error(error);
                    alert('Could not submit comment');
                }
            });
        });

        $('#send-challenge-button').click(function (e) {
            e.preventDefault();

            var form = $(this).closest('form');
            var formData = form.serializeArray();

            $.ajax({
                url: form.attr('action'),
                type: form.attr('method'),
                data: formData,
                success: function(response) {
                    console.log('Challenge send');
                    // Find all input, textarea, and select elements within the form and clear their values
                    if(response.errorText == null){
                        $('#send-challenge-error-label').text('');
                        form.find('input, textarea, select').each(function() {
                            var elementType = $(this).attr('type'); // Get the type attribute of the element

                            // Clear the value based on element type
                            switch (elementType) {
                                case 'text':
                                case 'password':
                                case 'textarea':
                                case 'email':
                                case 'number':
                                    $(this).val('');
                                    break;
                                case 'checkbox':
                                case 'radio':
                                    $(this).prop('checked', false);
                                    break;
                                case 'select-one':
                                    $(this).prop('selectedIndex', 0);
                                    break;
                                // Add more cases as needed for different types of inputs
                            }
                        });
                    }else{
                        $('#send-challenge-error-label').text(response.errorText);
                    }
                },
                error: function(xhr, status, error) {
                    console.error('Could not challenge');
                    console.error(error);
                    alert('Could not challenge');
                }
            });
        });

        $('#challenge-button').click(function (e) {
            var form = $('#challenge-form');
            if(form.hasClass('hidden')){
                form.removeClass('hidden');
            }else{
                form.addClass('hidden');
            }
        });
    });
</script>
</body>
</html>