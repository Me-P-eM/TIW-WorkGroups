package it.polimi.progettotiw.purehtml.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupDAO {
    private Connection connection;

    public GroupDAO(Connection connection) {
        this.connection = connection;
    }

    public List<String> getTitlesCreatedGroups(String username) throws SQLException {
        String query = "SELECT title FROM `group` WHERE creator=? ORDER BY title ASC";
        List<String> groups = new ArrayList<>();
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setString(1, username);
            try (ResultSet result = p.executeQuery()) {
                while (result.next()) {
                    groups.add(result.getString("title"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException(e);
        }
        return groups;
    }

    public List<String> getTitlesPartecipatingGroups(String username) throws SQLException {
        String query = "SELECT title FROM partecipation NATURAL JOIN `group` WHERE userID=? ORDER BY title ASC";
        List<String> groups = new ArrayList<>();
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setString(1, username);
            try (ResultSet result = p.executeQuery()) {
                while (result.next()) {
                    groups.add(result.getString("title"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException(e);
        }
        return groups;
    }
}
