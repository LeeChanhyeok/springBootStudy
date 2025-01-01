package com.study.HelloWorld.service;

import com.study.HelloWorld.dto.PageRequestDTO;
import com.study.HelloWorld.dto.PageResponseDTO;
import com.study.HelloWorld.dto.TodoDTO;

public interface TodoService {
    Long register(TodoDTO todoDTO);

    TodoDTO get(Long tno);

    void modify(TodoDTO todoDTO);

    void remove(Long tno);

    PageResponseDTO<TodoDTO> list(PageRequestDTO pageRequestDTO);
}
