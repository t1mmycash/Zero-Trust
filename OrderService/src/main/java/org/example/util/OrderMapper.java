package org.example.util;

import org.example.dto.OrderResponse;
import org.example.model.Order;

public class OrderMapper {
    public static OrderResponse orderToOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .description(order.getDescription())
                .status(order.getStatus())
                .build();
    }
}
