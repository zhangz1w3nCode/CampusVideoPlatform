package com.zzw.Entity.auth;

import java.util.Date;

//角色-元素操作-关联表
public class authRoleMenu {
    private Long id;
    private Long roleId;
    private Long menuId;
    private Date createTime;

    private authMenu authMenu;//

    public com.zzw.Entity.auth.authMenu getAuthMenu() {
        return authMenu;
    }

    public void setAuthMenu(com.zzw.Entity.auth.authMenu authMenu) {
        this.authMenu = authMenu;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
