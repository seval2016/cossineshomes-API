package com.project.payload.request.user;

import com.project.payload.request.abstracts.BaseUserRequest;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserSaveRequest extends BaseUserRequest {


    private Set<String> roles;


    private Set<Long> tourRequestIdList;


    private Set<Long> advertIdList;


    private Set<Long>tourRequests;

    @NotNull(message = "Please select builtIn value")
    private Boolean builtIn;


    private List<Long> favoritesList;

}
