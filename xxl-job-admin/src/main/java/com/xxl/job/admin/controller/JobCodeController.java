package com.xxl.job.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLogGlue;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.dao.XxlJobLogGlueDao;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.glue.GlueTypeEnum;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * job code controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@RestController
@RequestMapping("/api/jobcode")
public class JobCodeController {
	
	@Resource
	private XxlJobInfoDao xxlJobInfoDao;
	@Resource
	private XxlJobLogGlueDao xxlJobLogGlueDao;

	@RequestMapping
	public ResponseEntity index(@RequestParam int jobId) {
		XxlJobInfo jobInfo = xxlJobInfoDao.loadById(jobId);
		List<XxlJobLogGlue> jobLogGlues = xxlJobLogGlueDao.findByJobId(jobId);

		if (jobInfo == null) {
			throw new RuntimeException(I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
		}
		if (GlueTypeEnum.BEAN == GlueTypeEnum.match(jobInfo.getGlueType())) {
			throw new RuntimeException(I18nUtil.getString("jobinfo_glue_gluetype_unvalid"));
		}


		// Glue类型-字典
		//model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());

		//model.addAttribute("jobInfo", jobInfo);
		//model.addAttribute("jobLogGlues", jobLogGlues);
		return ResponseEntity.ok(jobLogGlues);
	}
	
	@PostMapping("/save")
	public ReturnT<String> save(@RequestBody Map<String,Object> dto) {
		int id = (int)dto.get("id");
		String glueSource = (String)dto.get("glueSource");
		String glueRemark = (String)dto.get("glueRemark");

		// valid
		if (glueRemark==null) {
			return new ReturnT<String>(500, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_glue_remark")) );
		}
		if (glueRemark.length()<4 || glueRemark.length()>100) {
			return new ReturnT<String>(500, I18nUtil.getString("jobinfo_glue_remark_limit"));
		}
		XxlJobInfo exists_jobInfo = xxlJobInfoDao.loadById(id);
		if (exists_jobInfo == null) {
			return new ReturnT<String>(500, I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
		}
		
		// update new code
		exists_jobInfo.setGlueSource(glueSource);
		exists_jobInfo.setGlueRemark(glueRemark);
		exists_jobInfo.setGlueUpdatetime(new Date());
		exists_jobInfo.setUpdateTime(new Date());
		//xxlJobInfoDao.update(exists_jobInfo);
		xxlJobInfoDao.updateById(exists_jobInfo);
		// log old code
		XxlJobLogGlue xxlJobLogGlue = new XxlJobLogGlue();
		xxlJobLogGlue.setJobId(exists_jobInfo.getId());
		xxlJobLogGlue.setGlueType(exists_jobInfo.getGlueType());
		xxlJobLogGlue.setGlueSource(glueSource);
		xxlJobLogGlue.setGlueRemark(glueRemark);
		//xxlJobLogGlueDao.save(xxlJobLogGlue);
		xxlJobLogGlueDao.insert(xxlJobLogGlue);
		// remove code backup more than 30
		List<Integer> list = new ArrayList<>();
		Page<XxlJobLogGlue> page = new Page();
		page.setCurrent(1);
		page.setSize(30);
		page.setSearchCount(false);
		list = xxlJobLogGlueDao.selectListId(page,new QueryWrapper<XxlJobLogGlue>()
				.eq("job_id", id)
				.orderByDesc("update_time"));

		xxlJobLogGlueDao.delete(new UpdateWrapper<XxlJobLogGlue>().eq("job_id",id).notIn("id",list));

		return ReturnT.SUCCESS;
	}
	
}
