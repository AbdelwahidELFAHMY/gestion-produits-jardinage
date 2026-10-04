package fr.univjardinage.jardinage.controller;

import tools.jackson.databind.ObjectMapper;
import fr.univjardinage.jardinage.dto.CreateProductDTO;
import fr.univjardinage.jardinage.entity.Product;
import fr.univjardinage.jardinage.entity.ProductCategory;
import fr.univjardinage.jardinage.entity.Role;
import fr.univjardinage.jardinage.entity.User;
import fr.univjardinage.jardinage.repository.ProductRepository;
import fr.univjardinage.jardinage.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Tests de securite")
class ProductSecurityTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    private Long productId;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        userRepository.deleteAll();

        User vendor = createUser("vendor1", Role.ROLE_VENDOR);

        createUser("manager1", Role.ROLE_MANAGER);
        createUser("admin", Role.ROLE_ADMIN);
        createUser("user1", Role.ROLE_USER);

        Product product = Product.builder()
                .name("Produit de securite")
                .category(ProductCategory.PLANTE)
                .price(new BigDecimal("10.00"))
                .stock(50)
                .active(true)
                .createdBy(vendor)
                .build();

        productId = productRepository.save(product).getId();
    }
    private User createUser(String username, Role role) {
        User user = User.builder()
                .username(username)
                .password("password")
                .email(username + "@test.com")
                .roles(Set.of(role))
                .enabled(true)
                .department("DEFAULT")
                .build();

        return userRepository.save(user);
    }

    @Test
    @DisplayName("Acces non authentifie doit etre refuse")
    void testUnauthenticatedAccess() throws Exception {

        mockMvc.perform(
                        get("/api/v1/products")
                )
                .andExpect(status().isUnauthorized());
    }


    @Test
    @DisplayName("USER peut lire les produits")
    void testUserCanRead() throws Exception {

        mockMvc.perform(
                        get("/api/v1/products")
                                .with(user("user1").roles("USER"))
                )
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("USER ne peut pas creer de produit")
    void testUserCannotCreate() throws Exception {

        CreateProductDTO dto = CreateProductDTO.builder()
                .name("Produit Test User")
                .category(ProductCategory.PLANTE)
                .price(new BigDecimal("10.00"))
                .stock(50)
                .build();

        mockMvc.perform(
                        post("/api/v1/products")
                                .with(user("user1").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("VENDOR peut creer un produit")
    void testVendorCanCreate() throws Exception {

        CreateProductDTO dto = CreateProductDTO.builder()
                .name("Produit Test Vendor")
                .category(ProductCategory.PLANTE)
                .price(new BigDecimal("10.00"))
                .stock(50)
                .build();

        mockMvc.perform(
                        post("/api/v1/products")
                                .with(user("vendor1").roles("VENDOR"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isCreated());
    }


    @Test
    @DisplayName("MANAGER peut modifier un produit")
    void testManagerCanUpdate() throws Exception {

        mockMvc.perform(
                        put("/api/v1/products/" + productId)
                                .with(user("manager1").roles("MANAGER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("VENDOR ne peut pas supprimer un produit")
    void testVendorCannotDelete() throws Exception {

        mockMvc.perform(
                        delete("/api/v1/products/" + productId)
                                .with(user("vendor1").roles("VENDOR"))
                )
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("ADMIN peut supprimer un produit")
    void testAdminCanDelete() throws Exception {

        mockMvc.perform(
                        delete("/api/v1/products/" + productId)
                                .with(user("admin").roles("ADMIN"))
                )
                .andExpect(status().isNoContent());
    }


    @Test
    @DisplayName("VENDOR peut modifier son propre produit")
    void testVendorCanUpdateOwnProduct() throws Exception {

        mockMvc.perform(
                        put("/api/v1/products/" + productId)
                                .with(user("vendor1").roles("VENDOR"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isOk());
    }
}