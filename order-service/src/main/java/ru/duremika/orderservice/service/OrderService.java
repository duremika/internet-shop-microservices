package ru.duremika.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.duremika.orderservice.dto.OrderLineDtoItems;
import ru.duremika.orderservice.dto.OrderRequest;
import ru.duremika.orderservice.model.Order;
import ru.duremika.orderservice.model.OrderLineItems;
import ru.duremika.orderservice.repository.OrderRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;


    public void placeOrder(OrderRequest request){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = request.getOrderLineDtoItemsList()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        order.setOrderLineItemsList(orderLineItems);
        repository.save(order);
    }

    private OrderLineItems mapToDto(OrderLineDtoItems orderLineDtoItems) {
        return OrderLineItems.builder()
                .price(orderLineDtoItems.getPrice())
                .quantity(orderLineDtoItems.getQuantity())
                .skuCode(orderLineDtoItems.getSkuCode()).build();

    }
}
