package com.xxl.job.admin.core.model;


import com.baomidou.mybatisplus.annotation.TableId;

/**
 * @Description:
 * @Date: Created in 18:41 2018/6/19
 * @Modified by
 */
public class XxlBase {
    @TableId(value = "id")
    private Long id;
    public int getId() {
        return id.intValue();
    }

    public void setId(Long id) {
        this.id = id;
    }
}
