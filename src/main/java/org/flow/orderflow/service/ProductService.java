package org.flow.orderflow.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.category.CategoryDto;
import org.flow.orderflow.dto.product.ProductDto;
import org.flow.orderflow.exception.NotFound;
import org.flow.orderflow.mapper.ProductMapper;
import org.flow.orderflow.model.Category;
import org.flow.orderflow.model.Product;
import org.flow.orderflow.repository.CartItemRepository;
import org.flow.orderflow.repository.OrderItemRepository;
import org.flow.orderflow.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
  private final ProductRepository productRepository;
  private final CategoryService categoryService;
  private final ProductMapper productMapper;
  private final CartItemRepository cartItemRepository;
  private final OrderItemRepository orderItemRepository;

  public List<ProductDto> getAllProducts() {
    List<Product> products = productRepository.findAll();
    return productMapper.toDtoList(products);
  }

  public ProductDto getProductById(Long id) {
    return productRepository.findById(id)
      .map(productMapper::toDto)
      .orElseThrow(() -> new NotFound("Product not found with id: " + id));
  }

  public ProductDto getProductByName(String name) {
    Product product = productRepository.findByName(name);
    if (product == null) {
      throw new NotFound("Product not found with name: " + name);
    }
    return productMapper.toDto(product);
  }

  public List<ProductDto> getProductsByCategoryId(Long categoryId) {
    List<Product> products = productRepository.findByCategoryId(categoryId);
    return productMapper.toDtoList(products);
  }

  public List<ProductDto> getProductsByCategoryName(String categoryName) {
    CategoryDto categoryDTO = categoryService.getCategoryByName(categoryName);
    List<Product> products = productRepository.findByCategoryId(categoryDTO.getId());
    return productMapper.toDtoList(products);
  }

  public ProductDto getProductNameById(Long id) {
    return productRepository.findById(id)
      .map(productMapper::toDto)
      .orElseThrow(() -> new NotFound("Product not found with id: " + id));
  }

  public ProductDto addProduct(ProductDto productDto) {
    CategoryDto categoryDTO = null;
    if (productDto.getCategoryId() != null) {
      categoryDTO = categoryService.getCategoryById(productDto.getCategoryId());
    }
    Product product = productMapper.toEntity(productDto);
    if (categoryDTO != null) {
      product.setCategory(Category.builder()
        .id(categoryDTO.getId())
        .name(categoryDTO.getName())
        .build());
    }
    Product savedProduct = productRepository.save(product);
    return productMapper.toDto(savedProduct);
  }

  public ProductDto updateProduct(Long id, ProductDto productDTO) {
    Product product = productRepository.findById(id)
      .orElseThrow(() -> new NotFound("Product not found with id: " + id));
    CategoryDto categoryDTO = categoryService.getCategoryById(productDTO.getCategoryId());
    Product updatedProduct = productMapper.partialUpdate(productDTO, product);
    product.setCategory(Category.builder()
      .id(categoryDTO.getId())
      .name(categoryDTO.getName())
      .build());
    return productMapper.toDto(productRepository.save(updatedProduct));
  }

  @Transactional
  public void deleteProduct(Long id) {
    Product product = productRepository.findById(id)
      .orElseThrow(() -> new NotFound("Product not found with id: " + id));
    cartItemRepository.deleteAllByProductId(id);
    orderItemRepository.deleteAllByProductId(id);
    productRepository.delete(product);
  }

  public List<ProductDto> searchProducts(String query, int size) {
    Pageable pageable = PageRequest.of(0, size);
    return productRepository.searchByNameContaining(query, pageable).stream()
      .map(productMapper::toDto)
      .collect(Collectors.toList());
  }

  public Page<ProductDto> getAllProducts(Pageable pageable) {
    if (!pageable.getSort().isSorted()) {
      pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdAt").descending());
    }
    Page<Product> products = productRepository.findAll(pageable);
    return products.map(productMapper::toDto);
  }

  public Page<ProductDto> filterSortAndSearchProducts(
    String searchTerm,
    Double minPrice,
    Double maxPrice,
    Boolean inStock,
    String sortBy,
    String sortDirection,
    int page,
    int size
  ) {
    Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
    Pageable pageable = PageRequest.of(page, size, sort);

    Page<Product> productsPage = productRepository
      .filterSortAndSearchProducts(searchTerm, minPrice, maxPrice, inStock, pageable);
    return productsPage.map(productMapper::toDto);
  }

  public int getProductStock(Long productId) {
    Product product = productRepository.findById(productId)
      .orElseThrow(() -> new NotFound("Product not found with id: " + productId));
    return product.getStock();
  }

}
