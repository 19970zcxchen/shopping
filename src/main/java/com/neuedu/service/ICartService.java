package com.neuedu.service;

import com.neuedu.common.ServerResponse;

public interface ICartService {
    /**
     * .购物车添加商品
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    public ServerResponse add(Integer userId,Integer productId,Integer count);
    /**
     * 查看购物车List列表
     * @param userId
     * @return
     */
    ServerResponse list(Integer userId);
    /**
     * 更新购物车某个产品数量
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    ServerResponse update(Integer userId,Integer productId, Integer count);

    /**
     * 移除购物车某个商品
     * @param userId
     * @param productIds
     * @return
     */
    ServerResponse delete_product(Integer userId,String productIds);

    /**
     * 购物车选中某个商品
     * @param userId
     * @param productId
     * @return
     */
    ServerResponse select(Integer userId,Integer productId,Integer check);
    /**
     * .查询在购物车里的产品数量
     */
    ServerResponse get_cart_product_count(Integer userId);
}
