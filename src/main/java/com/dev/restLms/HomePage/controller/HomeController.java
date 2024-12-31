package com.dev.restLms.HomePage.controller;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.HomePage.dto.RandSubjectVidDTO;
import com.dev.restLms.HomePage.projection.HomeOfferedSubjectsProjection;
import com.dev.restLms.HomePage.projection.HomeVidIdProjection;
import com.dev.restLms.HomePage.repository.HomeOfferedSubjectRepository;
import com.dev.restLms.HomePage.repository.HomeSubjectRepository;
import com.dev.restLms.HomePage.repository.HomeSubjectsOwnVideoRepository;
import com.dev.restLms.HomePage.repository.HomeVideoRepository;
import com.dev.restLms.entity.Subject;
import com.dev.restLms.entity.Video;

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
    String courseId = "individual-subjects";
    String officerSessionId = "2b3c4d5e-6f7g-8h9i-0j1k-l2m3n4o5p6q7";
    Optional<List<HomeOfferedSubjectsProjection>> opSpecificSubjects = homeOfferedSubjectRepository.findByCourseIdAndOfficerSessionId(courseId, officerSessionId);

    if(opSpecificSubjects.isPresent()){
      List<HomeOfferedSubjectsProjection> specificSubjects = opSpecificSubjects.get();
      Random random = new Random();
      Integer randIdx = random.nextInt(specificSubjects.size());
      String offeredSubjectsId = specificSubjects.get(randIdx).getOfferedSubjectsId();
      Optional<HomeVidIdProjection> opSubjectsOwnVideo = homeSubjectsOwnVideoRepository.findBySovOfferedSubjectsIdAndVideoSortIndex(offeredSubjectsId, "1");
      Optional<Subject> specificSubject = homeSubjectRepository.findById(specificSubjects.get(randIdx).getSubjectId());
      if(opSubjectsOwnVideo.isPresent() && specificSubject.isPresent()){
        Optional<Video> opSpecificVideo = homeVideoRepository.findById(opSubjectsOwnVideo.get().getSovVideoId());
        if(opSpecificVideo.isPresent()){
          Video specificVideo = opSpecificVideo.get();
          RandSubjectVidDTO result = RandSubjectVidDTO.builder()
            .vidTitle(specificVideo.getVideoTitle())
            .vidLink(specificVideo.getVideoLink())
            .subjectName(specificSubject.get().getSubjectName())
            .offeredSubjectsId(offeredSubjectsId)
            .build();
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("표시할 항목이 존재하지 않습니다.");       
      }
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("표시할 항목이 존재하지 않습니다.");
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("표시할 항목이 존재하지 않습니다.");
  }
}
