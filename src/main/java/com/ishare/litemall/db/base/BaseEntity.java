package com.ishare.litemall.db.base;


import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zhanggaojie
 * @date 2020/6/4
 *
 */

public class BaseEntity  implements Serializable
{

    private LocalDateTime addTime;

    private LocalDateTime updateTime;

    private String createBy;

    private String updateBy;

    private Boolean valid;

    public LocalDateTime getAddTime() {
        return this.addTime;
    }

    public void setAddTime(final LocalDateTime addTime) {
        this.addTime = addTime;
    }

    public LocalDateTime getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(final LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getAddBy() {
        return this.createBy;
    }

    public void setAddBy(final String createBy) {
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
