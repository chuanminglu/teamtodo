package com.teamtodo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamtodo.dto.AddMemberRequest;
import com.teamtodo.entity.ProjectMember;
import com.teamtodo.security.JwtAuthenticationFilter;
import com.teamtodo.security.JwtTokenService;
import com.teamtodo.security.ProjectAuthorizationService;
import com.teamtodo.security.SecurityConfig;
import com.teamtodo.service.ProjectMemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Base64;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectMemberController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtTokenService.class})
@TestPropertySource(properties = "security.jwt.secret=test-jwt-secret-for-security-tests-123456")
class ProjectMemberSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectMemberService projectMemberService;

    @MockBean
    private ProjectAuthorizationService projectAuthorizationService;

    @Test
    void shouldReturn401WhenNoToken() throws Exception {
        mockMvc.perform(get("/projects/1/members"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void shouldReturn401WhenTokenInvalid() throws Exception {
        mockMvc.perform(get("/projects/1/members")
                        .header("Authorization", "******"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void shouldReturn403WhenNotProjectMember() throws Exception {
        String token = createValidToken(100L);
        doThrow(new AccessDeniedException("not project member"))
                .when(projectAuthorizationService).requireProjectMember(1L, 100L);

        mockMvc.perform(get("/projects/1/members")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    void shouldReturn403WithoutLeakingReasonForAdminAction() throws Exception {
        String token = createValidToken(100L);
        AddMemberRequest request = new AddMemberRequest();
        request.setProjectId(1L);
        request.setUserId(2L);
        request.setRole("MEMBER");

        doNothing().when(projectAuthorizationService).requireProjectMember(1L, 100L);
        doThrow(new AccessDeniedException("only owner can add member"))
                .when(projectAuthorizationService).requireProjectAdmin(1L, 100L);

        mockMvc.perform(post("/projects/members")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    void shouldAllowAdminActionForProjectAdmin() throws Exception {
        String token = createValidToken(100L);
        AddMemberRequest request = new AddMemberRequest();
        request.setProjectId(1L);
        request.setUserId(2L);
        request.setRole("MEMBER");

        ProjectMember member = new ProjectMember();
        member.setId(9L);
        member.setProjectId(1L);
        member.setUserId(2L);
        member.setRole("MEMBER");

        doNothing().when(projectAuthorizationService).requireProjectMember(1L, 100L);
        doNothing().when(projectAuthorizationService).requireProjectAdmin(1L, 100L);
        when(projectMemberService.addMember(any(AddMemberRequest.class))).thenReturn(member);

        mockMvc.perform(post("/projects/members")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(9));
    }

    private String createValidToken(Long userId) throws Exception {
        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        long exp = Instant.now().plusSeconds(3600).getEpochSecond();
        String payloadJson = "{\"userId\":" + userId + ",\"exp\":" + exp + "}";

        String header = Base64.getUrlEncoder().withoutPadding().encodeToString(headerJson.getBytes());
        String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes());
        String signature = sign(header + "." + payload);
        return header + "." + payload + "." + signature;
    }

    private String sign(String content) throws Exception {
        javax.crypto.Mac hmac = javax.crypto.Mac.getInstance("HmacSHA256");
        byte[] secret = "test-jwt-secret-for-security-tests-123456".getBytes();
        hmac.init(new javax.crypto.spec.SecretKeySpec(secret, "HmacSHA256"));
        byte[] signature = hmac.doFinal(content.getBytes());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
    }
}
