package com.ishare.goodsrecycle.base;


import java.io.Serializable;
import java.util.Date;

/**
 * @author zhanggaojie
 * @date 2020/6/4
 *
 */

public class BaseEntity  implements Serializable
{

    private Date createTime;

    private Date updateTime;

    private String createBy;

    private String updateBy;

    private Boolean valid;

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(final Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(final Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateBy() {
        return this.createBy;
    }

    public void setCreateBy(final String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return this.updateBy;
    }

    public void setUpdateBy(final String updateBy) {
        this.updateBy = updateBy;
    }

    public Boolean getValid() {
        return this.valid;
    }

    public void setValid(final Boolean valid) {
        this.valid = valid;
    }

}
