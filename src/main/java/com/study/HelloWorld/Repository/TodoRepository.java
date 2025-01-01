package com.study.HelloWorld.Repository;


import com.study.HelloWorld.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
}
