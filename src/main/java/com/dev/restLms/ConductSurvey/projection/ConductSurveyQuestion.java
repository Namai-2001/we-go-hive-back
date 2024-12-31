package com.dev.restLms.ConductSurvey.projection;

public interface ConductSurveyQuestion {
    String getSurveyQuestionId();

    String getQuestionData();
    String getAnswerCategory();
    String getSurveyCategory();
    String getQuestionInactive();
}
