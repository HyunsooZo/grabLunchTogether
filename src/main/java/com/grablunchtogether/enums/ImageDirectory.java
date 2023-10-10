package com.grablunchtogether.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ImageDirectory {
    NAME_CARD("name-card"),
    PROFILE("profile");
    private final String string;
}
