package ru.duremika.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.duremika.inventoryservice.repository.InventoryRepository;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository repository;

    @Transactional(readOnly = true)
    public boolean isInStock(String skuCode){
        return repository.findBySkuCode(skuCode).isPresent();
    }
}
