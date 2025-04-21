package com.ifellow.bookstore.mapper;

public interface ToDtoMapper<Entity, Dto> {
    public Dto toDto(Entity entity);
}
