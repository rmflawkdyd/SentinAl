package com.example.sentinal.domain.rule

import com.example.sentinal.domain.model.ChatIntent
import javax.inject.Inject

class ChatQuestionClassifier @Inject constructor(){
    fun classify(question: String): ChatIntent {
        val text = question.lowercase()

        return when {
            text.contains("바이러스") ||
                    text.contains("악성") ||
                    text.contains("보안") ||
                    text.contains("검사") -> ChatIntent.SecurityCheck

            text.contains("발열") ||
                    text.contains("뜨거") ||
                    text.contains("온도") -> ChatIntent.HeatConcern

            text.contains("느려") ||
                    text.contains("버벅") ||
                    text.contains("렉") ||
                    text.contains("느림") -> ChatIntent.SlowDevice

            text.contains("배터리") ||
                    text.contains("충전") -> ChatIntent.BatteryConcern
            text.contains("앱") &&
                    (
                            text.contains("많이") ||
                                    text.contains("제일") ||
                                    text.contains("가장") ||
                                    text.contains("오래")
                            ) -> ChatIntent.MostUsedApp

            text.contains("카테고리") -> ChatIntent.CategoryUsage

            text.contains("심야") ||
                    text.contains("밤") ||
                    text.contains("새벽") -> ChatIntent.NightUsage

            text.contains("사용") ||
                    text.contains("시간") ||
                    text.contains("주간") -> ChatIntent.UsageSummary

            text.contains("요약") ||
                    text.contains("이번 주") -> ChatIntent.WeeklySummary

            text.contains("상태") ||
                    text.contains("문제") ||
                    text.contains("점수") ||
                    text.contains("위험") -> ChatIntent.DeviceStatus

            else -> ChatIntent.Unsupported
        }

    }
}
