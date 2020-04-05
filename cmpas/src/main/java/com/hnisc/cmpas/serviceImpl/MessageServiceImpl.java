package com.hnisc.cmpas.serviceImpl;

import com.hnisc.cmpas.bean.Message;
import com.hnisc.cmpas.mapper.MessageMapper;
import com.hnisc.cmpas.service.IMessageService;
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
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {
	
}
