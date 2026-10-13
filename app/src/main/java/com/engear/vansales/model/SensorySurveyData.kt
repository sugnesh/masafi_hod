package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class SensorySurveyData(
    @SerializedName("data")
    val `data`: List<Survey>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class Survey(
        @SerializedName("id")
        var id: String,
        @SerializedName("survey_type_id")
        var survey_type_id: String,
        @SerializedName("name")
        var name: String,
        @SerializedName("survey_question")
        var questions: List<SurveyQuestion>
    ) {
        data class SurveyQuestion(
            @SerializedName("id")
            var id: String,
            @SerializedName("question")
            var question: String,
            @SerializedName("question_type")
            var question_type: String,
            @SerializedName("survey_question_value")
            var questionValue: List<QuestionValue>
        ) {
            data class QuestionValue(
                @SerializedName("id")
                var id: String,
                @SerializedName("survey_id")
                var survey_id: String,
                @SerializedName("survey_question_id")
                var survey_question_id: String,
                @SerializedName("question_value")
                var question_value: String
            )
        }
    }
}