package com.shop.mapper;

import com.shop.dto.HomeProductDto;
import com.shop.dto.ProductDetailDto;
import com.shop.dto.ProductDto;
import com.shop.entity.Product;
import com.shop.entity.ProductImage;
import org.apache.ibatis.annotations.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Mapper
public interface SellerProductMapper {

    @Select("select p.name , p.type ,p.price , p.quantity , p.address ,pi.id AS imageId, pi.imageUrl , pi.image_type , p.description " +
            " from product as p " +
            " LEFT JOIN product_image as pi ON p.id = pi.productId" +
            " where pi.productId=#{id}")
    List<ProductDetailDto> selectProductDetailById(int id);

    @Select("select * from product where id=#{id}")
    Product selectProductById(int id);

    @Insert("insert into product ( name , type,description" +
            ",address,price,quantity,sellerId)" +
            "values( #{product.name}, #{product.type}, #{product.description}, #{product.address}, #{product.price}, #{product.quantity}, #{sellerId})")
    @Options(useGeneratedKeys = true, keyProperty = "product.id")
    int insertProduct(@Param("product") Product product , int sellerId );

    @Update("update product set name=#{name},type=#{type},description=#{description}" +
            ",address=#{address},price=#{price},quantity=#{quantity}" +
            " where id=#{id}")
    int updateProduct(Product product);

    @Select("SELECT * FROM product WHERE id = #{id} FOR UPDATE")
    Product selectProductForUpdate(int id);

    @Update("UPDATE product set status = 'delete' , deleted_at = now() where id =#{id} and status = 'taken_down'")
    int logicDeleteProduct(int id);



    @Select("SELECT p.id , p.name , p.price , p.rate , p.address , pi.imageUrl " +
            "From product as p  " +
            "LEFT JOIN product_image as pi " +
            "ON p.id = pi.productId AND image_type = 'cover' " +
            "where p.sellerId = #{sellerId} And p.status = #{status} limit #{pageNum} , #{pageSize} ")
    List<HomeProductDto> selectProductPageBySellerId(Integer pageNum , Integer pageSize , int sellerId , String status);

    @Select("select COALESCE(MAX(sort_order), 0) from product_image where productId = #{productId} ")
    int findMaxSortOrder(int productId);

    @Update("UPDATE product set status = 'taken_down' where id =#{id} and status = 'in_stock'")
    int takenDownProduct(int id);
}
