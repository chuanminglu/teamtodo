package com.teamtodo.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for member information response
 */
@Data
public class MemberResponse {
    
    private Long id;
    
    private Long userId;
    
    private String username;
    
    private String email;
    
    private String role;
    
    private LocalDateTime joinedAt;
}
