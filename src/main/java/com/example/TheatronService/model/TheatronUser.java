package com.example.TheatronService.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.Collection;
import java.util.List;

@ToString
@Getter
@Setter
@DynamoDbBean
public class TheatronUser implements UserDetails {

    private String username;
    private String email;
    private String name;
    private String password;
    private String currentRoom;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @DynamoDbPartitionKey
    public String getUsername() {
        return username;
    }

//    @Override
//    public boolean isAccountNonExpired() {
//        return UserDetails.super.isAccountNonExpired();
//    }

//    @Override
//    public boolean isAccountNonLocked() {
//        return UserDetails.super.isAccountNonLocked();
//    }

//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
////        return UserDetails.super.isCredentialsNonExpired();
//    }

//    @Override
//    public boolean isEnabled() {
//        return true;
////        return UserDetails.super.isEnabled();
//    }

    @DynamoDbSecondaryPartitionKey(indexNames = "UserEmailIndex")
    public String getEmail() {
        return email;
    }
}
