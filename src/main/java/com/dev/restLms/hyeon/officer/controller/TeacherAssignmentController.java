package com.dev.restLms.hyeon.officer.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.dev.restLms.entity.Assignment;
import com.dev.restLms.entity.Board;
import com.dev.restLms.entity.OfferedSubjects;
import com.dev.restLms.entity.Subject;
import com.dev.restLms.entity.UserOwnAssignmentEvaluation;
import com.dev.restLms.entity.UserOwnPermissionGroup;
import com.dev.restLms.hyeon.officer.DTO.OfferedSubjectsDTO;
import com.dev.restLms.hyeon.officer.projection.PursuitCourse;
import com.dev.restLms.hyeon.officer.projection.PursuitPermissionGroup;
import com.dev.restLms.hyeon.officer.projection.PursuitSubject;
import com.dev.restLms.hyeon.officer.projection.PursuitUser;
import com.dev.restLms.hyeon.officer.repository.PursuitCourseRepository;
import com.dev.restLms.hyeon.officer.repository.PursuitPermisionGroupRepository;
import com.dev.restLms.hyeon.officer.repository.PursuitSubjectRepository;
import com.dev.restLms.hyeon.officer.repository.PursuitUserRepository;
import com.dev.restLms.hyeon.officer.repository.TeacherAssignmentAssignmentRepository;
import com.dev.restLms.hyeon.officer.repository.TeacherAssignmentBoardRepository;
import com.dev.restLms.hyeon.officer.repository.TeacherAssignmentOsRepository;
import com.dev.restLms.hyeon.officer.repository.TeacherAssignmentSubjectRepository;
import com.dev.restLms.hyeon.officer.repository.TeacherAssignmentUoaeRepository;
import com.dev.restLms.hyeon.officer.repository.TeacherAssignmentUopgRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api")
@Tag(name = "TeacherAssignmentController", description = "강사 배치")
public class TeacherAssignmentController {

  @Autowired
  PursuitCourseRepository courseRepository;

  @Autowired
  PursuitSubjectRepository pursuitSubjectRepository;

  @Autowired
  TeacherAssignmentSubjectRepository subjectRepository;

  @Autowired
  PursuitUserRepository userRepository;

  @Autowired
  TeacherAssignmentOsRepository offeredSubjectsRepository;

  @Autowired
  TeacherAssignmentUopgRepository userOwnPermissionGroupRepository;

  @Autowired
  PursuitPermisionGroupRepository permisionGroupRepository;

  @Autowired
  TeacherAssignmentAssignmentRepository assignmentRepository;

  @Autowired
  TeacherAssignmentUoaeRepository userOwnAssignmentEvaluationRepository;

  @Autowired
  TeacherAssignmentBoardRepository boardRepository;

  // 현재 날짜를 기준으로 아직 끝나지 않은 과정을 구분
  private boolean isCourseOngoing(String courseEndDateStr) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    LocalDateTime currentDate = LocalDateTime.now();
    LocalDateTime courseEndDate = LocalDateTime.parse(courseEndDateStr, formatter);

    // 현재 날짜가 수료일 이전이면 true
    return currentDate.isBefore(courseEndDate);
  }

  @GetMapping("/courses/responsible/officer")
  @Operation(summary = "담당한 과정 조회", description = "로그인한 책임자가 담당한 과정 목록 조회")
  public ResponseEntity<?> getCoursesByOfficer(@RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "8") int size, @RequestParam(required = false) String name) {
    try {
      UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
          .getContext().getAuthentication();
      final String userSessionId = auth.getPrincipal().toString();

      Optional<UserOwnPermissionGroup> uopsOpt = userOwnPermissionGroupRepository.findBySessionId(userSessionId);
      if (uopsOpt.isEmpty()) {
        return ResponseEntity.status(403).body("접근 권한이 없습니다.");
      }
      PursuitPermissionGroup ppg = permisionGroupRepository
          .findByPermissionGroupUuid(uopsOpt.get().getPermissionGroupUuid2());

      if (ppg.getPermissionName().equals("OFFICER") || ppg.getPermissionName().equals("INDIV_OFFICER")) {

        List<PursuitCourse> pursuitCourses = courseRepository.findBySessionId(userSessionId);
        if (pursuitCourses == null || pursuitCourses.isEmpty()) {
          return ResponseEntity.status(404).body("담당한 과정을 찾을 수 없습니다.");
        }

        List<Map<String, Object>> courseList = new ArrayList<>();
        for (PursuitCourse pursuitCourse : pursuitCourses) {
          if (pursuitCourse == null || !isCourseOngoing(pursuitCourse.getCourseEndDate())) {
            continue;
          }
          boolean shouldAdd = true;
          if (name != null && !name.trim().isEmpty()) {
            if (!pursuitCourse.getCourseTitle().contains(name)) {
              shouldAdd = false;
            }
          }

          if (shouldAdd) {
            Map<String, Object> courseInfo = new HashMap<>();
            courseInfo.put("CourseId", pursuitCourse.getCourseId());
            courseInfo.put("CourseTitle", pursuitCourse.getCourseTitle());
            courseInfo.put("CourseEndDate", pursuitCourse.getCourseEndDate());
            courseList.add(courseInfo);
          }
        }
        // 페이징 처리
        int totalItems = courseList.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int start = Math.min(page * size, totalItems);
        int end = Math.min(start + size, totalItems);

        List<Map<String, Object>> pagedResultList = courseList.subList(start, end);

        Map<String, Object> response = new HashMap<>();
        response.put("courseList", pagedResultList);
        response.put("currentPage", page);
        response.put("totalItems", totalItems);
        response.put("totalPages", totalPages);

        return ResponseEntity.ok(response);
      } else {
        return ResponseEntity.status(403).body("접근 권한이 없습니다.");
      }
    } catch (Exception e) {
      return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
    }
  }

  @GetMapping("/courses/{courseId}/subjects")
  @Operation(summary = "과정별 과목 조회", description = "과정별 과목 목록 조회")
  public ResponseEntity<?> getSubjectsByCourseId(@RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "8") int size,
      @PathVariable String courseId,
      @RequestParam(required = false) String subjectName, @RequestParam(required = false) String teacherName) {
    try {
      UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
          .getContext().getAuthentication();
      final String userSessionId = auth.getPrincipal().toString();

      Optional<UserOwnPermissionGroup> uopsOpt = userOwnPermissionGroupRepository.findBySessionId(userSessionId);
      if (uopsOpt.isEmpty()) {
        return ResponseEntity.status(403).body("접근 권한이 없습니다.");
      }
      PursuitPermissionGroup ppg = permisionGroupRepository
          .findByPermissionGroupUuid(uopsOpt.get().getPermissionGroupUuid2());

      if (ppg.getPermissionName().equals("OFFICER") || ppg.getPermissionName().equals("INDIV_OFFICER")) {

        List<OfferedSubjects> offeredSubjectsList = offeredSubjectsRepository
            .findByCourseIdAndOfficerSessionId(courseId, userSessionId);
        if (offeredSubjectsList == null || offeredSubjectsList.isEmpty()) {
          return ResponseEntity.status(404).body("해당 과정의 개설 과목을 찾을 수 없습니다.");
        }

        List<Map<String, Object>> groupedCourseSubjects = new ArrayList<>();
        for (OfferedSubjects offeredSubjects : offeredSubjectsList) {

          List<PursuitCourse> pursuitCourses = courseRepository.findByCourseIdAndSessionId(courseId, userSessionId);
          if (pursuitCourses == null || pursuitCourses.isEmpty()) {
            return ResponseEntity.status(404).body("담당한 과정을 찾을 수 없습니다.");
          }

          for (PursuitCourse pursuitCourse : pursuitCourses) {
            // 종료일자가 지나지 않은 과정만 필터링
            if (pursuitCourse == null || !isCourseOngoing(pursuitCourse.getCourseEndDate())) {
              continue;
            }

            boolean shouldAdd = true;
            Optional<PursuitSubject> subjectOpt = pursuitSubjectRepository
                .findBySubjectId(offeredSubjects.getSubjectId());
            Optional<PursuitUser> userOpt = userRepository.findBySessionId(offeredSubjects.getTeacherSessionId());

            // subjectName 필터링
            if (subjectName != null && !subjectName.trim().isEmpty() && subjectOpt.isPresent()) {
              if (!subjectOpt.get().getSubjectName().contains(subjectName)) {
                shouldAdd = false;
              }
            }

            // teacherName 필터링
            if (teacherName != null && !teacherName.trim().isEmpty() && userOpt.isPresent()) {
              if (!userOpt.get().getUserName().contains(teacherName)) {
                shouldAdd = false;
              }
            }

            // 필터링된 결과만 추가
            if (shouldAdd) {
              Map<String, Object> offeredSubjectInfo = new HashMap<>();
              offeredSubjectInfo.put("OfficerSessionId", offeredSubjects.getOfficerSessionId());
              offeredSubjectInfo.put("SubjectId", offeredSubjects.getSubjectId());
              offeredSubjectInfo.put("CourseId", offeredSubjects.getCourseId());
              offeredSubjectInfo.put("OfferedSubjectsId", offeredSubjects.getOfferedSubjectsId());
              offeredSubjectInfo.put("CourseName", pursuitCourse.getCourseTitle());
              offeredSubjectInfo.put("SubjectName", subjectOpt.map(PursuitSubject::getSubjectName).orElse(""));

              // 강사 정보 추가
              String teacherSessionId = offeredSubjects.getTeacherSessionId();
              if (teacherSessionId != null && userOpt.isPresent()) {
                PursuitUser user = userOpt.get();
                offeredSubjectInfo.put("TeacherSessionId", teacherSessionId);
                offeredSubjectInfo.put("TeacherName", user.getUserName());
              } else {
                offeredSubjectInfo.put("TeacherSessionId", "");
                offeredSubjectInfo.put("TeacherName", "");
              }

              groupedCourseSubjects.add(offeredSubjectInfo);
            }
          }

        }
        // 페이징 처리
        int totalItems = groupedCourseSubjects.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int start = Math.min(page * size, totalItems);
        int end = Math.min(start + size, totalItems);

        List<Map<String, Object>> pagedResultList = groupedCourseSubjects.subList(start, end);

        Map<String, Object> response = new HashMap<>();
        response.put("groupedCourseSubjects", pagedResultList);
        response.put("currentPage", page);
        response.put("totalItems", totalItems);
        response.put("totalPages", totalPages);
        return ResponseEntity.ok(response);
      } else {
        return ResponseEntity.status(403).body("접근 권한이 없습니다.");
      }
    } catch (Exception e) {
      return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
    }
  }

  @GetMapping("/teachers")
  @Operation(summary = "모든 강사 조회", description = "모든 강사 목록 조회")
  public ResponseEntity<?> getAllTeachers(@RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "8") int size,
      @RequestParam(required = false) String teacherName) {
    try {
      UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
          .getContext().getAuthentication();
      final String userSessionId = auth.getPrincipal().toString();

      Optional<UserOwnPermissionGroup> uopsOpt = userOwnPermissionGroupRepository.findBySessionId(userSessionId);
      if (uopsOpt.isEmpty()) {
        return ResponseEntity.status(403).body("접근 권한이 없습니다.");
      }
      PursuitPermissionGroup ppg = permisionGroupRepository
          .findByPermissionGroupUuid(uopsOpt.get().getPermissionGroupUuid2());

      if (ppg.getPermissionName().equals("OFFICER") || ppg.getPermissionName().equals("INDIV_OFFICER")) {

        // 모든 강사 목록 가져오기
        PursuitPermissionGroup teacher = permisionGroupRepository.findByPermissionName("TEACHER");
        List<UserOwnPermissionGroup> uopg_list = userOwnPermissionGroupRepository
            .findByPermissionGroupUuid2(teacher.getPermissionGroupUuid());

        List<String> teacherSessionIdList = new ArrayList<>();
        for (UserOwnPermissionGroup uopg : uopg_list) {
          if (teacher.getPermissionGroupUuid().equals(uopg.getPermissionGroupUuid2())) {
            teacherSessionIdList.add(uopg.getSessionId());
          }
        }

        List<PursuitUser> teacherList = userRepository.findBySessionIdIn(teacherSessionIdList);

        List<Map<String, Object>> teacherInfoList = new ArrayList<>();
        for (PursuitUser pursuitUser : teacherList) {
          if (pursuitUser == null) {
            continue;
          }

          boolean shouldAdd = true;
          if (teacherName != null && !teacherName.trim().isEmpty()) {
            if (!pursuitUser.getUserName().contains(teacherName)) {
              shouldAdd = false;
            }
          }

          if (shouldAdd) {
            Map<String, Object> teacherInfo = new HashMap<>();
            teacherInfo.put("Name", pursuitUser.getUserName());
            teacherInfo.put("Email", pursuitUser.getUserEmail());
            teacherInfo.put("Phone", pursuitUser.getPhoneNumber());
            teacherInfo.put("SessionId", pursuitUser.getSessionId());
            teacherInfoList.add(teacherInfo);
          }
        }
        // 페이징 처리
        int totalItems = teacherInfoList.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int start = Math.min(page * size, totalItems);
        int end = Math.min(start + size, totalItems);

        List<Map<String, Object>> pagedResultList = teacherInfoList.subList(start, end);

        Map<String, Object> response = new HashMap<>();
        response.put("teacherInfoList", pagedResultList);
        response.put("currentPage", page);
        response.put("totalItems", totalItems);
        response.put("totalPages", totalPages);
        return ResponseEntity.ok(response);
      } else {
        return ResponseEntity.status(403).body("접근 권한이 없습니다.");
      }
    } catch (Exception e) {
      return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
    }
  }

  @PostMapping("/subjects/assignTeacher")
  @Operation(summary = "과목별 강사 배치", description = "과목에 강사 배치")
  public ResponseEntity<?> assignTeacherToSubject(@RequestBody OfferedSubjectsDTO offeredSubjectsDTO) {
    try {
      UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
          .getContext().getAuthentication();
      final String userSessionId = auth.getPrincipal().toString();

      Optional<UserOwnPermissionGroup> uopsOpt = userOwnPermissionGroupRepository.findBySessionId(userSessionId);
      if (uopsOpt.isEmpty()) {
        return ResponseEntity.status(403).body("접근 권한이 없습니다.");  
      }
      PursuitPermissionGroup ppg = permisionGroupRepository
          .findByPermissionGroupUuid(uopsOpt.get().getPermissionGroupUuid2());

      if (ppg.getPermissionName().equals("OFFICER") || ppg.getPermissionName().equals("INDIV_OFFICER")) {
        // 변경할 강사의 sessionId
        String newTeacherSessionId = offeredSubjectsDTO.getTeacherSessionId();

        // 여기 잠들다...
        // if (newTeacherSessionId == null || newTeacherSessionId.isEmpty()) {
        // Board board = Board.builder()
        // .boardCategory("Q&A 게시판")
        // .offeredSubjectsId(offeredSubjectsDTO.getOfferedSubjectsId())
        // .teacherSessionId(userSessionId)
        // .build();
        // boardRepository.save(board);
        // } else {}

        subjectRepository.findBySubjectId(offeredSubjectsDTO.getSubjectId())
            .ifPresentOrElse(subject -> {
              if (newTeacherSessionId.equals(subject.getTeacherSessionId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 배치된 강사입니다");
              }
              Subject s = Subject.builder()
                  .subjectId(subject.getSubjectId())
                  .subjectName(subject.getSubjectName())
                  .subjectDesc(subject.getSubjectDesc())
                  .subjectCategory(subject.getSubjectCategory())
                  .subjectImageLink(subject.getSubjectImageLink())
                  .subjectPromotion(subject.getSubjectPromotion())
                  .teacherSessionId(newTeacherSessionId)
                  .build();
              subjectRepository.save(s);

            }, () -> {
              throw new EntityNotFoundException("해당 과목을 찾을 수 없습니다.");
            });

        offeredSubjectsRepository.findByOfferedSubjectsId(offeredSubjectsDTO.getOfferedSubjectsId())
            .ifPresentOrElse(offeredSubjects -> {
              if (newTeacherSessionId.equals(offeredSubjects.getTeacherSessionId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 배치된 강사입니다");
              }
              OfferedSubjects os = OfferedSubjects.builder()
                  .offeredSubjectsId(offeredSubjects.getOfferedSubjectsId())
                  .courseId(offeredSubjects.getCourseId())
                  .subjectId(offeredSubjects.getSubjectId())
                  .officerSessionId(userSessionId)
                  .teacherSessionId(newTeacherSessionId)
                  .build();
              offeredSubjectsRepository.save(os);

              // 게시판 생성 또는 업데이트
              createOrUpdateBoard(offeredSubjectsDTO.getOfferedSubjectsId(), newTeacherSessionId); // Ensure it's done
                                                                                                   // here
            }, () -> {
              throw new EntityNotFoundException("해당 개설 과목을 찾을 수 없습니다.");
            });

        List<Assignment> assignments = assignmentRepository
            .findByOfferedSubjectsId(offeredSubjectsDTO.getOfferedSubjectsId());
        for (Assignment assignment : assignments) {
          if (newTeacherSessionId.equals(assignment.getTeacherSessionId())) {
            continue;
          }
          Assignment a = Assignment.builder()
              .assignmentId(assignment.getAssignmentId())
              .offeredSubjectsId(assignment.getOfferedSubjectsId())
              .deadline(assignment.getDeadline())
              .noticeNo(assignment.getNoticeNo())
              .cutline(assignment.getCutline())
              .assignmentContent(assignment.getAssignmentContent())
              .assignmentTitle(assignment.getAssignmentTitle())
              .teacherSessionId(newTeacherSessionId)
              .build();
          assignmentRepository.save(a);

          List<UserOwnAssignmentEvaluation> evaluations = userOwnAssignmentEvaluationRepository
              .findByAssignmentId(assignment.getAssignmentId());
          for (UserOwnAssignmentEvaluation evaluation : evaluations) {
            if (newTeacherSessionId.equals(evaluation.getTeacherSessionId())) {
              continue;
            }
            UserOwnAssignmentEvaluation e = UserOwnAssignmentEvaluation.builder()
                .submissionId(evaluation.getSubmissionId())
                .uoaeSessionId(evaluation.getUoaeSessionId())
                .score(evaluation.getScore())
                .fileNo(evaluation.getFileNo())
                .isSubmit(evaluation.getIsSubmit())
                .assignmentId(evaluation.getAssignmentId())
                .teacherSessionId(newTeacherSessionId)
                .build();
            userOwnAssignmentEvaluationRepository.save(e);
          }
        }

        return ResponseEntity.ok("강사가 성공적으로 배치되었습니다.");
      } else {
        return ResponseEntity.status(403).body("접근 권한이 없습니다.");
      }
    } catch (ResponseStatusException ex) {
      return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
    } catch (Exception e) {
      return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
    }
  }

  private void createOrUpdateBoard(String offeredSubjectsId, String newTeacherSessionId) {
    // 개설 과목에 해당하는 게시판 생성 또는 업데이트
    offeredSubjectsRepository.findByOfferedSubjectsId(offeredSubjectsId)
        .ifPresentOrElse(offeredSubject -> {
          // Get the subjectName from subjectRepository using offeredSubjectsId
          Optional<Subject> subjectOpt = subjectRepository.findBySubjectId(offeredSubject.getSubjectId());
          if (subjectOpt.isEmpty()) {
            throw new EntityNotFoundException("해당 과목을 찾을 수 없습니다: " + offeredSubject.getSubjectId());
          }
          String subjectName = subjectOpt.get().getSubjectName();
          String boardCategory = subjectName + " Q&A 게시판";

          boolean boardExists = boardRepository.existsByOfferedSubjectsIdAndBoardCategory(
              offeredSubjectsId, boardCategory);

          if (!boardExists) {
            // 게시판이 없으면 새로 생성
            Board newBoard = Board.builder()
                .boardCategory(boardCategory)
                .offeredSubjectsId(offeredSubjectsId)
                .teacherSessionId(newTeacherSessionId)
                .build();
            boardRepository.save(newBoard);
          } else {
            // 이미 게시판이 존재하면 강사 정보만 업데이트
            Optional<Board> opBoard = boardRepository.findByOfferedSubjectsId(offeredSubjectsId);
            if (opBoard.isPresent()) {
              Board board = opBoard.get();
              if (newTeacherSessionId.equals(board.getTeacherSessionId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 해당 강사의 게시판으로 변경되었습니다.");
              } else {
                board.setTeacherSessionId(newTeacherSessionId);
                boardRepository.save(board);
              }
            }
          }
        }, () -> {
          throw new EntityNotFoundException("해당 개설 과목을 찾을 수 없습니다: " + offeredSubjectsId);
        });
  }

}