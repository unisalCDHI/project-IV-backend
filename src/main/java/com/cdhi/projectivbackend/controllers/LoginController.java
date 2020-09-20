package com.cdhi.projectivbackend.controllers;

import com.cdhi.projectivbackend.dtos.CredentialsDTO;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "Login Controller")
@RestController
@RequestMapping(value = "login")
public class LoginController {

    @ApiOperation("User login")
    @ApiResponses(value = {
            @ApiResponse(code = 200,
                    message = "Response Headers",
                    responseHeaders = {
                            @ResponseHeader(name = "authorization",
                                    description = "Bearer <JWT value here>"),
                            @ResponseHeader(name = "userId",
                                    description = "<Public User Id value here>")
                    })
    })
    @PostMapping
    public void theFakeLogin(@RequestBody CredentialsDTO credentialsDTO) {
        throw new IllegalStateException("This method should not be called. This method is implemented by Spring Security");
    }

}
