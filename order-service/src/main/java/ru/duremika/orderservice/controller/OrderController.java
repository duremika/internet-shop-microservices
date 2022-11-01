package ru.duremika.orderservice.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.duremika.orderservice.dto.OrderRequest;
import ru.duremika.orderservice.service.OrderService;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    public String placeOrder(@RequestBody OrderRequest orderRequest) {
        service.placeOrder(orderRequest);
        return "Order Placed Successfully";
    }

    @SuppressWarnings("unused")
    public String fallbackMethod(OrderRequest orderRequest, RuntimeException runtimeException){
        return "Something went wrong, please order after some time!";
    }
}
