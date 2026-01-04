package com.company.exception;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class ErrorResponse<T>{
    private int statusCode;
    private T message;
}