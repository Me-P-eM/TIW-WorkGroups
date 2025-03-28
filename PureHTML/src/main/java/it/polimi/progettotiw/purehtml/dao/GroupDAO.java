package it.polimi.progettotiw.purehtml.dao;

import it.polimi.progettotiw.purehtml.beans.Group;
import it.polimi.progettotiw.purehtml.beans.User;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class GroupDAO {
    private final Connection connection;

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
                "WHERE userID=? AND CURDATE() < DATE_ADD(creation, INTERVAL activity DAY) " +
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
                if (result.isBeforeFirst()) {
                    result.next();
                    group = new Group();
                    group.setGroupID(groupID);
                    group.setCreator(result.getString("creator"));
                    group.setTitle(result.getString("title"));
                    group.setCreation(result.getDate("creation").toLocalDate());
                    group.setActivity(result.getInt("activity"));
                    group.setMin(result.getInt("min"));
                    group.setMax(result.getInt("max"));
                }
                return group;
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
                if (result.isBeforeFirst()) {
                    result.next();
                    creator = new User();
                    creator.setName(result.getString("name"));
                    creator.setSurname(result.getString("surname"));
                }
                return creator;
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

    /**
     * This method stores the group in the database and sets invitees to that group
     * @param group the group
     * @param invitees people invited to the group
     * @throws SQLException
     */
    public void createGroupAndSetInvitation(Group group, List<String> invitees) throws SQLException {
        String query =
                "INSERT INTO `group` (creator, title, creation, activity, min, max) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            connection.setAutoCommit(false);
            PreparedStatement p = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            p.setString(1, group.getCreator());
            p.setString(2, group.getTitle());
            p.setDate(3, Date.valueOf(group.getCreation()));
            p.setInt(4, group.getActivity());
            p.setInt(5, group.getMin());
            p.setInt(6, group.getMax());
            int rowsAffected = p.executeUpdate();
            if (rowsAffected == 1) {
                ResultSet generatedKeys = p.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int groupID = generatedKeys.getInt(1);
                    setInvitees(invitees, groupID);
                    connection.commit();
                } else {
                    throw new SQLException("Failed to retrieve groupID for the newly created group");
                }
            } else {
                throw new SQLException("Failed to create group: no rows affected");
            }
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
            throw new SQLException(e);
        } finally {
            connection.setAutoCommit(true);
        }
    }

    /**
     * This method sets invitees to a specific group
     * @param invitees a list of usernames of invitees
     * @param groupID the id of the group
     * @throws SQLException
     */
    private void setInvitees(List<String> invitees, int groupID) throws SQLException {
        String query =
                "INSERT INTO invitation (userID, groupID) " +
                "VALUES (?, ?)";
        try {
            PreparedStatement p = connection.prepareStatement(query);
            for (String invitee : invitees) {
                p.setString(1, invitee);
                p.setInt(2, groupID);
                p.addBatch();
            }
            int[] rowsAffected = p.executeBatch();
            boolean allSuccessful = true;
            for (int rows : rowsAffected) {
                if (rows != 1) {
                    allSuccessful = false;
                    break;
                }
            }
            if (allSuccessful) {
                connection.commit();
            } else {
                throw new SQLException("Failed to set invitees: not all rows affected");
            }
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
            throw new SQLException(e);
        }
    }

}
