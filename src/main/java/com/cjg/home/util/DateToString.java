package com.cjg.home.util;


import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

@Component
public class DateToString implements Function<LocalDateTime, String> {

    @Override
    public String apply(LocalDateTime localDateTime) {
        return localDateTime == null ? "" : localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
