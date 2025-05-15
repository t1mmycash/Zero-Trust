package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.OrderRequest;
import org.example.dto.OrderResponse;
import org.example.exception.AccessDeniedException;
import org.example.exception.IllegalStatusException;
import org.example.exception.OrderNotFoundException;
import org.example.model.Order;
import org.example.storage.OrderRepository;
import org.example.util.OrderMapper;
import org.example.util.Status;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderResponse createOrder(Long userId, OrderRequest orderRequest) {
        Order order = orderRepository.saveAndFlush(Order.builder()
                .description(orderRequest.getDescription())
                .status(Status.CREATED.name())
                .userId(userId)
                .build());
        return OrderMapper.orderToOrderResponse(order);
    }

    public OrderResponse getOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        String.format("Заказа с id = %d не существует", orderId)));
        if(!order.getUserId().equals(userId)) {
            throw new AccessDeniedException("Доступ запрещен");
        }
        return OrderMapper.orderToOrderResponse(order);
    }

    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream().map(OrderMapper::orderToOrderResponse).toList();
    }


    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(OrderMapper::orderToOrderResponse).toList();
    }

    public OrderResponse  updateRole(Long orderId, String status) {
        try {
            Status.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalStatusException(String.format("Статус %s - не вылидный", status));
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        String.format("Заказа с id = %d не существует", orderId)));
        order.setStatus(status);
        order = orderRepository.saveAndFlush(order);
        return OrderMapper.orderToOrderResponse(order);
    }


}
