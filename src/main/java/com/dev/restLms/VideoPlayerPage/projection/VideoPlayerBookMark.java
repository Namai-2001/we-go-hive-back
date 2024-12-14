package com.dev.restLms.VideoPlayerPage.projection;


public interface VideoPlayerBookMark {
  // 비디오 플레이어 내에서 CRD 진행 해야함
  // 북마크 시간이 기본키로 되어 있어서 원래는 ERD 상에서는 기본키 + 
  // 외래키 3개가 복합 기본키로 되어 있어야 하는데 지금은 시간이 기본키여서 중복됨.

   String getIncreaseId();
   String getBmSessionId();
   String getBmEpisodeId();
   String getBmOfferedSubjectsId();
   String getBookmarkTime();
   String getBookmarkContent();
}