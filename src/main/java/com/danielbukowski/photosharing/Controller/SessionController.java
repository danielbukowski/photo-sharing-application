package com.danielbukowski.photosharing.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/sessions")
public class SessionController {

    @SecurityRequirement(name = "Basic auth")
    @Operation(
            summary = "Create a valid session",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "A valid session has been created"
                    )
            }
    )
    @PostMapping
    public ResponseEntity<?> logIn() {
        return ResponseEntity
                .noContent()
                .build();
    }

    @SecurityRequirement(name = "Basic auth")
    @Operation(
            summary = "Delete a valid session",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "A valid session has been deleted"
                    )
            }
    )
    @DeleteMapping
    public ResponseEntity<?> logOut(HttpServletRequest httpRequest) {
        httpRequest.getSession().invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity
                .noContent()
                .build();
    }

}
