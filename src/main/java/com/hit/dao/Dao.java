package com.hit.dao;

import java.io.Serializable;

public interface Dao <V, T> {
	
	
	T get(V id);
	
	T getByValue(V value);
		
	void save(T t);
	
	void delete(V id);
	
}