package org.example.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.model.process.ProcessTemplate;
import org.example.model.process.ProcessType;
import org.example.process.mapper.OaProcessTemplateMapper;
import org.example.process.service.OaProcessService;
import org.example.process.service.OaProcessTemplateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.process.service.OaProcessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 审批模板 服务实现类
 * </p>
 *
 * @author yidong
 * @since 2023-04-05
 */
@Service
public class OaProcessTemplateServiceImpl extends ServiceImpl<OaProcessTemplateMapper, ProcessTemplate> implements OaProcessTemplateService {

    @Autowired
    private OaProcessTypeService processTypeService;

    @Autowired
    private OaProcessService processService;

    @Override
    public IPage<ProcessTemplate> selectPageProcessTemplate(Page<ProcessTemplate> pageParam) {
        // 调用mapper的方法实现分页查询
        Page<ProcessTemplate> processTemplatePage = baseMapper.selectPage(pageParam, null);
        // 第一步分页查询返回分页数据，从分页数据获取列表list集合
        List<ProcessTemplate> processTemplateList = processTemplatePage.getRecords();
        // 遍历list集合，得到每个对象的审批类型id
        for (ProcessTemplate processTemplate : processTemplateList) {
            // 得到每个对象的审批类型id
            Long processTypeId = processTemplate.getProcessTypeId();
            // 根据审批类型id，查询获取对应名称
            LambdaQueryWrapper<ProcessType> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProcessType::getId,processTypeId);
            ProcessType processType = processTypeService.getOne(wrapper);
            if (processType == null) {
                continue;
            }
            // 完成最终封装processTypeName
            processTemplate.setProcessTypeName(processType.getName());
        }
        return processTemplatePage;
    }

    // 修改模板发布状态，1代表已经发布
    // 流程定义部署
    @Override
    public void publish(Long id) {
        // 修改模板发布状态为1
        ProcessTemplate processTemplate = baseMapper.selectById(id);
        processTemplate.setStatus(1);
        baseMapper.updateById(processTemplate);

        //流程定义部署
        if (!StringUtils.isEmpty(processTemplate.getProcessDefinitionPath())) {
            processService.deployByZip(processTemplate.getProcessDefinitionPath());
        }

    }
}
