package com.xcc.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xcc.model.qc.Equipment;
import com.xcc.model.technology.FileData;
import com.xcc.model.vo.FileVo;
import com.xcc.system.mapper.FileDataMapper;
import com.xcc.system.service.FileDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 文件列表 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-09-04
 */
@Service
public class FileDataServiceImpl extends ServiceImpl<FileDataMapper, FileData> implements FileDataService {

    @Autowired
    FileDataMapper fileDataMapper;

    @Override
    public IPage<FileData> selectPage(Page<FileData> pageParam, FileVo vo) {
        IPage<FileData> pageModel = baseMapper.selectPage(pageParam,vo);
        return pageModel;

    }

    //在file_data表中根据material_basedata表中的id获取相应的文件列表（获取过程要加入表格名称判断，以获取指定表格中可以显示的文件类型）
    @Override
    public List<FileData> getFileListByMaterialId(Integer id,String tableName) {
        List<FileData> fileDataList = baseMapper.getFileListByMaterialId(id,tableName);
        return fileDataList;
    }

    //获取所有的产品标准
    @Override
    public List<FileData> getAllProductStandard() {
        List<FileData> fileDataList = fileDataMapper.getAllProductStandard();

        return fileDataList;
    }


}
