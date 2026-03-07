# Package Structure
Updated: 07-03-2026 03:10:59

```
safecube-android/
├── .build/com/safecube/tooling/
│   ├── FolderTreeToFile.class
│   └── Logger.class
├── .run/
│   ├── run-folder-tree.run.xml
│   └── verifyCoverage.run.xml
├── app/
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
├── core/
│   ├── auth/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/miguelrodriguez19/safecube/core/auth/
│   │   │   │   │   ├── data/
│   │   │   │   │   │   ├── local/
│   │   │   │   │   │   │   ├── EncryptedTokenPrefs.kt
│   │   │   │   │   │   │   └── EncryptedTokenStorage.kt
│   │   │   │   │   │   ├── mapper/
│   │   │   │   │   │   │   └── AuthErrorMapper.kt
│   │   │   │   │   │   ├── remote/
│   │   │   │   │   │   │   ├── NetworkResult.kt
│   │   │   │   │   │   │   └── RemoteAuthDataSource.kt
│   │   │   │   │   │   ├── repository/
│   │   │   │   │   │   │   └── AuthRepositoryImpl.kt
│   │   │   │   │   │   ├── session/
│   │   │   │   │   │   │   └── AuthTokenRefreshHandler.kt
│   │   │   │   │   │   └── vault/
│   │   │   │   │   │       └── FakeVaultSessionManager.kt
│   │   │   │   │   ├── di/
│   │   │   │   │   │   └── AuthModule.kt
│   │   │   │   │   └── domain/
│   │   │   │   │       ├── model/
│   │   │   │   │       │   ├── AuthError.kt
│   │   │   │   │       │   ├── AuthOperation.kt
│   │   │   │   │       │   ├── AuthResult.kt
│   │   │   │   │       │   ├── AuthTokens.kt
│   │   │   │   │       │   ├── RegisteredAccount.kt
│   │   │   │   │       │   ├── SessionState.kt
│   │   │   │   │       │   └── VaultState.kt
│   │   │   │   │       ├── repository/
│   │   │   │   │       │   ├── AuthRepository.kt
│   │   │   │   │       │   └── TokenStorage.kt
│   │   │   │   │       ├── session/
│   │   │   │   │       │   ├── SessionManager.kt
│   │   │   │   │       │   └── SessionManagerImpl.kt
│   │   │   │   │       └── vault/
│   │   │   │   │           └── VaultSessionManager.kt
│   │   │   │   └── AndroidManifest.xml
│   │   │   └── test/java/com/miguelrodriguez19/safecube/core/auth/
│   │   │       ├── data/
│   │   │       │   ├── local/
│   │   │       │   │   └── EncryptedTokenStorageTest.kt
│   │   │       │   ├── mapper/
│   │   │       │   │   └── AuthErrorMapperTest.kt
│   │   │       │   ├── remote/
│   │   │       │   │   └── RemoteAuthDataSourceTest.kt
│   │   │       │   ├── repository/
│   │   │       │   │   └── AuthRepositoryImplTest.kt
│   │   │       │   └── session/
│   │   │       │       └── AuthTokenRefreshHandlerTest.kt
│   │   │       └── domain/session/
│   │   │           └── SessionManagerImplTest.kt
│   │   └── build.gradle.kts
│   ├── crypto/
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
│   │   ├── build/
│   │   │   ├── generated/
│   │   │   │   ├── openapi/
│   │   │   │   │   ├── src/main/kotlin/com/miguelrodriguez19/safecube/core/network/generated/
│   │   │   │   │   │   ├── api/
│   │   │   │   │   │   │   ├── AuthControllerApi.kt
│   │   │   │   │   │   │   ├── UserProfileControllerApi.kt
│   │   │   │   │   │   │   ├── VaultControllerApi.kt
│   │   │   │   │   │   │   └── VaultKeyMaterialControllerApi.kt
│   │   │   │   │   │   ├── auth/
│   │   │   │   │   │   ├── infrastructure/
│   │   │   │   │   │   │   ├── AtomicBooleanAdapter.kt
│   │   │   │   │   │   │   ├── AtomicIntegerAdapter.kt
│   │   │   │   │   │   │   ├── AtomicLongAdapter.kt
│   │   │   │   │   │   │   ├── BigDecimalAdapter.kt
│   │   │   │   │   │   │   ├── BigIntegerAdapter.kt
│   │   │   │   │   │   │   ├── CollectionFormats.kt
│   │   │   │   │   │   │   ├── LocalDateAdapter.kt
│   │   │   │   │   │   │   ├── LocalDateTimeAdapter.kt
│   │   │   │   │   │   │   ├── OffsetDateTimeAdapter.kt
│   │   │   │   │   │   │   ├── ResponseExt.kt
│   │   │   │   │   │   │   ├── Serializer.kt
│   │   │   │   │   │   │   ├── StringBuilderAdapter.kt
│   │   │   │   │   │   │   ├── URIAdapter.kt
│   │   │   │   │   │   │   ├── URLAdapter.kt
│   │   │   │   │   │   │   └── UUIDAdapter.kt
│   │   │   │   │   │   └── model/
│   │   │   │   │   │       ├── AuthenticateAccountRequest.kt
│   │   │   │   │   │       ├── AuthTokensResponse.kt
│   │   │   │   │   │       ├── CreateSecureItemRequest.kt
│   │   │   │   │   │       ├── CreateSecureItemResult.kt
│   │   │   │   │   │       ├── CreateUserProfileRequest.kt
│   │   │   │   │   │       ├── DeleteSecureItemResult.kt
│   │   │   │   │   │       ├── Get400Response.kt
│   │   │   │   │   │       ├── InitVaultKeyMaterialRequest.kt
│   │   │   │   │   │       ├── ListSecureItemsResponse.kt
│   │   │   │   │   │       ├── RefreshTokenRequest.kt
│   │   │   │   │   │       ├── RegisterAccountRequest.kt
│   │   │   │   │   │       ├── RegisterAccountResult.kt
│   │   │   │   │   │       ├── SecureItemResponse.kt
│   │   │   │   │   │       ├── SecureItemSummaryResponse.kt
│   │   │   │   │   │       ├── UpdateMaster400Response.kt
│   │   │   │   │   │       ├── UpdateMasterWrappedKekRequest.kt
│   │   │   │   │   │       ├── UpdateSecureItemRequest.kt
│   │   │   │   │   │       ├── UpdateSecureItemResult.kt
│   │   │   │   │   │       ├── UpdateUserProfileRequest.kt
│   │   │   │   │   │       ├── UserProfileResponse.kt
│   │   │   │   │   │       └── VaultKeyMaterialResponse.kt
│   │   │   │   │   ├── .openapi-generator-ignore
│   │   │   │   │   ├── build.gradle
│   │   │   │   │   ├── gradlew
│   │   │   │   │   ├── gradlew.bat
│   │   │   │   │   ├── proguard-rules.pro
│   │   │   │   │   ├── README.md
│   │   │   │   │   └── settings.gradle
│   │   ├── openapi/
│   │   │   └── OpenAPI.json
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/miguelrodriguez19/safecube/core/network/
│   │   │   │   │   ├── di/
│   │   │   │   │   │   ├── NetworkModule.kt
│   │   │   │   │   │   ├── RefreshAuthApi.kt
│   │   │   │   │   │   ├── RefreshOkHttpClient.kt
│   │   │   │   │   │   ├── RefreshRetrofit.kt
│   │   │   │   │   │   └── TokenRefreshOptionalBindingModule.kt
│   │   │   │   │   ├── AuthInterceptor.kt
│   │   │   │   │   ├── NetworkClientFactory.kt
│   │   │   │   │   ├── NetworkConfig.kt
│   │   │   │   │   ├── TokenProvider.kt
│   │   │   │   │   ├── TokenRefreshAuthenticator.kt
│   │   │   │   │   └── TokenRefreshHandler.kt
│   │   │   │   └── AndroidManifest.xml
│   │   │   └── test/java/com/miguelrodriguez19/safecube/core/network/
│   │   │       ├── NetworkClientFactoryTest.kt
│   │   │       ├── TokenRefreshAuthenticatorFlowTest.kt
│   │   │       └── TokenRefreshAuthenticatorTest.kt
│   │   └── build.gradle.kts
│   ├── storage/
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
├── docs/
│   ├── architecture/
│   │   ├── openapi-auth-contract-integration.md
│   │   └── storage_decision.md
│   ├── package-structure/
│   │   └── package_structure.md
│   ├── roadmap/
│   │   ├── roadmap--fase-1.md
│   │   ├── roadmap--fase-2.md
│   │   ├── roadmap--fase-3.md
│   │   └── roadmap--high-level.md
│   ├── README.md
│   └── testing.md
├── feature/
│   ├── auth/
│   │   ├── src/main/
│   │   │   ├── java/com/miguelrodriguez19/safecube/feature/auth/
│   │   │   │   ├── navigation/
│   │   │   │   ├── presentation/
│   │   │   │   │   ├── login/
│   │   │   │   │   │   ├── LoginUiState.kt
│   │   │   │   │   │   └── LoginViewModel.kt
│   │   │   │   │   ├── signup/
│   │   │   │   │   │   ├── SignupUiState.kt
│   │   │   │   │   │   └── SignupViewModel.kt
│   │   │   │   │   └── AuthUiErrorMapper.kt
│   │   │   │   ├── screens/
│   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   ├── PostLoginGateScreen.kt
│   │   │   │   │   ├── SignupScreen.kt
│   │   │   │   │   └── WelcomeScreen.kt
│   │   │   │   └── AuthActionLabel.kt
│   │   │   ├── res/values/
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   ├── profile/
│   │   ├── src/main/
│   │   │   ├── java/com/miguelrodriguez19/safecube/feature/profile/navigation/
│   │   │   │   └── ProfileScreen.kt
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   └── vault/
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
├── scripts/
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
