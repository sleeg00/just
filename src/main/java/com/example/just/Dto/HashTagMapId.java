package com.example.just.Dto;

import java.io.Serializable;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class HashTagMapId implements Serializable {
    private Long post;
    private Long hashTag;
}
