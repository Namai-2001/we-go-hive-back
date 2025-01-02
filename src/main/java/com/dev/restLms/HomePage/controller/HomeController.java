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
import com.dev.restLms.HomePage.projection.HomeCourseOwnSubjectProjection;
import com.dev.restLms.HomePage.projection.HomeOfferedSubjectsProjection;
import com.dev.restLms.HomePage.projection.HomeVidIdProjection;
import com.dev.restLms.HomePage.repository.HomeCourseOwnSubjectRepository;
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

  @Autowired
  HomeCourseOwnSubjectRepository homeCourseOwnSubjectRepository;

  @GetMapping("/RandSubjectVid")
  public ResponseEntity<?> randSubjectVid() {
    String courseId = "individual-subjects";
    boolean notExist = true;

    Optional<List<HomeCourseOwnSubjectProjection>> opApprovedSubjects = homeCourseOwnSubjectRepository
        .findByCourseIdAndSubjectApproval(courseId, "T");

    if (opApprovedSubjects.isPresent()) {
      List<HomeCourseOwnSubjectProjection> approvedSubjects = opApprovedSubjects.get();
      while (notExist) {
        Random random = new Random();
        Integer randIdx = random.nextInt(approvedSubjects.size());
        String subjectId = approvedSubjects.get(randIdx).getSubjectId();
        Optional<HomeOfferedSubjectsProjection> opSpecificSubject = homeOfferedSubjectRepository
            .findBySubjectId(subjectId);

        if (opSpecificSubject.isPresent()) {
          HomeOfferedSubjectsProjection specificSubjects = opSpecificSubject.get();
          Optional<HomeVidIdProjection> opSubjectsOwnVideo = homeSubjectsOwnVideoRepository
              .findBySovOfferedSubjectsIdAndVideoSortIndex(specificSubjects.getOfferedSubjectsId(), "1");

          Optional<Subject> specificSubject = homeSubjectRepository.findById(specificSubjects.getSubjectId());

          // 비디오가 있다면면
          if (opSubjectsOwnVideo.isPresent() && specificSubject.isPresent()) {
            notExist = false;
            Optional<Video> opSpecificVideo = homeVideoRepository.findById(opSubjectsOwnVideo.get().getSovVideoId());
            if (opSpecificVideo.isPresent()) {
              Video specificVideo = opSpecificVideo.get();
              RandSubjectVidDTO result = RandSubjectVidDTO.builder()
                  .vidTitle(specificVideo.getVideoTitle())
                  .vidLink(specificVideo.getVideoLink())
                  .subjectName(specificSubject.get().getSubjectName())
                  .offeredSubjectsId(subjectId)
                  .build();
              return ResponseEntity.ok(result);
            } else
              continue;
          } else
            continue;
        } else
          continue;
      }
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("표시할 항목이 존재하지 않습니다.");
  }
}