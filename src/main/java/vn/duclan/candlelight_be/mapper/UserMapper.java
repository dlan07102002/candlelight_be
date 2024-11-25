package vn.duclan.candlelight_be.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import vn.duclan.candlelight_be.dto.request.RegisterRequest;
import vn.duclan.candlelight_be.dto.request.UpdateInfoRequest;
import vn.duclan.candlelight_be.dto.response.UserResponse;
import vn.duclan.candlelight_be.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Ánh xạ ngược từ RegisterRequest sang User
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "reviewList", ignore = true) // Không ánh xạ reviewList
    @Mapping(target = "wishlists", ignore = true) // Không ánh xạ wishlists
    @Mapping(target = "orderList", ignore = true) // Không ánh xạ orderList
    @Mapping(target = "roleList", ignore = true) // Không ánh xạ roleList
    User toUser(RegisterRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "reviewList", ignore = true) // Không ánh xạ reviewList
    @Mapping(target = "wishlists", ignore = true) // Không ánh xạ wishlists
    @Mapping(target = "orderList", ignore = true) // Không ánh xạ orderList
    @Mapping(target = "roleList", ignore = true)
    // Update field not null
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(@MappingTarget User user, UpdateInfoRequest request);

}
