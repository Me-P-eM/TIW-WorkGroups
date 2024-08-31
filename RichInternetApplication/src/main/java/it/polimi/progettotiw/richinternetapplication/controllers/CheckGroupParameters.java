package it.polimi.progettotiw.richinternetapplication.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import it.polimi.progettotiw.richinternetapplication.beans.Group;
import it.polimi.progettotiw.richinternetapplication.beans.User;
import it.polimi.progettotiw.richinternetapplication.util.LocalDateAdapter;
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
import java.time.LocalDate;

/**
 * Servlet implementation class CheckGroupParameters
 */
@WebServlet("/CheckGroupParameters")
@MultipartConfig
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
        int activity, min, max;
        // check parameters
        if (!ParameterChecker.checkString(request.getParameter("groupTitle"))
                || !ParameterChecker.checkString(request.getParameter("activity"))
                || !ParameterChecker.checkString(request.getParameter("min"))
                || !ParameterChecker.checkString(request.getParameter("max"))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Badly formatted request parameters");
            return;
        }
        if (!ParameterChecker.checkStringLength(request.getParameter("groupTitle"))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Title must have a maximum of 45 characters");
            return;
        }
        try {
            activity = Integer.parseInt(request.getParameter("activity"));
        } catch(NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("The activity you submitted for the group is not an integer number");
            return;
        }
        try {
            min = Integer.parseInt(request.getParameter("min"));
        } catch(NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("The minimum you submitted for the group is not an integer number");
            return;
        }
        try {
            max = Integer.parseInt(request.getParameter("max"));
        } catch(NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("The maximum you submitted for the group is not an integer number");
            return;
        }
        if (activity < 1) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("The activity must be 1 day at least");
            return;
        } else if (min < 1) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("The minimum must be 1 at least");
            return;
        } else if (max < 1) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("The maximum must be 1 at least");
            return;
        } else if (min > max) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("The minimum can't be greater than the maximum");
            return;
        }


        String title = request.getParameter("groupTitle");

        // if the number consistency constraint is respected, go to registry page
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        session.setAttribute("attempts", 0);
        Group group = new Group();
        group.setCreator(user.getUsername());
        group.setTitle(title);
        group.setActivity(activity);
        group.setMin(min);
        group.setMax(max);
        session.setAttribute("group", group);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Gson g = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).setPrettyPrinting().create();
        JsonObject groupJson = g.toJsonTree(group).getAsJsonObject();
        JsonObject result = new JsonObject();
        result.add("group", groupJson);
        response.getWriter().print(result);
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