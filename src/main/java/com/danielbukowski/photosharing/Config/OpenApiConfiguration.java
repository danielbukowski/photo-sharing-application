package com.danielbukowski.photosharing.Config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Daniel Bukowski"
                ),
                title = "Photo Sharing Application",
                description = "OpenApi documentation for Photo Sharing Application",
                version = "0.13.4"
        )
)
@SecuritySchemes(
        value = {
                @SecurityScheme(
                        name = "Basic auth",
                        scheme = "basic",
                        type = SecuritySchemeType.HTTP,
                        in = SecuritySchemeIn.HEADER
                )
        }
)
public class OpenApiConfiguration {
}
