package org.flow.orderflow.mapper;

import org.flow.orderflow.dto.product.ProductDto;
import org.flow.orderflow.model.Product;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

  Product toEntity(ProductDto productDTO);

  @Mapping(target = "categoryId", source = "category.id")
  @Mapping(target = "categoryName", source = "category.name")
  ProductDto toDto(Product product);

  List<ProductDto> toDtoList(List<Product> products);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Product partialUpdate(ProductDto productDTO, @MappingTarget Product product);
}
