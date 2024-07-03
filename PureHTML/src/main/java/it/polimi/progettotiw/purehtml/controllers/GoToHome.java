package it.polimi.progettotiw.purehtml.controllers;

import it.polimi.progettotiw.purehtml.beans.Group;
import it.polimi.progettotiw.purehtml.beans.User;
import it.polimi.progettotiw.purehtml.dao.GroupDAO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Something went wrong in the database");
            return;
        }

        // get the list of groups where the user of the session has been invited
        try {
            invitedGroups = groupDAO.getInvitedActiveGroups(user.getUsername());
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Something went wrong in the database");
            return;
        }

        String pathToResource = "WEB-INF/home.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(pathToResource);
        request.setAttribute("createdGroups", createdGroups);
        request.setAttribute("invitedGroups", invitedGroups);
        dispatcher.forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }
}