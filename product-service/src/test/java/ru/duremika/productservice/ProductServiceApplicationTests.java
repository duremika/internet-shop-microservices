package ru.duremika.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import ru.duremika.productservice.dto.ProductRequest;
import ru.duremika.productservice.model.Product;
import ru.duremika.productservice.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

    @ClassRule
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.14-rc0-focal");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository repository;


    @BeforeAll
    public static void before(){
        mongoDBContainer.start();
        System.out.println(mongoDBContainer.getReplicaSetUrl());
        System.setProperty("spring.data.mongodb.uri", mongoDBContainer.getReplicaSetUrl());
    }


    @AfterAll
    public static void after(){
        mongoDBContainer.stop();
    }

    @Test
    void shouldCreateProduct() throws Exception {
        ProductRequest request = getProductRequest();
        final String productRequestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        final List<Product> products = repository.findAll();
        Assertions.assertEquals(1, products.size());

    }

    private ProductRequest getProductRequest() {
        return ProductRequest.builder()
                .name("iPhone 13")
                .description("iPhone 13")
                .price(BigDecimal.valueOf(1_200))
                .build();
    }

}
