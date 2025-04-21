package com.skillconnect.services;

import com.skillconnect.utils.DatabaseConnection;
import com.skillconnect.utils.ValidationUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProjectServiceImpl implements ProjectService {

    @Override
    public void updateProjectStatus(int projectId, String newStatus) throws SQLException {
        if (!ValidationUtils.isValidId(projectId)) {
            throw new IllegalArgumentException("Invalid project ID");
        }

        String query = "UPDATE projects SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, projectId);

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Project not found with ID: " + projectId);
            }
        }
    }
}