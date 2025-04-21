package com.ifellow.bookstore.mapper;

public interface ToEntityMapper<Dto, Entity> {
    public Entity toEntity(Dto dto);
}
