package ru.ivanov_savelii.bankingserverdemo.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SendMoneyRequest {
    String to;
    BigDecimal amount;
}
