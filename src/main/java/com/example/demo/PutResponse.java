package com.example.demo;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PutResponse {
    private boolean success;
    private String message;
}
