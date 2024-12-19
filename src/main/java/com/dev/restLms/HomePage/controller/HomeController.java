package com.dev.restLms.HomePage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.HomePage.repository.HomeOfferedSubjectRepository;
import com.dev.restLms.HomePage.repository.HomeSubjectRepository;
import com.dev.restLms.HomePage.repository.HomeSubjectsOwnVideoRepository;
import com.dev.restLms.HomePage.repository.HomeVideoRepository;

import io.swagger.v3.oas.annotations.tags.Tag;

// 개설 과목인데 과정코드가 할당되지 않은 과목중 하나를 추출하여
// 해당 과목의 첫 번째 영상
// 링크와 제목, 과목이름을 반환
@RestController
@RequestMapping("/Home")
@Tag(name = "HomeController", description = "랜덤한 과목의 영상 및 데이터를 추출하는 컨트롤러")
public class HomeController {
  @Autowired
  HomeOfferedSubjectRepository homeOfferedSubjectRepository;

  @Autowired
  HomeSubjectRepository homeSubjectRepository;

  @Autowired
  HomeSubjectsOwnVideoRepository homeSubjectsOwnVideoRepository;

  @Autowired
  HomeVideoRepository homeVideoRepository;

  @GetMapping("/RandSubjectVid")
  public ResponseEntity<?> randSubjectVid() {
    return ResponseEntity.ok(homeOfferedSubjectRepository.findByCourseIdIsNull());
  }
}
