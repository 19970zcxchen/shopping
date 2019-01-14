package com.neuedu.service;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Category;
import com.neuedu.pojo.Product;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface IProductService {
    /**
     * 商品的增加/修改
     * @param product
     * @return
     */
    ServerResponse saveOrUpdate(Product product);
    /**
     * 商品的上下架
     * @param productId;商品的id
     * @param status;  商品的状态
     */
    ServerResponse set_sale_status(Integer productId,Integer status);
    /**
     * 后台--查看商品详情
     */
    ServerResponse detail(Integer productId);
    /**
     * 分页商品list
     */
    ServerResponse findAll(Integer pageNo, Integer pageSize);
    /**
     * 后台--搜索商品
     */
    ServerResponse search(Integer productId,String productName,Integer pageNum,Integer pageSize);
    /**
     * 图片上传
     */
    ServerResponse upload(MultipartFile file,String path);

    ServerResponse detail_portal(Integer productId);

    /**
     * 前台--商品搜索
     * @param categoryId
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @param orderBy 排序字段
     */
    ServerResponse list_portal(Integer categoryId, String keyword,Integer pageNum,Integer pageSize, String orderBy);
}
