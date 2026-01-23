package com.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table(name = "logistics_order")
public class LogisticsOrder {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer orderId;                    // 关联到 Order.id
    private Integer masterOrderId;             // 关联到 MasterOrder.id
    
    private String logisticsType;              // C2C / B2C
    private String storeType;                  // 7-ELEVEN / FAMILY / HILIFE / OK
    private String merchantOrderNo ;
    private String allPayLogisticsId;           // 物流单号（AllPayLogisticsID）

    private int sellerId ;
    private int buyerId ;

    private String senderName;                 // 寄件人姓名
    private String senderPhone;                // 寄件人电话
    private String senderCellPhone;            // 寄件人手机
    
    private String receiverName;               // 收件人姓名
    private String receiverPhone;              // 收件人电话
    private String receiverCellPhone;         // 收件人手机
    private String receiverEmail;              // 收件人Email
    
    private String storeId;                    // 取货门市代码
    private String storeName;                  // 取货门市名称
    
    private Boolean isCod;                     // 是否取货付款
    private Integer codAmount;                 // 取货付款金额
    
    private String logisticsStatus;            // 物流状态码（RetId）
    private String logisticsStatusDesc;        // 物流状态说明（RetString）
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean need_status_check ;
}
