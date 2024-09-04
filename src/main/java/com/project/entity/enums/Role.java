package com.project.entity.enums;

public enum Role {

    ADMIN("Admin"),
    MANAGER("Manager"),

    CUSTOMER("Customer");

    public final String roleName;

    Role(String roleName){
        this.roleName=roleName;
    }

    public String getRoleName(){
        return roleName;
    }
}
