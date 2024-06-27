package com.shop.service.serviceimpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shop.dto.CartProduct;
import com.shop.dto.ProductDto;
import com.shop.entity.Cart;
import com.shop.mapper.CartMapper;
import com.shop.service.CartService;
import com.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private UserService userService;

    @Override
    public List addToCart(int id, int quantity) {
        try {
            int userId = userService.findIdbyName();
            //獲取商品資料
            ProductDto product = cartMapper.selectProById(id);
            //1.先讀取商品賣家
            int sellerId = product.getSellerId();
            //試著先獲取redis 購物車 id
            List<Cart> gsonList = findUserCart();
            //判斷是否存在該賣家
            Cart cart = cartHasSeller(gsonList, sellerId);
            if (cart != null) {
                //有賣家就獲取 product 陣列的資料
                List<CartProduct> productList = cart.getSellerCart();
                //看有無相同的商品
                CartProduct oldPro = ProductDtoHasId(productList, id);
                if (oldPro != null) {
                    //有就+商品新的數量 直接更改數量
                    oldPro.setQuantity(oldPro.getQuantity() + quantity);
                    redisTemplate.opsForHash().put("Cart",userId,toValue(gsonList));
                } else {
                    //沒有商品
                    oldPro = new CartProduct(product.getId(),product.getName(),product.getPrice(),quantity);
                    //更改 cart sellercart內容 增加至原本的List
                    productList.add(oldPro);
                    cart.setSellerCart(productList);
                    redisTemplate.opsForHash().put("Cart",userId,toValue(gsonList));
                }
            } else {
                //沒有該賣家
                cart = new Cart();
                cart.setSellerId(product.getSellerId());
                List<CartProduct> newList = new ArrayList<>();
                CartProduct Pro =
                        new CartProduct(product.getId(),product.getName(),product.getPrice(),quantity);
                newList.add(Pro);
                cart.setSellerCart(newList);
                gsonList.add(cart);
                redisTemplate.opsForHash().put("Cart",userId,toValue(gsonList));

            }
            return gsonList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Cart> findUserCart() {
        int userId = userService.findIdbyName();
        //獲取 所有集合 userid的所有購物車
        String cartListJson = (String) redisTemplate.opsForHash().get("Cart", userId);
        if (cartListJson == null || cartListJson.isEmpty()) {
            //沒有
            cartListJson = "[]";
        }
        // 轉成List<Cart>
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Cart>>() {
        }.getType();

        List<Cart> gsonList = gson.fromJson(cartListJson, listType);
        System.out.println(gsonList);
        for (Cart cart : gsonList) {
            System.out.println(cart);
        }


        return gsonList;
    }

    public Cart cartHasSeller(List<Cart> gsonList, int sellerId) {
        for (Cart cart : gsonList) {
            if (cart.getSellerId() == sellerId) {
                return cart;
            }
        }
        return null;
    }

    public CartProduct ProductDtoHasId(List<CartProduct> CartProduct, int id) {
        for (CartProduct product : CartProduct) {
            if (product.getId() == id) {
                return product;
            }
        }
        return null;
    }

    public String toValue(List<Cart> gsonList){
        Gson gson = new Gson();
        String value =  gson.toJson(gsonList);
        return value;
    }
}

