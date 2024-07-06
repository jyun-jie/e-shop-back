package com.shop.service.serviceimpl;


import com.shop.dto.ProductDto;
import com.shop.entity.ProductPage;
import com.shop.entity.Product;
import com.shop.mapper.SellerProductMapper;
import com.shop.service.SellerProductService;
import com.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SellerProductServiceImpl implements SellerProductService {
    @Autowired
    SellerProductMapper sellerProMapper;

    @Autowired
    UserService userService;


    //新增商品
    @Override
    public int insertPro(Product product) {
        //找到使用者id 藉由userService
        int userId = userService.findIdbyName();
        int i = sellerProMapper.insert(userId , product);
        return i;
    }

    //獲取某商品的資料
    public ProductDto findProById(int id) {
        ProductDto product = sellerProMapper.selectProById(id);
        //如果有成功放入sql
        return product ;
    }

    //更新某商品資料
    public int updatePro(int id , Product product) {
        int i = sellerProMapper.update(id , product);
        //如果有成功放入sql
        System.out.println(i);
        return i ;
    }

    //刪除商品
    public int deletePro(int id) {
        int i = sellerProMapper.delete(id);
        //如果有成功放入sql
        return i;
    }

    //分頁查看商品
    @Override
    public ProductPage<Product> loadPro(Integer pageNum, Integer pageSize) {
        ProductPage<Product> pp = new ProductPage();
        //找到userid
        int id = userService.findIdbyName();
        List<Product> proList = sellerProMapper.loadPro(pageNum,pageSize,id);
        //獲取pagehelper得到的當前紀錄，當前頁數據
        int offset = pageNum + proList.size();
        pp.setPageNum(offset);
        pp.setProduct(proList);

        return pp;
    }

}
