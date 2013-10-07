package Model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

public class DatabaseAccess {

    static Connection con;
    static Statement st;
    static CallableStatement cStmt;
    static ResultSet rs;

    private static void Initialize() throws Exception {
        if (con == null) {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "password");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Insert">
    public static int InsertSurveyApplication(String title) {
        try {
            Initialize();
            cStmt = con.prepareCall("{call InsertSurveyApplication(?,?)}");
            cStmt.setString("vchTitle", title);
            cStmt.registerOutParameter("iApplicationID", Types.INTEGER);
            cStmt.execute();
            return cStmt.getInt("iApplicationID");
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public static int InsertPage(int iApplicationID, String name, int iIndex) {
        try {
            Initialize();
            cStmt = con.prepareCall("{call InsertPage(?,?,?,?)}");
            cStmt.setInt("iApplicationID", iApplicationID);
            cStmt.setString("vchName", name);
            cStmt.setInt("iIndex", iIndex);
            cStmt.registerOutParameter("iPageID", Types.INTEGER);
            cStmt.execute();
            return cStmt.getInt("iPageID");
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public static int InsertQuestion(int iPageID, String name, int iIndex) {
        try {
            Initialize();
            cStmt = con.prepareCall("{call InsertQuestion(?,?,?,?)}");
            cStmt.setInt("iPageID", iPageID);
            cStmt.setString("vchName", name);
            cStmt.setInt("iIndex", iIndex);
            cStmt.registerOutParameter("iQuestionID", Types.INTEGER);
            cStmt.execute();
            return cStmt.getInt("iQuestionID");
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Get/List">
    public static ResultSet ListSurveyApplications() {
        try {
            Initialize();
            cStmt = con.prepareCall("{call ListSurveyApplications()}");
            rs = cStmt.executeQuery();
        } catch (Exception ex) {
            ex.printStackTrace();
            rs = null;
        }
        return rs;
    }

    public static ResultSet ListPages(int iApplicationID) {
        try {
            Initialize();
            cStmt = con.prepareCall("{call ListPages(?)}");
            cStmt.setInt("iApplicationID", iApplicationID);
            rs = cStmt.executeQuery();
        } catch (Exception ex) {
            ex.printStackTrace();
            rs = null;
        }
        return rs;
    }

    public static ResultSet ListQuestions(int iPageID) {
        try {
            Initialize();
            cStmt = con.prepareCall("{call ListQuestions(?)}");
            cStmt.setInt("iPageID", iPageID);
            rs = cStmt.executeQuery();
        } catch (Exception ex) {
            ex.printStackTrace();
            rs = null;
        }
        return rs;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Update">
    public static void UpdateQuestion(Question q) {
        try {
            Initialize();
            cStmt = con.prepareCall("{call UpdateQuestion(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
            cStmt.setInt("iQuestionID", q.ID);
            cStmt.setInt("iIndex", q.questionIndex);
            cStmt.setString("vchName", q.name);
            cStmt.setString("vchQuestionText", q.questionText);
            cStmt.setString("vchQuestionText", q.questionText);
            cStmt.setString("vchQuestionText", q.questionText);
            cStmt.setBoolean("bRequired", q.isRequired);
            cStmt.setString("vchMin", q.min);
            cStmt.setString("vchMax", q.max);
            cStmt.setString("vchValidCharacters", q.validCharacters);
            cStmt.setString("vchDecimalPlaces", q.decimalPlaces);
            cStmt.setString("vchValidationType", q.validationType);
            cStmt.setString("vchAnswerChoices", q.answerChoices);
            cStmt.setString("vchOtherChoice", q.otherChoice);
            cStmt.setString("vchDisplayType", q.displayType);
            cStmt.setString("vchNumberOfAnswers", q.numberOfAnswers);
            cStmt.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Delete">
    public static void DeletePage(int iPageID) {
        try {
            Initialize();
            cStmt = con.prepareCall("{call DeletePage(?)}");
            cStmt.setInt("iPageID", iPageID);
            cStmt.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void DeleteQuestion(int iQuestionID) {
        try {
            Initialize();
            cStmt = con.prepareCall("{call DeleteQuestion(?)}");
            cStmt.setInt("iQuestionID", iQuestionID);
            cStmt.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    // </editor-fold>
}
