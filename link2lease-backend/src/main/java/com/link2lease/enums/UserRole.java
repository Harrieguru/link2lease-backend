package com.link2lease.enums;

public enum UserRole {
    TENANT("Tenant"),
    LANDLORD("Landlord"),
    ADMIN("Administrator");

    private final String displayName;

    UserRole(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
