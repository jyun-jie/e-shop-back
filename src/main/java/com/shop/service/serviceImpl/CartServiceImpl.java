package com.shop.service.serviceImpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shop.entity.Cart;
import com.shop.entity.CartProduct;
import com.shop.entity.Product;
import com.shop.mapper.CartMapper;
import com.shop.service.CartService;
import com.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private UserService userService;


    @Override
    public List insertProductToCart(int productId, int quantity) {
        try {
            Product product = selectProductById(productId);
            int sellerId = getSellerIdByProduct(product);
            List<Cart> cartList = findCartListByUser();
            Cart cart = getCartByTheSameSeller(cartList, sellerId);


            if (cart != null ) {
                addProductToCart(cart,productId,quantity);
            } else {
                cartList.add(createCart(product,quantity));
            }
            putUserCartListIntoRedis(cartList);

            return cartList;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Product selectProductById(int productId){
        return  cartMapper.selectProductById(productId);
    }

    //依照uber name分為多個cartList
    public List<Cart> findCartListByUser() {
        int userId =userService.findIdbyName();
        String cartListJson = (String) redisTemplate.opsForHash().get("Cart", userId);
        if (cartListJson == null || cartListJson.isEmpty()) {
            cartListJson = "[]";
        }
        return revertJsonToList(cartListJson);
    }

    public int getSellerIdByProduct(Product product){
        return  product.getSellerId();
    }

    public Cart getCartByTheSameSeller(List<Cart> cartList, int sellerId) {
        for (Cart cart : cartList) {
            if (cart.getSellerId() == sellerId) {
                return cart;
            }
        }
        return null;
    }

    public CartProduct addProductToCart(Cart cart, int productId , int quantity){
        List<CartProduct> productList = getProductListByCart(cart);
        CartProduct cartProduct = getProductFromProductList(productList, productId);
        if (cartProduct != null) {
            addQuantity(cartProduct , quantity);
        } else {
            productList = addProductToList(productList,productId,quantity);
            cart.setCartProductList(productList);
        }
        return cartProduct;
    }

    public Cart createCart(Product product, int quantity){
        CartProduct cartProduct =
                new CartProduct(product.getId(),product.getName(),product.getPrice(),quantity);
        List<CartProduct> productList = new ArrayList<>();
        productList.add(cartProduct);
        Cart cart = new Cart();
        cart.setSellerId(product.getSellerId());
        cart.setCartProductList(productList);
        return cart;
    }

    public void putUserCartListIntoRedis(List<Cart> cartList){
        int userId = userService.findIdbyName();
        redisTemplate.opsForHash().put("Cart",userId,toJson(cartList));
    }

    public List<Cart> revertJsonToList(String cartListJson){
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Cart>>() {
        }.getType();

        return gson.fromJson(cartListJson, listType);
    }

    public List<CartProduct> getProductListByCart(Cart cart){
        return cart.getCartProductList();
    }

    public CartProduct getProductFromProductList(List<CartProduct> productList, int id) {
        for (CartProduct product : productList) {
            if (product.getId() == id) {
                return product;
            }
        }
        return null;
    }

    public void addQuantity(CartProduct cartProduct,int quantity){
        cartProduct.setQuantity(cartProduct.getQuantity() + quantity); //低
    }

    public List<CartProduct> addProductToList(List<CartProduct> productList,int productId,int quantity){
        Product product = selectProductById(productId);
        CartProduct cartProduct = new CartProduct(product.getId(),product.getName(),product.getPrice(),quantity);//低
        productList.add(cartProduct);
        return productList;
    }

    public String toJson(List<Cart> gsonList){
        Gson gson = new Gson();
        return  gson.toJson(gsonList);
    }


}

