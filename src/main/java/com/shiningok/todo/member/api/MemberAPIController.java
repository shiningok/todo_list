package com.shiningok.todo.member.api;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shiningok.todo.member.data.LoginVO;
import com.shiningok.todo.member.entity.MemberInfoEntity;
import com.shiningok.todo.member.service.MemberService;


@RestController
@RequestMapping("/api/member")
public class MemberAPIController {
    @Autowired MemberService mService;
    @PutMapping("/join")
    public ResponseEntity<Object> memberJoin(@RequestBody MemberInfoEntity data){
      Map<String,Object> resulMap = mService.addMember(data);
      return new ResponseEntity<Object>(resulMap, (HttpStatus)resulMap.get("code"));

    }
    @PostMapping("/login")
    public ResponseEntity<Object> memberLogin(@RequestBody LoginVO data , HttpSession session) {
      Map<String,Object> resultMap = mService.loginMember(data);
      session.setAttribute("loginUser", resultMap.get("loginUser"));
      return new ResponseEntity<Object>(resultMap , (HttpStatus)resultMap.get("code"));

    }
  
  }
