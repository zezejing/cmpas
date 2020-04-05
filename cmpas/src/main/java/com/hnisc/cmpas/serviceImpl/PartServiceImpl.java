package com.hnisc.cmpas.serviceImpl;

import com.hnisc.cmpas.bean.Part;
import com.hnisc.cmpas.mapper.PartMapper;
import com.hnisc.cmpas.service.IPartService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * ${table.comment} 服务实现类
 * </p>
 *
 * @author humorchen
 * @since 2019-06-02
 */
@Service
public class PartServiceImpl extends ServiceImpl<PartMapper, Part> implements IPartService {
	
}
