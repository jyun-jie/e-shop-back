package com.shop.entity;

public enum OrderState {
    PENDING_PAYMENT, // 待付款 (新增)
    UNCHECKED,        // 待確認
    Not_Ship,        // 待出貨
    Shipping,        // 運送中
    ReadyForPickup,  // 待取貨
    Complete,        // 完成
    CANCELLED        // 已取消 (新增)
}
