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
    public List insertProductToCart(int productId, int quantity) {
        try {
            int userId = userService.findIdbyName();
            //獲取商品資料
            ProductDto product = cartMapper.selectProductById(productId);
            //1.先讀取商品賣家
            int sellerId = product.getSellerId();
            //試著先獲取redis 購物車 id
            List<Cart> cartList = findCartByUser();
            //判斷是否存在該賣家
            Cart cart = getCartBySeller(cartList, sellerId);
            if (cart != null) {
                //有賣家就獲取 product 陣列的資料
                List<CartProduct> productList = cart.getSellerCart();
                //看有無相同的商品
                CartProduct productInProductList = getProductInProductList(productList, productId);
                if (productInProductList != null) {
                    //有就+商品新的數量 直接更改數量
                    productInProductList.setQuantity(productInProductList.getQuantity() + quantity);
                    redisTemplate.opsForHash().put("Cart",userId,toJson(cartList));
                } else {
                    //沒有商品
                    productInProductList = new CartProduct(product.getId(),product.getName(),product.getPrice(),quantity);
                    //更改 cart sellercart內容 增加至原本的List
                    productList.add(productInProductList);
                    cart.setSellerCart(productList);
                    redisTemplate.opsForHash().put("Cart",userId,toJson(cartList));
                }
            } else {
                //沒有該賣家
                cart = new Cart();
                cart.setSellerId(product.getSellerId());
                List<CartProduct> productList = new ArrayList<>();
                CartProduct productInProductList =
                        new CartProduct(product.getId(),product.getName(),product.getPrice(),quantity);
                productList.add(productInProductList);
                cart.setSellerCart(productList);
                cartList.add(cart);
                redisTemplate.opsForHash().put("Cart",userId,toJson(cartList));

            }
            return cartList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Cart> findCartByUser() {
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

        List<Cart> cartList = gson.fromJson(cartListJson, listType);
        return cartList;
    }

    public Cart getCartBySeller(List<Cart> cartList, int sellerId) {
        for (Cart cart : cartList) {
            if (cart.getSellerId() == sellerId) {
                return cart;
            }
        }
        return null;
    }

    public CartProduct getProductInProductList(List<CartProduct> productList, int id) {
        for (CartProduct product : productList) {
            if (product.getId() == id) {
                return product;
            }
        }
        return null;
    }

    public String toJson(List<Cart> gsonList){
        Gson gson = new Gson();
        String value =  gson.toJson(gsonList);
        return value;
    }
}

