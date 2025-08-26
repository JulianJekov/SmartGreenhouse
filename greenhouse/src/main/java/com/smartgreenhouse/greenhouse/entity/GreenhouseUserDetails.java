package com.smartgreenhouse.greenhouse.entity;

import com.smartgreenhouse.greenhouse.enums.Role;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
public class GreenhouseUserDetails extends User {

    private final Long id;
    private final String name;
    private final Role role;


    public GreenhouseUserDetails(String username,
                                 String password,
                                 Collection<? extends GrantedAuthority> authorities,
                                 Long id,
                                 String name,
                                 Role role) {
        super(username, password, authorities);
        this.id = id;
        this.name = name;
        this.role = role;
    }

}
