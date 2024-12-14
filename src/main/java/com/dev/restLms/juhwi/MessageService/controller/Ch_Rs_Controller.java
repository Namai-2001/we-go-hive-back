package com.dev.restLms.juhwi.MessageService.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.entity.Message;
import com.dev.restLms.juhwi.MessageService.projection.Ch_M_MASR_Projection;
import com.dev.restLms.juhwi.MessageService.projection.Ch_M_MSR_Projection;
import com.dev.restLms.juhwi.MessageService.projection.Ch_M_Rs_Projection;
import com.dev.restLms.juhwi.MessageService.projection.Ch_M_Ss_Projcction;
import com.dev.restLms.juhwi.MessageService.repository.Ch_M_MASR_Repository;
import com.dev.restLms.juhwi.MessageService.repository.Ch_M_MSR_Repository;
import com.dev.restLms.juhwi.MessageService.repository.Ch_M_Rs_Repository;
import com.dev.restLms.juhwi.MessageService.repository.Ch_M_Ss_Repository;
import com.dev.restLms.juhwi.MessageService.repository.M_Default_Repository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@Tag(name = "받은 쪽지함 API", description = "받은 쪽지함 관련 기능을 모아둔 컨트롤러")
@RequestMapping("/message")
public class Ch_Rs_Controller {
        @Autowired
        Ch_M_Rs_Repository ch_M_Rs_Repository;

        @GetMapping("/inbox")
        @Operation(summary = "받은 쪽지함 조회", description = "받은 쪽지함을 사용자 기준으로 조회, 삭제 여부 선택")
        public Page<Ch_M_Rs_Projection> getReceivedPage(
                        @Parameter(description = "조회 기준 사용자 아이디", required = true) @RequestParam(defaultValue = "박상수") String userSessionId,

                        @Parameter(description = "조회 기준 T:삭제됨 F:삭제안됨", required = true) @RequestParam(defaultValue = "F") String isDelete,

                        @RequestParam(defaultValue = "0") int page,

                        @RequestParam(defaultValue = "10") int size) {
                Pageable pageable = PageRequest.of(page, size);
                Page<Ch_M_Rs_Projection> resultPage = ch_M_Rs_Repository
                                .findByReceiverDeleteAndReceiverSessionIdAndSenderSessionIdNot(isDelete,
                                                userSessionId, userSessionId, pageable);
                return resultPage;
        }

        @Autowired
        Ch_M_Ss_Repository ch_M_Ss_Repository;

        @GetMapping("/sent")
        @Operation(summary = "보낸 쪽지함 조회", description = "보낸 쪽지함을 사용자 기준으로 조회, 삭제 여부 선택")
        public Page<Ch_M_Ss_Projcction> getSentPage(
                        @Parameter(description = "조회 기준 사용자 아이디", required = true) @RequestParam(defaultValue = "박상수") String userSessionId,

                        @Parameter(description = "조회 기준 T:삭제됨 F:삭제안됨", required = true) @RequestParam(defaultValue = "F") String isDelete,

                        @RequestParam(defaultValue = "0") int page,

                        @RequestParam(defaultValue = "10") int size) {
                Pageable pageable = PageRequest.of(page, size);

                Page<Ch_M_Ss_Projcction> resultPage = ch_M_Ss_Repository
                                .findBySenderDeleteAndSenderSessionIdAndReceiverSessionIdNot(isDelete,
                                                userSessionId, userSessionId, pageable);
                return resultPage;
        }

        @Autowired
        Ch_M_MSR_Repository ch_M_MSR_Repository;

        @GetMapping("/self")
        @Operation(summary = "내게 쓴 쪽지함 조회", description = "보낸 쪽지함을 사용자 기준으로 조회, 삭제 여부 선택")
        public Page<Ch_M_MSR_Projection> getmySentPage(
                        @Parameter(description = "조회 기준 사용자 아이디", required = true) @RequestParam(defaultValue = "3박상수") String userSessionId,

                        @Parameter(description = "조회 기준 T:삭제됨 F:삭제안됨", required = true) @RequestParam(defaultValue = "F") String isDelete,

                        @RequestParam(defaultValue = "0") int page,

                        @RequestParam(defaultValue = "10") int size) {
                Pageable pageable = PageRequest.of(page, size);
                Page<Ch_M_MSR_Projection> resultPage = ch_M_MSR_Repository
                                .findBySenderDeleteAndSenderSessionIdAndReceiverSessionId(isDelete, userSessionId,
                                                userSessionId,
                                                pageable);
                return resultPage;
        }

        @Autowired
        Ch_M_MASR_Repository ch_M_MASR_Repository;

        @GetMapping("/admin")
        @Operation(summary = "관리자 공지 조회", description = "관리자가 작성한 공지를 조회, 삭제 여부 선택")
        public Page<Ch_M_MASR_Projection> getAdminMessage(
                        @Parameter(description = "권한명", required = true) @RequestParam(defaultValue = "관리자") String userReceiveSessionId,

                        @RequestParam(defaultValue = "0") int page,

                        @RequestParam(defaultValue = "10") int size) {

                Pageable pageable = PageRequest.of(page, size);

                // 추후 그룹이 고도화 되었을 때 변경
                String permissionGroup = "관리자";
                String targetGroup = "모두";
                Page<Ch_M_MASR_Projection> resultPage = ch_M_MASR_Repository.findBySenderSessionIdAndReceiverSessionId(
                                permissionGroup, targetGroup, pageable);
                return resultPage;
        }

        /*
         * 삭제 기능을 담당하는 Repository
         */
        @Autowired
        M_Default_Repository m_Default_Repository;
        // @PostMapping("/receiveDelete")
        // @Operation(summary = "수신자 기준 삭제", description = "수신자 관점의 TF 설정")
        // public String receiveDelete(
        // @Parameter(description = "로그인 세션아이디", required = true)
        // @RequestParam(defaultValue = "박상수") String userSessionId,

        // @Parameter(description = "삭제 대상 기본키", required = true)
        // @RequestParam(defaultValue = "16") String deleteId,

        // @Parameter(description = "조회 관점", required = true) @RequestParam(defaultValue
        // = "16") String messageType,

        // @Parameter(description = "삭제 여부 : T/F", required = true)
        // @RequestParam(defaultValue = "T") String deleteAt) {

        // Optional<Message> deleteSelect = m_Default_Repository.findById(deleteId);
        // if(deleteSelect.isPresent()){
        // Message deleteMessage = deleteSelect.get();

        // if (deleteMessage.getReceiverSessionId().equals(userSessionId) &&
        // messageType.equals("receiver")) {
        // deleteMessage.setReceiverDelete(deleteAt);
        // m_Default_Repository.save(deleteMessage);
        // return messageType + " 삭제 성공";
        // }
        // if (deleteMessage.getSenderSessionId().equals(userSessionId) &&
        // messageType.equals("sender")) {
        // deleteMessage.setSenderDelete(deleteAt);
        // m_Default_Repository.save(deleteMessage);
        // return messageType + " 삭제 성공";
        // }
        // }
        // // 추후 그룹이 고도화 되었을 때 변경
        // return "삭제 실패";
        // }

        // @PostMapping("/senderDelete")
        // @Operation(summary = "발송자 기준 삭제", description = "발송자 관점 TF 설정")
        // public String senderDelete(
        // @Parameter(description = "로그인 세션아이디", required = true)
        // @RequestParam(defaultValue = "박상수") String userSessionId,

        // @Parameter(description = "삭제 대상 기본키", required = true)
        // @RequestParam(defaultValue = "16") String deleteId,

        // @Parameter(description = "삭제 여부 : T/F", required = true)
        // @RequestParam(defaultValue = "T") String deleteAt) {

        // Optional<Message> deleteSelect = m_Default_Repository.findById(deleteId);
        // if(deleteSelect.isPresent()){
        // Message deleteMessage = deleteSelect.get();
        // if (deleteMessage.getSenderSessionId().equals(userSessionId)) {
        // deleteMessage.setSenderDelete(deleteAt);
        // m_Default_Repository.save(deleteMessage);
        // return "성공";
        // }
        // }
        // // 추후 그룹이 고도화 되었을 때 변경
        // return "실패";
        // }

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
        @PostMapping("/delete")
        @Operation(summary = "사용자자 기준 삭제", description = "사용자 관점의 TF 설정")
        public String delete(
                        @Parameter(description = "로그인 세션아이디", required = true) @RequestParam(defaultValue = "박상수") String userSessionId,
                        @Parameter(description = "삭제 대상 기본키", required = true) @RequestParam(defaultValue = "16") String deleteId,
                        @Parameter(description = "삭제 여부 : T/F", required = true) @RequestParam(defaultValue = "T") String deleteAt) {

                Optional<Message> deleteSelect = m_Default_Repository.findById(deleteId);
                if (deleteSelect.isPresent()) {
                        Message deleteMessage = deleteSelect.get();

                        if (deleteMessage.getReceiverSessionId().equals(userSessionId)) {
                                deleteMessage.setReceiverDelete(deleteAt);
                        }

                        if (deleteMessage.getSenderSessionId().equals(userSessionId)) {
                                deleteMessage.setSenderDelete(deleteAt);
                        }

                        // 변경된 메시지를 저장
                        m_Default_Repository.save(deleteMessage);
                        return "삭제 성공";
                }

                return "삭제 실패";
        }

        /*
         * ---나중에 배열로 처리도 가능하게 만들예정---
         * 1. 사용자 검증
         * 2. 데이터 셋 만들기
         * 3. 생성
         */

        @PostMapping("/create")
        @Operation(summary = "사용자 기준 생성", description = "사용자 관점의 TF 설정")
        public String create(
                        @Parameter(description = "로그인 세션아이디", required = true) @RequestParam(defaultValue = "박상수") String userSessionId,
                        @Parameter(description = "보내는 사람 세션아이디", required = true) @RequestParam(defaultValue = "박상수") String senderSessionId,
                        @Parameter(description = "받는 사람 세션아이디", required = true) @RequestParam(defaultValue = "차상수") String targetSessionId,
                        @Parameter(description = "삭제 여부", required = true) @RequestParam(defaultValue = "F") String deleteAt,
                        @Parameter(description = "메시지 제목", required = true) @RequestBody String messageTitle,
                        @Parameter(description = "메시지 내용", required = true) @RequestBody String messageContent) {
                // 시간 입력 서버로직 구현
                String sendDate = "2024-12-14 11:49";
                String receiveDate = "2024-12-14 11:50";
                if (userSessionId.equals(senderSessionId)) {
                        Message newMessage = Message.builder()
                                        .senderSessionId(senderSessionId)
                                        .receiverSessionId(senderSessionId)
                                        .senderDelete(deleteAt)
                                        .receiverDelete(deleteAt)
                                        .sendTime(sendDate)
                                        .messageTitle(messageTitle)
                                        .messageContent(messageContent)
                                        .receiveTime(receiveDate)
                                        .receiverCheck(deleteAt)
                                        .build();
                        m_Default_Repository.save(newMessage);
                        return "생성 성공";
                }
                return "생성 실패";
        }

        @GetMapping("/details")
        @Operation(summary = "사용자 기준 상세 정보 조회", description = "사용자 기준 메시지 조회")
        public Message details(
        @Parameter(description = "로그인 세션아이디", required = true) @RequestParam(defaultValue = "박상수") String userSessionId,
        @Parameter(description = "메시지 키", required = true) @RequestParam(defaultValue = "message-1") String messageId) {
                
                Optional<Message> detailSelect = m_Default_Repository.findById(messageId);
                if (detailSelect.isPresent()) {
                        Message detailMessage = detailSelect.get();

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
}