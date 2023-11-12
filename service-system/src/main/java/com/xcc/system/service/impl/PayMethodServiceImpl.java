package com.xcc.system.service.impl;

import com.xcc.model.purchase.PayMethod;
import com.xcc.system.mapper.PayMethodMapper;
import com.xcc.system.service.PayMethodService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 付款方式信息 服务实现类
 * </p>
 *
 * @author xcc
 * @since 2023-07-29
 */
@Service
public class PayMethodServiceImpl extends ServiceImpl<PayMethodMapper, PayMethod> implements PayMethodService {

}
