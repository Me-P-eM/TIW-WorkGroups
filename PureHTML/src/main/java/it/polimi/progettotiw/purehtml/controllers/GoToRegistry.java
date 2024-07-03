package it.polimi.progettotiw.purehtml.controllers;

import it.polimi.progettotiw.purehtml.beans.User;
import it.polimi.progettotiw.purehtml.dao.UserDAO;
import it.polimi.progettotiw.purehtml.util.ParameterChecker;

import javax.servlet.RequestDispatcher;
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

/**
 * Servlet implementation class GoToRegistry
 */
@WebServlet("/GoToRegistry")
public class GoToRegistry extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToRegistry() {
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
        // check parameters
        if (session.getAttribute("group") == null || session.getAttribute("attempts") == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Badly formatted request parameters");
            return;
        }

        User user = (User) session.getAttribute("user");
        List<User> users;
        UserDAO userDAO = new UserDAO(connection);

        // get all users
        try {
            users = userDAO.getAllUsersExceptOne(user.getUsername());
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Something went wrong in the database");
            return;
        }

        // go to registry page
        String pathToResource = "WEB-INF/registry.jsp";
        RequestDispatcher dispatcher = request.getRequestDispatcher(pathToResource);
        request.setAttribute("users", users);
        if (ParameterChecker.checkString(request.getParameter("errorMessage"))) {
            request.setAttribute("errorMessage", request.getParameter("errorMessage"));
        }
        if (session.getAttribute("selectedUsers") != null) {
            String[] selectedUsers = (String[]) session.getAttribute("selectedUsers");
            session.removeAttribute("selectedUsers");
            request.setAttribute("selectedUsers", selectedUsers);
        }
        dispatcher.forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}