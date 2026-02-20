# Package Structure
Updated: 20-02-2026 02:55:12

```
safecube-android/
├── .git/... # Skipped Content
├── .gradle/... # Skipped Content
├── .idea/... # Skipped Content
├── .kotlin/... # Skipped Content
├── app/
│   ├── build/... # Skipped Content
│   ├── src/
│   │   ├── androidTest/java/com/miguelrodriguez19/safecube/
│   │   │   ├── ExampleInstrumentedTest.kt
│   │   │   └── MainActivityComposeTest.kt
│   │   ├── main/
│   │   │   ├── java/com/miguelrodriguez19/safecube/
│   │   │   │   ├── ui/theme/
│   │   │   │   │   ├── Color.kt
│   │   │   │   │   ├── Theme.kt
│   │   │   │   │   └── Type.kt
│   │   │   │   └── MainActivity.kt
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
│   │   │   ├── java/
│   │   │   │   └── .gitkeep
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   ├── crypto/
│   │   ├── build/... # Skipped Content
│   │   ├── src/main/
│   │   │   ├── java/
│   │   │   │   ├── com/miguelrodriguez19/safecube/core/crypto/
│   │   │   │   │   ├── CryptoEngine.kt
│   │   │   │   │   ├── DecryptionRequest.kt
│   │   │   │   │   ├── EncryptionRequest.kt
│   │   │   │   │   ├── EncryptionResult.kt
│   │   │   │   │   ├── KdfEngine.kt
│   │   │   │   │   ├── KdfRequest.kt
│   │   │   │   │   ├── KeyUnwrapRequest.kt
│   │   │   │   │   ├── KeyWrapping.kt
│   │   │   │   │   └── KeyWrapRequest.kt
│   │   │   │   └── .gitkeep
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   ├── network/
│   │   ├── build/... # Skipped Content
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │   ├── AuthInterceptorFactory.kt
│   │   │   │   │   ├── NetworkClientFactory.kt
│   │   │   │   │   └── NetworkConfig.kt
│   │   │   │   └── AndroidManifest.xml
│   │   │   └── test/java/com/miguelrodriguez19/safecube/core/network/
│   │   │       └── NetworkClientFactoryTest.kt
│   │   └── build.gradle.kts
│   ├── storage/
│   │   ├── build/... # Skipped Content
│   │   ├── src/main/
│   │   │   ├── java/
│   │   │   │   └── .gitkeep
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   └── ui/
│       ├── build/... # Skipped Content
│       ├── src/main/
│       │   ├── java/com/miguelrodriguez19/safecube/core/ui/
│       │   │   ├── components/
│       │   │   │   └── .gitkeep
│       │   │   └── theme/
│       │   │       └── .gitkeep
│       │   ├── res/
│       │   │   ├── values/
│       │   │   │   └── strings.xml
│       │   │   └── values-es/
│       │   │       └── strings.xml
│       │   └── AndroidManifest.xml
│       └── build.gradle.kts
├── docs/
│   ├── architecture/
│   │   └── storage_decision.md
│   ├── package-structure/
│   │   └── package_structure.md
│   ├── README.md
│   └── testing.md
├── feature/
│   ├── auth/
│   │   ├── build/... # Skipped Content
│   │   ├── src/main/
│   │   │   ├── java/
│   │   │   │   ├── com/miguelrodriguez19/safecube/feature/auth/
│   │   │   │   │   └── AuthActionLabel.kt
│   │   │   │   └── .gitkeep
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   ├── profile/
│   │   ├── build/... # Skipped Content
│   │   ├── src/main/
│   │   │   ├── java/
│   │   │   │   └── .gitkeep
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   └── vault/
│       ├── build/... # Skipped Content
│       ├── src/main/
│       │   ├── java/
│       │   │   └── .gitkeep
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
