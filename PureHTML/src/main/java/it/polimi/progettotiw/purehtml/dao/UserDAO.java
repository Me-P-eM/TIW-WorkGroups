package it.polimi.progettotiw.purehtml.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.progettotiw.purehtml.beans.User;

public class UserDAO {
    private Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * This method checks credentials from the database for login
     * @param username the username inserted
     * @param password the password inserted
     * @return the user authenticated
     * @throws SQLException
     */
    public User checkCredentials(String username, String password) throws SQLException {
        String query = "SELECT userID, name, surname, email FROM `user` WHERE userID=? AND password=?";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setString(1, username);
            p.setString(2, password);
            try (ResultSet result = p.executeQuery()) {
                User u = null;
                if (!result.isBeforeFirst()) {
                    return u;
                } else {
                    result.next();
                    u = new User();
                    u.setUsername(result.getString("userID"));
                    u.setName(result.getString("name"));
                    u.setSurname(result.getString("surname"));
                    u.setEmail(result.getString("email"));
                    return u;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new SQLException(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException(e);
        }
    }

    /**
     * This method checks if the username already exists in the database
     * @param username the username inserted
     * @return true if the username is already taken, false if the username is usable
     * @throws SQLException
     */
    public boolean checkUsername(String username) throws SQLException {
        String query = "SELECT userID FROM user WHERE userID=?";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setString(1, username);
            try (ResultSet result = p.executeQuery()) {
                if (!result.isBeforeFirst()) {
                    return false;
                } else {
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new SQLException(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException(e);
        }
    }

    /**
     * This method checks if the email already exists in the database
     * @param email the email inserted
     * @return true if the email already exists, false if the email is usable
     * @throws SQLException
     */
    public boolean checkEmail(String email) throws SQLException {
        String query = "SELECT email FROM user WHERE email=?";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setString(1, email);
            try (ResultSet result = p.executeQuery()) {
                if (!result.isBeforeFirst()) {
                    return false;
                } else {
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new SQLException(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException(e);
        }
    }

    /**
     * This method registers a new user in the database
     * @return the user registered
     * @throws SQLException
     */
    public User registerUser(String username, String name, String surname, String email, String password) throws SQLException {
        String query = "INSERT INTO user (userID, name, surname, email, password) VALUES (?, ?, ?, ?, ?)";
        try {
            connection.setAutoCommit(false);
            PreparedStatement p = connection.prepareStatement(query);
            p.setString(1, username);
            p.setString(2, name);
            p.setString(3, surname);
            p.setString(4, email);
            p.setString(5, password);
            int rowsAffected = p.executeUpdate();
            if (rowsAffected == 1) {
                User u = new User();
                u.setUsername(username);
                u.setName(name);
                u.setSurname(surname);
                u.setEmail(email);
                connection.commit();
                return u;
            } else {
                throw new SQLException("Failed to register user: no rows affected");
            }
        } catch (SQLException e) {
            connection.rollback();
            connection.setAutoCommit(true);
            e.printStackTrace();
            throw e;
        }
    }
}