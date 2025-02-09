package org.flow.orderflow.controller.web;

import lombok.RequiredArgsConstructor;
import org.flow.orderflow.dto.category.CategoryDto;
import org.flow.orderflow.dto.product.ProductDto;
import org.flow.orderflow.service.CategoryService;
import org.flow.orderflow.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductControllerWeb {

  private final ProductService productService;
  private final CategoryService categoryService;


  @GetMapping
  public String listProducts(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "12") int size,
    @RequestParam(defaultValue = "createdAt") String sortBy,
    @RequestParam(defaultValue = "desc") String sortDirection,
    Model model
  ) {
    Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
    Pageable pageable = PageRequest.of(page, size, sort);

    Page<ProductDto> productsPage = productService.getAllProducts(pageable);


    model.addAttribute("products", productsPage.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", productsPage.getTotalPages());
    model.addAttribute("categories", categoryService.getAllCategories());

    return "products/product-list";
  }

  @GetMapping("/add")
  public String showAddProductForm(Model model) {
    model.addAttribute("product", new ProductDto());
    model.addAttribute("categories", categoryService.getAllCategories());
    return "products/product-add";
  }

  @PostMapping("/add")
  public String addProduct(@ModelAttribute ProductDto productDto,
                           RedirectAttributes redirectAttributes) {
    productService.addProduct(productDto);
    redirectAttributes.addFlashAttribute("message", "Product added successfully");
    return "redirect:/products";
  }

  @GetMapping("/edit/{id}")
  public String showEditProductForm(@PathVariable Long id, Model model) {
    ProductDto productDto = productService.getProductById(id);
    List<CategoryDto> categories = categoryService.getAllCategories();

    model.addAttribute("product", productDto);
    model.addAttribute("categories", categories);
    return "products/product-edit";
  }

  @PostMapping("/edit/{id}")
  public String updateProduct(@PathVariable Long id,
                              @ModelAttribute ProductDto productDto,
                              RedirectAttributes redirectAttributes) {
    productService.updateProduct(id, productDto);
    redirectAttributes.addFlashAttribute("message", "Product updated successfully");
    return "redirect:/products";
  }

  @GetMapping("/delete/{id}")
  public String deleteProduct(@PathVariable Long id,
                              RedirectAttributes redirectAttributes) {
    productService.deleteProduct(id);
    redirectAttributes.addFlashAttribute("message", "Product deleted successfully");
    return "redirect:/products";
  }

  @GetMapping("/{id}")
  public String showProductDetails(@PathVariable Long id, Model model) {
    ProductDto productDto = productService.getProductById(id);
    model.addAttribute("product", productDto);
    return "products/product-details";
  }

  @GetMapping("/search")
  public String searchProducts(
    @RequestParam("searchTerm") String searchTerm,
    Model model
  ) {
    List<ProductDto> allProducts = productService.getAllProducts();
    List<ProductDto> filteredProducts = allProducts.stream()
      .filter(product ->
        product.getName().toLowerCase().contains(searchTerm.toLowerCase())
      )
      .collect(Collectors.toList());

    List<CategoryDto> categories = categoryService.getAllCategories();

    model.addAttribute("products", filteredProducts);
    model.addAttribute("categories", categories);
    return "products/product-list :: product-grid";
  }

  @GetMapping("/filter-sort-search")
  public String filterSortAndSearchProducts(
    @RequestParam(required = false) String searchTerm,
    @RequestParam(required = false) Double minPrice,
    @RequestParam(required = false) Double maxPrice,
    @RequestParam(required = false) Boolean inStock,
    @RequestParam(defaultValue = "price") String sortBy,
    @RequestParam(defaultValue = "asc") String sortDirection,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "12") int size,
    Model model
  ) {
    Page<ProductDto> productsPage = productService.filterSortAndSearchProducts(
      searchTerm, minPrice, maxPrice, inStock, sortBy, sortDirection, page, size
    );
    model.addAttribute("products", productsPage.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", productsPage.getTotalPages());
    return "products/product-list :: product-grid";
  }
}




