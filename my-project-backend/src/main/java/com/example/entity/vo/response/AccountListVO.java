package com.example.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class AccountListVO {
    Integer id;
    String username;
    String email;
    String role;
    Date registerTime;

}
