package com.huy.converter;

import java.util.List;

public abstract class BaseConverter<E, D> {
	public abstract D toDTO(E entity);
	
	public List<D> toListDTO(List<E> listEntity) {
		return listEntity.stream().map(this::toDTO).toList();
	}
}
