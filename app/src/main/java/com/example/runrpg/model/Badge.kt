package com.example.runrpg.model

data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String
)

object Badges {
    val ALL = listOf(
        Badge("FIRST_RUN", "اولین قدم", "اولین جلسه دویدن خود را کامل کردید", "🏁"),
        Badge("FIRST_5K", "اولین ۵ کیلومتر", "یک دویدن ۵ کیلومتری یا بیشتر ثبت کردید", "🥇"),
        Badge("STREAK_3", "۳ روز متوالی", "سه روز پیاپی دویدید", "🔥"),
        Badge("STREAK_7", "یک هفته آتشین", "هفت روز پیاپی دویدید", "🔥🔥")
    )

    fun byId(id: String): Badge? = ALL.find { it.id == id }
}
