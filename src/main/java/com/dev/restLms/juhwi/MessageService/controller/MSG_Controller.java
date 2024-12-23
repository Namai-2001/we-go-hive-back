package com.dev.restLms.juhwi.MessageService.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.Auth.service.JwtTokenProvider;
import com.dev.restLms.entity.Message;
import com.dev.restLms.juhwi.MessageService.projection.MSG_Admin_Projection;
import com.dev.restLms.juhwi.MessageService.projection.MSG_Self_Projection;
import com.dev.restLms.juhwi.MessageService.dto.MSG_MessagePost_DTO;
import com.dev.restLms.juhwi.MessageService.projection.MSG_Details_Projection;
import com.dev.restLms.juhwi.MessageService.projection.MSG_GetFriendList_Projection;
import com.dev.restLms.juhwi.MessageService.projection.MSG_Inbox_Projection;
import com.dev.restLms.juhwi.MessageService.projection.MSG_Sent_Projcction;
import com.dev.restLms.juhwi.MessageService.projection.MSG_UserNickname_Projection;
import com.dev.restLms.juhwi.MessageService.repository.MSG_Admin_Repository;
import com.dev.restLms.juhwi.MessageService.repository.MSG_Details_Repository;
import com.dev.restLms.juhwi.MessageService.repository.MSG_Self_Repository;
import com.dev.restLms.juhwi.MessageService.repository.MSG_Inbox_Repository;
import com.dev.restLms.juhwi.MessageService.repository.MSG_Sent_Repository;
import com.dev.restLms.juhwi.MessageService.repository.MSG_GetFriendList_Repository;
import com.dev.restLms.juhwi.MessageService.repository.MSG_UserNickname_Repository;
import com.dev.restLms.juhwi.MessageService.repository.MSG_Entity_Repository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

// 마이그레이션 완료
@Slf4j
@RestController
@Tag(name = "받은 쪽지함 API", description = "받은 쪽지함 관련 기능을 모아둔 컨트롤러")
@RequestMapping("/message")
public class MSG_Controller {
        @Autowired
        MSG_Inbox_Repository ch_M_Rs_Repository;

        @GetMapping("/inbox")
        @Operation(summary = "받은 쪽지함 조회", description = "받은 쪽지함을 사용자 기준으로 조회, 삭제 여부 선택")
        public Page<MSG_Inbox_Projection> getInboxList(
                        @Parameter(description = "조회 기준 T:삭제됨 F:삭제안됨", required = true) @RequestParam(defaultValue = "F") String isDelete,

                        @Parameter(description = "검색 파라미터", required = false) @RequestParam(defaultValue = "") String searchParam,

                        @RequestParam(defaultValue = "0") int page,

                        @RequestParam(defaultValue = "10") int size) {

                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String userSessionId = auth.getPrincipal().toString();
                Pageable pageable = PageRequest.of(page, size);
                Page<MSG_Inbox_Projection> resultPage;
                if (searchParam.equals("")) {
                        resultPage = ch_M_Rs_Repository
                                        .findByReceiverDeleteAndReceiverSessionIdAndSenderSessionIdNot(isDelete,
                                                        userSessionId, userSessionId, pageable);
                } else {
                        resultPage = ch_M_Rs_Repository
                                        .findByReceiverDeleteAndReceiverSessionIdAndSenderSessionIdNotAndMessageTitleContaining(
                                                        isDelete,
                                                        userSessionId, userSessionId, searchParam, pageable);
                }
                return resultPage;
        }

        @Autowired
        MSG_Sent_Repository ch_M_Ss_Repository;

        @GetMapping("/sent")
        @Operation(summary = "보낸 쪽지함 조회", description = "보낸 쪽지함을 사용자 기준으로 조회, 삭제 여부 선택")
        public Page<MSG_Sent_Projcction> getSentPage(
                        @Parameter(description = "조회 기준 T:삭제됨 F:삭제안됨", required = true) @RequestParam(defaultValue = "F") String isDelete,

                        @Parameter(description = "검색 파라미터", required = false) @RequestParam(defaultValue = "") String searchParam,

                        @RequestParam(defaultValue = "0") int page,

                        @RequestParam(defaultValue = "10") int size) {

                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String userSessionId = auth.getPrincipal().toString();

                Pageable pageable = PageRequest.of(page, size);

                Page<MSG_Sent_Projcction> resultPage;
                if (searchParam.equals("")) {
                        resultPage = ch_M_Ss_Repository
                                        .findBySenderDeleteAndSenderSessionIdAndReceiverSessionIdNot(isDelete,
                                                        userSessionId, userSessionId, pageable);
                } else {
                        resultPage = ch_M_Ss_Repository
                                        .findBySenderDeleteAndSenderSessionIdAndReceiverSessionIdNotAndMessageTitleContaining(
                                                        isDelete,
                                                        userSessionId, userSessionId, searchParam, pageable);
                }
                return resultPage;
        }

        @Autowired
        MSG_Self_Repository ch_M_MSR_Repository;

        @GetMapping("/self")
        @Operation(summary = "내게 쓴 쪽지함 조회", description = "보낸 쪽지함을 사용자 기준으로 조회, 삭제 여부 선택")
        public Page<MSG_Self_Projection> getmySentPage(
                        @Parameter(description = "조회 기준 T:삭제됨 F:삭제안됨", required = true) @RequestParam(defaultValue = "F") String isDelete,

                        @Parameter(description = "검색 파라미터", required = false) @RequestParam(defaultValue = "") String searchParam,

                        @RequestParam(defaultValue = "0") int page,

                        @RequestParam(defaultValue = "10") int size) {
                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String userSessionId = auth.getPrincipal().toString();
                Pageable pageable = PageRequest.of(page, size);
                Page<MSG_Self_Projection> resultPage;
                if (searchParam.equals("")) {
                        resultPage = ch_M_MSR_Repository
                                        .findBySenderDeleteAndSenderSessionIdAndReceiverSessionId(isDelete,
                                                        userSessionId,
                                                        userSessionId,
                                                        pageable);
                } else {
                        resultPage = ch_M_MSR_Repository
                                        .findBySenderDeleteAndSenderSessionIdAndReceiverSessionIdAndMessageTitleContaining(
                                                        isDelete, userSessionId, userSessionId, searchParam, pageable);
                }
                return resultPage;
        }

        @Autowired
        MSG_Admin_Repository ch_M_MASR_Repository;

        @GetMapping("/admin")
        @Operation(summary = "관리자 공지 조회", description = "관리자가 작성한 공지를 조회, 삭제 여부 선택")
        public Page<MSG_Admin_Projection> getAdminMessage(
                        @Parameter(description = "권한명", required = true) @RequestParam(defaultValue = "관리자") String userReceiveSessionId,

                        @RequestParam(defaultValue = "0") int page,

                        @RequestParam(defaultValue = "10") int size) {

                Pageable pageable = PageRequest.of(page, size);

                // 추후 그룹이 고도화 되었을 때 변경
                String permissionGroup = "관리자";
                String targetGroup = "모두";
                Page<MSG_Admin_Projection> resultPage = ch_M_MASR_Repository
                                .findBySenderSessionIdAndReceiverSessionId(
                                                permissionGroup, targetGroup, pageable);
                return resultPage;
        }

        /*
         * 삭제 기능을 담당하는 Repository
         */
        @Autowired
        MSG_Entity_Repository m_Default_Repository;
        /*
         * 받은 쪽지함 삭제 업데이트 처리
         * 설명 : 실제로 테이블에서 삭제가 되는 것이 아니라서 업데이트 작업으로 처리함, 보낸 사람과 받은 사람이 같은 테이블을 참조함
         * 1. 받은 쪽지함을 세션아이디 기준으로 조회 -> 실제로 있는지 검증
         * 2. 받은 쪽지함에서 실제로 키가 있는지 검증 ->
         * 3. 받은 쪽지함에서 보낸 기본키로 삭제 업데이트 작업
         * 
         * 
         * 원래 필요한 값
         * 1. 세션아이디
         * 2. 조회 관점
         * 3. 타겟
         * 4. 값 (TF)
         * 
         * 
         * 실제 필요한 값
         * 1. 세션아이디
         * 2. 타겟
         * 3. 값 (TF)
         */

        @Autowired
        JwtTokenProvider jwtTokenProvider;

        @PostMapping("/delete")
        @Operation(summary = "사용자자 기준 삭제", description = "사용자 관점의 TF 설정")
        public ResponseEntity<?> delete(
                        @Parameter(description = "삭제 대상 기본키", required = true) @RequestParam(defaultValue = "16") String deleteId,
                        @Parameter(description = "삭제 여부 : T/F", required = true) @RequestParam(defaultValue = "T") String deleteAt) {

                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String userSessionId = auth.getPrincipal().toString();
                // 삭제할 메시지가 존재하는가
                Optional<Message> deleteSelect = m_Default_Repository.findById(deleteId);
                if (deleteSelect.isPresent()) {
                        Message deleteMessage = deleteSelect.get();

                        boolean isChanged = false;
                        // 삭제할 메시지를 삭제할 권한이 있는가
                        if (deleteMessage.getReceiverSessionId().equals(userSessionId)) {
                                deleteMessage.setReceiverDelete(deleteAt);
                                isChanged = true;
                        }
                        if (deleteMessage.getSenderSessionId().equals(userSessionId)) {
                                deleteMessage.setSenderDelete(deleteAt);
                                isChanged = true;
                        }
                        // 변경된 메시지를 저장
                        if (isChanged) {
                                m_Default_Repository.save(deleteMessage);
                                return ResponseEntity.ok("삭제 성공");
                        } else {
                                return ResponseEntity.ok("권한 없음");
                        }

                }
                return ResponseEntity.ok("삭제 실패");
        }

        /*
         * ---나중에 배열로 처리도 가능하게 만들예정---
         * 1. 사용자 검증
         * 2. 데이터 셋 만들기
         * 3. 생성
         */

        @PostMapping("/create")
        @Operation(summary = "사용자 기준 생성", description = "사용자가 리스트로 받은 사람들에게 메시지를 보내는 기능")
        public String create(
                        // @Parameter(description = "보내는 사람 세션아이디", required = true)
                        // @RequestParam(defaultValue = "4d5e6f7g-8h9i-0j1k-l2m3-n4o5p6q7r8s9") String
                        // senderSessionId,
                        // @Parameter(description = "받는 사람 세션아이디", required = true)
                        // @RequestParam(defaultValue = "4d5e6f7g-8h9i-0j1k-l2m3-n4o5p6q7r8s9") String
                        // targetSessionIds,
                        // @Parameter(description = "삭제 여부", required = true) @RequestParam(defaultValue
                        // = "F") String deleteAt,
                        // @Parameter(description = "확인 여부", required = true) @RequestParam(defaultValue
                        // = "F") String checkAt,
                        @Parameter(description = "메시지 제목, 내용", required = true) @RequestBody MSG_MessagePost_DTO msg_MessagePost_DTO) {
                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String userSessionId = auth.getPrincipal().toString();
                String sendDate = new Date().toString().replace("KST", "GMT"); // 기본 시스템 설정
                String receiveDate = new Date().toString().replace("KST", "GMT"); // 기본 시스템 설정
                String messageTitle = msg_MessagePost_DTO.getMessageTitle();
                String messageContent = msg_MessagePost_DTO.getMessageContent();
                String deleteAt = "F"; // 기본 시스템 설정
                String checkAt = "F"; // 기본 시스템 설정
                List<String> targetSessionIds = msg_MessagePost_DTO.getTargetSessionIds();
                // if (userSessionId.equals(userSessionId)) {
                for (String target : targetSessionIds) {
                        Message newMessage = Message.builder()
                                        .senderSessionId(userSessionId)
                                        .receiverSessionId(target)
                                        .senderDelete(deleteAt)
                                        .receiverDelete(deleteAt)
                                        .sendTime(sendDate)
                                        .receiveTime(receiveDate)
                                        .messageTitle(messageTitle)
                                        .messageContent(messageContent)
                                        .senderCheck(checkAt)
                                        .receiverCheck(checkAt)
                                        .build();
                        m_Default_Repository.save(newMessage);
                }
                return "생성 성공";
                // }
                // return "생성 실패";
        }

        @Autowired
        MSG_GetFriendList_Repository friendList_Repository;

        @GetMapping("/getFriendList")
        @Operation(summary = "친구 목록 조회", description = "사용자 기준 친구 목록 조회")
        public Page<MSG_GetFriendList_Projection> getFriendList(
                        @Parameter(description = "검색 파라미터", required = false) @RequestParam(required = false) String searchParam,
                        int page, int size) {
                Pageable pageable = PageRequest.of(page, size);
                Page<MSG_GetFriendList_Projection> selected = friendList_Repository.findByNicknameContaining(
                                searchParam,
                                pageable);
                return selected;
        }

        @Autowired
        MSG_Details_Repository msg_Details_Repository;

        // 조회 하는 부분
        @GetMapping("/details")
        @Operation(summary = "사용자 기준 상세 정보 조회", description = "사용자 기준 메시지 조회")
        public MSG_Details_Projection details(
                        @Parameter(description = "메시지 키", required = true) @RequestParam(defaultValue = "self-1") String messageId) {

                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String userSessionId = auth.getPrincipal().toString();
                Optional<MSG_Details_Projection> detailSelect = msg_Details_Repository.findByMessageId(messageId);
                if (detailSelect.isPresent()) {
                        MSG_Details_Projection detailMessage = detailSelect.get();

                        if (detailMessage.getReceiverSessionId().equals(userSessionId)) {
                                return detailMessage;
                        }

                        if (detailMessage.getSenderSessionId().equals(userSessionId)) {
                                return detailMessage;
                        }
                        // 변경된 메시지를 저장
                        return null;
                }
                return null;
        }

        @Autowired
        MSG_UserNickname_Repository msg_UserNickname_Repository;

        // 닉네임 변환 기능 메시지에 내가 포함되는 경우
        @GetMapping("/parse/nickname")
        @Operation(summary = "세션아이디로 닉네임 변환", description = "사용자 닉네임 불러오기")
        public MSG_UserNickname_Projection parseSessionToNickname(
                        @Parameter(description = "변경세션아이디", required = true) @RequestParam(defaultValue = "4d5e6f7g-8h9i-0j1k-l2m3-n4o5p6q7r8s9") String parseSessionId,
                        @Parameter(description = "조회 메시지", required = true) @RequestParam(defaultValue = "self-1") String messageId) {

                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String userSessionId = auth.getPrincipal().toString();

                Optional<Message> detailSelect = m_Default_Repository.findById(messageId);
                if (detailSelect.isPresent()) {
                        Message detailMessage = detailSelect.get();
                        Optional<MSG_UserNickname_Projection> userSelect = msg_UserNickname_Repository
                                        .findBySessionId(parseSessionId);
                        if (detailMessage.getReceiverSessionId().equals(userSessionId)
                                        || detailMessage.getSenderSessionId().equals(userSessionId)
                                                        && userSelect.isPresent()) {
                                return userSelect.get();
                        }
                }
                return null;
        }

        @PostMapping("/check")
        @Operation(summary = "사용자 기준 읽음 표시 업데이트", description = "사용자 관점의 TF 설정")
        public String check(
                        @Parameter(description = "변경 대상 기본키", required = true) @RequestParam(defaultValue = "16") String updateId,
                        @Parameter(description = "변경 여부 : T/F", required = true) @RequestParam(defaultValue = "T") String updateAt) {

                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String userSessionId = auth.getPrincipal().toString();
                Optional<Message> deleteSelect = m_Default_Repository.findById(updateId);
                if (deleteSelect.isPresent()) {
                        Message deleteMessage = deleteSelect.get();

                        if (deleteMessage.getReceiverSessionId().equals(userSessionId)) {
                                deleteMessage.setReceiverCheck(updateAt);
                        }

                        if (deleteMessage.getSenderSessionId().equals(userSessionId)) {
                                deleteMessage.setSenderCheck(updateAt);
                        }
                        // 변경된 메시지를 저장
                        m_Default_Repository.save(deleteMessage);
                        return "변경 성공";
                }
                return "변경 실패";
        }

}