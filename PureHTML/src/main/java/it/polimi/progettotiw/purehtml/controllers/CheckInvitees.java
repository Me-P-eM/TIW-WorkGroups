package it.polimi.progettotiw.purehtml.controllers;

import it.polimi.progettotiw.purehtml.beans.Group;
import it.polimi.progettotiw.purehtml.beans.User;
import it.polimi.progettotiw.purehtml.dao.GroupDAO;
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
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet implementation class CheckInvitees
 */
@WebServlet("/CheckInvitees")
public class CheckInvitees extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckInvitees() {
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
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        UserDAO userDAO = new UserDAO(connection);
        String[] selectedUsers = request.getParameterValues("selectedUsers");
        List<String> invitees = new ArrayList<>();
        //check parameters
        if (session.getAttribute("group") == null || session.getAttribute("attempts") == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Badly formatted request parameters");
            return;
        }
        // check and get parameters (invitees) if selectedUser is not null and his size is > 0
        if (ParameterChecker.checkString(request.getParameter("selectedUsers"))) {
            // check if usernames exist in db and if it's not the username of the creator (we want only invitees)
            for (String username : selectedUsers) {
                try {
                    if (!userDAO.checkUsername(username) || user.getUsername().equals(username)) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Badly formatted request parameters");
                        return;
                    } else {
                        invitees.add(username);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Something went wrong in the database");
                    return;
                }
            }
        }


        Group group = (Group) session.getAttribute("group");
        // check if selected invitees respect the minimum participant number
        if (invitees.size()+1 < group.getMin()) {
            int attempts = (int) session.getAttribute("attempts");
            attempts++;
            session.setAttribute("attempts", attempts);
            // se il limite dei tentativi è stato raggiunto
            if (attempts > 2) {
                session.removeAttribute("group");
                session.removeAttribute("attempts");
                String pathToResource = "WEB-INF/cancellation.jsp";
                RequestDispatcher dispatcher = request.getRequestDispatcher(pathToResource);
                dispatcher.forward(request, response);
            } else {
                String errorMessage = "Troppo pochi utenti selezionati, aggiungerne almeno " + (group.getMin() - (invitees.size() + 1));
                String redirectionPath = getServletContext().getContextPath() + "/GoToRegistry";
                redirectionPath += "?errorMessage=" + URLEncoder.encode(errorMessage, "UTF-8");
                session.setAttribute("selectedUsers", selectedUsers);
                response.sendRedirect(redirectionPath);
            }

        // check if selected invitees respect the maximum participant number
        } else if (invitees.size()+1 > group.getMax()) {
            int attempts = (int) session.getAttribute("attempts");
            attempts++;
            session.setAttribute("attempts", attempts);
            // se il limite dei tentativi è stato raggiunto
            if (attempts > 2) {
                session.removeAttribute("group");
                session.removeAttribute("attempts");
                String pathToResource = "WEB-INF/cancellation.jsp";
                RequestDispatcher dispatcher = request.getRequestDispatcher(pathToResource);
                dispatcher.forward(request, response);
            } else {
                String errorMessage = "Troppi utenti selezionati, eliminarne almeno " + ((invitees.size() + 1) - group.getMax());
                String redirectionPath = getServletContext().getContextPath() + "/GoToRegistry";
                redirectionPath += "?errorMessage=" + URLEncoder.encode(errorMessage, "UTF-8");
                session.setAttribute("selectedUsers", selectedUsers);
                response.sendRedirect(redirectionPath);
            }

        // if the number of selected invitees is OK create the group
        } else {
            GroupDAO groupDAO = new GroupDAO(connection);
            LocalDate creation = LocalDate.now();
            group.setCreation(creation);
            try {
                groupDAO.createGroupAndSetInvitation(group, invitees);
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Something went wrong in the database");
                return;
            }
            System.out.println("Group created");
            session.removeAttribute("group");
            String redirectionPath = getServletContext().getContextPath() + "/GoToHome";
            response.sendRedirect(redirectionPath);
        }
    }
}