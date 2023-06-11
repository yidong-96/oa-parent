package org.example.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.model.process.ProcessTemplate;
import org.example.model.process.ProcessType;
import org.example.process.mapper.OaProcessTypeMapper;
import org.example.process.service.OaProcessTemplateService;
import org.example.process.service.OaProcessTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author yidong
 * @since 2023-04-05
 */
@Service
@SuppressWarnings({"unchecked","rawtypes"})
public class OaProcessTypeServiceImpl extends ServiceImpl<OaProcessTypeMapper, ProcessType> implements OaProcessTypeService {

    @Autowired
    private OaProcessTemplateService processTemplateService;

    // 查询所有审批分类和每个分类所有的审批模板
    @Override
    public List<ProcessType> findProcessType() {
        // 查询所有审批分类,返回list集合
        List<ProcessType> processTypeList = baseMapper.selectList(null);
        // 遍历返回所有审批分类list集合
        for (ProcessType processType : processTypeList) {
            // 得到每个审批分类，根据审批分类id查询对应的审批模板
            // 审批分类id
            Long typeId = processType.getId();
            // 根据审批分类id查询对应的审批模板
            LambdaQueryWrapper<ProcessTemplate> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProcessTemplate::getProcessTypeId,typeId);
            List<ProcessTemplate> processTemplateList = processTemplateService.list(wrapper);
            // 根据审批分类id查询对应审批模板数据List封装到每个审批分类对象里面
            processType.setProcessTemplateList(processTemplateList);
        }
        return processTypeList;
    }
}
