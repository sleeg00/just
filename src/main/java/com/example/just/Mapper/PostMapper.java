package com.example.just.Mapper;

import com.example.just.Dao.Post;
import com.example.just.Dto.Post.PutPostDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper extends EntityMapper<PutPostDto, Post>{
}
