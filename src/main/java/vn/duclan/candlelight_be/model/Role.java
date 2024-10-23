package vn.duclan.candlelight_be.model;

import java.util.List;

import lombok.Data;

@Data
public class Role {
    private int roleId;
    private String roleName;
    List<User> userList;
}
