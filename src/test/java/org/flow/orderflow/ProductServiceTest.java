package org.flow.orderflow;

import org.flow.orderflow.dto.category.CategoryDto;
import org.flow.orderflow.dto.product.ProductDto;
import org.flow.orderflow.exception.NotFound;
import org.flow.orderflow.mapper.ProductMapper;
import org.flow.orderflow.model.Category;
import org.flow.orderflow.model.Product;
import org.flow.orderflow.repository.CartItemRepository;
import org.flow.orderflow.repository.OrderItemRepository;
import org.flow.orderflow.repository.ProductRepository;
import org.flow.orderflow.service.CategoryService;
import org.flow.orderflow.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
  @Mock
  private ProductRepository productRepository;

  @Mock
  private CategoryService categoryService;

  @Mock
  private ProductMapper productMapper;

  @Mock
  private CartItemRepository cartItemRepository;

  @Mock
  private OrderItemRepository orderItemRepository;

  @InjectMocks
  private ProductService productService;

  private Product product;
  private ProductDto productDto;
  private CategoryDto categoryDto;

  @BeforeEach
  void setUp() {
    // Initialize test data
    Category category = Category.builder()
      .id(1L)
      .name("Electronics")
      .build();

    categoryDto = CategoryDto.builder()
      .id(1L)
      .name("Electronics")
      .build();

    product = Product.builder()
      .id(1L)
      .name("Laptop")
      .description("High performance laptop")
      .price(1200.00)
      .stock(10)
      .category(category)
      .build();

    productDto = ProductDto.builder()
      .id(1L)
      .name("Laptop")
      .description("High performance laptop")
      .price(1200.00)
      .stock(10)
      .categoryId(1L)
      .build();
  }

  @Test
  void getAllProducts_shouldReturnAllProducts() {
    // Arrange
    List<Product> products = Collections.singletonList(product);
    List<ProductDto> expectedDtos = Collections.singletonList(productDto);

    when(productRepository.findAll()).thenReturn(products);
    when(productMapper.toDtoList(products)).thenReturn(expectedDtos);

    // Act
    List<ProductDto> actualDtos = productService.getAllProducts();

    // Assert
    assertThat(actualDtos).isEqualTo(expectedDtos);
    verify(productRepository).findAll();
    verify(productMapper).toDtoList(products);
  }

  @Test
  void getProductById_whenProductExists_shouldReturnProduct() {
    // Arrange
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(productMapper.toDto(product)).thenReturn(productDto);

    // Act
    ProductDto result = productService.getProductById(1L);

    // Assert
    assertThat(result).isEqualTo(productDto);
    verify(productRepository).findById(1L);
    verify(productMapper).toDto(product);
  }

  @Test
  void getProductById_whenProductDoesNotExist_shouldThrowNotFound() {
    // Arrange
    when(productRepository.findById(999L)).thenReturn(Optional.empty());

    // Act & Assert
    NotFound exception = assertThrows(NotFound.class, () ->
      productService.getProductById(999L)
    );

    assertThat(exception.getMessage()).contains("Product not found with id: 999");
    verify(productRepository).findById(999L);
  }

  @Test
  void getProductByName_whenProductExists_shouldReturnProduct() {
    // Arrange
    String name = "Laptop";
    when(productRepository.findByName(name)).thenReturn(product);
    when(productMapper.toDto(product)).thenReturn(productDto);

    // Act
    ProductDto result = productService.getProductByName(name);

    // Assert
    assertThat(result).isEqualTo(productDto);
    verify(productRepository).findByName(name);
    verify(productMapper).toDto(product);
  }

  @Test
  void getProductByName_whenProductDoesNotExist_shouldThrowNotFound() {
    // Arrange
    String name = "NonExistentProduct";
    when(productRepository.findByName(name)).thenReturn(null);

    // Act & Assert
    NotFound exception = assertThrows(NotFound.class, () ->
      productService.getProductByName(name)
    );

    assertThat(exception.getMessage()).contains("Product not found with name: " + name);
    verify(productRepository).findByName(name);
  }

  @Test
  void getProductsByCategoryId_shouldReturnProductsInCategory() {
    // Arrange
    Long categoryId = 1L;
    List<Product> products = Collections.singletonList(product);
    List<ProductDto> expectedDtos = Collections.singletonList(productDto);

    when(productRepository.findByCategoryId(categoryId)).thenReturn(products);
    when(productMapper.toDtoList(products)).thenReturn(expectedDtos);

    // Act
    List<ProductDto> result = productService.getProductsByCategoryId(categoryId);

    // Assert
    assertThat(result).isEqualTo(expectedDtos);
    verify(productRepository).findByCategoryId(categoryId);
    verify(productMapper).toDtoList(products);
  }

  @Test
  void getProductsByCategoryName_shouldReturnProductsInCategory() {
    // Arrange
    String categoryName = "Electronics";
    List<Product> products = Collections.singletonList(product);
    List<ProductDto> expectedDtos = Collections.singletonList(productDto);

    when(categoryService.getCategoryByName(categoryName)).thenReturn(categoryDto);
    when(productRepository.findByCategoryId(categoryDto.getId())).thenReturn(products);
    when(productMapper.toDtoList(products)).thenReturn(expectedDtos);

    // Act
    List<ProductDto> result = productService.getProductsByCategoryName(categoryName);

    // Assert
    assertThat(result).isEqualTo(expectedDtos);
    verify(categoryService).getCategoryByName(categoryName);
    verify(productRepository).findByCategoryId(categoryDto.getId());
    verify(productMapper).toDtoList(products);
  }

  @Test
  void addProduct_withCategory_shouldSaveAndReturnProduct() {
    // Arrange
    when(categoryService.getCategoryById(productDto.getCategoryId())).thenReturn(categoryDto);
    when(productMapper.toEntity(productDto)).thenReturn(product);
    when(productRepository.save(any(Product.class))).thenReturn(product);
    when(productMapper.toDto(product)).thenReturn(productDto);

    // Act
    ProductDto result = productService.addProduct(productDto);

    // Assert
    assertThat(result).isEqualTo(productDto);
    verify(categoryService).getCategoryById(productDto.getCategoryId());
    verify(productMapper).toEntity(productDto);
    verify(productRepository).save(any(Product.class));
    verify(productMapper).toDto(product);
  }

  @Test
  void updateProduct_whenProductExists_shouldUpdateAndReturnProduct() {
    // Arrange
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    when(categoryService.getCategoryById(productDto.getCategoryId())).thenReturn(categoryDto);
    when(productMapper.partialUpdate(eq(productDto), any(Product.class))).thenReturn(product);
    when(productRepository.save(any(Product.class))).thenReturn(product);
    when(productMapper.toDto(product)).thenReturn(productDto);

    // Act
    ProductDto result = productService.updateProduct(1L, productDto);

    // Assert
    assertThat(result).isEqualTo(productDto);
    verify(productRepository).findById(1L);
    verify(categoryService).getCategoryById(productDto.getCategoryId());
    verify(productMapper).partialUpdate(eq(productDto), any(Product.class));
    verify(productRepository).save(any(Product.class));
    verify(productMapper).toDto(product);
  }

  @Test
  void updateProduct_whenProductDoesNotExist_shouldThrowNotFound() {
    // Arrange
    when(productRepository.findById(999L)).thenReturn(Optional.empty());

    // Act & Assert
    NotFound exception = assertThrows(NotFound.class, () ->
      productService.updateProduct(999L, productDto)
    );

    assertThat(exception.getMessage()).contains("Product not found with id: 999");
    verify(productRepository).findById(999L);
    verifyNoInteractions(productMapper);
  }

  @Test
  void deleteProduct_whenProductExists_shouldDeleteProduct() {
    // Arrange
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    // Act
    productService.deleteProduct(1L);

    // Assert
    verify(productRepository).findById(1L);
    verify(cartItemRepository).deleteAllByProductId(1L);
    verify(orderItemRepository).deleteAllByProductId(1L);
    verify(productRepository).delete(product);
  }

  @Test
  void deleteProduct_whenProductDoesNotExist_shouldThrowNotFound() {
    // Arrange
    when(productRepository.findById(999L)).thenReturn(Optional.empty());

    // Act & Assert
    NotFound exception = assertThrows(NotFound.class, () ->
      productService.deleteProduct(999L)
    );

    assertThat(exception.getMessage()).contains("Product not found with id: 999");
    verify(productRepository).findById(999L);
    verifyNoInteractions(cartItemRepository);
    verifyNoInteractions(orderItemRepository);
  }

  @Test
  void searchProducts_shouldReturnMatchingProducts() {
    // Arrange
    String query = "laptop";
    int size = 10;
    Pageable pageable = PageRequest.of(0, size);
    List<Product> products = Collections.singletonList(product);
    List<ProductDto> expectedDtos = Collections.singletonList(productDto);

    when(productRepository.searchByNameContaining(query, pageable)).thenReturn(products);
    when(productMapper.toDto(product)).thenReturn(productDto);

    // Act
    List<ProductDto> result = productService.searchProducts(query, size);

    // Assert
    assertThat(result).isEqualTo(expectedDtos);
    verify(productRepository).searchByNameContaining(query, pageable);
  }

  @Test
  void getAllProducts_withPageable_shouldReturnPageOfProducts() {
    // Arrange
    Pageable pageable = PageRequest.of(0, 10);
    Page<Product> productPage = new PageImpl<>(Collections.singletonList(product), pageable, 1);

    when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);
    when(productMapper.toDto(product)).thenReturn(productDto);

    // Act
    Page<ProductDto> result = productService.getAllProducts(pageable);

    // Assert
    assertThat(result.getContent()).containsExactly(productDto);
    assertThat(result.getTotalElements()).isEqualTo(1);
    verify(productRepository).findAll(any(Pageable.class));
  }

  @Test
  void filterSortAndSearchProducts_shouldReturnFilteredProducts() {
    // Arrange
    String searchTerm = "laptop";
    Double minPrice = 1000.0;
    Double maxPrice = 2000.0;
    Boolean inStock = true;
    String sortBy = "price";
    String sortDirection = "ASC";
    int page = 0;
    int size = 10;

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));
    Page<Product> productPage = new PageImpl<>(Collections.singletonList(product), pageable, 1);

    when(productRepository.filterSortAndSearchProducts(
      searchTerm, minPrice, maxPrice, inStock, pageable))
      .thenReturn(productPage);
    when(productMapper.toDto(product)).thenReturn(productDto);

    // Act
    Page<ProductDto> result = productService.filterSortAndSearchProducts(
      searchTerm, minPrice, maxPrice, inStock, sortBy, sortDirection, page, size);

    // Assert
    assertThat(result.getContent()).containsExactly(productDto);
    assertThat(result.getTotalElements()).isEqualTo(1);
    verify(productRepository).filterSortAndSearchProducts(
      searchTerm, minPrice, maxPrice, inStock, pageable);
  }

  @Test
  void getProductStock_whenProductExists_shouldReturnStock() {
    // Arrange
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    // Act
    int stock = productService.getProductStock(1L);

    // Assert
    assertThat(stock).isEqualTo(10);
    verify(productRepository).findById(1L);
  }

  @Test
  void getProductStock_whenProductDoesNotExist_shouldThrowNotFound() {
    // Arrange
    when(productRepository.findById(999L)).thenReturn(Optional.empty());

    // Act & Assert
    NotFound exception = assertThrows(NotFound.class, () ->
      productService.getProductStock(999L)
    );

    assertThat(exception.getMessage()).contains("Product not found with id: 999");
    verify(productRepository).findById(999L);
  }
}
