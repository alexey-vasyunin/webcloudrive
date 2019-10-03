package ru.vasyunin.springcloudrive.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class UserDto {

    private String fistName;
    private String lastName;

    @NotNull(message = "is required")
    @Email
    private String username;

    @NotNull(message = "is required")
    @Size(min = 6, message = "is too short")
    private String password;

    @NotNull(message = "is required")
    @Size(min = 6, message = "is too short")
    private String matchPassword;

}
