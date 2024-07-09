package it.polimi.progettotiw.richinternetapplication.controllers;

import it.polimi.progettotiw.richinternetapplication.beans.User;
import it.polimi.progettotiw.richinternetapplication.dao.UserDAO;
import it.polimi.progettotiw.richinternetapplication.util.ParameterChecker;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.google.gson.Gson;

/**
 * Servlet implementation class CheckLogin
 */
@WebServlet("/CheckLogin")
@MultipartConfig
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
        // check parameters
        if (!ParameterChecker.checkString(request.getParameter("username"))
                || !ParameterChecker.checkString(request.getParameter("password"))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Badly formatted request parameters");
            return;
        }

        HttpSession s = request.getSession(true);
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        UserDAO userDao = new UserDAO(connection);
        User u;

        // check credentials
        try {
            u = userDao.checkCredentials(username, password);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.getWriter().print("Something went wrong in the database");
            return;
        }

        // if user is authenticated
        if (u != null) {
            s.setAttribute("user", u);
            System.out.println("Setting http session ...");
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            Gson g = new Gson();
            response.getWriter().print(g.toJson(u));

        // if user is not authenticated
        } else {
            System.out.println("Login was not successful");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print("Username o password errati");
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