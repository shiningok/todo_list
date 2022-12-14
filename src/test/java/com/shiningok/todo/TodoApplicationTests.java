package com.shiningok.todo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.shiningok.todo.todoList.entity.TodoInfoEntity;
import com.shiningok.todo.todoList.repository.TodoInfoRepository;
import com.shiningok.todo.todoList.service.TodoInfoService;

@SpringBootTest
class TodoApplicationTests {
	//@Autowired TodoInfoService tService;
	@Autowired TodoInfoRepository tRepo;
	@Test
	void leadTodo() throws Exception{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date start = formatter.parse("2022-12-19");
		Date end = formatter.parse("2022-12-29");
		List<TodoInfoEntity> list =tRepo.findByEndDtBetweenAndMiSeq(start, end, 2L);
		for(TodoInfoEntity t : list) {
			System.out.println(t);
		}

	}

}
