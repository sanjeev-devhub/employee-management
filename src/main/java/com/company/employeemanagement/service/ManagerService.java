package com.company.employeemanagement.service;

import com.company.employeemanagement.dto.request.ManagerAssignRequest;
import com.company.employeemanagement.dto.response.ManagerResponse;

import java.util.List;

public interface ManagerService {

    ManagerResponse assignManager(ManagerAssignRequest request);

    void removeManager(ManagerAssignRequest request);

    List<ManagerResponse> getAllManagers();

    List<ManagerResponse> getManagersByDepartment(String deptNo);
}
