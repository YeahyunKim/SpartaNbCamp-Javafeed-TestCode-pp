package com.sparta.javafeed.dto;

import com.sparta.javafeed.entity.Newsfeed;
import com.sparta.javafeed.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class NewsfeedRequestDto {
    private String title;
    private String description;

    public Newsfeed toEntity(User user) {
        return new Newsfeed(this.title, this.description, user);
    }

    @Builder
    public NewsfeedRequestDto(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
