package it.polimi.progettotiw.richinternetapplication.controllers;

import it.polimi.progettotiw.richinternetapplication.beans.Group;
import it.polimi.progettotiw.richinternetapplication.beans.User;
import it.polimi.progettotiw.richinternetapplication.dao.GroupDAO;
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

/**
 * Servlet implementation class RemoveParticipant
 */
@WebServlet("/RemoveParticipant")
public class RemoveParticipant extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public RemoveParticipant() {
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
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int groupID;
        // check parameters
        if (!ParameterChecker.checkString(request.getParameter("groupID"))
                || !ParameterChecker.checkString(request.getParameter("username"))) {
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
        String participant = request.getParameter("username");
        User creator;
        boolean isParticipant;
        GroupDAO groupDAO = new GroupDAO(connection);

        // check that the requester is the creator of the group
        try {
            creator = groupDAO.getCreatorByGroupID(groupID);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.getWriter().print("Something went wrong in the database");
            return;
        }
        if (!creator.getUsername().equals(user.getUsername())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().print("You must be the creator of the group in order to remove a participant");
            return;
        }

        // check that the user to remove is a participant
        try {
            isParticipant = groupDAO.checkParticipant(groupID, participant);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.getWriter().print("Something went wrong in the database");
            return;
        }
        if (!isParticipant) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Badly formatted request parameters");
            return;
        }

        // check that the user to remove is not the creator
        if (participant.equals(creator.getUsername())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("You can't remove the creator");
        }

        // check if the minimum number of participants is respected
        int currentParticipants;
        try {
            currentParticipants = groupDAO.countParticipants(groupID);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.getWriter().print("Something went wrong in the database");
            return;
        }
        Group group;
        try {
            group = groupDAO.getGroupByGroupID(groupID);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.getWriter().print("Something went wrong in the database");
            return;
        }
        if (currentParticipants <= group.getMin()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Non puoi effettuare quest'operazione perché il numero minimo di partecipanti dev'essere rispettato");
            return;
        }

        // remove the participant from the group
        try {
            groupDAO.removeParticipant(participant, groupID);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.getWriter().print("Something went wrong in the database");
            return;
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("Partecipante rimosso con successo");
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
