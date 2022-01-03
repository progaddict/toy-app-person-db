package com.mymegacorp.persondbapp.personmanagement.control;

import com.mymegacorp.persondbapp.personmanagement.entity.PersonModel;

import java.util.List;
import java.util.function.Function;

public interface PersonParser<T> extends Function<T, List<PersonModel>> {
}
