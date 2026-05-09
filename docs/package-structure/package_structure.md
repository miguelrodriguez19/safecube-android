# Package Structure
Updated: 10-05-2026 12:19:34

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
│   │   │   ├── java/com/miguelrodriguez19/safecube/app/
│   │   │   │   ├── entrypoint/
│   │   │   │   │   ├── MainActivity.kt
│   │   │   │   │   └── SafeCubeApp.kt
│   │   │   │   └── presentation/
│   │   │   │       ├── navigation/
│   │   │   │       │   ├── gate/
│   │   │   │       │   │   ├── NavigationGates.kt
│   │   │   │       │   │   └── NavigationGatesEntryPoint.kt
│   │   │   │       │   ├── host/
│   │   │   │       │   │   ├── NavigationBackPolicy.kt
│   │   │   │       │   │   ├── NavigationDependencies.kt
│   │   │   │       │   │   ├── NavigationGraph.kt
│   │   │   │       │   │   ├── NavigationSessionCoordinator.kt
│   │   │   │       │   │   └── NavigationWrapper.kt
│   │   │   │       │   └── route/
│   │   │   │       │       └── Routes.kt
│   │   │   │       ├── theme/
│   │   │   │       │   ├── Color.kt
│   │   │   │       │   ├── Theme.kt
│   │   │   │       │   └── Type.kt
│   │   │   │       └── ui/
│   │   │   │           └── SplashGateScreen.kt
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
│   │   │   │   │   │   └── session/
│   │   │   │   │   │       └── AuthTokenRefreshHandler.kt
│   │   │   │   │   ├── di/
│   │   │   │   │   │   └── AuthModule.kt
│   │   │   │   │   └── domain/
│   │   │   │   │       ├── model/
│   │   │   │   │       │   ├── AuthError.kt
│   │   │   │   │       │   ├── AuthOperation.kt
│   │   │   │   │       │   ├── AuthResult.kt
│   │   │   │   │       │   ├── AuthTokens.kt
│   │   │   │   │       │   ├── RegisteredAccount.kt
│   │   │   │   │       │   └── SessionState.kt
│   │   │   │   │       ├── repository/
│   │   │   │   │       │   ├── AuthRepository.kt
│   │   │   │   │       │   └── TokenStorage.kt
│   │   │   │   │       └── session/
│   │   │   │   │           ├── SessionManager.kt
│   │   │   │   │           └── SessionManagerImpl.kt
│   │   │   │   └── AndroidManifest.xml
│   │   │   └── test/java/com/miguelrodriguez19/safecube/core/auth/
│   │   │       ├── data/
│   │   │       │   ├── local/
│   │   │       │   │   └── EncryptedTokenStorageTest.kt
│   │   │       │   ├── mapper/
│   │   │       │   │   └── AuthErrorMapperTest.kt
│   │   │       │   ├── remote/
│   │   │       │   │   ├── RemoteAuthDataSourceIntegrationTest.kt
│   │   │       │   │   └── RemoteAuthDataSourceTest.kt
│   │   │       │   ├── repository/
│   │   │       │   │   └── AuthRepositoryImplTest.kt
│   │   │       │   └── session/
│   │   │       │       └── AuthTokenRefreshHandlerTest.kt
│   │   │       └── domain/session/
│   │   │           └── SessionManagerImplTest.kt
│   │   └── build.gradle.kts
│   ├── crypto/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/miguelrodriguez19/safecube/core/crypto/
│   │   │   │   │   ├── data/engine/
│   │   │   │   │   │   ├── AesGcmCryptoEngine.kt
│   │   │   │   │   │   ├── AesGcmKeyWrapping.kt
│   │   │   │   │   │   └── Argon2KdfEngine.kt
│   │   │   │   │   ├── di/
│   │   │   │   │   │   └── CryptoModule.kt
│   │   │   │   │   └── domain/
│   │   │   │   │       ├── model/
│   │   │   │   │       │   ├── DecryptionRequest.kt
│   │   │   │   │       │   ├── EncryptionRequest.kt
│   │   │   │   │       │   ├── EncryptionResult.kt
│   │   │   │   │       │   ├── KdfRequest.kt
│   │   │   │   │       │   ├── KeyUnwrapRequest.kt
│   │   │   │   │       │   └── KeyWrapRequest.kt
│   │   │   │   │       ├── port/
│   │   │   │   │       │   ├── CryptoEngine.kt
│   │   │   │   │       │   ├── KdfEngine.kt
│   │   │   │   │       │   └── KeyWrapping.kt
│   │   │   │   │       └── service/
│   │   │   │   │           └── SaltGenerator.kt
│   │   │   │   └── AndroidManifest.xml
│   │   │   └── test/java/com/miguelrodriguez19/safecube/core/crypto/
│   │   │       ├── data/engine/
│   │   │       │   ├── AesGcmCryptoEngineTest.kt
│   │   │       │   ├── AesGcmKeyWrappingTest.kt
│   │   │       │   └── Argon2KdfEngineTest.kt
│   │   │       └── domain/
│   │   │           ├── model/
│   │   │           │   └── CryptoRequestModelsTest.kt
│   │   │           └── service/
│   │   │               └── SaltGeneratorTest.kt
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
│   │   │   │   │   │       ├── ErrorResponse.kt
│   │   │   │   │   │       ├── InitVaultKeyMaterialRequest.kt
│   │   │   │   │   │       ├── ListSecureItemsResponse.kt
│   │   │   │   │   │       ├── RefreshTokenRequest.kt
│   │   │   │   │   │       ├── RegisterAccountRequest.kt
│   │   │   │   │   │       ├── RegisterAccountResult.kt
│   │   │   │   │   │       ├── SecureItemResponse.kt
│   │   │   │   │   │       ├── SecureItemSummaryResponse.kt
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
│   │   │   │   │   ├── data/
│   │   │   │   │   │   ├── auth/
│   │   │   │   │   │   │   ├── AuthInterceptor.kt
│   │   │   │   │   │   │   └── TokenRefreshAuthenticator.kt
│   │   │   │   │   │   └── client/
│   │   │   │   │   │       └── NetworkClientFactory.kt
│   │   │   │   │   ├── di/
│   │   │   │   │   │   ├── NetworkModule.kt
│   │   │   │   │   │   ├── RefreshAuthApi.kt
│   │   │   │   │   │   ├── RefreshOkHttpClient.kt
│   │   │   │   │   │   ├── RefreshRetrofit.kt
│   │   │   │   │   │   └── TokenRefreshOptionalBindingModule.kt
│   │   │   │   │   ├── domain/
│   │   │   │   │   │   ├── model/
│   │   │   │   │   │   │   └── NetworkConfig.kt
│   │   │   │   │   │   └── port/
│   │   │   │   │   │       ├── TokenProvider.kt
│   │   │   │   │   │       └── TokenRefreshHandler.kt
│   │   │   │   │   └── serialization/
│   │   │   │   │       ├── Base64ByteArraySerializer.kt
│   │   │   │   │       └── InstantIso8601Serializer.kt
│   │   │   │   └── AndroidManifest.xml
│   │   │   └── test/java/com/miguelrodriguez19/safecube/core/network/
│   │   │       ├── data/
│   │   │       │   ├── auth/
│   │   │       │   │   ├── AuthInterceptorTest.kt
│   │   │       │   │   ├── TokenRefreshAuthenticatorFlowIntegrationTest.kt
│   │   │       │   │   └── TokenRefreshAuthenticatorTest.kt
│   │   │       │   └── client/
│   │   │       │       └── NetworkClientFactoryIntegrationTest.kt
│   │   │       └── domain/model/
│   │   │           └── NetworkConfigTest.kt
│   │   └── build.gradle.kts
│   ├── storage/
│   │   ├── schemas/com.miguelrodriguez19.safecube.core.storage.AppDatabase/
│   │   │   ├── 2.json
│   │   │   ├── 3.json
│   │   │   ├── 4.json
│   │   │   └── 5.json
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/miguelrodriguez19/safecube/core/storage/
│   │   │   │   │   ├── di/
│   │   │   │   │   │   ├── StorageBindingsModule.kt
│   │   │   │   │   │   └── StorageModule.kt
│   │   │   │   │   ├── local/
│   │   │   │   │   │   ├── SecureItemDraftEntityMapper.kt
│   │   │   │   │   │   ├── SecureItemDraftLocalStorage.kt
│   │   │   │   │   │   ├── SecureItemLocalStorage.kt
│   │   │   │   │   │   └── SecureItemSyncCheckpointLocalStorage.kt
│   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   ├── SecureItemDao.kt
│   │   │   │   │   ├── SecureItemDraftDao.kt
│   │   │   │   │   ├── SecureItemDraftEntity.kt
│   │   │   │   │   ├── SecureItemDraftTypeDb.kt
│   │   │   │   │   ├── SecureItemEntity.kt
│   │   │   │   │   ├── SecureItemSyncCheckpointDao.kt
│   │   │   │   │   ├── SecureItemSyncCheckpointEntity.kt
│   │   │   │   │   ├── SecureItemSyncStateDb.kt
│   │   │   │   │   ├── StorageMigrations.kt
│   │   │   │   │   └── StorageTypeConverters.kt
│   │   │   │   └── AndroidManifest.xml
│   │   │   └── test/java/com/miguelrodriguez19/safecube/core/storage/
│   │   │       ├── local/
│   │   │       │   ├── SecureItemDraftEntityMapperTest.kt
│   │   │       │   ├── SecureItemDraftLocalStorageTest.kt
│   │   │       │   ├── SecureItemLocalStorageTest.kt
│   │   │       │   └── SecureItemSyncCheckpointLocalStorageTest.kt
│   │   │       ├── SecureItemDaoIntegrationTest.kt
│   │   │       ├── SecureItemDraftDaoIntegrationTest.kt
│   │   │       ├── SecureItemDraftTypeDbTest.kt
│   │   │       ├── SecureItemSyncCheckpointDaoIntegrationTest.kt
│   │   │       ├── StorageMigrationsIntegrationTest.kt
│   │   │       └── StorageTypeConvertersTest.kt
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
│   ├── vault/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/miguelrodriguez19/safecube/core/vault/
│   │   │   │   │   ├── data/
│   │   │   │   │   │   ├── codec/
│   │   │   │   │   │   │   ├── JsonSecureItemContentCodec.kt
│   │   │   │   │   │   │   ├── NoteSecureItemContentJsonAdapter.kt
│   │   │   │   │   │   │   ├── PasswordSecureItemContentJsonAdapter.kt
│   │   │   │   │   │   │   └── SecureItemContentJsonAdapter.kt
│   │   │   │   │   │   ├── crypto/
│   │   │   │   │   │   │   ├── SecureItemCryptoContextProvider.kt
│   │   │   │   │   │   │   ├── SecureItemPayloadAadFactory.kt
│   │   │   │   │   │   │   ├── SecureItemPayloadEnvelopeV1Codec.kt
│   │   │   │   │   │   │   └── VaultItemCipher.kt
│   │   │   │   │   │   ├── local/
│   │   │   │   │   │   │   ├── EncryptedVaultKeyMaterialPrefs.kt
│   │   │   │   │   │   │   └── VaultKeyMaterialCache.kt
│   │   │   │   │   │   ├── remote/
│   │   │   │   │   │   │   ├── RemoteSecureItemDataSource.kt
│   │   │   │   │   │   │   └── RemoteVaultKeyMaterialDataSource.kt
│   │   │   │   │   │   └── session/
│   │   │   │   │   │       ├── VaultInMemoryKekStore.kt
│   │   │   │   │   │       └── VaultSessionManagerImpl.kt
│   │   │   │   │   ├── di/
│   │   │   │   │   │   └── VaultModule.kt
│   │   │   │   │   └── domain/
│   │   │   │   │       ├── codec/
│   │   │   │   │       │   ├── SecureItemContentCodec.kt
│   │   │   │   │       │   ├── SecureItemContentDecodeError.kt
│   │   │   │   │       │   └── SecureItemContentDecodeResult.kt
│   │   │   │   │       ├── config/
│   │   │   │   │       │   └── VaultCryptoDefaults.kt
│   │   │   │   │       ├── model/
│   │   │   │   │       │   ├── initialize/
│   │   │   │   │       │   │   ├── VaultInitializeError.kt
│   │   │   │   │       │   │   └── VaultInitializeResult.kt
│   │   │   │   │       │   ├── remote/
│   │   │   │   │       │   │   ├── request/
│   │   │   │   │       │   │   │   ├── RemoteCreateSecureItemRequest.kt
│   │   │   │   │       │   │   │   └── RemoteUpdateSecureItemRequest.kt
│   │   │   │   │       │   │   ├── result/
│   │   │   │   │       │   │   │   ├── RemoteCreateSecureItemResult.kt
│   │   │   │   │       │   │   │   ├── RemoteDeleteSecureItemResult.kt
│   │   │   │   │       │   │   │   ├── RemoteUpdateSecureItemResult.kt
│   │   │   │   │       │   │   │   ├── SecureItemRemoteResult.kt
│   │   │   │   │       │   │   │   └── VaultKeyMaterialRemoteResult.kt
│   │   │   │   │       │   │   ├── RemoteListVaultItemsRequestParams.kt
│   │   │   │   │       │   │   ├── RemoteSecureItem.kt
│   │   │   │   │       │   │   └── RemoteSecureItemSummary.kt
│   │   │   │   │       │   ├── secureitem/
│   │   │   │   │       │   │   ├── crud/
│   │   │   │   │       │   │   │   ├── ObserveSecureItemDetailResult.kt
│   │   │   │   │       │   │   │   ├── SecureItemCrudError.kt
│   │   │   │   │       │   │   │   ├── SecureItemDetail.kt
│   │   │   │   │       │   │   │   ├── SecureItemMutationResult.kt
│   │   │   │   │       │   │   │   ├── SecureNoteDraft.kt
│   │   │   │   │       │   │   │   ├── SecurePasswordDraft.kt
│   │   │   │   │       │   │   │   └── VaultItemSummary.kt
│   │   │   │   │       │   │   ├── itemcontent/
│   │   │   │   │       │   │   │   ├── NoteSecureItemContent.kt
│   │   │   │   │       │   │   │   ├── PasswordSecureItemContent.kt
│   │   │   │   │       │   │   │   └── SecureItemContent.kt
│   │   │   │   │       │   │   ├── EncodedSecureItemContent.kt
│   │   │   │   │       │   │   ├── SecureItem.kt
│   │   │   │   │       │   │   ├── SecureItemDraftType.kt
│   │   │   │   │       │   │   ├── SecureItemSyncDraft.kt
│   │   │   │   │       │   │   ├── SecureItemSyncState.kt
│   │   │   │   │       │   │   └── SecureItemType.kt
│   │   │   │   │       │   ├── sync/
│   │   │   │   │       │   │   ├── draft/
│   │   │   │   │       │   │   │   ├── DiscardSecureItemDraftResult.kt
│   │   │   │   │       │   │   │   └── PublishSecureItemDraftResult.kt
│   │   │   │   │       │   │   ├── pull/
│   │   │   │   │       │   │   │   ├── PullVaultDeltaApplyResults.kt
│   │   │   │   │       │   │   │   ├── PullVaultDeltaError.kt
│   │   │   │   │       │   │   │   ├── PullVaultDeltaFetchResults.kt
│   │   │   │   │       │   │   │   └── PullVaultDeltaResult.kt
│   │   │   │   │       │   │   ├── push/
│   │   │   │   │       │   │   │   ├── PushLocalVaultChangesError.kt
│   │   │   │   │       │   │   │   ├── PushLocalVaultChangesInternal.kt
│   │   │   │   │       │   │   │   └── PushLocalVaultChangesResult.kt
│   │   │   │   │       │   │   ├── VaultSyncError.kt
│   │   │   │   │       │   │   └── VaultSyncResult.kt
│   │   │   │   │       │   ├── unlock/
│   │   │   │   │       │   │   ├── VaultUnlockError.kt
│   │   │   │   │       │   │   └── VaultUnlockResult.kt
│   │   │   │   │       │   ├── UnlockedKeyring.kt
│   │   │   │   │       │   ├── VaultKeyMaterial.kt
│   │   │   │   │       │   └── VaultState.kt
│   │   │   │   │       ├── repository/
│   │   │   │   │       │   ├── SecureItemDraftRepository.kt
│   │   │   │   │       │   ├── SecureItemRemoteRepository.kt
│   │   │   │   │       │   ├── SecureItemRepository.kt
│   │   │   │   │       │   ├── SecureItemSyncCheckpointRepository.kt
│   │   │   │   │       │   ├── VaultKeyMaterialLocalRepository.kt
│   │   │   │   │       │   └── VaultKeyMaterialRemoteRepository.kt
│   │   │   │   │       ├── service/
│   │   │   │   │       │   ├── EncryptedSecureItemPayload.kt
│   │   │   │   │       │   ├── SecureItemCryptoError.kt
│   │   │   │   │       │   ├── SecureItemCryptoService.kt
│   │   │   │   │       │   ├── SecureItemDecryptionResult.kt
│   │   │   │   │       │   └── SecureItemEncryptionResult.kt
│   │   │   │   │       ├── session/
│   │   │   │   │       │   └── VaultSessionManager.kt
│   │   │   │   │       └── usecase/
│   │   │   │   │           ├── secureitem/
│   │   │   │   │           │   ├── note/
│   │   │   │   │           │   │   ├── CreateSecureNoteUseCase.kt
│   │   │   │   │           │   │   ├── NoteDraftToContentMapper.kt
│   │   │   │   │           │   │   └── UpdateSecureNoteUseCase.kt
│   │   │   │   │           │   ├── password/
│   │   │   │   │           │   │   ├── CreateSecurePasswordUseCase.kt
│   │   │   │   │           │   │   ├── PasswordDraftToContentMapper.kt
│   │   │   │   │           │   │   └── UpdateSecurePasswordUseCase.kt
│   │   │   │   │           │   ├── CurrentInstantProvider.kt
│   │   │   │   │           │   ├── ObserveSecureItemDetailUseCase.kt
│   │   │   │   │           │   ├── ObserveVaultItemSummariesUseCase.kt
│   │   │   │   │           │   ├── SecureItemIdGenerator.kt
│   │   │   │   │           │   ├── SecureItemMutationCoordinator.kt
│   │   │   │   │           │   └── SoftDeleteSecureItemUseCase.kt
│   │   │   │   │           ├── sync/
│   │   │   │   │           │   ├── draft/
│   │   │   │   │           │   │   ├── DiscardSecureItemDraftUseCase.kt
│   │   │   │   │           │   │   ├── PublishSecureItemDraftUseCase.kt
│   │   │   │   │           │   │   ├── SecureItemDraftPolicyCoordinator.kt
│   │   │   │   │           │   │   └── SecureItemDraftPolicyMappings.kt
│   │   │   │   │           │   ├── pull/
│   │   │   │   │           │   │   ├── PullVaultDeltaMappings.kt
│   │   │   │   │           │   │   └── PullVaultDeltaUseCase.kt
│   │   │   │   │           │   ├── push/
│   │   │   │   │           │   │   ├── PushLocalVaultChangesMappings.kt
│   │   │   │   │           │   │   └── PushLocalVaultChangesUseCase.kt
│   │   │   │   │           │   ├── ObserveVaultSyncingUseCase.kt
│   │   │   │   │           │   ├── SyncVaultNowUseCase.kt
│   │   │   │   │           │   ├── VaultSyncExecutionLock.kt
│   │   │   │   │           │   ├── VaultSyncTrigger.kt
│   │   │   │   │           │   └── VaultSyncUseCase.kt
│   │   │   │   │           └── vault/
│   │   │   │   │               ├── VaultInitializeUseCase.kt
│   │   │   │   │               ├── VaultUnlocker.kt
│   │   │   │   │               └── VaultUnlockUseCase.kt
│   │   │   │   └── AndroidManifest.xml
│   │   │   └── test/java/com/miguelrodriguez19/safecube/core/vault/
│   │   │       ├── data/
│   │   │       │   ├── codec/
│   │   │       │   │   ├── JsonSecureItemContentCodecTest.kt
│   │   │       │   │   └── SecureItemContentJsonAdapterTest.kt
│   │   │       │   ├── crypto/
│   │   │       │   │   ├── SecureItemCryptoContextProviderTest.kt
│   │   │       │   │   ├── SecureItemPayloadAadFactoryTest.kt
│   │   │       │   │   ├── SecureItemPayloadEnvelopeV1CodecTest.kt
│   │   │       │   │   └── VaultItemCipherTest.kt
│   │   │       │   ├── local/
│   │   │       │   │   └── VaultKeyMaterialCacheTest.kt
│   │   │       │   ├── remote/
│   │   │       │   │   ├── RemoteSecureItemDataSourceIntegrationTest.kt
│   │   │       │   │   ├── RemoteSecureItemDataSourceTest.kt
│   │   │       │   │   ├── RemoteVaultKeyMaterialDataSourceIntegrationTest.kt
│   │   │       │   │   └── RemoteVaultKeyMaterialDataSourceTest.kt
│   │   │       │   └── session/
│   │   │       │       ├── VaultInMemoryKekStoreTest.kt
│   │   │       │       └── VaultSessionManagerImplTest.kt
│   │   │       └── domain/
│   │   │           ├── model/secureitem/
│   │   │           │   ├── itemcontent/
│   │   │           │   │   └── NoteSecureItemContentTest.kt
│   │   │           │   ├── SecureItemContentTest.kt
│   │   │           │   ├── SecureItemSyncDraftTest.kt
│   │   │           │   └── SecureItemTest.kt
│   │   │           └── usecase/
│   │   │               ├── draft/
│   │   │               │   ├── DiscardSecureItemDraftUseCaseTest.kt
│   │   │               │   ├── PublishSecureItemDraftUseCaseTest.kt
│   │   │               │   └── SecureItemDraftPolicyCoordinatorTest.kt
│   │   │               ├── note/
│   │   │               │   └── NoteDraftToContentMapperTest.kt
│   │   │               ├── password/
│   │   │               │   └── PasswordDraftToContentMapperTest.kt
│   │   │               ├── CreateSecureNoteUseCaseTest.kt
│   │   │               ├── CreateSecurePasswordUseCaseTest.kt
│   │   │               ├── ObserveSecureItemDetailUseCaseTest.kt
│   │   │               ├── ObserveVaultItemSummariesUseCaseTest.kt
│   │   │               ├── PullVaultDeltaUseCaseTest.kt
│   │   │               ├── PushLocalVaultChangesUseCaseTest.kt
│   │   │               ├── SecureItemMutationCoordinatorTest.kt
│   │   │               ├── SoftDeleteSecureItemUseCaseTest.kt
│   │   │               ├── UpdateSecureNoteUseCaseTest.kt
│   │   │               ├── UpdateSecurePasswordUseCaseTest.kt
│   │   │               ├── VaultInitializeUseCaseTest.kt
│   │   │               ├── VaultSyncExecutionLockTest.kt
│   │   │               ├── VaultSyncTriggerTest.kt
│   │   │               ├── VaultSyncUseCaseTest.kt
│   │   │               └── VaultUnlockUseCaseTest.kt
│   │   └── build.gradle.kts
├── docs/
│   ├── architecture/
│   │   ├── crypto-v1.md
│   │   ├── openapi-auth-contract-integration.md
│   │   ├── openapi-vault-items-contract-integration.md
│   │   ├── openapi-vault-key-material-contract-integration.md
│   │   ├── secure-item-payload-v1.md
│   │   ├── storage_decision.md
│   │   ├── vault-sync-conflict-draft-resolution.md
│   │   └── vault-sync-v1.md
│   ├── package-structure/
│   │   └── package_structure.md
│   ├── roadmap/
│   │   ├── roadmap--fase-1.md
│   │   ├── roadmap--fase-2.md
│   │   ├── roadmap--fase-3.md
│   │   ├── roadmap--fase-4.md
│   │   ├── roadmap--fase-5.md
│   │   └── roadmap--high-level.md
│   ├── testing/
│   │   ├── testing.md
│   │   └── TESTING_STANDARD.md
│   └── README.md
├── feature/
│   ├── auth/
│   │   ├── src/main/
│   │   │   ├── java/com/miguelrodriguez19/safecube/feature/auth/
│   │   │   │   ├── presentation/
│   │   │   │   │   ├── component/
│   │   │   │   │   │   └── AuthActionLabel.kt
│   │   │   │   │   ├── gate/ui/
│   │   │   │   │   │   └── PostLoginGateScreen.kt
│   │   │   │   │   ├── login/
│   │   │   │   │   │   ├── action/
│   │   │   │   │   │   │   └── LoginUiAction.kt
│   │   │   │   │   │   ├── event/
│   │   │   │   │   │   │   └── LoginUiEvent.kt
│   │   │   │   │   │   ├── state/
│   │   │   │   │   │   │   └── LoginUiState.kt
│   │   │   │   │   │   ├── ui/
│   │   │   │   │   │   │   └── LoginScreen.kt
│   │   │   │   │   │   └── viewmodel/
│   │   │   │   │   │       └── LoginViewModel.kt
│   │   │   │   │   ├── mapper/
│   │   │   │   │   │   └── AuthUiErrorMapper.kt
│   │   │   │   │   ├── signup/
│   │   │   │   │   │   ├── action/
│   │   │   │   │   │   │   └── SignupUiAction.kt
│   │   │   │   │   │   ├── event/
│   │   │   │   │   │   │   └── SignupUiEvent.kt
│   │   │   │   │   │   ├── state/
│   │   │   │   │   │   │   └── SignupUiState.kt
│   │   │   │   │   │   ├── ui/
│   │   │   │   │   │   │   └── SignupScreen.kt
│   │   │   │   │   │   └── viewmodel/
│   │   │   │   │   │       └── SignupViewModel.kt
│   │   │   │   │   └── welcome/ui/
│   │   │   │   │       └── WelcomeScreen.kt
│   │   │   │   └── screens/
│   │   │   ├── res/values/
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   ├── profile/
│   │   ├── src/main/
│   │   │   ├── java/com/miguelrodriguez19/safecube/feature/profile/
│   │   │   │   ├── navigation/
│   │   │   │   └── presentation/profile/ui/
│   │   │   │       └── ProfileScreen.kt
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   └── vault/
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/com/miguelrodriguez19/safecube/feature/vault/presentation/
│       │   │   │   ├── create/
│       │   │   │   │   ├── action/
│       │   │   │   │   │   └── CreateVaultUiAction.kt
│       │   │   │   │   ├── event/
│       │   │   │   │   │   └── CreateVaultUiEvent.kt
│       │   │   │   │   ├── state/
│       │   │   │   │   │   └── CreateVaultUiState.kt
│       │   │   │   │   ├── ui/
│       │   │   │   │   │   └── CreateVaultScreen.kt
│       │   │   │   │   └── viewmodel/
│       │   │   │   │       └── CreateVaultViewModel.kt
│       │   │   │   ├── folders/ui/
│       │   │   │   │   └── VaultFoldersScreen.kt
│       │   │   │   ├── home/
│       │   │   │   │   ├── action/
│       │   │   │   │   ├── state/
│       │   │   │   │   │   └── VaultHomeUiState.kt
│       │   │   │   │   ├── ui/
│       │   │   │   │   │   └── VaultScreen.kt
│       │   │   │   │   └── viewmodel/
│       │   │   │   │       └── VaultHomeViewModel.kt
│       │   │   │   ├── noteeditor/
│       │   │   │   │   ├── action/
│       │   │   │   │   │   └── NoteEditorUiAction.kt
│       │   │   │   │   ├── event/
│       │   │   │   │   │   └── NoteEditorUiEvent.kt
│       │   │   │   │   ├── state/
│       │   │   │   │   │   └── NoteEditorUiState.kt
│       │   │   │   │   ├── ui/
│       │   │   │   │   │   └── NoteEditorScreen.kt
│       │   │   │   │   └── viewmodel/
│       │   │   │   │       └── NoteEditorViewModel.kt
│       │   │   │   ├── passwordeditor/
│       │   │   │   │   ├── action/
│       │   │   │   │   │   └── PasswordEditorUiAction.kt
│       │   │   │   │   ├── event/
│       │   │   │   │   │   └── PasswordEditorUiEvent.kt
│       │   │   │   │   ├── state/
│       │   │   │   │   │   └── PasswordEditorUiState.kt
│       │   │   │   │   ├── ui/
│       │   │   │   │   │   └── PasswordEditorScreen.kt
│       │   │   │   │   └── viewmodel/
│       │   │   │   │       └── PasswordEditorViewModel.kt
│       │   │   │   ├── recovery/
│       │   │   │   │   ├── action/
│       │   │   │   │   │   └── RecoveryKeyUiAction.kt
│       │   │   │   │   ├── event/
│       │   │   │   │   │   └── RecoveryKeyUiEvent.kt
│       │   │   │   │   ├── state/
│       │   │   │   │   │   └── RecoveryKeyUiState.kt
│       │   │   │   │   ├── ui/
│       │   │   │   │   │   └── RecoveryKeyScreen.kt
│       │   │   │   │   └── viewmodel/
│       │   │   │   │       └── RecoveryKeyViewModel.kt
│       │   │   │   ├── settings/ui/
│       │   │   │   │   └── SettingsScreen.kt
│       │   │   │   ├── shared/
│       │   │   │   │   ├── editor/
│       │   │   │   │   │   └── SecureItemEditorScaffold.kt
│       │   │   │   │   ├── error/
│       │   │   │   │   │   └── SecureItemCrudErrorMessageMapper.kt
│       │   │   │   │   ├── navigation/
│       │   │   │   │   │   └── NavigationBar.kt
│       │   │   │   │   └── sync/
│       │   │   │   │       ├── SyncIconButton.kt
│       │   │   │   │       └── VaultSyncUiMapper.kt
│       │   │   │   └── unlock/
│       │   │   │       ├── action/
│       │   │   │       │   └── UnlockVaultUiAction.kt
│       │   │   │       ├── event/
│       │   │   │       │   └── UnlockVaultUiEvent.kt
│       │   │   │       ├── state/
│       │   │   │       │   └── UnlockVaultUiState.kt
│       │   │   │       ├── ui/
│       │   │   │       │   └── UnlockVaultScreen.kt
│       │   │   │       └── viewmodel/
│       │   │   │           └── UnlockVaultViewModel.kt
│       │   │   └── AndroidManifest.xml
│       │   └── test/java/com/miguelrodriguez19/safecube/feature/vault/
│       │       ├── presentation/
│       │       │   ├── home/viewmodel/
│       │       │   │   └── VaultHomeViewModelTest.kt
│       │       │   ├── noteeditor/viewmodel/
│       │       │   │   └── NoteEditorViewModelTest.kt
│       │       │   └── passwordeditor/viewmodel/
│       │       │       └── PasswordEditorViewModelTest.kt
│       │       └── test/
│       │           └── MainDispatcherRule.kt
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
