package com.shiningok.todo.member.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "member_info")
// pwd는 입력이 가능하고 , 출력은 불가능학다
@JsonIgnoreProperties(
  value = {"pwd"}, allowSetters = true, allowGetters = false
)
public class MemberInfoEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "mi_seq") private Long seq;
  @Column(name = "mi_email" ) private String email;
  @JsonProperty("pwd")
  @Column(name = "mi_pwd" ) private String pwd;
  @Column(name =  "mi_name") private String name;
  @Column(name = "mi_birth") private Date birth;
  @Column(name = "mi_reg_dt") private Date regDt;
}
