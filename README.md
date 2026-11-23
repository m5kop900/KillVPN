# Kill VPN

یک اپ کوچک برای قطع کردن VPN فعال. با یک تپ روی تایل Quick Settings، VPN فعلی قطع می‌شود.

## ساخت APK
1. پروژه را در Android Studio باز کن (Open → پوشه KillVpn).
2. Build → Build APK(s) یا Build → Generate Signed Bundle/APK برای نسخه release.
3. یا از خط فرمان: `gradle wrapper && ./gradlew assembleRelease`
   (نیاز به JDK 17 و Android SDK با platform 34)

## نصب و استفاده
1. APK را نصب کن و اپ را باز کن؛ دکمه‌ی «اجازه‌ی VPN» را بزن و تأیید کن.
2. پنل Quick Settings → ویرایش (قلم) → تایل «قطع VPN» را اضافه کن.
3. هر بار که تایل را بزنی، VPN فعلی قطع می‌شود.

## محدودیت‌ها
- اگر VPN قبلی روی Always-on یا Lockdown باشد، اندروید اجازه‌ی قطع نمی‌دهد.
- بعضی ROMهای سفارشی ممکن است رفتار متفاوت داشته باشند.
