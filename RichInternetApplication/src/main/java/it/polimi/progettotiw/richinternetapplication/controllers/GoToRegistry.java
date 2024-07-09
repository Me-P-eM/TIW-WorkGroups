package it.polimi.progettotiw.richinternetapplication.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.polimi.progettotiw.richinternetapplication.beans.User;
import it.polimi.progettotiw.richinternetapplication.dao.UserDAO;

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
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Badly formatted request parameters");
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
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.getWriter().print("Could not access the database");
            return;
        }

        // go to registry page
        JsonArray usersJsonArray = new JsonArray();
        for (User u : users) {
            JsonObject userJson = new JsonObject();
            userJson.addProperty("username", u.getUsername());
            userJson.addProperty("name", u.getName());
            userJson.addProperty("surname", u.getSurname());
            usersJsonArray.add(userJson);
        }
        JsonObject result = new JsonObject();
        result.add("users", usersJsonArray);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        response.getWriter().print(gson.toJson(result));
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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