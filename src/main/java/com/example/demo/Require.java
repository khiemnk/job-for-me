package com.example.demo;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Require {
    private String title;
    private String description;
    private String category;
    private List<String> keywords;
    private int words;
    private LocalDateTime expiresDate;
    private int status;
}
