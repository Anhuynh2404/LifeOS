package com.example.core.engine

import com.example.core.event.LifeEvent
import com.example.data.AppDatabase
import com.example.data.entity.EventLogEntity
import com.example.data.entity.TransactionEntity
import com.example.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RuleEngine(
    private val database: AppDatabase,
    private val userPrefs: UserPreferencesRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun processEvent(event: LifeEvent, onActionPrompt: ((title: String, message: String) -> Unit)? = null) {
        scope.launch {
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            when (event) {
                is LifeEvent.AlarmDismissed -> {
                    // +XP Kỷ luật, check-in thử thách Dậy sớm
                    val xp = if (event.onTime) 50 else 20
                    userPrefs.addXpAndCoins(xpGained = xp, coinsGained = 15)

                    // Find and check in early wake-up challenge if exists
                    val challenges = database.challengeDao().getAllChallenges().first()
                    val earlyWakeChallenge = challenges.find { it.title.contains("Dậy sớm", ignoreCase = true) }
                    if (earlyWakeChallenge != null && earlyWakeChallenge.lastCheckInDate != todayStr) {
                        val newStreak = earlyWakeChallenge.currentStreak + 1
                        val updated = earlyWakeChallenge.copy(
                            currentStreak = newStreak,
                            bestStreak = maxOf(earlyWakeChallenge.bestStreak, newStreak),
                            lastCheckInDate = todayStr
                        )
                        database.challengeDao().updateChallenge(updated)

                        // Milestone streak award
                        if (newStreak % 7 == 0 || newStreak == 21 || newStreak == 30) {
                            userPrefs.addSpinTickets(1)
                            userPrefs.addXpAndCoins(xpGained = 100, coinsGained = 50)
                        }
                    }

                    // Level up Kỷ luật skill
                    val skills = database.skillDao().getAllSkills().first()
                    val disciplineSkill = skills.find { it.title.contains("Kỷ luật", ignoreCase = true) || it.title.contains("Dậy sớm", ignoreCase = true) }
                    if (disciplineSkill != null) {
                        val newXp = disciplineSkill.currentXp + xp
                        if (newXp >= disciplineSkill.requiredXp) {
                            database.skillDao().updateSkill(
                                disciplineSkill.copy(
                                    level = disciplineSkill.level + 1,
                                    currentXp = newXp - disciplineSkill.requiredXp,
                                    requiredXp = (disciplineSkill.requiredXp * 1.5f).toInt()
                                )
                            )
                        } else {
                            database.skillDao().updateSkill(disciplineSkill.copy(currentXp = newXp))
                        }
                    }

                    database.eventLogDao().insertEvent(
                        EventLogEntity(
                            eventType = "ALARM_DISMISSED",
                            description = "Thức dậy đúng giờ với nhiệm vụ ${event.missionUsed}. +$xp XP Kỷ luật!",
                            xpGained = xp,
                            coinsGained = 15
                        )
                    )
                }

                is LifeEvent.TaskCompleted -> {
                    val xp = when (event.difficulty) {
                        "HARD" -> 80
                        "MEDIUM" -> 45
                        else -> 25
                    }
                    val coins = when (event.difficulty) {
                        "HARD" -> 30
                        "MEDIUM" -> 15
                        else -> 10
                    }
                    userPrefs.addXpAndCoins(xpGained = xp, coinsGained = coins)

                    // Update linked skill
                    if (event.skillId != null) {
                        val skill = database.skillDao().getSkillById(event.skillId)
                        if (skill != null) {
                            val newXp = skill.currentXp + xp
                            if (newXp >= skill.requiredXp) {
                                database.skillDao().updateSkill(
                                    skill.copy(
                                        level = skill.level + 1,
                                        currentXp = newXp - skill.requiredXp,
                                        requiredXp = (skill.requiredXp * 1.5f).toInt()
                                    )
                                )
                            } else {
                                database.skillDao().updateSkill(skill.copy(currentXp = newXp))
                            }
                        }
                    }

                    // Update linked goal
                    if (event.goalId != null) {
                        val goal = database.goalDao().getGoalById(event.goalId)
                        if (goal != null) {
                            val step = (goal.targetKpi / 20f).coerceAtLeast(0.1f)
                            val newKpi = (goal.currentKpi + step).coerceAtMost(goal.targetKpi)
                            database.goalDao().updateGoal(goal.copy(currentKpi = newKpi))
                        }
                    }

                    // Check in linked challenge
                    if (event.challengeId != null) {
                        val challenge = database.challengeDao().getChallengeById(event.challengeId)
                        if (challenge != null && challenge.lastCheckInDate != todayStr) {
                            val newStreak = challenge.currentStreak + 1
                            database.challengeDao().updateChallenge(
                                challenge.copy(
                                    currentStreak = newStreak,
                                    bestStreak = maxOf(challenge.bestStreak, newStreak),
                                    lastCheckInDate = todayStr
                                )
                            )
                        }
                    }

                    database.eventLogDao().insertEvent(
                        EventLogEntity(
                            eventType = "TASK_COMPLETED",
                            description = "Hoàn thành: ${event.title}. +$xp XP, +$coins Xu",
                            xpGained = xp,
                            coinsGained = coins
                        )
                    )
                }

                is LifeEvent.CookingChallengeCompleted -> {
                    // Trigger suggestion: "Chuyển 50.000đ vào Quỹ Du Lịch"
                    val funds = database.financeDao().getAllSavingsFunds().first()
                    val travelFund = funds.find { it.name.contains("Du lịch", ignoreCase = true) }
                    if (travelFund != null) {
                        database.financeDao().updateSavingsFund(
                            travelFund.copy(currentAmount = travelFund.currentAmount + 50000)
                        )
                    }
                    database.eventLogDao().insertEvent(
                        EventLogEntity(
                            eventType = "HABIT_TRANSFER",
                            description = "Thử thách tự nấu ăn hoàn thành! Đã cộng 50.000đ vào Quỹ Du Lịch.",
                            savedAmount = 50000,
                            xpGained = 40,
                            coinsGained = 20
                        )
                    )
                    userPrefs.addXpAndCoins(40, 20)
                }

                is LifeEvent.ImpulseRejected -> {
                    // Record money saved, +XP Tự chủ tài chính, upgrade virtual base
                    userPrefs.addXpAndCoins(xpGained = 75, coinsGained = 50)
                    val skills = database.skillDao().getAllSkills().first()
                    val financeSkill = skills.find { it.title.contains("Tài chính", ignoreCase = true) }
                    if (financeSkill != null) {
                        val newXp = financeSkill.currentXp + 75
                        database.skillDao().updateSkill(financeSkill.copy(currentXp = newXp))
                    }

                    database.eventLogDao().insertEvent(
                        EventLogEntity(
                            eventType = "IMPULSE_REJECTED",
                            description = "Đã vượt qua cám dỗ mua '${event.itemName}'. Tiết kiệm được ${event.savedPrice}đ!",
                            savedAmount = event.savedPrice,
                            xpGained = 75,
                            coinsGained = 50
                        )
                    )
                }

                is LifeEvent.ImpulseApproved -> {
                    // Create planned transaction
                    val wallets = database.financeDao().getAllWallets().first()
                    val defaultWallet = wallets.firstOrNull()?.id ?: 1L
                    database.financeDao().insertTransaction(
                        TransactionEntity(
                            walletId = defaultWallet,
                            amount = event.price,
                            type = "PLANNED_EXPENSE",
                            category = "Mua sắm đã cân nhắc",
                            note = "Phê duyệt sau thời gian chờ: ${event.itemName}",
                            isConfirmed = true
                        )
                    )
                    database.eventLogDao().insertEvent(
                        EventLogEntity(
                            eventType = "IMPULSE_APPROVED",
                            description = "Đã xác nhận mua '${event.itemName}' sau thời gian suy ngẫm."
                        )
                    )
                }

                is LifeEvent.WheelRewardWon -> {
                    // If monetary reward, create planned expense in Quỹ Giải Trí
                    if (event.isMonetary && event.amount > 0) {
                        val funds = database.financeDao().getAllSavingsFunds().first()
                        val entertainmentFund = funds.find { it.name.contains("Giải trí", ignoreCase = true) }
                        if (entertainmentFund != null) {
                            val newAmount = (entertainmentFund.currentAmount - event.amount).coerceAtLeast(0)
                            database.financeDao().updateSavingsFund(
                                entertainmentFund.copy(currentAmount = newAmount)
                            )
                        }

                        val wallets = database.financeDao().getAllWallets().first()
                        val walletId = wallets.firstOrNull()?.id ?: 1L
                        database.financeDao().insertTransaction(
                            TransactionEntity(
                                walletId = walletId,
                                amount = event.amount,
                                type = "PLANNED_EXPENSE",
                                category = "Quỹ Giải Trí - Thưởng bản thân",
                                note = "Trúng thưởng vòng quay: ${event.rewardTitle}",
                                isConfirmed = true
                            )
                        )
                    }

                    database.eventLogDao().insertEvent(
                        EventLogEntity(
                            eventType = "WHEEL_REWARD",
                            description = "Trúng thưởng: ${event.rewardTitle}!"
                        )
                    )
                }

                is LifeEvent.FocusSessionCompleted -> {
                    val xp = event.durationMinutes * 2
                    val coins = event.durationMinutes / 2
                    userPrefs.addXpAndCoins(xpGained = xp, coinsGained = coins)
                    database.eventLogDao().insertEvent(
                        EventLogEntity(
                            eventType = "FOCUS_SESSION",
                            description = "Hoàn thành phiên tập trung ${event.durationMinutes} phút. Chặn ${event.appDistractionsBlocked} cám dỗ.",
                            xpGained = xp,
                            coinsGained = coins
                        )
                    )
                }

                else -> {}
            }
        }
    }
}
