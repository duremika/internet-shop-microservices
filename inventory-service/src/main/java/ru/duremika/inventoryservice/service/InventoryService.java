package ru.duremika.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.duremika.inventoryservice.dto.InventoryResponse;
import ru.duremika.inventoryservice.repository.InventoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository repository;

    @Transactional(readOnly = true)
    @SneakyThrows
    public List<InventoryResponse> isInStock(List<String> skuCodes) {
        List<InventoryResponse> foundInventoryResponses = repository.findBySkuCodeIn(skuCodes).stream()
                .map(inventory -> InventoryResponse.builder()
                        .skuCode(inventory.getSkuCode())
                        .isInStock(inventory.getQuantity() > 0)
                        .build())
                .collect(Collectors.toList());
        List<String> foundedSkuCodes = foundInventoryResponses.stream()
                .map(InventoryResponse::getSkuCode)
                .collect(Collectors.toList());
        List<String> unfoundedSkuCodes = skuCodes.stream()
                .filter(s -> !foundedSkuCodes.contains(s))
                .collect(Collectors.toList());
        foundInventoryResponses.addAll(unfoundedSkuCodes.stream()
                .map(s -> InventoryResponse.builder()
                        .skuCode(s)
                        .isInStock(false)
                        .build())
                .collect(Collectors.toList()));
        return foundInventoryResponses;

    }
}
