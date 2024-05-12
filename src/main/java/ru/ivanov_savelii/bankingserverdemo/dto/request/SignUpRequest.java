package ru.ivanov_savelii.bankingserverdemo.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
    String login;
    String password;
}
