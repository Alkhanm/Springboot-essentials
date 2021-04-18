package com.github.alkhanm.exception;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
public class ExceptionDetails {
    protected String title;
    protected Integer status;
    protected String details;
    protected String developerMsg;
    protected LocalDateTime timestamp;
}
