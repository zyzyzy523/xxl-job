package com.xxl.job.admin.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xxl.job.admin.core.model.XxlJobLogGlue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * job log for glue
 * @author xuxueli 2016-5-19 18:04:56
 */
public interface XxlJobLogGlueDao extends BaseMapper<XxlJobLogGlue> {

	
	List<XxlJobLogGlue> findByJobId(@Param("jobId") int jobId);


	int deleteByJobId(@Param("jobId") int jobId);

	List<Integer> selectListId(IPage rowBounds, @Param("ew") QueryWrapper<XxlJobLogGlue> wrapper);
}
