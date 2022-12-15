package com.shiningok.todo.member.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.shiningok.todo.member.data.LoginVO;
import com.shiningok.todo.member.entity.MemberImageEntity;
import com.shiningok.todo.member.entity.MemberInfoEntity;
import com.shiningok.todo.member.repository.MemberImageRepository;
import com.shiningok.todo.member.repository.MemberRepository;
import com.shiningok.todo.utils.AESAlgorithm;

@Service
public class MemberService {
  @Autowired MemberRepository m_repo;
  @Autowired MemberImageRepository mi_repo;
  private Long miSeq;
public Map <String, Object> addMemberImage(MemberImageEntity data, Long miSeq) {
    Map<String, Object> resultMap = new LinkedHashMap<String , Object>();
    data.setMiSeq(miSeq);
    mi_repo.save(data);
    resultMap.put("status", true);
    resultMap.put("message", "이미지 저장 완");
    resultMap.put("code", HttpStatus.OK);
    return resultMap;
  }
public String getFilenameByUri(String uri) {
   List<MemberImageEntity> data = mi_repo.findTopByUriOrderBySeqDesc(uri);
   return data.get(0).getFileName();
}
  public Map<String, Object> addMember(MemberInfoEntity data) { // ctrl + . -> 빠른수정
    Map <String, Object> resultMap = new LinkedHashMap<String, Object>();
    if(m_repo.countByEmail(data.getEmail()) == 1) {
      resultMap.put("status", false);
      resultMap.put("message", data.getEmail() +"이미 가입되었삼");
      resultMap.put("code", HttpStatus.BAD_REQUEST);
    
    }
    else{
      try{
        String encPwd = AESAlgorithm.Encrypt(data.getPwd());
        data.setPwd(encPwd);
      } catch(Exception e) {e.printStackTrace();}
      m_repo.save(data);
      resultMap.put("status", true);
      resultMap.put("message", "등록 완");
      resultMap.put("code",HttpStatus.CREATED);
    }
    return resultMap;
  }
   public Map<String, Object> loginMember(LoginVO data) {
    Map<String, Object> resultMap = new LinkedHashMap<String,Object>();
    MemberInfoEntity loginUser = null;
    try {
      loginUser = m_repo.findByEmailAndPwd(data.getEmail(), AESAlgorithm.Encrypt(data.getPwd()));
    } catch (Exception e ) {e.printStackTrace();}
    if(loginUser == null) {
      resultMap.put("status",false);
      resultMap.put("message","이메일이나 비번 오류임");
      resultMap.put("code",HttpStatus.BAD_REQUEST);
    }
    else {
      resultMap.put("status",true);
      resultMap.put("message","로그인 완");
      resultMap.put("code",HttpStatus.ACCEPTED);
      resultMap.put("loginUser", loginUser);     
    }
     return resultMap;
   }
  
}
