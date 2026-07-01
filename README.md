# RunRPG — برنامه دویدن با المان‌های نقش‌آفرینی

## نحوه راه‌اندازی
1. پوشه `RunRPG` را در Android Studio (نسخه Koala یا جدیدتر) با گزینه **Open** باز کنید.
2. Android Studio به‌صورت خودکار Gradle Wrapper را می‌سازد (یا از منوی `File > Sync Project with Gradle Files` استفاده کنید).
3. یک دستگاه فیزیکی یا شبیه‌ساز با Google Play Services (برای FusedLocationProviderClient) انتخاب کرده و برنامه را اجرا کنید.
4. هنگام اولین اجرا، مجوزهای موقعیت مکانی و نوتیفیکیشن را تایید کنید.

## ساختار پروژه
```
app/src/main/java/com/example/runrpg/
├── data/            # RunEntity, RunDao, AppDatabase (Room) و UserProgressRepository (DataStore)
├── service/         # LocationTrackingService (Foreground Service) و TrackingState
├── gamification/     # GamificationEngine: فرمول‌های XP، کالری، Loot
├── model/            # Scenario (متن‌های داستانی) و Badge
├── viewmodel/         # RunViewModel: اتصال UI به سرویس، دیتابیس و پیشرفت کاربر
└── ui/                # صفحات Jetpack Compose + Navigation
```

## جریان استفاده از برنامه
داشبورد → انتخاب سناریو → متن شروع داستان → صفحه دویدن زنده (با Pause/Resume) →
پایان دویدن → متن پایان داستان → خلاصه نتیجه (XP، Loot، نشان‌های جدید) → بازگشت به داشبورد

## نکات فنی مهم
- **ردیابی GPS**: `LocationTrackingService` یک Foreground Service با نوع `location` است که با
  `FusedLocationProviderClient` نقاط GPS را می‌گیرد، نقاط با دقت پایین (accuracy > 20 متر) را فیلتر
  می‌کند و فاصله بین نقاط متوالی را جمع می‌زند.
- **تایمر Pause/Resume**: با ثبت لحظه شروع هر Segment و جمع‌زدن segment‌ها در `accumulatedSeconds`
  محاسبه می‌شود، بنابراین زمان Pause جزو مدت فعالیت حساب نمی‌شود.
- **فرمول‌ها** (`GamificationEngine`):
  - سرعت میانگین = مسافت (متر) ÷ زمان (ثانیه)
  - کالری = MET(۸) × وزن(kg) × زمان(ساعت)
  - XP = (مسافت متر ÷ ۱۰۰) × ۱۰
  - Loot: سرعت بالاتر → Loot کمیاب‌تر (COMMON → RARE → EPIC → LEGENDARY)
- **Streak و Badges** در `UserProgressRepository` بر اساس روزهای متوالی دویدن محاسبه می‌شود.
- برای استفاده واقعی، وزن کاربر باید از یک صفحه تنظیمات گرفته شود (متد `viewModel.setWeight()`
  از قبل آماده است، فقط کافی‌ست یک UI برای آن اضافه کنید).

## وابستگی‌های کلیدی
Jetpack Compose · Material3 · Navigation Compose · Room · DataStore Preferences ·
Play Services Location · Kotlin Coroutines
