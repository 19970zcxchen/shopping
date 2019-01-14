package com.neuedu.service.impl;

import com.google.common.collect.Lists;
import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.CartMapper;
import com.neuedu.dao.ProductMapper;
import com.neuedu.pojo.Cart;
import com.neuedu.pojo.Product;
import com.neuedu.service.ICartService;
import com.neuedu.utils.BigDecimalUtils;
import com.neuedu.vo.CartProductVO;
import com.neuedu.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements ICartService {
    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;

    /**
     * .购物车添加商品
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    @Override
    public ServerResponse add(Integer userId,Integer productId, Integer count) {
        //1.参数非空校验
        if (productId==null&&count==null){
            return ServerResponse.createServerResponseByError("参数不能为空");
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServerResponse.createServerResponseByError("要添加的商品不存在");
        }
        //2.根据productIdhe UserId查询购物信息
        Cart cart = cartMapper.selectCarByUserIdAndProductId(userId, productId);
        if (cart==null){
            //添加
            Cart cart1=new Cart();
            cart1.setUserId(userId);
            cart1.setProductId(productId);
            cart1.setQuantity(count);
            cart1.setChecked(Const.CartCheckedEnum.PRODUCT_CHECKED.getCode());
            cartMapper.insert(cart1);
        }else {
            //更新
            Cart cart1=new Cart();
            cart1.setId(cart.getId());
            cart1.setProductId(productId);
            cart1.setUserId(userId);
            cart1.setQuantity(count);
            cart1.setChecked(cart.getChecked());
            cartMapper.updateByPrimaryKey(cart1);
        }
        CartVO cartVO=getCartVOLimit(userId);
        return ServerResponse.createServerResponseBySuccess(cartVO);
    }

    /**
     * 查看购物车List列表
     * @param userId
     * @return
     */
    @Override
    public ServerResponse list(Integer userId) {
        CartVO cartVO = getCartVOLimit(userId);
        return ServerResponse.createServerResponseBySuccess(cartVO);
    }

    /**
     * 更新购物车某个产品数量
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    @Override
    public ServerResponse update(Integer userId, Integer productId, Integer count) {
        //1.参数的判断
        if (productId==null&&count==null){
            return ServerResponse.createServerResponseByError("参数不能为空");
        }
        //2.查询购物车中的商品
        Cart cart = cartMapper.selectCarByUserIdAndProductId(userId, productId);
        if (cart!=null){
            //3.更新数量
            cart.setQuantity(count);
            int update = cartMapper.updateByPrimaryKey(cart);
            if (update>0){
                return ServerResponse.createServerResponseBySuccess("更新成功",getCartVOLimit(userId));
            }else{
                return ServerResponse.createServerResponseBySuccess("更新失败",getCartVOLimit(userId));
            }
        }
        return ServerResponse.createServerResponseByError("更新失败,商品不存在");
        }
        //4.返回结果cartVO
    /**
     * 移除购物车某个商品
     * @param userId
     * @param productIds
     * @return
     */
    @Override
    public ServerResponse delete_product(Integer userId, String productIds) {
        //1.参数的非空校验
        if (productIds==null&&productIds.equals("")){
            return ServerResponse.createServerResponseByError("参数不能为空");
        }
        //2.productIds  ---> List<Integer>
        List<Integer> productIdList=Lists.newArrayList();
        String[] productIdsArr = productIds.split(",");
        if (productIdsArr!=null&&productIdsArr.length>0){
            for (String productIdstr:productIdsArr){
                Integer productId=Integer.parseInt(productIdstr);
                productIdList.add(productId);
            }
        }
        //3.调用dao
        cartMapper.deleteByUseridAndProductIds(userId,productIdList);
        //4.返回结果
        return ServerResponse.createServerResponseBySuccess(getCartVOLimit(userId));
    }
    /**
     * 购物车选中某个商品
     * @param userId
     * @param productId
     * @return
     */
    @Override
    public ServerResponse select(Integer userId, Integer productId,Integer check) {
        //1.参数非空校验
        if (productId==null){
            return ServerResponse.createServerResponseByError("参数不能为空");
        }
        //2.dao接口
        cartMapper.selectOrUnselectProduct(userId,productId,check);
        //3.返回结果
        return ServerResponse.createServerResponseBySuccess(userId);
    }
    /**
     * .查询在购物车里的产品数量
     */
    @Override
    public ServerResponse get_cart_product_count(Integer userId) {
        int quantity = cartMapper.get_cart_product_count(userId);
        return ServerResponse.createServerResponseBySuccess(quantity);
    }

    private CartVO getCartVOLimit(Integer userId){
        CartVO cartVO=new CartVO();
        //1.根据userID查询购物信息-->List<Cart>
        List<Cart> cartList = cartMapper.selectCarByUserId(userId);
        //2.List<Cart>---->List<CartProductVO>
        List<CartProductVO> cartProductVOList= Lists.newArrayList();

        //购物车总价格
        BigDecimal carttotalprice=new BigDecimal(0);

        if (cartList!=null&&cartList.size()>0){
            for (Cart cart:cartList){
                CartProductVO cartProductVO=new CartProductVO();
                cartProductVO.setId(cart.getId());
                cartProductVO.setQuantity(cart.getQuantity());
                cartProductVO.setUserId(userId);
                cartProductVO.setProductChecked(cart.getChecked());
                //查询商品
                Product product = productMapper.selectByPrimaryKey(cart.getProductId());
                if (product!=null){
                    cartProductVO.setProductId(cart.getProductId());
                    cartProductVO.setProductMainImage(product.getMainImage());
                    cartProductVO.setProductName(product.getName());
                    cartProductVO.setProductPrice(product.getPrice());
                    cartProductVO.setProductStatus(product.getStatus());
                    cartProductVO.setProductStock(product.getStock());
                    cartProductVO.setProductSubtitle(product.getSubtitle());

                    int stock=product.getStock();
                    int limitProductCount=0;
                    if (stock>=cart.getQuantity()){
                        limitProductCount=cart.getQuantity();
                        cartProductVO.setLimitQuantity("LIMIT_NUM_SUCCESS");
                    }else {    //商品库存不足
                        limitProductCount=stock;
                        //更新购物车中商品的数量
                        Cart cart1=new Cart();
                        cart1.setId(cart.getId());
                        cart1.setQuantity(stock);
                        cart1.setProductId(cart.getProductId());
                        cart1.setChecked(cart.getChecked());
                        cart1.setUserId(userId);
                        cartMapper.updateByPrimaryKey(cart1);
                        cartProductVO.setLimitQuantity("LIMIT_NUM_FAIL");
                    }
                    cartProductVO.setQuantity(limitProductCount);
                    cartProductVO.setProductTotalPrice(BigDecimalUtils.mul(product.getPrice().doubleValue(),Double.valueOf(cartProductVO.getQuantity())));
                }
                carttotalprice = BigDecimalUtils.add(carttotalprice.doubleValue(),cartProductVO.getProductTotalPrice().doubleValue());

                cartProductVOList.add(cartProductVO);
            }
        }
        cartVO.setCartProductVOList(cartProductVOList);
        //3.计算总价格
        cartVO.setCarttotalprice(carttotalprice);
        //4.判断购物车是否全选
        int count=cartMapper.isCheckedAll(userId);
        if (count>0){
            cartVO.setIsallchecked(false);
        }else {
            cartVO.setIsallchecked(true);
        }
        //5.返回结果
        return cartVO;
    }







}
