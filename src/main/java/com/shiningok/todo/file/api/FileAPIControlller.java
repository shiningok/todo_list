package com.shiningok.todo.file.api;

import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.shiningok.todo.member.entity.MemberImageEntity;
import com.shiningok.todo.member.service.MemberService;
import com.shiningok.todo.todoList.entity.TodoImageEntity;
import com.shiningok.todo.todoList.service.TodoInfoService;

@RestController
public class FileAPIControlller {
  @Autowired TodoInfoService tService;
  @Autowired MemberService mService;

  @Value("${file.image.todo}") String todo_img_path;
  @Value("${file.image.member}") String member_img_path;

  @PutMapping("/{type}/upload")
  public ResponseEntity < Object > putImageUpload(
    // 파일 객체 변수 - file (전송 할 때도 파일의 파라미터 이름을 file로 지정해야된다)
    @PathVariable String type, @RequestPart MultipartFile file, @RequestParam Long seq
  ) {
    Map < String, Object > map = new LinkedHashMap < String, Object > ();
    //업로드 할 파일의 원본 이름 (확장자까지)
    System.out.println(file.getOriginalFilename());
    // Path - 폴더 및 파일의 위치를 나타내는 객체
    // Paths - 폴더 및 파일을 가져오고 경로를 만들기 위한 파일 유틸리티 클래스
    // todo_img_path 문자열로부터 실제 폴더 경로를 가져온다.
    Path folderLocation = null;
    if (type.equals("todo")) {
      folderLocation = Paths.get(todo_img_path);
    } else if (type.equals("member")) {
      folderLocation = Paths.get(member_img_path);
    } else {
      map.put("status", false);
      map.put("message", "타입 정보가 잘못되었습니다 (ex :/todo/upload, /member/upload)");
      return new ResponseEntity < > (map, HttpStatus.BAD_REQUEST);
    }
    // java_ex.pdf
    String originfileName = file.getOriginalFilename();
    // {"java_ex", "pdf"} --> 렝쓰 2
    String[] split = originfileName.split("\\.");
    // a.b.c.png
    String ext = split[split.length - 1]; // 렝쓰 -1됐음
    String filename = "";
    for (int i = 0; i < split.length - 1; i++) {
      filename += split[i]; // "java_ex"
    }
    String saveFilename = type + "_"; // "todo_"
    Calendar c = Calendar.getInstance();
    saveFilename += c.getTimeInMillis() + "." + ext; //"todo_1654542156.pdf"




    // 폴더 경로와 파일의 이름을 합쳐서 목표 파일의 경로를 만든다.
    Path targetFile = folderLocation.resolve(saveFilename);
    try {
      // Files는 파일 처리에 대한 유틸리티 클래스
      // copy - 복사, file.getInputStream() - 파일을 열어서 내용을 읽는 준비
      // targetFile 경로로 , StandardCopyOption.REPLACE_EXISTING - 덮어쓰기 모드
      Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if(type.equals("todo")) {
      TodoImageEntity data = new TodoImageEntity();
      data.setFileName(saveFilename); // 실제 파일의 경로
      data.setUri(filename); // 파일을 요청할 때 주소에 넣는 값
      tService.addTodoImage(data, seq);
    }
    else if(type.equals("member")) {
      MemberImageEntity data = new MemberImageEntity();
      data.setFileName(saveFilename); // 실제 파일의 경로
      data.setUri(filename); // 파일을 요청할 때 주소에 넣는 값
      mService.addMemberImage(data, seq);
    }


    return new ResponseEntity < > (map, HttpStatus.OK);
  }

  @GetMapping("/images/{type}/{uri}")
  public ResponseEntity getImage(
    @PathVariable String uri, HttpServletRequest request,
    @PathVariable String type
    ) throws Exception {
    // todo_img_path 문자열로부터 실제 폴더 경로를 가져온다. 
    Path folderLocation = null;
    if (type.equals("todo")) {
      folderLocation = Paths.get(todo_img_path);
    } else if (type.equals("member")) {
      folderLocation = Paths.get(member_img_path);
    }

    String filename = null;
    if (type.equals("todo")) {
      filename = tService.getFilenameByUri(uri);
    }
    else if (type.equals("member")) {
      filename = mService.getFilenameByUri(uri);
    }
    



    String[] split = filename.split("\\.");
    String ext = split[split.length - 1];
    String exportName = uri + "." + ext;
    // 내보낼 파일의 이름을 만든다. 
    // 폴더 경로와 파일의 이름을 합쳐서 목표 파일의 경로를 만든다. 
    Path targetFile = folderLocation.resolve(filename);
    // 다운로드 가능한 형태로 변환하기 위한 Resource 객체 생성 
    Resource r = null;
    try {
      // 일반파일 -> Url로 첨부 가능한 형태로 변환 
      r = new UrlResource(targetFile.toUri());
    } catch (Exception e) {
      e.printStackTrace();
    }
    // 첨부된 파일의 타입을 저장하기위한 변수 생성 
    String contentType = null;
    try {
      // 첨부할 파일의 타입 정보 산출 
      contentType = request.getServletContext().getMimeType(r.getFile().getAbsolutePath());
      // 산출한 파일의 타입이 null 이라면 
      if (contentType == null) {
        // 일반 파일로 처리한다. 
        contentType = "application/octet-stream";
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ResponseEntity.ok()
      // 응답의 코드를 200 OK로 설정하고 
      // 산출한 타입을 응답에 맞는 형태로 변환 
      .contentType(MediaType.parseMediaType(contentType))
      // 내보낼 내용의 타입을 설정 (파일), 
      // attachment; filename*=\""+r.getFilename()+"\" 요청한 쪽에서 다운로드 한 
      // 파일의 이름을 결정 
      .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(exportName, "UTF-8") + "\"")
      .body(r);
    // 변환된 파일을 ResponseEntity에 추가 }



  }
}