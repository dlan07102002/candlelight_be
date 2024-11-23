package vn.duclan.candlelight_be.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.duclan.candlelight_be.dto.request.RegisterRequest;
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

}
