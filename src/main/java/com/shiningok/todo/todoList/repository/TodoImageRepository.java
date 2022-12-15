package com.shiningok.todo.todoList.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shiningok.todo.todoList.entity.TodoImageEntity;

@Repository
public interface TodoImageRepository extends JpaRepository <TodoImageEntity , Long> {
    public List<TodoImageEntity> findByTiSeq(Long tiSeq);
    // select * from todo_images_info where tii_uri = uri order by tii_seq desc limit 1
    public List<TodoImageEntity> findTopByUriOrderBySeqDesc(String uri);
}
