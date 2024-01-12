package com.example.entity.vo.request;

import lombok.Data;

@Data
public class EditAccountVO {
    int id;
    String username;
    String email;
    String role;
}
