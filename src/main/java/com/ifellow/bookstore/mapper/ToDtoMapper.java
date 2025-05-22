package com.ifellow.bookstore.mapper;

public interface ToDtoMapper<Entity, Dto> {
    Dto toDto(Entity entity);
}
