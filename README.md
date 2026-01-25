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
│               ├── data
│               ├── MainActivity.kt
│               ├── SplashActivity.kt
│               ├── ui
│               ├── utils
│               └── viewmodel
└── res
    ├── drawable
    ├── mipmap-anydpi-v26
    ├── mipmap-hdpi
    ├── mipmap-mdpi
    ├── mipmap-xhdpi
    ├── mipmap-xxhdpi
    ├── mipmap-xxxhdpi
    ├── values
    └── xml
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
