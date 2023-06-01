package com.example.severdemo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ImageDTO {

    private String uuid;
    private String fileName;
    private String fileUrl;
}
