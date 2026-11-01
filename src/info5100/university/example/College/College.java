/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info5100.university.example.College;

import info5100.university.example.Department.Department;
import java.util.ArrayList;

/**
 *
 * @author kal bugrara
 */
public class College {
    ArrayList<Department> departments;
    private final String name;
    
    public College(String name){
        this.name = name;
        departments = new ArrayList();
    }
    /** Integration methods added by Shai Rashada-Parasram. */
    public String getName() { return name; }
    public void addDepartment(Department department) {
        if (department == null) throw new IllegalArgumentException("Department required.");
        if (!departments.contains(department)) departments.add(department);
    }
    public java.util.List<Department> getDepartments() {
        return java.util.Collections.unmodifiableList(departments);
    }
}
