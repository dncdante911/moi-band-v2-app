# 🎸 MASTER OF ILLUSION - Android App

**Официальное Android приложение для музыкального проекта Master of Illusion**

Полноценный музыкальный плеер для Power Metal, Heavy Metal, Gothic Metal и Punk Rock композиций, созданных с помощью AI (Suno) с авторскими текстами.

---

## 📋 Особенности

✅ **Аутентификация** - JWT токены, безопасное хранение  
✅ **Альбомы** - Просмотр всех альбомов с треками  
✅ **Музыкальный плеер** - ExoPlayer с поддержкой фонового воспроизведения  
✅ **Оффлайн режим** - Скачивание треков для прослушивания без интернета  
✅ **3 Темы** - Power Metal (золотая), Heavy Metal (серебро), Punk Rock (неон)  
✅ **Новости** - Последние новости о релизах  
✅ **Галерея** - Фото из студии, концертов, промо  
✅ **Чат** - Общение с другими фанатами  
✅ **Тексты песен** - Просмотр lyrics прямо в плеере  

---

## 🚀 Быстрый старт

### 1. Требования
- Android Studio Hedgehog (2023.1.1) или новее
- JDK 17+
- Android SDK 34
- Минимальная версия Android: **7.0 (API 24)**

### 2. Клонирование и запуск

```bash
# Клонировать репозиторий
git clone https://github.com/yourusername/MoiBand-Android.git
cd MoiBand-Android

# Открыть в Android Studio
# File -> Open -> выбрать папку MoiBand

# Подождать синхронизации Gradle (1-2 минуты)

# Запустить на эмуляторе или устройстве
# Run -> Run 'app' (Shift + F10)
```

### 3. Первый запуск

1. **Регистрация**: Username (минимум 3 символа), Email, Пароль (минимум 8 символов)
2. **Вход**: Username или Email + Пароль
3. **Готово!** Исследуй альбомы, слушай музыку, меняй темы

---

## 🔧 Конфигурация API

### Production (по умолчанию)
```kotlin
API_BASE_URL = "https://moi-band.com.ua/api/v1"
BASE_URL = "https://moi-band.com.ua"
```

### Локальная разработка
В `app/build.gradle.kts` раскомментируй и измени IP:
```kotlin
buildConfigField("String", "API_BASE_URL", "\"http://192.168.1.X:80/api/v1\"")
buildConfigField("String", "BASE_URL", "\"http://192.168.1.X\"")
```

---

## 📱 Экраны приложения

### 🔐 Авторизация
- **LoginScreen** - Вход по username/email + пароль
- **RegisterScreen** - Регистрация нового аккаунта

### 📀 Альбомы
- **AlbumsScreen** - Список всех альбомов
- **AlbumDetailScreen** - Детали альбома с треками

### 🎵 Плеер
- **MiniPlayer** - Компактный плеер снизу экрана
- **FullPlayerScreen** - Полноэкранный плеер с обложкой

### 📰 Новости
- **NewsScreen** - Список новостей (релизы, события)
- **NewsDetailScreen** - Полный текст новости

### 🖼️ Галерея
- **GalleryScreen** - Grid фотографий (студия, концерты, промо)

### 💬 Чат
- **ChatRoomsScreen** - Список комнат чата
- **ChatMessagesScreen** - Сообщения в комнате

### 👤 Профиль
- **ProfileScreen** - Информация о пользователе, статистика, смена темы

---

## 🎨 Темы

### ⚔️ Power Metal (Default)
- **Цвета**: Золотой (#FFD700), Оранжевый, Черный фон
- **Стиль**: Эпический, величественный
- **Для**: Классических power metal фанатов

### 🤘 Heavy Metal
- **Цвета**: Серебристый (#C0C0C0), Steel Gray, Темный фон
- **Стиль**: Строгий, брутальный, классический
- **Для**: Олдскул металлистов

### 🎸 Punk Rock
- **Цвета**: Deep Pink (#FF1493), Lime Green (#00FF00), Неон
- **Стиль**: Яркий, хаотичный, энергичный
- **Для**: Панк-рок бунтарей

**Как сменить**: Профиль -> Тема приложения -> Выбрать

---

## 🗂️ Структура проекта

```
MoiBand/
├── app/
│   ├── src/main/
│   │   ├── java/com/moi/band/
│   │   │   ├── MoiApplication.kt          # Главный класс
│   │   │   ├── MainActivity.kt            # Главная активность
│   │   │   ├── data/
│   │   │   │   ├── api/ApiService.kt      # Retrofit API
│   │   │   │   ├── model/Models.kt        # Data классы
│   │   │   │   ├── repository/            # Репозитории
│   │   │   │   └── local/AppDatabase.kt   # Room DB
│   │   │   ├── di/AppModule.kt            # Hilt DI
│   │   │   ├── ui/
│   │   │   │   ├── auth/                  # Авторизация
│   │   │   │   ├── albums/                # Альбомы
│   │   │   │   ├── main/MainScreen.kt     # Главный экран
│   │   │   │   ├── navigation/NavGraph.kt # Навигация
│   │   │   │   ├── screens/               # Экраны
│   │   │   │   └── theme/                 # Темы
│   │   │   └── util/
│   │   │       ├── TokenManager.kt        # JWT хранилище
│   │   │       └── Resource.kt            # State wrapper
│   │   ├── res/                           # Ресурсы
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

---

## 🔐 Безопасность

- **JWT Токены**: Хранятся в DataStore (зашифровано)
- **HTTPS**: Только защищенное соединение
- **Password Hashing**: Argon2ID на сервере
- **Auto Logout**: При истечении токена (24 часа)

---

## 📦 Зависимости

```kotlin
// Core
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
implementation("androidx.activity:activity-compose:1.8.1")

// Compose
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.navigation:navigation-compose:2.7.5")

// Hilt DI
implementation("com.google.dagger:hilt-android:2.48")

// Retrofit
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// Coil (Image loading)
implementation("io.coil-kt:coil-compose:2.5.0")

// ExoPlayer (Music)
implementation("androidx.media3:media3-exoplayer:1.2.0")

// Room Database
implementation("androidx.room:room-runtime:2.6.1")
```

---

## 🧪 Тестирование

```bash
# Unit тесты
./gradlew test

# UI тесты
./gradlew connectedAndroidTest
```

---

## 📦 Сборка APK

### Debug
```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

### Release
```bash
./gradlew assembleRelease
# APK: app/build/outputs/apk/release/app-release.apk
```

---

## 🔗 API Endpoints

| Endpoint | Метод | Описание | Auth |
|----------|-------|----------|------|
| `/auth/login.php` | POST | Вход | ❌ |
| `/auth/register.php` | POST | Регистрация | ❌ |
| `/albums/list.php` | GET | Список альбомов | ❌ |
| `/albums/detail.php` | GET | Детали альбома | ❌ |
| `/tracks/search.php` | GET | Поиск треков | ❌ |
| `/news/list.php` | GET | Новости | ❌ |
| `/gallery/list.php` | GET | Галерея | ❌ |
| `/chat/rooms.php` | GET | Комнаты чата | ✅ |
| `/chat/messages.php` | GET | Сообщения | ✅ |
| `/users/profile.php` | GET | Профиль | ✅ |

---

## 🐛 Troubleshooting

### Gradle sync failed
```bash
File -> Invalidate Caches / Restart
```

### API не отвечает
Проверь `BuildConfig.API_BASE_URL` и интернет

### Изображения не загружаются
Проверь что используешь `BuildConfig.BASE_URL + imagePath`

---

## 📄 Лицензия

MIT License - используй как хочешь!

---

## 👨‍💻 Разработчик

**Master of Illusion** - Музыкальный проект  
🌐 Website: https://moi-band.com.ua  
🎸 API: https://moi-band.com.ua/api/v1  

---

**Версия:** 1.0.0  
**Дата:** 22.10.2025  
**Статус:** ✅ Production Ready  

Made with 🎸 for Metal fans 🤘
