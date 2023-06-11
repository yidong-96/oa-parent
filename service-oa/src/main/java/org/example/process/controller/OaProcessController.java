package org.example.process.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.example.common.result.Result;
import org.example.model.process.Process;
import org.example.process.service.OaProcessService;
import org.example.vo.process.ProcessQueryVo;
import org.example.vo.process.ProcessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 审批类型 前端控制器
 * </p>
 *
 * @author yidong
 * @since 2023-04-05
 */
@RestController
@RequestMapping(value = "/admin/process")
public class OaProcessController {

    @Autowired
    private OaProcessService processService;

    // 审批管理列表
    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page,
                        @PathVariable Long limit,
                        ProcessQueryVo processQueryVo) {
        Page<ProcessVo> pageParam = new Page<>(page,limit);
        IPage<ProcessVo> pageModel = processService.selectPage(pageParam,processQueryVo);
        System.out.println("========================================================================");
        System.out.println(pageModel);
        return Result.ok(pageModel);
    }

}

