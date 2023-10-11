package com.danielbukowski.photosharing.Controller;

import com.danielbukowski.photosharing.Dto.SimpleDataResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/csrf")
public class CsrfController {

    @GetMapping
    public SimpleDataResponse<CsrfToken> getCsrfToken(CsrfToken csrfToken) {
        return new SimpleDataResponse<>(csrfToken);
    }

}
