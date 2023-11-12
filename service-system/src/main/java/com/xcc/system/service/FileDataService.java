package com.xcc.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.model.technology.FileData;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xcc.model.vo.FileVo;

import java.util.List;

/**
 * <p>
 * 文件列表 服务类
 * </p>
 *
 * @author xcc
 * @since 2023-09-04
 */
public interface FileDataService extends IService<FileData> {

    IPage<FileData> selectPage(Page<FileData> pageParam, FileVo vo);

    //在file_data表中根据material_basedata表中的id获取相应的文件列表（获取过程要加入表格名称判断，以获取指定表格中可以显示的文件类型）
    List<FileData> getFileListByMaterialId(Integer id,String tableName);

    //获取所有的产品标准
    List<FileData> getAllProductStandard();

}
