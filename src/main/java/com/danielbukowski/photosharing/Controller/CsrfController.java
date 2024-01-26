package com.danielbukowski.photosharing.Controller;

import com.danielbukowski.photosharing.Dto.SimpleDataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/csrf")
public class CsrfController {

    @Operation(
            summary = "Returns a csrf token",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "A list of images in form of ids have been returned"
                    )
            }
    )
    @GetMapping
    public SimpleDataResponse<CsrfToken> getCsrfToken(CsrfToken csrfToken) {
        return new SimpleDataResponse<>(csrfToken);
    }

}
