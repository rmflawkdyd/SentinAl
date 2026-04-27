package com.example.sentinal.domain.usecase.guardian

import com.example.sentinal.domain.model.GuardianResult
import com.example.sentinal.domain.repository.DeviceAggregateRepository
import com.example.sentinal.domain.rule.GuardianRuleEngine
import javax.inject.Inject

class GetGuardianResultUseCase  @Inject constructor(
    private val deviceAggregateRepository: DeviceAggregateRepository,
    private val guardianRuleEngine: GuardianRuleEngine
){
    suspend operator fun invoke(): GuardianResult?{
        val latestAggregate = deviceAggregateRepository.getLatestAggregate()?:return null
        return guardianRuleEngine.evaluate(latestAggregate)
    }
}