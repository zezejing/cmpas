package com.hnisc.cmpas.serviceImpl;

import com.hnisc.cmpas.bean.User;
import com.hnisc.cmpas.mapper.UserMapper;
import com.hnisc.cmpas.service.IUserService;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
	
}
