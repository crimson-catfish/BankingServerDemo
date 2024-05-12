package ru.ivanov_savelii.bankingserverdemo.service;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    public static final org.slf4j.Logger logger = LoggerFactory.getLogger(LogService.class);
}
