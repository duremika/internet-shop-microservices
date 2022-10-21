package ru.duremika.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;
import ru.duremika.productservice.dto.ProductRequest;
import ru.duremika.productservice.model.Product;
import ru.duremika.productservice.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

    final static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.4.14-rc0-focal"))
            .withExposedPorts(27017);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository repository;


    @DynamicPropertySource
    static void mongoDbProperties(DynamicPropertyRegistry props) {
        mongoDBContainer.start();
        props.add("spring.data.mongodb.uri", () -> "mongodb://" + mongoDBContainer.getHost() + ":"
                + mongoDBContainer.getFirstMappedPort() + "/testdb");
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
        Assertions.assertFalse( products.isEmpty());

    }

    private ProductRequest getProductRequest() {
        return ProductRequest.builder()
                .name("iPhone 13")
                .description("iPhone 13")
                .price(BigDecimal.valueOf(1_200))
                .build();
    }

}
