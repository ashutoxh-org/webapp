package com.csye6225.cloud.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishResponseDTO {
    private String email;

    @Override
    public String toString() {
        return "{" +
                "\"email\":\"" + email +
                "\"}";
    }
}
