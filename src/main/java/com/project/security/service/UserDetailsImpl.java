package com.project.security.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails { //security entity class

        private Long id;
        private String username;
        private String name;
        @JsonIgnore
        private String password;
        private Collection<? extends  GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String username, String name, String password, String role) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.password = password;

        List<GrantedAuthority> grantedAuthorities=new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(role));
        this.authorities=grantedAuthorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() { //eğer username üzerinden kontrol yaparsak bu şekilde yazılmalı ama bazı projelerde mail kullanılır o zaman bu kısım mail olarak güncellenmeli
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean equals(Object o){
        if(this == o){ // bu methodu cagiran nesne ile Parametrede gelen "o" isimli
            // nesneyi karsilastirir, ama Java bu nesnelerin bellekteki referanslarını (adreslerini) karşılaştırır.
            return true;
        }
        //parametre ile gelen nesnenin userDetailsImpl turunden mi kontrolu
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        //UserDetailsImpl sınıfından olduğu doğrulanan nesnenin id özelliğinin
        // mevcut nesnenin id özelliği ile eşit olup olmadığını kontrol eder.
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.getId()); // id ile kiyaslama
    }

}