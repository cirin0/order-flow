package org.flow.orderflow.mapper;

import org.flow.orderflow.dto.review.ReviewCreate;
import org.flow.orderflow.dto.review.ReviewDto;
import org.flow.orderflow.model.Review;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReviewMapper {
  @Mapping(source = "userLast_name", target = "user.last_name")
  @Mapping(source = "userFirst_name", target = "user.first_name")
  @Mapping(source = "userId", target = "user.id")
  @Mapping(source = "productName", target = "product.name")
  @Mapping(source = "productId", target = "product.id")
  Review toEntity(ReviewDto reviewDto);

  @InheritInverseConfiguration(name = "toEntity")
  ReviewDto toDto(Review review);

  @InheritConfiguration(name = "toEntity")
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Review partialUpdate(ReviewDto reviewDto, @MappingTarget Review review);

  List<ReviewDto> toDtoList(List<Review> reviews);

  @Mapping(source = "productId", target = "product.id")
  @Mapping(source = "userId", target = "user.id")
  Review toEntity(ReviewCreate reviewCreate);

  @Mapping(source = "product.id", target = "productId")
  @Mapping(source = "user.id", target = "userId")
  ReviewCreate toReviewCreateDto(Review review);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(source = "productId", target = "product.id")
  Review partialUpdate(ReviewCreate reviewCreate, @MappingTarget Review review);
}
