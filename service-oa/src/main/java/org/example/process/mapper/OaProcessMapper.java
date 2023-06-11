package org.example.process.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.example.model.process.Process;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.vo.process.ProcessQueryVo;
import org.example.vo.process.ProcessVo;

/**
 * <p>
 * 审批类型 Mapper 接口
 * </p>
 *
 * @author yidong
 * @since 2023-04-05
 */
public interface OaProcessMapper extends BaseMapper<Process> {

    // 审批管理列表
    IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, @Param("vo") ProcessQueryVo processQueryVo);
}
