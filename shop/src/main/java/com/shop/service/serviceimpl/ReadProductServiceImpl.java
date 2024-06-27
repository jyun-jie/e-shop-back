package com.shop.service.serviceimpl;

import com.shop.dto.ProductDto;
import com.shop.entity.ProPage;
import com.shop.entity.Product;
import com.shop.mapper.ReadProductMapper;
import com.shop.service.ReadProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReadProductServiceImpl implements ReadProductService {
    @Autowired
    private ReadProductMapper ReadProMapper;

    @Override
    public ProPage<Product> loadPro(Integer pageNum, Integer pageSize) {
        ProPage<Product> pp = new ProPage();
        //找到userid
        List<Product> proList = ReadProMapper.loadPro(pageNum,pageSize);
        //獲取pagehelper得到的當前紀錄，當前頁數據
        int offset = pageNum + proList.size();
        pp.setPageNum(offset);
        pp.setProduct(proList);
        return pp;
    }

    //獲取某商品的資料
    @Override
    public ProductDto findProById(int id) {
        ProductDto product = ReadProMapper.selectProById(id);
        //如果有成功放入sql
        return product ;
    }
}
