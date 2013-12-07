package Model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class DatabaseAccess {

    private static Connection con;
    private static Statement st;
    private static CallableStatement cStmt;
    private static ResultSet rs;

    private static void Initialize() throws Exception {
        if (con == null) {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/survey_application_generator", "root", "password");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Insert">
    public static int InsertSurveyProject(String title) {
        try {
            Initialize();
            cStmt = con.prepareCall("{call InsertSurveyProject(?,?)}");
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

    public static String CreateDatabaseAndTable(String sName, HashMap<String, String> columns) {
        String sql1 = "";
        String sql2 = "";
        sName = sName.trim().replace(" ", "_");
        try {
            Initialize();
            st = con.createStatement();
            sql1 = "CREATE DATABASE `" + sName + "` /*!40100 DEFAULT CHARACTER SET latin1 */;";
            System.out.println(sql1);
            st.execute(sql1);
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + sName, "root", "password");
            sql2 += "CREATE TABLE `" + sName + "`.`" + sName + "` (iEntryID INT NOT NULL AUTO_INCREMENT, ";
            for (Map.Entry<String, String> entry : columns.entrySet()) {
                sql2 += "`" + entry.getKey() + "` " + entry.getValue() + ", ";
            }
            sql2 += "PRIMARY KEY (`iEntryID`)) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;";
            System.out.println(sql2);
            st.execute(sql2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return sql1 + sql2;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Get/List">
    public static ResultSet ListSurveyProjects() {
        try {
            Initialize();
            cStmt = con.prepareCall("{call ListSurveyProjects()}");
            rs = cStmt.executeQuery();
        } catch (Exception ex) {
            ex.printStackTrace();
            rs = null;
        }
        return rs;
    }

    public static boolean IsProjectTitleUnique(String title) {
        try {
            Initialize();
            cStmt = con.prepareCall("{call IsProjectTitleUnique(?)}");
            cStmt.setString("vchTitle", title);
            rs = cStmt.executeQuery();
            return !rs.first();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
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
            cStmt = con.prepareCall("{call UpdateQuestion(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
            cStmt.setInt("iQuestionID", q.getID());
            cStmt.setInt("iIndex", q.getQuestionIndex());
            cStmt.setString("vchQuestionText", q.getQuestionText());
            cStmt.setString("vchQuestionID", q.getQuestionID());
            cStmt.setString("vchQuestionType", q.getQuestionType());
            cStmt.setBoolean("bRequired", q.isRequired());
            cStmt.setString("vchMin", q.getMin());
            cStmt.setString("vchMax", q.getMax());
            cStmt.setBoolean("bValidateText", q.isValidateText());
            cStmt.setString("vchAllowTypes", q.getAllowTypes());
            cStmt.setString("vchValidSpecialCharacters", q.getValidSpecialCharacters());
            cStmt.setString("vchDecimalPlaces", q.getDecimalPlaces());
            cStmt.setString("vchValidationType", q.getValidationType());
            cStmt.setString("vchAnswerChoices", q.getAnswerChoices());
            cStmt.setString("vchOtherChoice", q.getOtherChoice());
            cStmt.setString("vchDisplayType", q.getDisplayType());
            cStmt.setString("vchNumberOfAnswers", q.getNumberOfAnswers());
            cStmt.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void RenameProject(int iProjectID, String newName) {
        try {
            Initialize();
            cStmt = con.prepareCall("{call RenameProject(?,?)}");
            cStmt.setInt("iApplicationID", iProjectID);
            cStmt.setString("vchTitle", newName);
            cStmt.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static int CopyProject(int iProjectID, String newName) {
        try {
            Initialize();
            cStmt = con.prepareCall("{call CopyProject(?,?,?)}");
            cStmt.setInt("iApplicationID", iProjectID);
            cStmt.setString("vchTitle", newName);
            cStmt.registerOutParameter("iNewApplicationID", Types.INTEGER);
            cStmt.execute();
            return cStmt.getInt("iNewApplicationID");
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
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
