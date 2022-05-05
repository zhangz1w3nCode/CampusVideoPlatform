package com.zzw.Entity.auth;

import java.util.Date;


//角色-操作-关联表
public class authRoleElementOperation {
    private Long id;
    private Long roleId;
    private Long elementOperationId;
    private Date createTime;
    private com.zzw.Entity.auth.authElementOperation authElementOperation;// 查一次 就可以得到 角色 对应的操作

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

    public Long getElementOperationId() {
        return elementOperationId;
    }

    public void setElementOperationId(Long elementOperationId) {
        this.elementOperationId = elementOperationId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public com.zzw.Entity.auth.authElementOperation getAuthElementOperation() {
        return authElementOperation;
    }

    public void setAuthElementOperation(com.zzw.Entity.auth.authElementOperation authElementOperation) {
        this.authElementOperation = authElementOperation;
    }
}
