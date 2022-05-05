package com.zzw.Entity.auth;

import java.util.List;

public class userAuthorities {

    private List<authRoleElementOperation> RoleElementOperationList;
    private List<authRoleMenu> authRoleMenuList;

    public List<authRoleElementOperation> getRoleElementOperationList() {
        return RoleElementOperationList;
    }

    public void setRoleElementOperationList(List<authRoleElementOperation> roleElementOperationList) {
        RoleElementOperationList = roleElementOperationList;
    }

    public List<authRoleMenu> getAuthRoleMenuList() {
        return authRoleMenuList;
    }

    public void setAuthRoleMenuList(List<authRoleMenu> authRoleMenuList) {
        this.authRoleMenuList = authRoleMenuList;
    }
}
