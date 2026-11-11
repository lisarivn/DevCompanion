# DevCompanion

**DevCompanion** is an Android application that combines GitHub integration, repository management, and developer utilities in a single mobile workspace.

The application was developed as a university coursework project using Kotlin, Jetpack Compose, and modern Android development practices.

## Features

### Dashboard
- Overview of saved repositories
- Recently added favorites
- Recent public GitHub activity

### GitHub Hub
- Search GitHub repositories
- Filter and sort search results
- Browse repositories associated with the authenticated account
- View repository details and branches
- Browse issues, pull requests, and commits
- View detailed information about issues, pull requests, and commits
- Search GitHub user profiles
- Save and manage favorite repositories
- Paginated data loading

### GitHub Authentication
- GitHub OAuth Device Authorization Flow
- Authenticated user profile
- Persistent authentication
- Login and logout functionality

### Developer Tools
- **JSON Formatter:** Format and validate JSON data
- **UUID Generator:** Generate unique identifiers
- **Timestamp Converter:** Work with Unix timestamps
- **Regex Tester:** Test regular expressions
- **HTTP Client:** Send HTTP requests and inspect responses

### Workspace
- GitHub account management
- Light, Dark, and System theme selection
- Persistent appearance preferences
- Application information

## Tech Stack

| Technology | Purpose |
|---|---|
| Kotlin | Application development |
| Jetpack Compose | Declarative UI |
| Material 3 | UI components and theming |
| MVVM | Presentation architecture |
| Hilt | Dependency injection |
| Retrofit | REST API communication |
| OkHttp | HTTP networking |
| Room | Local favorites storage |
| Kotlin Coroutines & Flow | Asynchronous operations and reactive state |
| Navigation Compose | Screen navigation |
| Coil | Image loading |
| GitHub REST API | GitHub data integration |
| SharedPreferences | Theme preference persistence |

## Architecture

The application follows an MVVM-based architecture with a separation between presentation, domain, and data layers.

```text
app/
└── src/main/java/com/example/devcompanion/
    ├── core/
    │   ├── di/
    │   ├── navigation/
    │   └── ui/theme/
    ├── data/
    │   ├── auth/
    │   ├── local/
    │   ├── mapper/
    │   ├── remote/
    │   └── repository/
    ├── domain/
    │   ├── model/
    │   └── repository/
    ├── feature/
    │   ├── auth/
    │   ├── commits/
    │   ├── dashboard/
    │   ├── devtools/
    │   ├── favorites/
    │   ├── issues/
    │   ├── profile/
    │   ├── pullrequests/
    │   ├── repositories/
    │   └── workspace/
    ├── DevCompanionApplication.kt
    └── MainActivity.kt
```

## Getting Started

### Requirements
- Android Studio
- Android SDK
- JDK 17
- Android device or emulator running Android 7.0 (API 24) or later

### Installation

1. Clone the repository:

```bash
git clone https://github.com/<your-username>/DevCompanion.git
```

2. Open the project in Android Studio.
3. Sync the project with Gradle.
4. Configure the required GitHub OAuth application credentials.
5. Run the application on an Android emulator or physical device.

## Screenshots

Screenshots of the main application screens will be added here.

Suggested screens:
- Dashboard
- GitHub Hub
- Repository Details
- Favorites
- Developer Tools
- Workspace

## Project Information

**Project type:** PGR for Mobile Device Programming  
**Platform:** Android  
**Language:** Kotlin  
**Version:** 1.0

## License

No license has been specified for this project.
