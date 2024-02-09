package com.proselyte.fakepaymentprovider.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;

@Setter
@Getter
@EqualsAndHashCode(callSuper=true)
public class SecurityUserDetails extends User {

    private UUID id;

    public SecurityUserDetails(String clientId, String clientSecret, Collection<? extends GrantedAuthority> authorities, UUID id) {
        super(clientId, clientSecret, authorities);
        this.id = id;
    }
}