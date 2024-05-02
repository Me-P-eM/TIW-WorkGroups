package it.polimi.progettotiw.purehtml.controllers;

import it.polimi.progettotiw.purehtml.beans.Group;
import it.polimi.progettotiw.purehtml.beans.User;
import it.polimi.progettotiw.purehtml.dao.GroupDAO;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet implementation class CheckGroupParameters
 */
@WebServlet("/CheckGroupParameters")
public class CheckGroupParameters extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckGroupParameters() {
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
            throw new UnavailableException("Could't load database driver");
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
        int activity, min, max;
        // check parameters
        if (!ParameterChecker.checkString(request.getParameter("title"))
                || !ParameterChecker.checkString(request.getParameter("activity"))
                || !ParameterChecker.checkString(request.getParameter("min"))
                || !ParameterChecker.checkString(request.getParameter("max"))) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Badly formatted request parameters");
            return;
        }
        try {
            activity = Integer.parseInt(request.getParameter("activity"));
        } catch(NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The activity you submitted for the group is not a number");
            return;
        }
        try {
            min = Integer.parseInt(request.getParameter("min"));
        } catch(NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The minimum you submitted for the group is not a number");
            return;
        }
        try {
            max = Integer.parseInt(request.getParameter("max"));
        } catch(NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The maximum you submitted for the group is not a number");
            return;
        }

        String title = request.getParameter("title");

        // if the number consistency constraint is respected and the title contains valid characters, go to registry page
        if (min <= max && !ParameterChecker.containsInvalidCharacters(title)) {
            String redirectionPath = getServletContext().getContextPath() + "/GoToRegistry";
            redirectionPath += "?title=" + title;
            redirectionPath += "&activity=" + activity;
            redirectionPath += "&min=" + min;
            redirectionPath += "&max=" + max;
            response.sendRedirect(redirectionPath);

        // if the minimum number of participants is greater than the maximum number or the title contains invalid characters, reload the home page showing errors
        } else {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            List<Group> createdGroups = new ArrayList<>();
            List<Group> invitedGroups = new ArrayList<>();
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
            request.setAttribute("createdGroups", createdGroups);
            request.setAttribute("invitedGroups", invitedGroups);
            request.setAttribute("title", title);
            request.setAttribute("activity", activity);
            request.setAttribute("min", min);
            request.setAttribute("max", max);
            if (min > max) {
                request.setAttribute("errorMessageMin", "Il numero minimo non può essere maggiore del numero massimo");
            }
            if (ParameterChecker.containsInvalidCharacters(title)) {
                request.setAttribute("errorMessageTitle", "Il titolo non deve includere: \\ : / ? & = # + e spazi vuoti");
            }
            String pathToResource = "WEB-INF/home.jsp";
            RequestDispatcher dispatcher = request.getRequestDispatcher(pathToResource);
            dispatcher.forward(request, response);
        }
    }
}