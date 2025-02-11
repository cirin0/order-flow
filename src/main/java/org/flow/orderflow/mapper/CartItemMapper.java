package org.flow.orderflow.mapper;

import org.flow.orderflow.dto.cart.CartItemDto;
import org.flow.orderflow.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
  @Mapping(source = "product.id", target = "productId")
  @Mapping(source = "product.name", target = "productName")
  @Mapping(source = "product.price", target = "price")
  @Mapping(source = "product.stock", target = "stockQuantity")
  CartItemDto toDTO(CartItem cartItem);

  @Mapping(target = "product", ignore = true)
  @Mapping(target = "cart", ignore = true)
  CartItem toEntity(CartItemDto dto);
}
