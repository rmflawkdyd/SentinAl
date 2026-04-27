package com.example.sentinal.data.ai

import com.example.sentinal.domain.ai.ChatAnswerGenerator
import com.example.sentinal.domain.model.ChatAnswer
import com.example.sentinal.domain.model.ChatContext
import com.example.sentinal.domain.model.ChatIntent
import com.example.sentinal.domain.model.ModelTier
import javax.inject.Inject

class TemplateChatAnswerGenerator @Inject constructor(): ChatAnswerGenerator {
     override suspend fun generateAnswer(
        question: String,
        intent: ChatIntent,
        context: ChatContext,
    ): ChatAnswer {
        val guardian = context.guardianResult
        val analytics = context.analyticsSummary

        return when (intent) {
            ChatIntent.DeviceStatus -> {
                if (guardian == null) {
                    ChatAnswer(
                        text = "아직 최근 기기 상태 데이터가 충분하지 않습니다. Usage Access 권한과 데이터 수집 상태를 확인해 주세요.",
                        source = "Guardian 최근 집계 데이터",
                        modelTier = ModelTier.Template
                    )
                } else {
                    ChatAnswer(
                        text = "현재 상태는 ${guardian.status}이고 리소스 점수는 ${guardian.score}점입니다. ${guardian.insight}",
                        source = "Guardian 최근 5분 집계 데이터",
                        modelTier = ModelTier.Template
                    )
                }
            }

            ChatIntent.HeatConcern -> {
                if (guardian == null) {
                    ChatAnswer(
                        text = "기기 온도는 직접 수집하지 않아 발열 여부를 판단할 수 없습니다. 아직 최근 리소스 데이터도 부족해 참고 분석을 제공하기 어렵습니다.",
                        source = "Guardian 최근 집계 데이터",
                        modelTier = ModelTier.Template
                    )
                } else {
                    ChatAnswer(
                        text = "기기 온도는 직접 수집하지 않아 발열 자체를 단정할 수는 없습니다. 다만 현재 상태는 ${guardian.status}이고, 메모리 여유율은 ${guardian.avgAvailableMemPercent.format(1)}%, 앱 전환은 ${guardian.appSwitchCount}회입니다. 이 지표가 나쁘면 기기에 부담이 있었을 가능성은 있습니다.",
                        source = "Guardian 최근 5분 집계 데이터",
                        modelTier = ModelTier.Template
                    )
                }
            }

            ChatIntent.SlowDevice -> {
                if (guardian == null) {
                    ChatAnswer(
                        text = "아직 최근 리소스 데이터가 부족해서 느려짐 원인을 분석하기 어렵습니다.",
                        source = "Guardian 최근 집계 데이터",
                        modelTier = ModelTier.Template
                    )
                } else {
                    ChatAnswer(
                        text = "느려짐을 직접 진단할 수는 없지만, 내부 지표 기준으로는 메모리 여유율 ${guardian.avgAvailableMemPercent.format(1)}%, 앱 전환 ${guardian.appSwitchCount}회가 확인됩니다. 현재 상태는 ${guardian.status}입니다.",
                        source = "Guardian 최근 5분 집계 데이터",
                        modelTier = ModelTier.Template
                    )
                }
            }

            ChatIntent.BatteryConcern -> {
                ChatAnswer(
                    text = "배터리 상태나 수명은 직접 수집하지 않아 단정할 수 없습니다. 다만 최근 7일 총 사용 시간은 ${analytics.totalUsageMillis.toHoursText()}이고, 사용량 변화율은 ${analytics.changeRatePercent.format(1)}%입니다. 사용 시간이 급증했다면 배터리 소모 체감에 영향을 줄 수 있습니다.",
                    source = "Analytics 주간 사용량 데이터",
                    modelTier = ModelTier.Template
                )
            }

            ChatIntent.SecurityCheck -> {
                if (guardian == null) {
                    ChatAnswer(
                        text = "이 앱은 백신처럼 바이러스를 검사하지 않습니다. 또한 아직 최근 리소스 데이터가 부족해 이상 징후 참고도 어렵습니다. 정확한 검사는 Android 보안 설정 또는 신뢰할 수 있는 보안 앱을 사용해 주세요.",
                        source = "Template 안전 안내",
                        modelTier = ModelTier.Template
                    )
                } else {
                    ChatAnswer(
                        text = "이 앱은 바이러스 검사나 악성 앱 판정을 수행하지 않습니다. 다만 내부 지표 기준으로 현재 상태는 ${guardian.status}, 리소스 점수는 ${guardian.score}점입니다. 이 정보는 보안 진단이 아니라 최근 리소스 이상 징후 참고용입니다.",
                        source = "Guardian 최근 5분 집계 데이터",
                        modelTier = ModelTier.Template
                    )
                }
            }

            ChatIntent.UsageSummary,
            ChatIntent.WeeklySummary -> {
                ChatAnswer(
                    text = "최근 7일 총 사용 시간은 ${analytics.totalUsageMillis.toHoursText()}입니다. 지난 기간 대비 변화율은 ${analytics.changeRatePercent.format(1)}%입니다.",
                    source = "Analytics 주간 사용량 데이터",
                    modelTier = ModelTier.Template
                )
            }

            ChatIntent.CategoryUsage -> {
                val topCategory = context.topCategory

                if (topCategory == null) {
                    ChatAnswer(
                        text = "아직 카테고리별 사용 데이터가 충분하지 않습니다.",
                        source = "Analytics 카테고리 데이터",
                        modelTier = ModelTier.Template
                    )
                } else {
                    ChatAnswer(
                        text = "가장 많이 사용한 카테고리는 ${topCategory.category}이고, 사용 시간은 ${topCategory.usageMillis.toHoursText()}입니다.",
                        source = "Analytics 카테고리 데이터",
                        modelTier = ModelTier.Template
                    )
                }
            }

            ChatIntent.MostUsedApp -> {
                val topApp = analytics.topApps.firstOrNull()

                if (topApp == null) {
                    ChatAnswer(
                        text = "아직 앱별 사용량 데이터가 충분하지 않습니다.",
                        source = "Analytics 앱별 사용량 데이터",
                        modelTier = ModelTier.Template
                    )
                } else {
                    ChatAnswer(
                        text = "최근 7일 동안 가장 오래 사용한 앱은 ${topApp.appName}입니다. 사용 시간은 ${topApp.usageMillis.toHoursText()}이고, 실행 횟수는 ${topApp.launchCount}회입니다.",
                        source = "Analytics 앱별 사용량 데이터",
                        modelTier = ModelTier.Template
                    )
                }
            }

            ChatIntent.NightUsage -> {
                ChatAnswer(
                    text = "최근 7일 사용 시간 중 심야 사용 비중은 ${analytics.nightUsageRatePercent.format(1)}%입니다. 심야 사용은 밤 10시부터 다음 날 오전 6시까지의 사용 시간을 기준으로 계산했습니다.",
                    source = "Analytics 심야 사용량 데이터",
                    modelTier = ModelTier.Template
                )
            }


            ChatIntent.Unsupported -> {
                ChatAnswer(
                    text = "저는 현재 기기 상태, 발열 참고, 느려짐 참고, 배터리 사용 패턴, 보안 관련 제한 안내, 주간 사용량, 심야 사용 비중, 많이 쓴 앱, 카테고리 사용량처럼 앱 내부 데이터에 근거한 질문에 답할 수 있습니다.",
                    source = "Template fallback",
                    modelTier = ModelTier.Template
                )
            }
        }
    }


    private fun Long.toHoursText(): String {
        val hours = this / (1000f * 60f * 60f)
        return "${hours.format(1)}시간"
    }

    private fun Float.format(digits: Int): String {
        return "%.${digits}f".format(this)
    }
}
