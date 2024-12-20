package vn.duclan.candlelight_be.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import vn.duclan.candlelight_be.dto.request.RegisterRequest;
import vn.duclan.candlelight_be.dto.request.UpdateInfoRequest;
import vn.duclan.candlelight_be.dto.response.UserResponse;
import vn.duclan.candlelight_be.model.User;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface UserMapper {

    // Ánh xạ ngược từ RegisterRequest sang User
    @Mapping(target = "userId", ignore = true) // skip userId field
    @Mapping(target = "reviewList", ignore = true)
    @Mapping(target = "wishlists", ignore = true)
    @Mapping(target = "orderList", ignore = true)
    @Mapping(target = "roleList", ignore = true)
    @Mapping(target = "reviewCnt", ignore = true)
    User toUser(RegisterRequest request);

    UserResponse toUserResponse(User user);

    // Update field not null
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "reviewList", ignore = true)
    @Mapping(target = "wishlists", ignore = true)
    @Mapping(target = "orderList", ignore = true)
    @Mapping(target = "roleList", ignore = true)
    @Mapping(target = "reviewCnt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

    void updateUser(@MappingTarget User user, UpdateInfoRequest request);
}
