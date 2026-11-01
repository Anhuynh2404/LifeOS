package com.example.core.event

sealed class LifeEvent {
    // Phase 1: Alarm & Morning Routine
    data class AlarmDismissed(val alarmId: Long, val onTime: Boolean, val missionUsed: String) : LifeEvent()
    data class MorningRoutineCompleted(val quoteRead: Boolean, val meditationDone: Boolean) : LifeEvent()

    // Phase 2 & 3: Calendar & Tasks & Focus
    data class TaskCompleted(
        val taskId: Long,
        val title: String,
        val skillId: Long? = null,
        val goalId: Long? = null,
        val challengeId: Long? = null,
        val difficulty: String = "MEDIUM"
    ) : LifeEvent()
    data class FocusSessionCompleted(val durationMinutes: Int, val appDistractionsBlocked: Int) : LifeEvent()

    // Phase 4: Goals & Skills
    data class GoalKpiUpdated(val goalId: Long, val newKpi: Float, val targetKpi: Float) : LifeEvent()
    data class SkillLeveledUp(val skillId: Long, val skillTitle: String, val newLevel: Int) : LifeEvent()

    // Phase 5: Challenges & Social
    data class ChallengeStreakIncreased(val challengeId: Long, val challengeTitle: String, val streak: Int) : LifeEvent()
    data class ChallengeFailed(val challengeId: Long, val challengeTitle: String, val wagerAmount: Long, val wagerType: String) : LifeEvent()
    data class CookingChallengeCompleted(val challengeId: Long) : LifeEvent()

    // Phase 2: Mindful Finance
    data class ImpulseRejected(val itemId: Long, val itemName: String, val savedPrice: Long) : LifeEvent()
    data class ImpulseApproved(val itemId: Long, val itemName: String, val price: Long) : LifeEvent()
    data class BudgetSurplusAchieved(val monthYear: String, val savedAmount: Long) : LifeEvent()
    data class WheelRewardWon(val rewardTitle: String, val isMonetary: Boolean, val amount: Long) : LifeEvent()
}
