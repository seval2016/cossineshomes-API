package com.project.entity.enums;

public enum RoleType {

    ADMIN("Admin"),
    MANAGER("Manager"),

    CUSTOMER("Customer");

    public final String roleName;

    RoleType(String roleName){
        this.roleName=roleName;
    }

    public String getRoleName(){
        return roleName;
    }
}
