package ru.duremika.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.duremika.orderservice.dto.InventoryResponse;
import ru.duremika.orderservice.dto.OrderLineDtoItems;
import ru.duremika.orderservice.dto.OrderRequest;
import ru.duremika.orderservice.event.OrderPlacedEvent;
import ru.duremika.orderservice.model.Order;
import ru.duremika.orderservice.model.OrderLineItems;
import ru.duremika.orderservice.repository.OrderRepository;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {
    private final OrderRepository repository;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;


    @SuppressWarnings("unused")
    public String placeOrder(OrderRequest request) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = request.getOrderLineDtoItemsList()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        order.setOrderLineItemsList(orderLineItems);
        List<String> skuCodes = orderLineItems.stream()
                .map(OrderLineItems::getSkuCode)
                .collect(Collectors.toList());


        Span inventoryServiceLookup = tracer.nextSpan().name("InventoryServiceLookup");
        try (Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookup.start())) {
            Boolean result = isInStock(skuCodes);
            if (result) {
                repository.save(order);
                kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
                return "Order Placed Successfully";
            } else {
                throw new IllegalArgumentException("Product is not in stock, please try again latter.");
            }
        } finally {
            inventoryServiceLookup.end();
        }
    }

    private Boolean isInStock(List<String> skuCodes) {
        WebClient webClient = webClientBuilder.build();
        log.info("Calling inventory service");
        InventoryResponse[] inventoryResponseArray = webClient
                .get().uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .blockOptional()
                .orElse(new InventoryResponse[0]);
        return Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);
    }

    private OrderLineItems mapToDto(OrderLineDtoItems orderLineDtoItems) {
        return OrderLineItems.builder()
                .price(orderLineDtoItems.getPrice())
                .quantity(orderLineDtoItems.getQuantity())
                .skuCode(orderLineDtoItems.getSkuCode()).build();

    }
}
