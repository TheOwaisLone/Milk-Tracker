# 🥛 Milk Tracker – Android Milk Management App

Milk Tracker is a simple Android app built with **Kotlin** and **Jetpack Compose** to help users record daily milk entries (bought/sold), calculate monthly totals, and set reminders. Designed especially for villagers and small dairy households to replace error-prone handwritten logs.

---

## ⭐ Features
- Add daily milk entries (Sold = green, Bought = red)
- Calendar-based entry view
- Monthly summary (litres + profit/loss)
- Daily reminder using AlarmManager
- Import/Export data backup
- Offline-first (Room Database + DataStore)

---

## 🛠 Tech Stack
- **Kotlin**
- **Jetpack Compose**
- **MVVM Architecture**
- **Room Database**
- **DataStore Preferences**
- **AlarmManager + Notifications**

---

## 📂 Project Structure
```bash
/app/src/main/ 
├── AndroidManifest.xml
├── java
│   └── com
│       └── owais
│           └── milktracker
│               ├── alarm
│               │   ├── MilkReminderReceiver.kt
│               │   └── ReminderManager.kt
│               ├── data
│               │   ├── database
│               │   │   ├── Converters.kt
│               │   │   ├── MilkDatabase.kt
│               │   │   └── MilkEntryDao.kt
│               │   ├── model
│               │   │   └── MilkEntry.kt
│               │   ├── repository
│               │   │   └── MilkRepository.kt
│               │   └── SettingsDataStore.kt
│               ├── MainActivity.kt
│               ├── SplashActivity.kt
│               ├── ui
│               │   ├── calendar
│               │   │   └── CalendarScreen.kt
│               │   ├── components
│               │   │   └── EntryDialog.kt
│               │   ├── settings
│               │   │   └── SettingsScreen.kt
│               │   └── theme
│               │       ├── Color.kt
│               │       ├── Shape.kt
│               │       ├── Theme.kt
│               │       └── Type.kt
│               ├── utils
│               │   ├── AlarmUtils.kt
│               │   ├── NotificationUtils.kt
│               │   └── SettingsPreferences.kt
│               └── viewmodel
│                   ├── MilkViewModelFactory.kt
│                   ├── MilkViewModel.kt
│                   ├── SettingsViewModelFactory.kt
│                   └── SettingsViewModel.kt
└── res
    ├── drawable
    │   ├── ic_launcher_background.xml
    │   ├── ic_launcher_foreground.xml
    │   ├── logo.png
    │   └── splash_background.xml
    ├── mipmap-anydpi-v26
    │   ├── ic_launcher_round.xml
    │   └── ic_launcher.xml
    ├── mipmap-hdpi
    │   ├── ic_launcher_round.webp
    │   └── ic_launcher.webp
    ├── mipmap-mdpi
    │   ├── ic_launcher_round.webp
    │   └── ic_launcher.webp
    ├── mipmap-xhdpi
    │   ├── ic_launcher_round.webp
    │   └── ic_launcher.webp
    ├── mipmap-xxhdpi
    │   ├── ic_launcher_round.webp
    │   └── ic_launcher.webp
    ├── mipmap-xxxhdpi
    │   ├── ic_launcher_round.webp
    │   └── ic_launcher.webp
    ├── values
    │   ├── colors.xml
    │   ├── strings.xml
    │   └── themes.xml
    └── xml
        ├── backup_rules.xml
        └── data_extraction_rules.xml
```
---

## 🚀 Getting Started
Clone and open in Android Studio:


git clone https://github.com/TheOwaisLone/Milk-Tracker.git
cd Milk-Tracker

Run the project on an emulator or Android device.


---

🔮 Future Enhancements

Cloud backup

Multi-language support

Voice entry

PDF report generation
