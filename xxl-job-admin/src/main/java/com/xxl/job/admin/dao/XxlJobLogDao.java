package com.xxl.job.admin.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xxl.job.admin.core.model.XxlJobLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * job log
 * @author xuxueli 2016-1-12 18:03:06
 */

public interface XxlJobLogDao extends BaseMapper<XxlJobLog> {

	List<XxlJobLog> pageList(IPage page,
							 @Param("jobGroup") int jobGroup,
							 @Param("jobId") int jobId,
							 @Param("triggerTimeStart") Date triggerTimeStart,
							 @Param("triggerTimeEnd") Date triggerTimeEnd,
							 @Param("logStatus") int logStatus,
							 @Param("tenantId") Long tenantId);

	XxlJobLog load(@Param("id") long id);



	int updateTriggerInfo(XxlJobLog xxlJobLog);

	int updateHandleInfo(XxlJobLog xxlJobLog);
	
	int deleteByJobId(@Param("jobId") int jobId);

	Map<String, Object> findLogReport(@Param("from") Date from,
										 @Param("to") Date to);




	List<Long> findFailJobLogIds(@Param("pagesize") int pagesize);

	int updateAlarmStatus(@Param("logId") long logId,
								 @Param("oldAlarmStatus") int oldAlarmStatus,
								 @Param("newAlarmStatus") int newAlarmStatus);
	List<Integer> selectListId(IPage rowBounds, @Param("ew") QueryWrapper<XxlJobLog> wrapper);

	public List<Long> findLostJobIds(@Param("losedTime") Date losedTime);

}
