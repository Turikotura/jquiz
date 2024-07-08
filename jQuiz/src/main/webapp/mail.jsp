<%--MAIL--%>
<div id="mail-panel">
    <%
        if(curUser == null){
            // loads if there is no login
    %>
    <h3 id="log-in-first">Log in first</h3>
    <%
    }
    %>
    <%
        for(int i = 0; i < mails.size(); i++){
            Mail mail = mails.get(i);
            String senderName = senderNames.get(i);

            String active = "";
            if(!mail.getSeen()){
                active = "active-message";
            }
    %>
    <%--mail info--%>
    <form id="mail-form-<%=mail.getId()%>" method="post" action="MailPanel">
        <input type="hidden" name="mailId" value="<%=mail.getId()%>">
        <div class="message-box <%=active%>">
            <p class="message-date"><%=mail.getTimeSent()%></p>
            <h3 class="message-text"><%=mail.getText()%></h3>
            <h4 class="message-author">From: <%=senderName%></h4>

            <%
                if(mail.getType() == MailTypes.CHALLENGE){
                    // Display challenge visual
            %>
            <p>Best Score: <%=maxGrades.get(mail.getId())%> pts</p>
            <a href="quizInfo.jsp?quizId=<%=mail.getQuizId()%>">Accept</a>
            <%
            }else if(mail.getType() == MailTypes.FRIEND_REQUEST){
                // Display friend request visual
            %>
            <input type="submit" value="accept" class="friend-acpt-submit">
            <input type="submit" value="reject" class="friend-rjct-submit">
            <%
                } else if(mail.getType() == MailTypes.QUIZ_REPORT) {
            %>
                <a href="quizInfo.jsp?quizId=<%=mail.getQuizId()%>">Check out</a>
            <% } %>
        </div>
    </form>
    <%
        }
    %>
</div>
<%----%>