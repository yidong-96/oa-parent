package org.example.process.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.example.auth.service.SysUserService;
import org.example.model.process.Process;
import org.example.model.process.ProcessTemplate;
import org.example.model.system.SysUser;
import org.example.process.service.MessageService;
import org.example.process.service.OaProcessService;
import org.example.process.service.OaProcessTemplateService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private OaProcessService processService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private OaProcessTemplateService processTemplateService;

    @Autowired
    private WxMpService wxMpService;

    // 推送待审批人员
    @Override
    public void pushPendingMessage(Long processId, Long userId, String taskId) {
        // 根据id查询流程信息
        Process process = processService.getById(processId);
        // 根据userid查询要推送的人的信息
        SysUser sysUser = sysUserService.getById(userId);
        // 查询审批模板信息
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());
        // 获取提交审批人的信息
        SysUser submitSysUser = sysUserService.getById(process.getUserId());

        // 获取推送对象的openId值
        String openId = sysUser.getOpenId();
        if (StringUtils.isEmpty(openId)) {
            //todo 为了测试使用，先加入自己的openId值
            openId = "o2G9h6c9CEu2JQAY6cuX9M0LGp-0";
        }
        // 设置消息发送的信息
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                // 给谁发消息，传入openId值
                .toUser(openId)
                // 创建模板信息的id值：https://mp.weixin.qq.com/debug/cgi-bin/sandboxinfo?action=showinfo&t=sandbox/index
                .templateId("aAoPzSTYkI8WwLhaDdcpjlT3xuvbiBr29eMjg0FgJcY")
                // 点击消息，跳转的地址
                .url("http://oaparent.5gzvip.91tunnel.com/#/show/" + processId + "/" + taskId)
                .build();

        JSONObject jsonObject = JSON.parseObject(process.getFormValues());
        JSONObject formShowData = jsonObject.getJSONObject("formShowData");
        StringBuffer content = new StringBuffer();
        for (Map.Entry entry : formShowData.entrySet()) {
            content.append(entry.getKey()).append("：").append(entry.getValue()).append("\n ");
        }

        // 设置模板中的参数值
        templateMessage.addData(new WxMpTemplateData("first",submitSysUser.getName()+"提交了"+processTemplate.getName()+"申请，请注意查看！","#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword1", process.getProcessCode(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword2", new DateTime(process.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"), "#272727"));
        templateMessage.addData(new WxMpTemplateData("content", content.toString(), "#272727"));
        // 调用方法推送消息
        try {
            String msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
            System.out.println(msg);
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void pushProcessedMessage(Long processId, Long userId, Integer status) {
        //
    }
}
