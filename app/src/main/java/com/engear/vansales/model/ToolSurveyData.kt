package com.engear.vansales.model

import com.google.gson.annotations.SerializedName

data class ToolSurveyData(
    @SerializedName("data")
    val `data`: List<DistData>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
) {
    data class DistData(
        @SerializedName("user_id")
        var cusotmer_id: String,
        @SerializedName("distribution_customer")
        var distributionCustomer: List<DistributionCustomer>
    ) {
        data class DistributionCustomer(
            @SerializedName("distribution_id")
            var id: String,
            @SerializedName("distribution")
            var distribution: DistributionObject?
        ) {
            data class DistributionObject(
                @SerializedName("id")
                var id: String,
                @SerializedName("name")
                var name: String,
                @SerializedName("distribution_survey")
                var survey: List<Survey>
            ) {
                data class Survey(
                    @SerializedName("id")
                    var id: String,
                    @SerializedName("survey_type_id")
                    var survey_type_id: String,
                    @SerializedName("distribution_id")
                    var distribution_id: String,
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
        }
    }
}