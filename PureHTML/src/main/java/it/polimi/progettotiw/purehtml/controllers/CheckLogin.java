package it.polimi.progettotiw.purehtml.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.progettotiw.purehtml.beans.User;
import it.polimi.progettotiw.purehtml.dao.UserDAO;
import it.polimi.progettotiw.purehtml.util.ParameterChecker;

/**
 * Servlet implementation class CheckLogin
 */
@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckLogin() {
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
        // check parameters
        if (!ParameterChecker.checkString(request.getParameter("username"))
                || !ParameterChecker.checkString(request.getParameter("password"))) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Badly formatted request parameters");
            return;
        }

        HttpSession s = request.getSession(true);
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        UserDAO userDao = new UserDAO(connection);
        User u = null;

        // check credentials
        try {
            u = userDao.checkCredentials(username, password);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Couldn't perform database interrogation");
            return;
        }

        // if user is authenticated
        if (u != null) {
            s.setAttribute("user", u);
            System.out.println("Setting http session ...");
            String redirectionPath = getServletContext().getContextPath() + "/GoToHome";
            response.sendRedirect(redirectionPath);

        //if user is not authenticated
        } else {
            System.out.println("Login was not successful");
            request.setAttribute("username", username);
            request.setAttribute("password", password);
            request.setAttribute("errorMessage", "Username o password errati");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
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