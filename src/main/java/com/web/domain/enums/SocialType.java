package com.web.domain.enums;

public enum SocialType {
    FACEBOOK("facebook"),
    GOOGLE("google"),
    KAKAO("kakao");


    /**
     * ROLE_FACEBOOK
     * ROLE_GOOGLE
     * ROLE_KAKAO
     **/
    private final String ROLE_PREFIX = "ROLE_";
    private String name;

    SocialType(String name){
        this.name = name;
    }

    public String getRoleType() {
        return ROLE_PREFIX + name.toUpperCase();
    }

    public String getValue(){
        return name;
    }

    public boolean isEques(String authority){
        return this.getRoleType().equals(authority);
    }
}
