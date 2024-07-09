package it.polimi.progettotiw.richinternetapplication.controllers;

import com.google.gson.Gson;
import it.polimi.progettotiw.richinternetapplication.beans.User;
import it.polimi.progettotiw.richinternetapplication.dao.UserDAO;
import it.polimi.progettotiw.richinternetapplication.util.ParameterChecker;
import org.apache.commons.validator.routines.EmailValidator;

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

/**
 * Servlet implementation class CheckRegistration
 */
@WebServlet("/CheckRegistration")
@MultipartConfig
public class CheckRegistration extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckRegistration() {
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
        if (!ParameterChecker.checkString(request.getParameter("name"))
                || !ParameterChecker.checkString(request.getParameter("surname"))
                || !ParameterChecker.checkString(request.getParameter("reg-username"))
                || !ParameterChecker.checkString(request.getParameter("email"))
                || !ParameterChecker.checkString(request.getParameter("reg-password"))
                || !ParameterChecker.checkString(request.getParameter("passwordCheck"))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Badly formatted request parameters");
            return;
        }

        HttpSession s = request.getSession(true);
        String name = request.getParameter("name");
        String surname = request.getParameter("surname");
        String username = request.getParameter("reg-username");
        String email = request.getParameter("email");
        String password = request.getParameter("reg-password");
        String passwordCheck = request.getParameter("passwordCheck");
        UserDAO userDao = new UserDAO(connection);
        User u = null;
        boolean usernameExists;

        // check username
        try {
            usernameExists = userDao.checkUsername(username);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.getWriter().print("Something went wrong in the database");
            return;
        }

        // check email
        boolean validEmail;
        boolean emailExists = true;
        validEmail = EmailValidator.getInstance(true).isValid(email);
        if (validEmail) {
            try {
                emailExists = userDao.checkEmail(email);
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
                response.getWriter().print("Something went wrong in the database");
                return;
            }
        }

        // check password
        boolean validPassword = password.equals(passwordCheck);

        // if everything is correct, try register user in the database
        if (!usernameExists && !emailExists && validPassword) {
            try {
                u = userDao.registerUser(username, name, surname, email, password);
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
                response.getWriter().print("Something went wrong in the database");
                return;
            }
        }

        // registration went OK
        if (u != null) {
            s.setAttribute("user", u);
            System.out.println("Setting http session ...");
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            Gson g = new Gson();
            response.getWriter().print(g.toJson(u));

        // registration was not possible due to parameters problems
        } else {
            System.out.println("Registration was not successful");
            if (usernameExists) {
                System.out.println("Username already exists");
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().print("Username already exists");
                return;
            }
            if (!validEmail) {
                System.out.println("Email is not valid");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print("Invalid email format");
                return;
            } else if (emailExists) {
                System.out.println("Email already exists");
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().print("Email already exists");
                return;
            }
            if (!validPassword) {
                System.out.println("Password does not match");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print("Passwords do not match");
            }
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