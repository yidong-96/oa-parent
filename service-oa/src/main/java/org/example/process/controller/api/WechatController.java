package org.example.process.controller.api;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import org.example.auth.service.SysUserService;
import org.example.common.jwt.JwtHelper;
import org.example.common.result.Result;
import org.example.model.system.SysUser;
import org.example.vo.wechat.BindPhoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller  // 可以进行页面跳转
@RequestMapping("/admin/wechat")
@CrossOrigin // 跨域
public class WechatController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private WxMpService wxMpService;

//    @Value("${wechat.userInfoUrl}")
    private String userInfoUrl="http://oaparent1.5gzvip.91tunnel.com/admin/wechat/userInfo";

    @GetMapping("/authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl,
                            HttpServletRequest request) {
        //由于授权回调成功后，要返回原地址路径，原地址路径带“#”号，当前returnUrl获取带“#”的url获取不全，因此前端把“#”号替换为“oatest”了，这里要还原一下
        // buildAuthorizationUrl传入三个参数：授权路径、授权类型(固定值)和授权成功后跳转的路径
        String redirectUrl = null;
        try {
            redirectUrl = wxMpService.getOAuth2Service()
                    .buildAuthorizationUrl(userInfoUrl,
                            WxConsts.OAuth2Scope.SNSAPI_USERINFO,
                            URLEncoder.encode(returnUrl.replace("oatest", "#"),"utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("authorize被调用");
        System.out.println("returnUrl:" + returnUrl);
        System.out.println("redirectUrl:" + redirectUrl);
        return "redirect:" + redirectUrl;
    }

    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code,
                           @RequestParam("state") String returnUrl) throws Exception {
        // 获取accessToken
        WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service().getAccessToken(code);
        // 使用accessToken，获取openId
        String openId = accessToken.getOpenId();
        System.out.println("openId" + openId);

        // 获取微信用户信息
        WxOAuth2UserInfo wxMpUser = wxMpService.getOAuth2Service().getUserInfo(accessToken, null);
        System.out.println("微信用户信息" + JSON.toJSONString(wxMpUser));

        // 根据openId查询用户表
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getOpenId,openId);
        SysUser sysUser = sysUserService.getOne(wrapper);
        String token = "";
        // 判断openId是否存在
        if (sysUser != null) {
            token = JwtHelper.createToken(sysUser.getId(),sysUser.getUsername());
        }
        int index = returnUrl.indexOf("?");
        if(index == -1) {
            String redirectUrl = "redirect:" + returnUrl + "?token=" + token + "&openId=" + openId;
            return redirectUrl;
        } else {
            String redirectUrl = "redirect:" + returnUrl + "&token=" + token + "&openId=" + openId;
            return redirectUrl;
        }
    }

    @ApiOperation(value = "微信账号绑定手机")
    @PostMapping("/bindPhone")
    @ResponseBody // 可以返回数据
    public Result bindPhone(@RequestBody BindPhoneVo bindPhoneVo) {
        // 根据手机号查询数据库
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone,bindPhoneVo.getPhone());
        SysUser sysUser = sysUserService.getOne(wrapper);
        System.out.println("bindPhone被调用");

        // 如果存在，更新记录openId
        if (sysUser != null) {
            sysUser.setOpenId(bindPhoneVo.getOpenId());
            sysUserService.updateById(sysUser);
            String token = JwtHelper.createToken(sysUser.getId(),sysUser.getUsername());
            return Result.ok(token);
        } else {
            return Result.fail("手机号不存在，请联系管理员修改");
        }
    }
}
