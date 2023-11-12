package com.xcc.system.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.model.qc.Equipment;
import com.xcc.model.technology.FileData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xcc.model.vo.EquipmentQueryVo;
import com.xcc.model.vo.FileVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 文件列表 Mapper 接口
 * </p>
 *
 * @author xcc
 * @since 2023-09-04
 */

@Repository
public interface FileDataMapper extends BaseMapper<FileData> {

    //条件分页查询
    IPage<FileData> selectPage(Page<FileData> pageParam, @Param("vo") FileVo vo);

    //在file_data表中根据material_basedata表中的id获取相应的文件列表（获取过程要加入表格名称判断，以获取指定表格中可以显示的文件类型）
    List<FileData> getFileListByMaterialId(@Param("correlationId") Integer id,@Param("tableName") String tableName);

    //获取所有的产品标准
    List<FileData> getAllProductStandard();

}
