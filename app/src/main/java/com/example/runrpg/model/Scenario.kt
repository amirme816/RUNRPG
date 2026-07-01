package com.example.runrpg.model

enum class ScenarioType { ZOMBIE, MARS }

data class Scenario(
    val type: ScenarioType,
    val title: String,
    val introText: String,
    val outroText: String
)

object Scenarios {

    val ZOMBIE_ESCAPE = Scenario(
        type = ScenarioType.ZOMBIE,
        title = "فرار از زامبی‌ها",
        introText = "آژیر خطر به صدا درآمده است. زامبی‌ها خطوط دفاعی شهر را شکسته‌اند و به سمت پناهگاه شما می‌آیند. " +
                "هیچ خودرویی روشن نمی‌شود. تنها راه نجات، دویدن تا پناهگاه اصلی در ۳ کیلومتری اینجاست. " +
                "بند کفش‌هایت را محکم کن، زامبی‌ها نزدیکند... حرکت کن!",
        outroText = "درب‌های آهنی پناهگاه پشت سرت بسته شدند! صدای ضربه زامبی‌ها به درب می‌آید اما تو جایت امن است. " +
                "آذوقه‌ای که در راه جمع کردی به بازمانده‌ها کمک می‌کند. تو امروز را زنده ماندی!"
    )

    val MARS_MISSION = Scenario(
        type = ScenarioType.MARS,
        title = "مأموریت مریخ",
        introText = "سفینه اکتشافی شما روی صخره‌های مریخ سقوط کرده است. مخزن اکسیژن لباس فضانوردی آسیب دیده و " +
                "فقط به اندازه چند کیلومتر دویدن دوام می‌آورد. ایستگاه مرکزی در دیدرس است. " +
                "سیستم ناوبری فعال شد... دویدن به سمت ایستگاه را آغاز کن!",
        outroText = "کد امنیتی تایید شد. دریچه هوای ایستگاه مریخ باز شد و اکسیژن خالص به لباستان تزریق گردید. " +
                "سیستم تعلیق لباس ارتقا یافت. مأموریت امروز با موفقیت به پایان رسید فضانورد!"
    )

    fun byType(type: ScenarioType): Scenario = when (type) {
        ScenarioType.ZOMBIE -> ZOMBIE_ESCAPE
        ScenarioType.MARS -> MARS_MISSION
    }
}
