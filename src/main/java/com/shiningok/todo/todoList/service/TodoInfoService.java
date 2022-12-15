package com.shiningok.todo.todoList.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.shiningok.todo.member.entity.MemberInfoEntity;
import com.shiningok.todo.todoList.entity.TodoImageEntity;
import com.shiningok.todo.todoList.entity.TodoInfoEntity;
import com.shiningok.todo.todoList.repository.TodoImageRepository;
import com.shiningok.todo.todoList.repository.TodoInfoRepository;

@Service
public class TodoInfoService {
  @Autowired TodoInfoRepository tRepo;
  @Autowired TodoImageRepository tiRepo;
  public Map <String, Object> addTodoImage(TodoImageEntity data, Long tiSeq) {
    Map<String, Object> resultMap = new LinkedHashMap<String , Object>();
    data.setTiSeq(tiSeq);
    tiRepo.save(data);
    resultMap.put("status", true);
    resultMap.put("message", "이미지 저장 완");
    resultMap.put("code", HttpStatus.OK);
    return resultMap;
  }
public String getFilenameByUri(String uri) {
   List<TodoImageEntity> data = tiRepo.findTopByUriOrderBySeqDesc(uri);
  return data.get(0).getFileName();
}



  public Map<String, Object> addTodoInfo(TodoInfoEntity data , HttpSession session) {
    Map<String, Object> resultMap = new LinkedHashMap<String , Object>();
    MemberInfoEntity loginUser = (MemberInfoEntity)session.getAttribute("loginUser");
    if (loginUser == null) {
      resultMap.put("status", false);
      resultMap.put("message", "로그인 낸하");
      resultMap.put("code", HttpStatus.FORBIDDEN);
    }
    else {
      data.setMiSeq(loginUser.getSeq());
      tRepo.save(data);
      resultMap.put("status", true);
      resultMap.put("message", "일정 추가 완");
      resultMap.put("code", HttpStatus.CREATED);
    }
    return resultMap;
  } 

  public Map<String, Object> getTodoList(HttpSession session) {
    Map<String, Object> resultMap = new LinkedHashMap<String,Object>();
    MemberInfoEntity loginUser = (MemberInfoEntity)session.getAttribute("loginUser");
     if (loginUser == null) {
      resultMap.put("status", false);
      resultMap.put("message", "로그인 낸하");
      resultMap.put("code", HttpStatus.FORBIDDEN);
    }
    else {
      resultMap.put("status", true);
      resultMap.put("message", "조회 완");
      resultMap.put("code", HttpStatus.OK);
      resultMap.put("list", tRepo.findAllByMiSeq(loginUser.getSeq()));
    }
    
    return resultMap;
  }

  public Map<String, Object> updateTodoStatus (Integer status, Long seq) {
     Map<String, Object> resultMap = new LinkedHashMap<String,Object>();
     TodoInfoEntity todo = tRepo.findBySeq(seq);
     if(todo == null){
      resultMap.put("status", false);
      resultMap.put("message", "Todo 번호 잘못됨");
      resultMap.put("code", HttpStatus.FORBIDDEN);
    }
      else {
      todo.setStatus(status);
      tRepo.save(todo);
      resultMap.put("status", true);
      resultMap.put("message", "Todo 상태 변경 완.");
      resultMap.put("code", HttpStatus.OK);
    }
     return resultMap;


  }
  public Map<String, Object> updateTodoContent(String content, Long seq) {
     Map<String, Object> resultMap = new LinkedHashMap<String,Object>();
      TodoInfoEntity todo = tRepo.findBySeq(seq);
     if(todo == null){
      resultMap.put("status", false);
      resultMap.put("message", "Todo 번호 잘못됨");
      resultMap.put("code", HttpStatus.FORBIDDEN);
    }
    else {
      todo.setContent(content);
      tRepo.save(todo);
      resultMap.put("status", true);
      resultMap.put("message", "Todo 내용 변경 완.");
      resultMap.put("code", HttpStatus.OK);
    }
     return resultMap;
  }
  @Transactional
  public Map<String, Object> deleteTodo(Long seq, HttpSession session) {
    Map<String, Object> resultMap = new LinkedHashMap<String,Object>();
    MemberInfoEntity loginUser = (MemberInfoEntity)session.getAttribute("loginUser");
    if(loginUser == null) {
        resultMap.put("status", false);
      resultMap.put("message", "로그인을 먼저 해주세ㅐ요");
      resultMap.put("code", HttpStatus.FORBIDDEN);
      return resultMap;

    }

    TodoInfoEntity todo = tRepo.findBySeqAndMiSeq(seq, loginUser.getSeq());
    if(todo == null) {
      resultMap.put("status", false);
      resultMap.put("message", "Todo 번호 잘못됨 아니면 권한 없음");
      resultMap.put("code", HttpStatus.FORBIDDEN);

    }
    else {

    tRepo.deleteBySeqAndMiSeq(seq, loginUser.getSeq());
      resultMap.put("status", true);
      resultMap.put("message", "todo 삭제 완");
      resultMap.put("code", HttpStatus.OK);

    }

    return resultMap;
  }
  public Map<String, Object> selectTodoListByTerm(
    HttpSession session, String start, String end
  ){
    Map<String, Object> resultMap = new LinkedHashMap<String,Object>();
    MemberInfoEntity loginUser = (MemberInfoEntity)session.getAttribute("loginUser");
    if(loginUser == null) {
      resultMap.put("status", false);
      resultMap.put("message", "로그인을 먼저 해주세ㅐ요");
      resultMap.put("code", HttpStatus.FORBIDDEN);
      return resultMap;

    }
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    Date startDt = null;
    Date endDt = null;
    try {
      startDt = formatter.parse(start);
      endDt = formatter.parse(end);
    }
    catch(Exception e) {
      resultMap.put("status", false);
      resultMap.put("message", "날짜 형식 확인 바람 (yyyMMdd ex:19950918)");
      resultMap.put("code", HttpStatus.BAD_REQUEST);
      return resultMap;
    }
    resultMap.put("status", true);
    resultMap.put("message", "조회 완");
    resultMap.put("list", tRepo.findByEndDtBetweenAndMiSeq(startDt, endDt, loginUser.getSeq()));
    resultMap.put("code", HttpStatus.OK);
    return resultMap; 

    
  
}
}
