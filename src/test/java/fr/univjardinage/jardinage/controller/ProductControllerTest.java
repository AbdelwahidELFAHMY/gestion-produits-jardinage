package fr.univjardinage.jardinage.controller;

import tools.jackson.databind.ObjectMapper;

import fr.univjardinage.jardinage.dto.CreateProductDTO;
import fr.univjardinage.jardinage.dto.ProductDTO;
import fr.univjardinage.jardinage.entity.ProductCategory;
import fr.univjardinage.jardinage.exception.ProductNotFoundException;
import fr.univjardinage.jardinage.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProductController.class)
@DisplayName("Tests du ProductController")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;


    @Test
    @DisplayName("GET /api/v1/products doit retourner tous les produits")
    void testGetAllProducts() throws Exception {

        // Given
        List<ProductDTO> products = Arrays.asList(
                ProductDTO.builder()
                        .id(1L)
                        .name("Rosier")
                        .price(new BigDecimal("15.99"))
                        .build(),

                ProductDTO.builder()
                        .id(2L)
                        .name("Tondeuse")
                        .price(new BigDecimal("299.99"))
                        .build()
        );

        when(productService.getAllActiveProducts()).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Rosier")))
                .andExpect(jsonPath("$[1].name", is("Tondeuse")));
    }


    @Test
    @DisplayName("GET /api/v1/products/{id} doit retourner un produit")
    void testGetProductById() throws Exception {

        // Given
        ProductDTO product = ProductDTO.builder()
                .id(1L)
                .name("Rosier")
                .category(ProductCategory.PLANTE)
                .price(new BigDecimal("15.99"))
                .stock(50)
                .active(true)
                .build();

        when(productService.getProductById(1L)).thenReturn(product);

        // When & Then
        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Rosier")))
                .andExpect(jsonPath("$.price", is(15.99)));
    }


    @Test
    @DisplayName("GET /api/v1/products/{id} doit retourner 404 si produit non trouvé")
    void testGetProductById_NotFound() throws Exception {

        // Given
        when(productService.getProductById(999L))
                .thenThrow(new ProductNotFoundException(999L));

        // When & Then
        mockMvc.perform(get("/api/v1/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("PRODUCT_NOT_FOUND")));
    }


    @Test
    @DisplayName("POST /api/v1/products doit créer un produit")
    void testCreateProduct() throws Exception {

        // Given
        CreateProductDTO createDto = CreateProductDTO.builder()
                .name("Nouveau Produit")
                .category(ProductCategory.OUTIL)
                .price(new BigDecimal("25.00"))
                .stock(100)
                .build();

        ProductDTO createdProduct = ProductDTO.builder()
                .id(1L)
                .name("Nouveau Produit")
                .category(ProductCategory.OUTIL)
                .price(new BigDecimal("25.00"))
                .stock(100)
                .active(true)
                .build();

        when(productService.createProduct(any()))
                .thenReturn(createdProduct);

        // When & Then
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Nouveau Produit")));
    }


    @Test
    @DisplayName("PUT /api/v1/products/{id} doit mettre à jour un produit")
    void testUpdateProduct() throws Exception {

        // Given
        ProductDTO updateDto = ProductDTO.builder()
                .name("Produit Modifie")
                .category(ProductCategory.PLANTE)
                .price(new BigDecimal("20.00"))
                .stock(30)
                .active(true)
                .build();

        when(productService.updateProduct(anyLong(), any()))
                .thenReturn(updateDto);

        // When & Then
        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Produit Modifie")));
    }


    @Test
    @DisplayName("DELETE /api/v1/products/{id} doit supprimer un produit")
    void testDeleteProduct() throws Exception {

        // When & Then
        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isNoContent());
    }


    @Test
    @DisplayName("PATCH /api/v1/products/{id}/stock doit ajuster le stock")
    void testAdjustStock() throws Exception {

        // Given
        ProductDTO product = ProductDTO.builder()
                .id(1L)
                .name("Rosier")
                .stock(60)
                .build();

        when(productService.adjustStock(1L, 10))
                .thenReturn(product);

        // When & Then
        mockMvc.perform(patch("/api/v1/products/1/stock")
                        .param("quantity", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock", is(60)));
    }
}