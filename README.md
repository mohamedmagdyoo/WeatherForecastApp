<img width="800" height="500" alt="Gemini_Generated_Image_jj5ibsjj5ibsjj5i (1)" src="https://github.com/user-attachments/assets/486a192a-14cd-4190-945b-d28694d91111" />

# ⛅ Weather Forecast App

An Android application that displays real-time weather information based on your current location or any location you choose on the map. Built with modern Android development practices using MVVM architecture.

---

## 📱 Screenshots

> Add your screenshots here

---

## ✨ Features

- 🌍 **Current Location Weather** — Automatically fetches weather based on GPS
- 🗺️ **Map Location Picker** — Pick any location on the map to get its weather
- 🔍 **City Search** — Auto-complete search to find any city
- ⭐ **Favorites** — Save and manage your favorite locations
- 🔔 **Weather Alerts** — Set alerts for temperature, wind, and rain conditions
- 🌐 **Multi-language** — Supports English and Arabic
- 🌡️ **Unit Settings** — Celsius, Fahrenheit, Kelvin / m/s, mph

---

## 🖥️ Screens

| Screen | Description |
|---|---|
| Home | Current weather, hourly forecast, 5-day forecast |
| Favorites | Saved locations with full weather details |
| Alerts | Create and manage weather alerts |
| Settings | Location source, units, language preferences |

---

## 🏗️ Architecture

```
MVVM (Model - View - ViewModel)

UI Layer        → Jetpack Compose Screens
ViewModel       → StateFlow, business logic
Repository      → single source of truth
Data Sources    → Remote (Retrofit) + Local (Room)
```

---

## 🛠️ Tech Stack

| Technology | Usage |
|---|---|
| Kotlin | Primary language |
| Jetpack Compose | UI framework |
| MVVM | Architecture pattern |
| Retrofit | Network calls |
| Room | Local database |
| Coroutines + Flow | Async operations |
| WorkManager | Background alert scheduling |
| Google Maps SDK | Map location picker |
| Places API | City auto-complete search |
| Coil | Image loading |
| MockK + JUnit | Unit & integration testing |

---

## 🌐 API

This app uses the [OpenWeatherMap API](https://openweathermap.org/api)

```
Base URL: https://api.openweathermap.org/data/2.5/
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 24+
- OpenWeatherMap API key
- Google Maps API key

### Setup

1. **Clone the repo:**
```bash
git clone https://github.com/your-username/weather-forecast.git
```

2. **Add your API keys to `local.properties`:**
```properties
WEATHER_API_KEY=your_openweathermap_api_key
MAPS_API_KEY=your_google_maps_api_key
```

3. **Build and run the project in Android Studio**

---

## 🔔 Alert System

Alerts are powered by **WorkManager** — they run in the background and check weather conditions periodically between the start and end time you set.

```
Save Alert → WorkManager schedules periodic check
           → checks weather API every 30 minutes
           → condition met? → fires notification or alarm sound
           → end time reached → stops automatically
```

Supported alert types:
- 🌡️ Temperature threshold
- 💨 Wind speed threshold
- 🌧️ Rain detection

---

## 🧪 Testing

The project includes both unit tests and instrumented tests:

```
Unit Tests          → ViewModels, Repositories, DataSources (MockK)
Instrumented Tests  → Room DAO tests with in-memory database
```


## 📋 Requirements

- Min SDK: 24 (Android 7.0)
- Target SDK: 36
- Kotlin: 2.0.21

