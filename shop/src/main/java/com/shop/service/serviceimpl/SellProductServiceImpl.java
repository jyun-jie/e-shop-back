package com.shop.service.serviceimpl;


import com.shop.dto.ProductDto;
import com.shop.entity.ProPage;
import com.shop.entity.Product;
import com.shop.mapper.SellProductMapper;
import com.shop.service.SellProductService;
import com.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SellProductServiceImpl implements SellProductService {
    @Autowired
    SellProductMapper sellProMapper;

    @Autowired
    UserService userService;


    //新增商品
    @Override
    public int insertPro(Product product) {
        //找到使用者id 藉由userService
        int userId = userService.findIdbyName();
        int i = sellProMapper.insert(userId , product);
        return i;
    }

    //獲取某商品的資料
    public ProductDto findProById(int id) {
        ProductDto product = sellProMapper.selectProById(id);
        //如果有成功放入sql
        return product ;
    }

    //更新某商品資料
    public int updatePro(int id , Product product) {
        int i = sellProMapper.update(id , product);
        //如果有成功放入sql
        System.out.println(i);
        return i ;
    }

    //刪除商品
    public int deletePro(int id) {
        int i = sellProMapper.delete(id);
        //如果有成功放入sql
        return i;
    }

    //分頁查看商品
    @Override
    public ProPage<Product> loadPro(Integer pageNum, Integer pageSize) {
        ProPage<Product> pp = new ProPage();
        //找到userid
        int id = userService.findIdbyName();
        List<Product> proList = sellProMapper.loadPro(pageNum,pageSize,id);
        //獲取pagehelper得到的當前紀錄，當前頁數據
        int offset = pageNum + proList.size();
        pp.setPageNum(offset);
        pp.setProduct(proList);

        return pp;
    }

}
