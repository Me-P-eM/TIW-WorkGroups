package it.polimi.progettotiw.richinternetapplication.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.polimi.progettotiw.richinternetapplication.beans.Group;
import it.polimi.progettotiw.richinternetapplication.beans.User;
import it.polimi.progettotiw.richinternetapplication.dao.GroupDAO;
import it.polimi.progettotiw.richinternetapplication.util.LocalDateAdapter;
import it.polimi.progettotiw.richinternetapplication.util.ParameterChecker;

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
import java.time.LocalDate;
import java.util.List;

/**
 * Servlet implementation class GetGroupDetails
 */
@WebServlet("/GetGroupDetails")
public class GetGroupDetails extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetGroupDetails() {
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
        int groupID;
        // check parameters
        if (!ParameterChecker.checkString(request.getParameter("groupID"))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Badly formatted request parameters");
            return;
        }
        try {
            groupID = Integer.parseInt(request.getParameter("groupID"));
        } catch(NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("The id you submitted for the group is not a number");
            return;
        }
        if (groupID < 1) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Badly formatted request parameters");
            return;
        }

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        boolean isParticipant;
        User creator;
        Group group;
        List<User> invitees;
        GroupDAO groupDAO = new GroupDAO(connection);

        // check if the user of the session is a participant of the selected group
        try {
            isParticipant = groupDAO.checkParticipant(groupID, user.getUsername());
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.getWriter().print("Could not access the database");
            return;
        }

        // if he's not a participant, he can't access group details
        if (!isParticipant) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().print("You do not have access to details of this group, or this group does not exists");
            return;
        }

        // get group information
        try {
            group = groupDAO.getGroupByGroupID(groupID);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.getWriter().print("Could not access the database");
            return;
        }

        // get creator's username, name and surname
        try {
            creator = groupDAO.getCreatorByGroupID(groupID);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.getWriter().print("Could not access the database");
            return;
        }

        // get invitees
        try {
            invitees = groupDAO.getInviteesByGroupID(groupID);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.getWriter().print("Could not access the database");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).setPrettyPrinting().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("group", gson.toJsonTree(group));
        jsonResponse.add("creator", gson.toJsonTree(creator));
        JsonArray inviteesArray = gson.toJsonTree(invitees).getAsJsonArray();
        jsonResponse.add("invitees", inviteesArray);
        response.getWriter().print(gson.toJson(jsonResponse));
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