/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db.handle;

import db.bean.ActivitiesBean;
import db.bean.ActivitiesRecordBean;
import db.bean.ActivityBudgetBean;
import db.bean.AdminBean;
import db.bean.MemberBean;
import db.bean.UserBean;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author a1
 */
public class DB_Select {

    public DB_Select() {
    }

    private String url = "";
    private String username = "";
    private String password = "";

    public DB_Select(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;

    }

    public Connection getConnection() throws SQLException, IOException {
        System.setProperty("jdbc.drivers", "com.mysql.jdbc.Driver");

        try {
            Class.forName("com.mysql.jdbc.Driver");

        } catch (ClassNotFoundException ex) {
            //  Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }

        return DriverManager.getConnection(url, username, password);
    }

    public ArrayList querySelect(String sql, String bean) {
        Connection cnnct = null;
        PreparedStatement pStmnt = null;
        ArrayList list = new ArrayList();
        try {

            ResultSet rs = null;
            cnnct = getConnection();
            String preQueryStatement = sql;
            //String preQueryStatement = "SELECT* FROM ADMIN";
            pStmnt = cnnct.prepareStatement(preQueryStatement);
            rs = pStmnt.executeQuery();
            list = getList(rs, bean);
            pStmnt.close();
            cnnct.close();

        } catch (SQLException ex) {
            while (ex != null) {
                ex.printStackTrace();
                ex = ex.getNextException();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public ArrayList getList(ResultSet rs, String bean) throws SQLException {
        ArrayList list = new ArrayList();

        if (bean.equals("activities")) {
            ActivitiesBean activities;
            while (rs.next()) {
                activities = new ActivitiesBean(rs.getString("activitiesID"), rs.getString("name"), rs.getString("districtNo"), rs.getInt("quota"), rs.getInt("remain"), rs.getInt("targetAgeMax"), rs.getInt("targetAgeMin"), rs.getString("deadline"), rs.getString("venue"), rs.getString("date"), rs.getString("tag"), rs.getString("staffID"), rs.getString("description"));
                list.add(activities);
            }
        }
        if (bean.equals("admin")) {
            AdminBean admin;
            while (rs.next()) {
                admin = new AdminBean(rs.getString("adminID"), rs.getString("login_time"), rs.getString("modify_time"), rs.getString("position"));
                list.add(admin);
            }
        }

        if (bean.equals("activitiesrecord")) {
            ActivitiesRecordBean activitiesRecord;
            while (rs.next()) {
                activitiesRecord = new ActivitiesRecordBean(rs.getString("activitiesRecordID"), rs.getString("activitiesID"), rs.getString("memberID"), rs.getString("state"));
                list.add(activitiesRecord);
            }
        }

        if (bean.equals("activitybudget")) {
            ActivityBudgetBean activityBudget;
            while (rs.next()) {
                activityBudget = new ActivityBudgetBean(rs.getString("itemID"), rs.getString("activitiesID"), rs.getString("itemName"), rs.getString("itemType"), rs.getDouble("cost"), rs.getInt("number"), rs.getString("remark"));
                list.add(activityBudget);
            }
        }
        return list;
    }

    public ArrayList queryCustomSelect(String sql) {
        Connection cnnct = null;
        PreparedStatement pStmnt = null;
        ArrayList list = new ArrayList();
        try {

            ResultSet rs = null;
            cnnct = getConnection();
            String preQueryStatement = sql;

            pStmnt = cnnct.prepareStatement(preQueryStatement);
            rs = pStmnt.executeQuery();
            while (rs.next()) {
            }
            pStmnt.close();
            cnnct.close();

        } catch (SQLException ex) {
            while (ex != null) {
                ex.printStackTrace();
                ex = ex.getNextException();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return list;

    }

    public String[][] getSql(String aa) {
        Connection cnnct = null;
        PreparedStatement pStmnt = null;
        String[][] temp = null;
        try {

            ResultSet rs = null;
            cnnct = getConnection();
            String preQueryStatement = aa;

            pStmnt = cnnct.prepareStatement(preQueryStatement);
            rs = pStmnt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            rs.last();
            temp = new String[rs.getRow() + 1][columnCount];
            Logger _log = Logger.getLogger(DB_Select.class.getName());
            rs.beforeFirst();
            for (int i = 0; i < columnCount; i++) {
                temp[0][i] = rsmd.getColumnName(i + 1);
            }
            int count = 0;
            while (rs.next()) {
                count++;
                for (int j = 0; j < temp[0].length; j++) {
                    temp[count][j] = rs.getString(j + 1);
                }
            }
            pStmnt.close();
            cnnct.close();

        } catch (SQLException ex) {
            while (ex != null) {
                ex.printStackTrace();
                ex = ex.getNextException();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return temp;

    }

    public boolean isValidUser(String username, String password) {
        Connection cnnct = null;
        PreparedStatement pStmnt = null;
        String[][] temp = null;
        boolean isValid = false;
        try {
            cnnct = getConnection();
            String preQueryStatement = "SELECT * FROM USER WHERE USERNAME =  ? and  PASSWORD =  ? ";
            pStmnt = cnnct.prepareStatement(preQueryStatement);
            pStmnt.setString(1, username);
            pStmnt.setString(2, password);

            ResultSet rs = pStmnt.executeQuery();
            if (rs.next()) {
                isValid = true;
            }

            pStmnt.close();
            cnnct.close();
        } catch (SQLException ex) {
            while (ex != null) {
                ex.printStackTrace();
                ex = ex.getNextException();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return isValid;
    }

    public UserBean queryUserByUsername(String userName) {
        Connection cnnct = null;
        PreparedStatement pStmnt = null;
        ArrayList list = new ArrayList();
        UserBean user = null;
        try {

            ResultSet rs = null;
            cnnct = getConnection();
            String preQueryStatement = "SELECT* FROM USER WHERE USERNAME=? and adminID is null";
            pStmnt = cnnct.prepareStatement(preQueryStatement);

            pStmnt.setString(1, userName);
            rs = pStmnt.executeQuery();
            while (rs.next()) {
                user = new UserBean(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12), rs.getInt(13));
            }
            pStmnt.close();
            cnnct.close();

        } catch (SQLException ex) {
            while (ex != null) {
                ex.printStackTrace();
                ex = ex.getNextException();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return user;
    }

    public String getAvailMemberID() {
        Connection cnnct = null;
        boolean isSuccess = false;
        SimpleDateFormat formatter = null;
        java.util.Date utilDate = null;
        ResultSet rs = null;
        String maxID = null;
        int newAvaildID = -1;
        try {
            cnnct = getConnection();
            String preQueryStatement = "select memberID from member ORDER BY CAST(memberID AS SIGNED) DESC LIMIT 1";
            PreparedStatement pStmnt = cnnct.prepareStatement(preQueryStatement);
            rs = pStmnt.executeQuery();
            while (rs.next()) {
                maxID = rs.getString(1);
            }
            newAvaildID = Integer.parseInt(maxID) + 1;
        } catch (SQLException ex) {
            while (ex != null) {
                ex.printStackTrace();
                ex = ex.getNextException();

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return newAvaildID + "";
    }

    public String getAvailUserID() {
        Connection cnnct = null;
        boolean isSuccess = false;
        SimpleDateFormat formatter = null;
        java.util.Date utilDate = null;
        ResultSet rs = null;
        String maxID = null;
        int newAvaildID = -1;
        try {
            cnnct = getConnection();
            String preQueryStatement = "select userID from user ORDER BY CAST(userID AS SIGNED) DESC LIMIT 1";
            PreparedStatement pStmnt = cnnct.prepareStatement(preQueryStatement);
            rs = pStmnt.executeQuery();
            while (rs.next()) {
                maxID = rs.getString(1);
            }
            newAvaildID = Integer.parseInt(maxID) + 1;
        } catch (SQLException ex) {
            while (ex != null) {
                ex.printStackTrace();
                ex = ex.getNextException();

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return newAvaildID + "";
    }

    public boolean addGeneralUser_User(String availdID, String userName, String password, String availMemberID, String givenName, String surName, String gender, String tel, String chinese, String email, int isAuthenticated) throws ParseException, IOException {
        PreparedStatement pStmnt = null;
        Connection cnnct = null;
        boolean isSuccess = false;
        Logger _log = Logger.getLogger("addGeneralUser");
        try {
            String preQueryStatement = "INSERT INTO USER VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            cnnct = getConnection();
            pStmnt = cnnct.prepareStatement(preQueryStatement);
            pStmnt.setString(1, availdID);
            pStmnt.setString(2, userName);
            pStmnt.setString(3, password);
            pStmnt.setString(4, availMemberID);
            pStmnt.setString(5, null);
            pStmnt.setString(6, null);
            pStmnt.setString(7, givenName);
            pStmnt.setString(8, surName);
            pStmnt.setString(9, gender);
            pStmnt.setString(10, tel);
            pStmnt.setString(11, chinese);
            pStmnt.setString(12, email);
            pStmnt.setInt(13, 0);
            _log.log(Level.INFO, "addGeneralUser{0}", " " + pStmnt.toString());

            int rowCount = pStmnt.executeUpdate();
            if (rowCount >= 1) {
                isSuccess = true;
            }
            pStmnt.close();
            cnnct.close();

        } catch (SQLException ex) {
            while (ex != null) {
                ex.printStackTrace();
                ex = ex.getNextException();

            }

            /*             uname
                 passwd     
                        availMemberID
                                
                                givenname
                                surname
                        gender
                                        tel
                                                chinese
                                             email
                                                     0*/
        }

        return isSuccess;
    }

    public boolean addGeneralUser_Member(String availdID, String districtID, String nickName, String birthday, String address, String parent, String relationship, String emergency_phone, String school) throws ParseException {
        PreparedStatement pStmnt = null;
        Connection cnnct = null;
        boolean isSuccess = false;
        SimpleDateFormat formatter = null;
        java.util.Date utilDate = null;

        Logger _log = Logger.getLogger("addGeneralUser ");
        try {

            formatter = new SimpleDateFormat("yyyy-MM-dd");
            utilDate = formatter.parse(birthday);
            cnnct = getConnection();

            String preQueryStatement = "INSERT INTO MEMBER VALUES (?,?,?,?,?,?,?,?,?,?,?)";
            pStmnt = cnnct.prepareStatement(preQueryStatement);
            pStmnt.setString(1, availdID + "");
            pStmnt.setString(2, "2");
            pStmnt.setString(3, nickName);
            pStmnt.setTimestamp(4, new java.sql.Timestamp(utilDate.getTime()));
            pStmnt.setString(5, address);
            pStmnt.setString(6, parent);
            pStmnt.setString(7, relationship);
            pStmnt.setString(8, emergency_phone);
            pStmnt.setString(9, school);
            pStmnt.setTimestamp(10, new java.sql.Timestamp(new java.util.Date().getTime()));
            pStmnt.setInt(11, 0);
            _log.log(Level.INFO, "addGeneralUser{0}", " " + pStmnt.toString());

            int rowCount = pStmnt.executeUpdate();
            if (rowCount >= 1) {
                isSuccess = true;
            }
            pStmnt.close();
            cnnct.close();

        } catch (SQLException ex) {
            while (ex != null) {
                ex.printStackTrace();
                ex = ex.getNextException();

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return isSuccess;

    }

    public ArrayList queryActivitiesBySql(String sql) {
        Connection cnnct = null;
        PreparedStatement pStmnt = null;
        ArrayList list = new ArrayList();
        ActivitiesBean activities;
        try {

            ResultSet rs = null;
            cnnct = getConnection();
            String preQueryStatement = sql;
            pStmnt = cnnct.prepareStatement(preQueryStatement);

            rs = pStmnt.executeQuery();
            while (rs.next()) {
                activities = new ActivitiesBean(rs.getString("activitiesID"), rs.getString("name"), rs.getString("districtNo"), rs.getInt("quota"), rs.getInt("remain"), rs.getInt("targetAgeMax"), rs.getInt("targetAgeMin"), rs.getString("deadline").substring(0, rs.getString("deadline").length() - 2), rs.getString("venue"), rs.getString("date").substring(0, rs.getString("date").length() - 2), rs.getString("tag"), rs.getString("staffID"), rs.getString("description"));
                list.add(activities);
            }
            pStmnt.close();
            cnnct.close();

        } catch (SQLException ex) {
            while (ex != null) {
                ex.printStackTrace();
                ex = ex.getNextException();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    public ArrayList queryActivitiesRecordBySql(String sql) {
        Connection cnnct = null;
        PreparedStatement pStmnt = null;
        ArrayList list = new ArrayList();
        ActivitiesRecordBean ar;
        try {

            ResultSet rs = null;
            cnnct = getConnection();
            String preQueryStatement = sql;
            pStmnt = cnnct.prepareStatement(preQueryStatement);

            rs = pStmnt.executeQuery();
            while (rs.next()) {
                ar = new ActivitiesRecordBean(rs.getString("activitiesRecordID"), rs.getString("activitiesID"), rs.getString("memberID"), rs.getString("state"));
                list.add(ar);
            }
            pStmnt.close();
            cnnct.close();

        } catch (SQLException ex) {
            while (ex != null) {
                ex.printStackTrace();
                ex = ex.getNextException();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    public ArrayList queryUserBySql(String sql) {
        Connection cnnct = null;
        PreparedStatement pStmnt = null;
        ArrayList list = new ArrayList();
        UserBean user;
        try {

            ResultSet rs = null;
            cnnct = getConnection();
            String preQueryStatement = sql;
            pStmnt = cnnct.prepareStatement(preQueryStatement);

            rs = pStmnt.executeQuery();
            while (rs.next()) {
                user = new UserBean(rs.getString("userID"), rs.getString("userName"), rs.getString("password"), rs.getString("memberID"), rs.getString("adminID"), rs.getString("staffID"), rs.getString("firstName_eng"), rs.getString("lastName_eng"), rs.getString("sex"), rs.getString("tel"), rs.getString("name_ch"), rs.getString("email"), rs.getInt("isAuthenticated"));
                list.add(user);
            }
            pStmnt.close();
            cnnct.close();

        } catch (SQLException ex) {
            while (ex != null) {
                ex.printStackTrace();
                ex = ex.getNextException();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    public ArrayList queryMemberBySql(String sql) {
        Connection cnnct = null;
        PreparedStatement pStmnt = null;
        ArrayList list = new ArrayList();
        MemberBean member;
        try {

            ResultSet rs = null;
            cnnct = getConnection();
            String preQueryStatement = sql;
            pStmnt = cnnct.prepareStatement(preQueryStatement);

            rs = pStmnt.executeQuery();
            while (rs.next()) {
                member = new MemberBean(rs.getString("memberID"), rs.getString("districtID"), rs.getString("nickName"), rs.getString("birthday"), rs.getString("address"), rs.getString("parent"), rs.getString("relationship"), rs.getString("emergency_phone"), rs.getString("school"), rs.getString("reg_date"), rs.getInt("isHelper"));
                list.add(member);
            }
            pStmnt.close();
            cnnct.close();

        } catch (SQLException ex) {
            while (ex != null) {
                ex.printStackTrace();
                ex = ex.getNextException();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    public boolean addActivitiesRecord(String activitiesRecordID, String activitiesID, String memberID, String state) throws ParseException, IOException {
        PreparedStatement pStmnt = null;
        Connection cnnct = null;
        boolean isSuccess = false;
        Logger _log = Logger.getLogger("addActivitiesRecord");
        try {
            
            String preQueryStatement = "INSERT INTO activitiesrecord VALUES (?,?,?,?)";
            cnnct = getConnection();
            pStmnt = cnnct.prepareStatement(preQueryStatement);
            pStmnt.setString(1, activitiesRecordID);
            pStmnt.setString(2, activitiesID);
            pStmnt.setString(3, memberID);
            pStmnt.setString(4, state);
            _log.log(Level.INFO, "addActivitiesRecord{0}", " " + pStmnt.toString());

            int rowCount = pStmnt.executeUpdate();
            if (rowCount >= 1) {
                isSuccess = true;
            }
            pStmnt.close();
            cnnct.close();

        } catch (SQLException ex) {
            while (ex != null) {
                ex.printStackTrace();
                ex = ex.getNextException();

            }
        }

        return isSuccess;
    }
    
    public boolean editMemberRecordForUser(String mid, String tel, String email) {
        Connection cnnct = null;
        PreparedStatement pStmnt = null;
        boolean isSuccess = false;

        try {
            cnnct = getConnection();
            String preQueryStatement = "UPDATE user SET"+
                    " tel=?, email=? WHERE memberID=?";
            pStmnt = cnnct.prepareStatement(preQueryStatement);
            pStmnt.setString(1, tel);
            pStmnt.setString(2, email);
            pStmnt.setString(3, mid);
            int rowCount = pStmnt.executeUpdate();
            if (rowCount >= 1) {
                isSuccess = true;
            }
            pStmnt.close();
            cnnct.close();

        } catch (SQLException ex) {
            while (ex != null) {
                ex.printStackTrace();
                ex = ex.getNextException();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return isSuccess;
    }
    public boolean editMemberRecordForMember(String mid, String nickname, String address
            , String parent, String relation,String eTel , String school) {
        Connection cnnct = null;
        PreparedStatement pStmnt = null;
        boolean isSuccess = false;

        try {
            cnnct = getConnection();
            String preQueryStatement = "UPDATE member SET"+
                    " nickName=?, address=?, parent=?, relationship=?,emergency_phone=?, school=? WHERE memberID=?";
            pStmnt = cnnct.prepareStatement(preQueryStatement);
            pStmnt.setString(1, nickname);
            pStmnt.setString(2, address);
            pStmnt.setString(3, parent);
            pStmnt.setString(4, relation);
            pStmnt.setString(5, eTel);
            pStmnt.setString(6, school);
            pStmnt.setString(7, mid);
            int rowCount = pStmnt.executeUpdate();
            if (rowCount >= 1) {
                isSuccess = true;
            }

            pStmnt.close();
            cnnct.close();

        } catch (SQLException ex) {
            while (ex != null) {
                ex.printStackTrace();
                ex = ex.getNextException();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return isSuccess;
    }
    
    public boolean editMemberRecordForPw(String mid, String pw) {
        Connection cnnct = null;
        PreparedStatement pStmnt = null;
        boolean isSuccess = false;

        try {
            cnnct = getConnection();
            String preQueryStatement = "UPDATE user SET password = ? WHERE memberID = ?";
            pStmnt = cnnct.prepareStatement(preQueryStatement);
            pStmnt.setString(1, pw);
            pStmnt.setString(2, mid);
            int rowCount = pStmnt.executeUpdate();
            if (rowCount >= 1) {
                isSuccess = true;
            }
            pStmnt.close();
            cnnct.close();

        } catch (SQLException ex) {
            while (ex != null) {
                ex.printStackTrace();
                ex = ex.getNextException();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return isSuccess;
    }

}
