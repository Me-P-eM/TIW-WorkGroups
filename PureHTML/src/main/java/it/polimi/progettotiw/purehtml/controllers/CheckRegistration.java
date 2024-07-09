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
import org.apache.commons.validator.routines.EmailValidator;

/**
 * Servlet implementation class CheckRegistration
 */
@WebServlet("/CheckRegistration")
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
                || !ParameterChecker.checkString(request.getParameter("username"))
                || !ParameterChecker.checkString(request.getParameter("email"))
                || !ParameterChecker.checkString(request.getParameter("password"))
                || !ParameterChecker.checkString(request.getParameter("passwordCheck"))) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Badly formatted request parameters");
            return;
        }

        HttpSession s = request.getSession(true);
        String name = request.getParameter("name");
        String surname = request.getParameter("surname");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String passwordCheck = request.getParameter("passwordCheck");
        UserDAO userDao = new UserDAO(connection);
        User u = null;
        boolean usernameExists;

        // check username
        try {
            usernameExists = userDao.checkUsername(username);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Something went wrong in the database");
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
                response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Something went wrong in the database");
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
                response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Something went wrong in the database");
                return;
            }
        }

        // registration went OK
        if (u != null) {
            s.setAttribute("user", u);
            System.out.println("Setting http session ...");
            String redirectionPath = getServletContext().getContextPath() + "/GoToHome";
            response.sendRedirect(redirectionPath);

        // registration was not possible due to parameters problems
        } else {
            System.out.println("Registration was not successful");
            if (usernameExists) {
                System.out.println("Username already exists");
                request.setAttribute("errorMessageUsername", "Username già utilizzato");
            }
            if (!validEmail) {
                System.out.println("Email is not valid");
                request.setAttribute("errorMessageEmail", "Email non valida");
            } else if (emailExists) {
                System.out.println("Email already exists");
                request.setAttribute("errorMessageEmail", "Email già utilizzata");
            }
            if (!validPassword) {
                System.out.println("Password does not match");
                request.setAttribute("errorMessagePassword", "La password non corrisponde");
            }
            request.setAttribute("name", name);
            request.setAttribute("surname", surname);
            request.setAttribute("username", username);
            request.setAttribute("email", email);
            request.setAttribute("password", password);
            request.setAttribute("passwordCheck", passwordCheck);
            request.getRequestDispatcher("/registration.jsp").forward(request, response);
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