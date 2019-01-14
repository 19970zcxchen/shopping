package com.neuedu.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.ShippingMapper;
import com.neuedu.pojo.Shipping;
import com.neuedu.service.IAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AddressServiceImpl implements IAddressService {
    @Autowired
    ShippingMapper shippingMapper;
    @Override
    public ServerResponse add(Integer userId, Shipping shipping) {
        //1.参数非空校验
        if (shipping==null){
            return ServerResponse.createServerResponseByError("参数错误");
        }
        //2.添加
        shipping.setUserId(userId);
        shippingMapper.insert(shipping);
        //3.返回结果
        Map<String,Integer> map= Maps.newHashMap();
        map.put("shippingId",shipping.getId());
        return ServerResponse.createServerResponseBySuccess(map);
    }

    @Override
    public ServerResponse del(Integer userId, Integer shippingId) {
        //1.参数非空校验
        if (shippingId==null){
            return ServerResponse.createServerResponseByError("参数错误");
        }
        //2.删除
        int result = shippingMapper.deleteByUserIdAndShippingId(userId, shippingId);
        //3.返回结果
        if (result>0){
            return ServerResponse.createServerResponseBySuccess();
        }
        return ServerResponse.createServerResponseBySuccess("删除失败");
    }
    /**
     * 登录状态更新地
     */
    @Override
    public ServerResponse update(Shipping shipping) {
        //1.参数的非空校验
        if (shipping==null){
            return ServerResponse.createServerResponseByError("参数错误");
        }
        //2.更新
        int result = shippingMapper.updateBySelectiveKey(shipping);
        //3.返回结果
        if (result>0){
            return ServerResponse.createServerResponseBySuccess();
        }
        return ServerResponse.createServerResponseBySuccess("更新失败");
    }

    /**
     *  选中查看具体地址
     * @param shippingId
     * @return
     */
    @Override
    public ServerResponse select(Integer shippingId) {

        //1.参数的非空校验
        if (shippingId==null){
            return ServerResponse.createServerResponseByError("参数错误");
        }
        Shipping shipping = shippingMapper.selectByPrimaryKey(shippingId);
        return ServerResponse.createServerResponseBySuccess(shipping);
    }

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse list(Integer userId,Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo=new PageInfo(shippingList);
        return ServerResponse.createServerResponseBySuccess(pageInfo);
    }


}
