package com.danielbukowski.photosharing.Controller;

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

        @PostMapping
        public ResponseEntity<?> logIn() {
            return ResponseEntity
                    .ok()
                    .build();
        }

        @DeleteMapping
        public ResponseEntity<?> logOut(HttpServletRequest httpRequest) {
            httpRequest.getSession().invalidate();
            SecurityContextHolder.clearContext();
            return ResponseEntity
                    .noContent()
                    .build();
        }

}
