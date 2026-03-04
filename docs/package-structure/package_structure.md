# Package Structure
Updated: 04-03-2026 07:46:29

```
safecube-android/
├── .build/com/safecube/tooling/
│   ├── FolderTreeToFile.class
│   └── Logger.class
├── .git/... # Skipped Content
├── .gradle/... # Skipped Content
├── .idea/... # Skipped Content
├── .kotlin/... # Skipped Content
├── .run/
│   └── run-folder-tree.run.xml
├── app/
│   ├── build/... # Skipped Content
│   ├── src/
│   │   ├── androidTest/java/com/miguelrodriguez19/safecube/
│   │   │   ├── ExampleInstrumentedTest.kt
│   │   │   └── MainActivityComposeTest.kt
│   │   ├── main/
│   │   │   ├── java/com/miguelrodriguez19/safecube/
│   │   │   │   ├── app/navigation/
│   │   │   │   │   ├── NavigationGates.kt
│   │   │   │   │   ├── NavigationWrapper.kt
│   │   │   │   │   └── Routes.kt
│   │   │   │   ├── core/
│   │   │   │   ├── ui/theme/
│   │   │   │   │   ├── Color.kt
│   │   │   │   │   ├── Theme.kt
│   │   │   │   │   └── Type.kt
│   │   │   │   ├── view/core/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   └── SafeCubeApp.kt
│   │   │   ├── res/
│   │   │   │   ├── drawable/
│   │   │   │   │   ├── ic_launcher_background.xml
│   │   │   │   │   └── ic_launcher_foreground.xml
│   │   │   │   ├── mipmap-anydpi/
│   │   │   │   │   ├── ic_launcher.xml
│   │   │   │   │   └── ic_launcher_round.xml
│   │   │   │   ├── mipmap-hdpi/
│   │   │   │   │   ├── ic_launcher.webp
│   │   │   │   │   └── ic_launcher_round.webp
│   │   │   │   ├── mipmap-mdpi/
│   │   │   │   │   ├── ic_launcher.webp
│   │   │   │   │   └── ic_launcher_round.webp
│   │   │   │   ├── mipmap-xhdpi/
│   │   │   │   │   ├── ic_launcher.webp
│   │   │   │   │   └── ic_launcher_round.webp
│   │   │   │   ├── mipmap-xxhdpi/
│   │   │   │   │   ├── ic_launcher.webp
│   │   │   │   │   └── ic_launcher_round.webp
│   │   │   │   ├── mipmap-xxxhdpi/
│   │   │   │   │   ├── ic_launcher.webp
│   │   │   │   │   └── ic_launcher_round.webp
│   │   │   │   ├── values/
│   │   │   │   │   └── strings.xml
│   │   │   │   └── xml/
│   │   │   │       ├── backup_rules.xml
│   │   │   │       └── data_extraction_rules.xml
│   │   │   └── AndroidManifest.xml
│   │   └── test/java/com/miguelrodriguez19/safecube/
│   │       └── ExampleUnitTest.kt
│   ├── .gitignore
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build/... # Skipped Content
├── core/
│   ├── auth/
│   │   ├── build/... # Skipped Content
│   │   ├── src/main/
│   │   │   ├── java/com/miguelrodriguez19/safecube/core/auth/
│   │   │   │   ├── di/
│   │   │   │   │   └── AuthModule.kt
│   │   │   │   ├── internal/
│   │   │   │   │   ├── EncryptedTokenStorage.kt
│   │   │   │   │   └── FakeVaultSessionManager.kt
│   │   │   │   ├── AuthRepository.kt
│   │   │   │   ├── SessionManager.kt
│   │   │   │   ├── SessionState.kt
│   │   │   │   ├── TokenStorage.kt
│   │   │   │   ├── VaultSessionManager.kt
│   │   │   │   └── VaultState.kt
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   ├── crypto/
│   │   ├── build/... # Skipped Content
│   │   ├── src/main/
│   │   │   ├── java/com/miguelrodriguez19/safecube/core/crypto/
│   │   │   │   ├── di/
│   │   │   │   │   └── CryptoModule.kt
│   │   │   │   ├── internal/
│   │   │   │   │   └── FakeCryptoEngine.kt
│   │   │   │   ├── CryptoEngine.kt
│   │   │   │   ├── DecryptionRequest.kt
│   │   │   │   ├── EncryptionRequest.kt
│   │   │   │   ├── EncryptionResult.kt
│   │   │   │   ├── KdfEngine.kt
│   │   │   │   ├── KdfRequest.kt
│   │   │   │   ├── KeyUnwrapRequest.kt
│   │   │   │   ├── KeyWrapping.kt
│   │   │   │   └── KeyWrapRequest.kt
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   ├── network/
│   │   ├── build/... # Skipped Content
│   │   ├── openapi/
│   │   │   └── OpenAPI.json
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │   ├── di/
│   │   │   │   │   │   └── NetworkModule.kt
│   │   │   │   │   ├── ApiService.kt
│   │   │   │   │   ├── AuthInterceptor.kt
│   │   │   │   │   ├── NetworkClientFactory.kt
│   │   │   │   │   ├── NetworkConfig.kt
│   │   │   │   │   └── TokenProvider.kt
│   │   │   │   └── AndroidManifest.xml
│   │   │   └── test/java/com/miguelrodriguez19/safecube/core/network/
│   │   │       └── NetworkClientFactoryTest.kt
│   │   │   │   └── build.gradle.kts
│   ├── storage/
│   │   ├── build/... # Skipped Content
│   │   ├── src/main/
│   │   │   ├── java/com/miguelrodriguez19/safecube/core/storage/
│   │   │   │   ├── di/
│   │   │   │   │   └── StorageModule.kt
│   │   │   │   ├── AppDatabase.kt
│   │   │   │   ├── SecureItemDao.kt
│   │   │   │   └── SecureItemEntity.kt
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   ├── ui/
│   │   ├── build/... # Skipped Content
│   │   ├── src/main/
│   │   │   ├── java/com/miguelrodriguez19/safecube/core/ui/
│   │   │   │   ├── components/
│   │   │   │   │   └── .gitkeep
│   │   │   │   └── theme/
│   │   │   │       └── .gitkeep
│   │   │   ├── res/
│   │   │   │   ├── values/
│   │   │   │   │   └── strings.xml
│   │   │   │   └── values-es/
│   │   │   │       └── strings.xml
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   ├── docs/
│   ├── architecture/
│   │   └── storage_decision.md
│   ├── package-structure/
│   │   └── package_structure.md
│   ├── roadmap/
│   │   ├── roadmap--fase-1.md
│   │   ├── roadmap--fase-2.md
│   │   └── roadmap--high-level.md
│   ├── README.md
│   └── testing.md
├── feature/
│   ├── auth/
│   │   ├── build/... # Skipped Content
│   │   ├── src/main/
│   │   │   ├── java/com/miguelrodriguez19/safecube/feature/auth/
│   │   │   │   ├── navigation/
│   │   │   │   ├── screens/
│   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   ├── PostLoginGateScreen.kt
│   │   │   │   │   ├── SignupScreen.kt
│   │   │   │   │   └── WelcomeScreen.kt
│   │   │   │   └── AuthActionLabel.kt
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   ├── profile/
│   │   ├── build/... # Skipped Content
│   │   ├── src/main/
│   │   │   ├── java/com/miguelrodriguez19/safecube/feature/profile/navigation/
│   │   │   │   └── ProfileScreen.kt
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   └── vault/
│       ├── build/... # Skipped Content
│       ├── src/main/
│       │   ├── java/com/miguelrodriguez19/safecube/feature/vault/navigation/
│       │   │   ├── CreateVaultScreen.kt
│       │   │   ├── NavigationBar.kt
│       │   │   ├── RecoveryKeyScreen.kt
│       │   │   ├── SettingsScreen.kt
│       │   │   ├── UnlockVaultScreen.kt
│       │   │   ├── VaultFoldersScreen.kt
│       │   │   └── VaultScreen.kt
│       │   └── AndroidManifest.xml
│       └── build.gradle.kts
├── gradle/... # Skipped Content
├── scripts/
│   ├── .build/... # Skipped Content
│   ├── resources/com/safecube/tooling/
│   │   └── FolderTreeToFile.java
│   └── run-folder-tree.sh
├── .gitignore
├── build.gradle.kts
├── gradlew
├── gradlew.bat
├── LICENSE
└── settings.gradle.kts
```
