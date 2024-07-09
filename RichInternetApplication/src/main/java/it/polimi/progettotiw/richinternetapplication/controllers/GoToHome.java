package it.polimi.progettotiw.richinternetapplication.controllers;

import it.polimi.progettotiw.richinternetapplication.beans.Group;
import it.polimi.progettotiw.richinternetapplication.beans.User;
import it.polimi.progettotiw.richinternetapplication.dao.GroupDAO;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class GoToHome
 */
@WebServlet("/GoToHome")
public class GoToHome extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToHome() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void init() throws ServletException {
        try {
            ServletContext context = getServletContext();
            String driver = context.getInitParameter("dbDriver");
            String url = context.getInitParameter("dbUrl");
            String user = context.getInitParameter("dbUser");
            String password = context.getInitParameter("dbPassword");
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new UnavailableException("Couldn't load database driver");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new UnavailableException("Couldn't connect to the database");
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        List<Group> createdGroups;
        List<Group> invitedGroups;
        GroupDAO groupDAO = new GroupDAO(connection);

        // get the list of groups which the user of the session has created
        try {
            createdGroups = groupDAO.getCreatedActiveGroups(user.getUsername());
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.getWriter().print("Something went wrong in the database");
            return;
        }

        // get the list of groups where the user of the session has been invited
        try {
            invitedGroups = groupDAO.getInvitedActiveGroups(user.getUsername());
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.getWriter().print("Something went wrong in the database");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        JsonArray createdGroupsTitlesJson = new JsonArray();
        for (Group group : createdGroups) {
            JsonObject groupJson = new JsonObject();
            groupJson.addProperty("id", group.getGroupID());
            groupJson.addProperty("title", group.getTitle());
            createdGroupsTitlesJson.add(groupJson);
        }
        JsonArray invitedGroupsTitlesJson = new JsonArray();
        for (Group group : invitedGroups) {
            JsonObject groupJson = new JsonObject();
            groupJson.addProperty("id", group.getGroupID());
            groupJson.addProperty("title", group.getTitle());
            invitedGroupsTitlesJson.add(groupJson);
        }
        JsonObject result = new JsonObject();
        result.add("createdGroups", createdGroupsTitlesJson);
        result.add("invitedGroups", invitedGroupsTitlesJson);
        response.getWriter().print(result);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

    public void destroy() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("Couldn't close database connection after use");
            }
        }
    }
}