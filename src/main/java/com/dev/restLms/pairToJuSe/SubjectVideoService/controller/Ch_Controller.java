package com.dev.restLms.pairToJuSe.SubjectVideoService.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.pairToJuSe.SubjectVideoService.projection.Ch_OS_S_Projection;
import com.dev.restLms.pairToJuSe.SubjectVideoService.projection.Ch_SOV_Projection;
import com.dev.restLms.pairToJuSe.SubjectVideoService.projection.Ch_S_Projection;
import com.dev.restLms.pairToJuSe.SubjectVideoService.projection.Ch_UOSV_Projection;
import com.dev.restLms.pairToJuSe.SubjectVideoService.projection.Ch_U_Projection;
import com.dev.restLms.pairToJuSe.SubjectVideoService.projection.Ch_V_Projection;
import com.dev.restLms.pairToJuSe.SubjectVideoService.repository.Ch_OS_S_Repository;
import com.dev.restLms.pairToJuSe.SubjectVideoService.repository.Ch_SOV_Repository;
import com.dev.restLms.pairToJuSe.SubjectVideoService.repository.Ch_S_Repository;
import com.dev.restLms.pairToJuSe.SubjectVideoService.repository.Ch_UOSV_Repository;
import com.dev.restLms.pairToJuSe.SubjectVideoService.repository.Ch_U_Repository;
import com.dev.restLms.pairToJuSe.SubjectVideoService.repository.Ch_V_Repository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/lecture")
@Tag(name = "과목 영상 목록 컨트롤러", description = "과목 영상 목록 제공 페이지가 요청하는 엔드포인트")
public class Ch_Controller {

    @Autowired
    private Ch_SOV_Repository ch_SOV_Repository;

    @GetMapping("/getlist")
    @Operation(summary = "영상 목록 조회", description = "개설 과목 코드로 목록 조회")
    public List<Ch_SOV_Projection> getList(
            @Parameter(description = "개설 과목 코드", required = true) String sovOfferedSubjectsId,
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Ch_SOV_Projection> searchResult = ch_SOV_Repository
                .findBySovOfferedSubjectsIdContaining(sovOfferedSubjectsId, pageable);
        return searchResult.getContent();
    }

    @Autowired
    Ch_V_Repository ch_V_Repository;

    @GetMapping("/getlist/info")
    @Operation(summary = "영상 목록 별 영상 정보", description = "영상 목록마다 영상 정보 통합")
    public List<Ch_V_Projection> getListInfo(
            @Parameter(description = "개설 과목 코드", required = true) String sovOfferedSubjectsId,
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Ch_SOV_Projection> searchResult = ch_SOV_Repository
                .findBySovOfferedSubjectsIdContaining(sovOfferedSubjectsId, pageable);
        List<Ch_SOV_Projection> resultList = searchResult.getContent();

        List<Ch_V_Projection> videosInfo = new ArrayList<Ch_V_Projection>();
        for (Ch_SOV_Projection ch_Projection : resultList) {
            Ch_V_Projection each = ch_V_Repository.findByVideoId(ch_Projection.getSovVideoId());
            videosInfo.add(each);
        }
        return videosInfo;
    }

    @Autowired
    Ch_UOSV_Repository ch_UOSV_Repository;

    // 과목의 영상 개수와 맞지 않는 학생을 조회하면 오류
    @GetMapping("/getlist/info/cal")
    @Operation(summary = "영상 목록 별 진행도 계산", description = "영상 목록 별 진행도를 계산해준다")
    public List<String> getListInfoCalculateProgress(
            @Parameter(description = "개설 과목 코드", required = true) String sovOfferedSubjectsId,
            @Parameter(description = "사용자 세션 아이디", required = true) String sessionId,
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Ch_SOV_Projection> searchResult = ch_SOV_Repository
                .findBySovOfferedSubjectsIdContaining(sovOfferedSubjectsId, pageable);
        List<Ch_SOV_Projection> resultList = searchResult.getContent();

        List<String> calsList = new ArrayList<>();
        // for (Ch_SOV_Projection ch_Projection : resultList) {
        // Ch_V_Projection each =
        // ch_V_Repository.findByVideoId(ch_Projection.getSovVideoId());
        // // 기본키 가져오기
        // String eachEpi = ch_Projection.getEpisodeId();
        // // 진행도 문자열
        // String str =
        // ch_UOSV_Repository.findByUosvEpisodeIdAndUosvOfferedSubjectsId(eachEpi,
        // sovOfferedSubjectsId)
        // .getProgress();
        // // 진행
        // double progress = Integer.parseInt(str);
        // // 최대
        // double max = Integer.parseInt(each.getMax());

        // int result = (int) Math.round((progress / max) * 100);
        // String current = Integer.toString(result);

        // calsList.add(current);
        // }

        for (Ch_SOV_Projection ch_SOV_Projection : resultList) {
            String episodeId = ch_SOV_Projection.getEpisodeId();
            Ch_UOSV_Projection ch_UOSV_Projection = ch_UOSV_Repository
                    .findByUosvSessionIdAndUosvOfferedSubjectsIdAndUosvEpisodeId(sessionId, sovOfferedSubjectsId,
                            episodeId);

            String result = ch_UOSV_Projection.getProgress();
            calsList.add(result);
        }
        return calsList;
    }

    @GetMapping("/getSubjectVideoInfo")
    @Operation(summary = "통합 영상 정보 조회", description = "개설 과목 코드와 사용자 세션 아이디를 통해 영상 정보와 진행도를 통합하여 조회합니다.")
    public List<Map<String, String>> getSubjectVideoInfo(
            @Parameter(description = "개설 과목 코드", required = true) @RequestParam String sovOfferedSubjectsId,
            @Parameter(description = "사용자 세션 아이디", required = true) @RequestParam String sessionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Ch_SOV_Projection> sovResults = ch_SOV_Repository
                .findBySovOfferedSubjectsIdContaining(sovOfferedSubjectsId, pageable);

        List<Map<String, String>> pageInfoList = new ArrayList<>();
        for (Ch_SOV_Projection sovProjection : sovResults) {
            Ch_V_Projection vProjection = ch_V_Repository.findByVideoId(sovProjection.getSovVideoId());
            Ch_UOSV_Projection uosvProjection = ch_UOSV_Repository
                    .findByUosvSessionIdAndUosvOfferedSubjectsIdAndUosvEpisodeId(sessionId, sovOfferedSubjectsId,
                            sovProjection.getEpisodeId());

            // double max = Double.parseDouble(vProjection.getMax());
            // double finalstat = Double.parseDouble(uosvProjection.getProgress());

            // int progressPercentage = (int) Math.ceil(finalstat / max * 100);
            // String progressString = Integer.toString(progressPercentage);

            Map<String, String> infoDetails = new HashMap<>();
            infoDetails.put("title", sovProjection.getVideoSortIndex() + "회차");
            // infoDetails.put("sovVideoId", sovProjection.getSovVideoId());
            // infoDetails.put("episodeId", sovProjection.getEpisodeId());
            // infoDetails.put("videoLink", vProjection.getVideoLink());
            infoDetails.put("image", vProjection.getVideoImg());
            infoDetails.put("description", vProjection.getVideoTitle());
            infoDetails.put("progress", uosvProjection.getProgress());

            pageInfoList.add(infoDetails);
        }
        return pageInfoList;
    }

    @Autowired
    Ch_OS_S_Repository ch_OS_S_Repository;

    @Autowired
    Ch_S_Repository ch_S_Repository;

    @Autowired
    Ch_U_Repository ch_U_Repository;

    @GetMapping("/getSubjectInfo")
    @Operation(summary = "과목 정보 조회", description = "과목명, 강사명 조회")
    public Map<String, String> getSubjectName(
            @Parameter(description = "개설 과목 코드", required = true) @RequestParam String sovOfferedSubjectsId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // 과목 명 찾기
        Ch_OS_S_Projection sovResults = ch_OS_S_Repository.findByOfferedSubjectsId(sovOfferedSubjectsId);
        String searchKey = sovResults.getSubjectId();
        Ch_S_Projection subjectPro = ch_S_Repository.findBySubjectId(searchKey);
        String subjectName = subjectPro.getSubjectName();

        // 강사 명 찾기
        String teacherSessionId = sovResults.getTeacherSessionId();
        Ch_U_Projection userPro = ch_U_Repository.findBySessionId(teacherSessionId);
        String teacherName = userPro.getUserName();

        // 합치기
        Map<String, String> result = new HashMap<>();
        result.put("title", subjectName);
        result.put("teacherName", teacherName);
        return result;
    }
}
