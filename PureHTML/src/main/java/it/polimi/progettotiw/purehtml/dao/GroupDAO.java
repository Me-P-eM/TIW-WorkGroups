package it.polimi.progettotiw.purehtml.dao;

import it.polimi.progettotiw.purehtml.beans.Group;
import it.polimi.progettotiw.purehtml.beans.User;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class GroupDAO {
    private Connection connection;

    public GroupDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * This method gets a list of active groups which a specific user created
     * @param username the user who created groups
     * @return the list of groups
     * @throws SQLException
     */
    public List<Group> getCreatedActiveGroups(String username) throws SQLException {
        String query =
            "SELECT groupId, title " +
            "FROM `group` " +
            "WHERE creator=? AND CURDATE() < DATE_ADD(creation, INTERVAL activity DAY) " +
            "ORDER BY title ASC";
        List<Group> groups = new ArrayList<>();
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setString(1, username);
            try (ResultSet result = p.executeQuery()) {
                while (result.next()) {
                    Group group = new Group();
                    group.setGroupID(result.getInt("groupID"));
                    group.setTitle(result.getString("title"));
                    groups.add(group);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException(e);
        }
        return groups;
    }

    /**
     * This method gets a list of active groups where a user has been invited
     * @param username the user invited
     * @return the list of groups
     * @throws SQLException
     */
    public List<Group> getInvitedActiveGroups(String username) throws SQLException {
        String query =
                "SELECT groupID, title " +
                "FROM invitation NATURAL JOIN `group` " +
                "WHERE userID=? AND CURDATE() < DATE_ADD(creation, INTERVAL activity DAY)" +
                "ORDER BY title ASC";
        List<Group> groups = new ArrayList<>();
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setString(1, username);
            try (ResultSet result = p.executeQuery()) {
                while (result.next()) {
                    Group group = new Group();
                    group.setGroupID(result.getInt("groupID"));
                    group.setTitle(result.getString("title"));
                    groups.add(group);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException(e);
        }
        return groups;
    }

    /**
     * This method gets group info by his groupID
     * @param groupID the id of the group
     * @return the group info
     * @throws SQLException
     */
    public Group getGroupByGroupID(int groupID) throws SQLException {
        String query =
                "SELECT creator, title, creation, activity, min, max " +
                "FROM `group` " +
                "WHERE groupID=?";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setInt(1, groupID);
            try (ResultSet result = p.executeQuery()) {
                Group group = null;
                if (!result.isBeforeFirst()) {
                    return group;
                } else {
                    result.next();
                    group = new Group();
                    group.setCreator(result.getString("creator"));
                    group.setTitle(result.getString("title"));
                    group.setCreation(result.getDate("creation").toLocalDate());
                    group.setActivity(result.getInt("activity"));
                    group.setMin(result.getInt("min"));
                    group.setMax(result.getInt("max"));
                    return group;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException(e);
        }
    }

    /**
     * This method gets info of a creator who created a specific group
     * @param groupID the id of the group a user created
     * @return the creator
     * @throws SQLException
     */
    public User getCreatorByGroupID(int groupID) throws SQLException {
        String query =
                "SELECT name, surname " +
                "FROM `group` JOIN `user` ON creator=userID " +
                "WHERE groupID=?";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setInt(1, groupID);
            try (ResultSet result = p.executeQuery()) {
                User creator = null;
                if (!result.isBeforeFirst()) {
                    return creator;
                } else {
                    result.next();
                    creator = new User();
                    creator.setName(result.getString("name"));
                    creator.setSurname(result.getString("surname"));
                    return creator;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException(e);
        }
    }

    /**
     * This method gets invitees of a certain group
     * @param groupID the id of the group
     * @return the list of invitees
     * @throws SQLException
     */
    public List<User> getInviteesByGroupID(int groupID) throws SQLException {
        String query =
                "SELECT name, surname " +
                "FROM invitation NATURAL JOIN `user` " +
                "WHERE groupID=? " +
                "ORDER BY surname ASC";
        List<User> invitees = new ArrayList<>();
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setInt(1, groupID);
            try (ResultSet result = p.executeQuery()) {
                while (result.next()) {
                    User invitee = new User();
                    invitee.setName(result.getString("name"));
                    invitee.setSurname(result.getString("surname"));
                    invitees.add(invitee);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException(e);
        }
        return invitees;
    }

    /**
     * This method checks if a user is a participant of a certain group
     * @param groupID the id of the group
     * @param username the username of the user
     * @return true if the user is a participant, false if he is not
     * @throws SQLException
     */
    public boolean checkParticipant(int groupID, String username) throws SQLException {
        String query =
                "SELECT groupID " +
                "FROM `group` NATURAL LEFT JOIN invitation " +
                "WHERE groupID=? AND (creator=? OR userID=?)";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setInt(1, groupID);
            p.setString(2, username);
            p.setString(3, username);
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
}
