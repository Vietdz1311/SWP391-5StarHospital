package Dao;

import DBConnection.DBConnection;
import Model.FamilyMember;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FamilyMemberDAO {
    private Connection conn;

    public FamilyMemberDAO() {
        try {
            conn = DBConnection.connect();
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    public List<FamilyMember> getFamilyMembersByUserId(int userId) {
        List<FamilyMember> familyMembers = new ArrayList<>();
        String query = "SELECT * FROM FamilyMembers WHERE user_id = ? AND status = 'Active'";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                FamilyMember member = new FamilyMember();
                member.setId(resultSet.getInt("id"));
                member.setUserId(resultSet.getInt("user_id"));
                member.setFullName(resultSet.getString("full_name"));
                member.setBirthDate(resultSet.getObject("birth_date", LocalDate.class));
                member.setGender(resultSet.getString("gender"));
                member.setRelationship(resultSet.getString("relationship"));
                member.setIdCardNumber(resultSet.getString("id_card_number"));
                member.setStatus(resultSet.getString("status"));
                member.setCreatedAt(resultSet.getObject("created_at", LocalDateTime.class));
                member.setUpdatedAt(resultSet.getObject("updated_at", LocalDateTime.class));
                member.setCreatedBy(resultSet.getObject("created_by", Integer.class));
                member.setUpdatedBy(resultSet.getObject("updated_by", Integer.class));
                familyMembers.add(member);
            }
        } catch (SQLException e) {
            System.out.println("Get family members error: " + e.getMessage());
        }
        return familyMembers;
    }
}