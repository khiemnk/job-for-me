package com.example.demo;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetListResponse {
        private int pageIndex;
        private int pageSize;
        private int totalPages;
        private int totalItem;
        private List<Data> data;
}
