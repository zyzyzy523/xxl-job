package com.xxl.job.admin.core.model;



import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

/**
 * Created by xuxueli on 16/9/30.
 */
@TableName("xxl_job_registry")
@KeySequence("xxl_job_registry_s")
public class XxlJobRegistry extends XxlBase{


    private String registryGroup;
    private String registryKey;
    private String registryValue;
    private Date updateTime;



    public String getRegistryGroup() {
        return registryGroup;
    }

    public void setRegistryGroup(String registryGroup) {
        this.registryGroup = registryGroup;
    }

    public String getRegistryKey() {
        return registryKey;
    }

    public void setRegistryKey(String registryKey) {
        this.registryKey = registryKey;
    }

    public String getRegistryValue() {
        return registryValue;
    }

    public void setRegistryValue(String registryValue) {
        this.registryValue = registryValue;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
