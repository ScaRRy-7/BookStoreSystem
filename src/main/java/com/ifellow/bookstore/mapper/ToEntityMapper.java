package com.ifellow.bookstore.mapper;

public interface ToEntityMapper<Dto, Entity> {
    Entity toEntity(Dto dto);
}
