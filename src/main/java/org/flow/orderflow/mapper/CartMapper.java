// CartMapper.java
package org.flow.orderflow.mapper;

import org.flow.orderflow.dto.cart.CartDto;
import org.flow.orderflow.model.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface CartMapper {
  @Mapping(source = "user.id", target = "userId")
  CartDto toDTO(Cart cart);

  @Mapping(target = "user", ignore = true)
  Cart toEntity(CartDto dto);
}
