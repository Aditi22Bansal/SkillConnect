package com.skillconnect.services;

import java.sql.SQLException;

public interface ProjectService {
    void updateProjectStatus(int projectId, String newStatus) throws SQLException;
}