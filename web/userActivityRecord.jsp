<%-- 
    Document   : userActivityRecord
    Created on : 2016年1月22日, 上午10:16:17
    Author     : kwok1
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="db.handle.DB_Select"%>
<%@page import="db.bean.ActivitiesBean"%>
<%@page import="db.bean.ActivitiesRecordBean"%>
<%@page import="db.bean.UserBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="db.handle.DB_Select"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <%
            String dbUser = this.getServletContext().getInitParameter("dbUsername");
            String dbPassword = this.getServletContext().getInitParameter("dbPassword");
            String dbUrl = this.getServletContext().getInitParameter("dbUrl");
            DB_Select db_select = new DB_Select(dbUrl, dbUser, dbPassword);
        %>
        <%
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            ArrayList aryData = db_select.queryActivitiesRecordBySql("SELECT * FROM activitiesrecord"
                    + " where memberID = '1'");
        %>
        <h1>Activity Record</h1>
        <table>
            <tr>
                <th>Activity Record ID</th>
                <th>Activity ID</th>
                <th>Name</th>
                <th>Date</th>
                <th>Venue</th>
                <th>Person in charge</th>
                <th>State</th>
            </tr>
            <%
                for (int i = 0; i < aryData.size(); i++) {
                    ActivitiesRecordBean ar = (ActivitiesRecordBean) aryData.get(i);
                    ArrayList actData = db_select.queryActivitiesBySql("SELECT * FROM activities"
                            + " where activitiesID = '" + ar.getActivitiesID()+"'");
                    ActivitiesBean act = (ActivitiesBean) actData.get(0);
                    ArrayList staffData = db_select.queryUserBySql("SELECT * FROM user"
                            + " where staffID = '" + act.getStaffID()+"'");
                    UserBean user = (UserBean) staffData.get(0);
            %>
            <tr>
                <td><%= ar.getActivitiesRecordID()%></td>
                <td><%= ar.getActivitiesID()%></td>
                <td><%= act.getName()%></td>
                <%
                    String dateInString = act.getDate();
                    Date date = sdf.parse(dateInString);
                %>
                <td><%= sdf.format(date)%></td>
                <td><%= act.getVenue()%></td>
                <td><%= user.getFirstName_eng() %> <%= user.getLastName_eng() %></td>
                <td><%= ar.getState()%></td>
            </tr>
            <%
                }
            %>


        </table>

    </body>
</html>