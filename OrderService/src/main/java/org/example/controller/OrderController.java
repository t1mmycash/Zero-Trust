package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.OrderRequest;
import org.example.dto.OrderResponse;
import org.example.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody OrderRequest orderRequest, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(orderService.createOrder(Long.parseLong(jwt.getSubject()), orderRequest));
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity
                .ok(orderService.getOrder(orderId, Long.parseLong(jwt.getSubject()), jwt.getClaimAsStringList("roles")));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(Long.parseLong(jwt.getSubject())));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PatchMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long orderId, @RequestBody String status) {
        return ResponseEntity.ok(orderService.updateRole(orderId, status));
    }
}
