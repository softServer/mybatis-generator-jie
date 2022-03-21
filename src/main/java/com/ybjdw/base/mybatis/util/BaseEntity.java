package com.ybjdw.base.mybatis.util;



import java.io.Serializable;
import java.util.Date;

/**
 * @author zhanggaojie
 * @date 2020/6/4
 *
 */

public class BaseEntity  implements Serializable
{
    /**
     * 排序方式，降序
     */
    private static final String ORDER_DESC = " desc";
    /**
     * 排序方式，升序
     */
    private static final String ORDER_ASC = " asc";

    /**
     * sql like 的通配符 %
     */
    private static final String LIKE_SYMBOL = "%";

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


    public static String orderByDesc(String field) {
        return field.concat(ORDER_DESC);
    }
    public static String orderByAsc(String field) {
        return field.concat(ORDER_ASC);
    }

    public static String like(String data) {
        return LIKE_SYMBOL.concat(data).concat(LIKE_SYMBOL);
    }
}
