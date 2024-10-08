package org.UserService.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.UserService.customAnnotation.AgeLimit;
import org.UserService.model.User;
import org.Utils.UserIdentifier;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {

    private String name;

    private String email;

    @NotBlank
    private String phoneNo;

    private String address;

    private String password;


    @AgeLimit(minimumAge = 16, message = "your age should be lesser than 16")
    private String dob;

    private UserIdentifier userIdentifier;   //kind of identification from which user wants to  identify

    private String userIdentifierValue;

    public User toUser() {
        return User.builder().
                name(this.name).
                email(this.email).
                phoneNo(this.phoneNo).
                address(this.address).
                password(this.password).
                userIdentifier(this.userIdentifier).
                userIdentifierValue(this.userIdentifierValue).
                build();
    }
}
