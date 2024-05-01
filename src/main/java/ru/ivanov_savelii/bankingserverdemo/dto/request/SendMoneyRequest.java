package ru.ivanov_savelii.bankingserverdemo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendMoneyRequest {
    String to;
    BigDecimal amount;
}
