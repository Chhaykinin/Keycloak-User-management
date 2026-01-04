package com.company.exception;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FieldError{
    private  String field;
    private  String detail;
}