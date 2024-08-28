package com.example.int221integratedkk1_backend.Services;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component

public class ListMapper {
    private static final ListMapper listMapper = new ListMapper();
    private ModelMapper modelMapper = new ModelMapper();

    private ListMapper() {
    }

    public <S, T> List<T> mapList(List<S> source, Class<T> targetClass, ModelMapper modelMapper) {
        return source.stream().map(entity -> modelMapper.map(entity, targetClass)).collect(Collectors.toList());
    }

    public <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return mapList(source, targetClass, modelMapper);
    }

    public static ListMapper getInstance() {
        return listMapper;
    }
}