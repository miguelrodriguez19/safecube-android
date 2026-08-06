# Package Structure
Updated: 06-08-2026 04:00:23

```
safecube-android/
├── .build/com/safecube/tooling/
│   ├── FolderTreeToFile.class
│   └── Logger.class
├── .github/
│   ├── actions/
│   ├── scripts/
│   ├── workflows/
│   │   ├── codeql.yml
│   │   ├── dependency-review.yml
│   │   ├── kotlin-ci-reusable.yml
│   │   ├── pull-request-quality.yml
│   │   ├── release.yml
│   │   └── secret-scan.yml
│   └── dependabot.yml
├── .run/
│   ├── run-folder-tree.run.xml
│   └── verifyCoverage.run.xml
├── app/
│   ├── src/
│   │   ├── androidTest/java/com/miguelrodriguez19/safecube/
│   │   │   └── MainActivitySmokeTest.kt
│   │   ├── main/
│   │   │   ├── java/com/miguelrodriguez19/safecube/app/
│   │   │   │   ├── entrypoint/
│   │   │   │   │   ├── MainActivity.kt
│   │   │   │   │   └── SafeCubeApp.kt
│   │   │   │   ├── presentation/
│   │   │   │   │   ├── navigation/
│   │   │   │   │   │   ├── gate/
│   │   │   │   │   │   │   ├── NavigationGates.kt
│   │   │   │   │   │   │   └── NavigationGatesEntryPoint.kt
│   │   │   │   │   │   ├── host/
│   │   │   │   │   │   │   ├── NavigationBackPolicy.kt
│   │   │   │   │   │   │   ├── NavigationDependencies.kt
│   │   │   │   │   │   │   ├── NavigationGraph.kt
│   │   │   │   │   │   │   ├── NavigationSessionCoordinator.kt
│   │   │   │   │   │   │   └── NavigationWrapper.kt
│   │   │   │   │   │   └── route/
│   │   │   │   │   │       └── Routes.kt
│   │   │   │   │   ├── theme/
│   │   │   │   │   │   ├── Color.kt
│   │   │   │   │   │   ├── Theme.kt
│   │   │   │   │   │   └── Type.kt
│   │   │   │   │   └── ui/
│   │   │   │   │       └── SplashGateScreen.kt
│   │   │   │   └── session/
│   │   │   │       ├── AccountSessionLifecycleImpl.kt
│   │   │   │       └── AccountSessionModule.kt
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
│   │       ├── app/session/
│   │       │   └── AccountSessionLifecycleImplTest.kt
│   │       └── ExampleUnitTest.kt
│   ├── .gitignore
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── buildSrc/
│   ├── build/
│   │   ├── classes/kotlin/
│   │   │   ├── main/
│   │   │   │   ├── com/miguelrodriguez19/safecube/buildlogic/
│   │   │   │   │   ├── AppVersion.class
│   │   │   │   │   ├── AppVersionComparator$compareVersionNames$1.class
│   │   │   │   │   ├── AppVersionComparator$compareVersionNames$2.class
│   │   │   │   │   ├── AppVersionComparator$compareVersionNames$3.class
│   │   │   │   │   ├── AppVersionComparator$ParsedSemVer$Companion.class
│   │   │   │   │   ├── AppVersionComparator$ParsedSemVer.class
│   │   │   │   │   ├── AppVersionComparator.class
│   │   │   │   │   ├── AppVersionParser.class
│   │   │   │   │   ├── ReleaseSigningConfig.class
│   │   │   │   │   └── ReleaseSigningCredentials.class
│   │   │   │   └── META-INF/
│   │   │   │       └── buildSrc.kotlin_module
│   │   │   └── test/
│   │   │       ├── com/miguelrodriguez19/safecube/buildlogic/
│   │   │       │   ├── AppVersionTest$rejects a missing property$exception$1.class
│   │   │       │   ├── AppVersionTest$rejects a version code that does not increase$exception$1.class
│   │   │       │   ├── AppVersionTest$rejects a version name that changes only build metadata$exception$1.class
│   │   │       │   ├── AppVersionTest$rejects a version name that does not increase$exception$1.class
│   │   │       │   ├── AppVersionTest$rejects an empty property$exception$1.class
│   │   │       │   ├── AppVersionTest$rejects an invalid semver$exception$1.class
│   │   │       │   ├── AppVersionTest$rejects an invalid version code$exception$1.class
│   │   │       │   ├── AppVersionTest.class
│   │   │       │   ├── ReleaseSigningConfigTest$rejects blank values as missing configuration$exception$1.class
│   │   │       │   ├── ReleaseSigningConfigTest$rejects partial release signing configuration$exception$1.class
│   │   │       │   ├── ReleaseSigningConfigTest$requires an existing keystore for verification$exception$1.class
│   │   │       │   └── ReleaseSigningConfigTest.class
│   │   │       └── META-INF/
│   │   │           └── buildSrc_test.kotlin_module
│   │   ├── kotlin/
│   │   │   ├── compileKotlin/
│   │   │   │   ├── cacheable/
│   │   │   │   │   ├── caches-jvm/
│   │   │   │   │   │   ├── inputs/
│   │   │   │   │   │   │   ├── source-to-output.tab
│   │   │   │   │   │   │   ├── source-to-output.tab.keystream
│   │   │   │   │   │   │   ├── source-to-output.tab.keystream.len
│   │   │   │   │   │   │   ├── source-to-output.tab.len
│   │   │   │   │   │   │   ├── source-to-output.tab.values.at
│   │   │   │   │   │   │   ├── source-to-output.tab_i
│   │   │   │   │   │   │   └── source-to-output.tab_i.len
│   │   │   │   │   │   ├── jvm/kotlin/
│   │   │   │   │   │   │   ├── class-attributes.tab
│   │   │   │   │   │   │   ├── class-attributes.tab.keystream
│   │   │   │   │   │   │   ├── class-attributes.tab.keystream.len
│   │   │   │   │   │   │   ├── class-attributes.tab.len
│   │   │   │   │   │   │   ├── class-attributes.tab.values.at
│   │   │   │   │   │   │   ├── class-attributes.tab_i
│   │   │   │   │   │   │   ├── class-attributes.tab_i.len
│   │   │   │   │   │   │   ├── class-fq-name-to-source.tab
│   │   │   │   │   │   │   ├── class-fq-name-to-source.tab.keystream
│   │   │   │   │   │   │   ├── class-fq-name-to-source.tab.keystream.len
│   │   │   │   │   │   │   ├── class-fq-name-to-source.tab.len
│   │   │   │   │   │   │   ├── class-fq-name-to-source.tab.values.at
│   │   │   │   │   │   │   ├── class-fq-name-to-source.tab_i
│   │   │   │   │   │   │   ├── class-fq-name-to-source.tab_i.len
│   │   │   │   │   │   │   ├── constants.tab
│   │   │   │   │   │   │   ├── constants.tab.keystream
│   │   │   │   │   │   │   ├── constants.tab.keystream.len
│   │   │   │   │   │   │   ├── constants.tab.len
│   │   │   │   │   │   │   ├── constants.tab.values.at
│   │   │   │   │   │   │   ├── constants.tab_i
│   │   │   │   │   │   │   ├── constants.tab_i.len
│   │   │   │   │   │   │   ├── internal-name-to-source.tab
│   │   │   │   │   │   │   ├── internal-name-to-source.tab.keystream
│   │   │   │   │   │   │   ├── internal-name-to-source.tab.keystream.len
│   │   │   │   │   │   │   ├── internal-name-to-source.tab.len
│   │   │   │   │   │   │   ├── internal-name-to-source.tab.values.at
│   │   │   │   │   │   │   ├── internal-name-to-source.tab_i
│   │   │   │   │   │   │   ├── internal-name-to-source.tab_i.len
│   │   │   │   │   │   │   ├── proto.tab
│   │   │   │   │   │   │   ├── proto.tab.keystream
│   │   │   │   │   │   │   ├── proto.tab.keystream.len
│   │   │   │   │   │   │   ├── proto.tab.len
│   │   │   │   │   │   │   ├── proto.tab.values.at
│   │   │   │   │   │   │   ├── proto.tab_i
│   │   │   │   │   │   │   ├── proto.tab_i.len
│   │   │   │   │   │   │   ├── source-to-classes.tab
│   │   │   │   │   │   │   ├── source-to-classes.tab.keystream
│   │   │   │   │   │   │   ├── source-to-classes.tab.keystream.len
│   │   │   │   │   │   │   ├── source-to-classes.tab.len
│   │   │   │   │   │   │   ├── source-to-classes.tab.values.at
│   │   │   │   │   │   │   ├── source-to-classes.tab_i
│   │   │   │   │   │   │   └── source-to-classes.tab_i.len
│   │   │   │   │   │   └── lookups/
│   │   │   │   │   │       ├── counters.tab
│   │   │   │   │   │       ├── file-to-id.tab
│   │   │   │   │   │       ├── file-to-id.tab.keystream
│   │   │   │   │   │       ├── file-to-id.tab.keystream.len
│   │   │   │   │   │       ├── file-to-id.tab.len
│   │   │   │   │   │       ├── file-to-id.tab.values.at
│   │   │   │   │   │       ├── file-to-id.tab_i
│   │   │   │   │   │       ├── file-to-id.tab_i.len
│   │   │   │   │   │       ├── id-to-file.tab
│   │   │   │   │   │       ├── id-to-file.tab.keystream
│   │   │   │   │   │       ├── id-to-file.tab.keystream.len
│   │   │   │   │   │       ├── id-to-file.tab.len
│   │   │   │   │   │       ├── id-to-file.tab.values.at
│   │   │   │   │   │       ├── id-to-file.tab_i
│   │   │   │   │   │       ├── id-to-file.tab_i.len
│   │   │   │   │   │       ├── lookups.tab
│   │   │   │   │   │       ├── lookups.tab.keystream
│   │   │   │   │   │       ├── lookups.tab.keystream.len
│   │   │   │   │   │       ├── lookups.tab.len
│   │   │   │   │   │       ├── lookups.tab.values.at
│   │   │   │   │   │       ├── lookups.tab_i
│   │   │   │   │   │       └── lookups.tab_i.len
│   │   │   │   │   └── last-build.bin
│   │   │   │   └── classpath-snapshot/
│   │   │   │       └── shrunk-classpath-snapshot.bin
│   │   │   └── compileTestKotlin/
│   │   │       ├── cacheable/
│   │   │       │   ├── caches-jvm/
│   │   │       │   │   ├── inputs/
│   │   │       │   │   │   ├── source-to-output.tab
│   │   │       │   │   │   ├── source-to-output.tab.keystream
│   │   │       │   │   │   ├── source-to-output.tab.keystream.len
│   │   │       │   │   │   ├── source-to-output.tab.len
│   │   │       │   │   │   ├── source-to-output.tab.values.at
│   │   │       │   │   │   ├── source-to-output.tab_i
│   │   │       │   │   │   └── source-to-output.tab_i.len
│   │   │       │   │   ├── jvm/kotlin/
│   │   │       │   │   │   ├── class-attributes.tab
│   │   │       │   │   │   ├── class-attributes.tab.keystream
│   │   │       │   │   │   ├── class-attributes.tab.keystream.len
│   │   │       │   │   │   ├── class-attributes.tab.len
│   │   │       │   │   │   ├── class-attributes.tab.values.at
│   │   │       │   │   │   ├── class-attributes.tab_i
│   │   │       │   │   │   ├── class-attributes.tab_i.len
│   │   │       │   │   │   ├── class-fq-name-to-source.tab
│   │   │       │   │   │   ├── class-fq-name-to-source.tab.keystream
│   │   │       │   │   │   ├── class-fq-name-to-source.tab.keystream.len
│   │   │       │   │   │   ├── class-fq-name-to-source.tab.len
│   │   │       │   │   │   ├── class-fq-name-to-source.tab.values.at
│   │   │       │   │   │   ├── class-fq-name-to-source.tab_i
│   │   │       │   │   │   ├── class-fq-name-to-source.tab_i.len
│   │   │       │   │   │   ├── internal-name-to-source.tab
│   │   │       │   │   │   ├── internal-name-to-source.tab.keystream
│   │   │       │   │   │   ├── internal-name-to-source.tab.keystream.len
│   │   │       │   │   │   ├── internal-name-to-source.tab.len
│   │   │       │   │   │   ├── internal-name-to-source.tab.values.at
│   │   │       │   │   │   ├── internal-name-to-source.tab_i
│   │   │       │   │   │   ├── internal-name-to-source.tab_i.len
│   │   │       │   │   │   ├── proto.tab
│   │   │       │   │   │   ├── proto.tab.keystream
│   │   │       │   │   │   ├── proto.tab.keystream.len
│   │   │       │   │   │   ├── proto.tab.len
│   │   │       │   │   │   ├── proto.tab.values.at
│   │   │       │   │   │   ├── proto.tab_i
│   │   │       │   │   │   ├── proto.tab_i.len
│   │   │       │   │   │   ├── source-to-classes.tab
│   │   │       │   │   │   ├── source-to-classes.tab.keystream
│   │   │       │   │   │   ├── source-to-classes.tab.keystream.len
│   │   │       │   │   │   ├── source-to-classes.tab.len
│   │   │       │   │   │   ├── source-to-classes.tab.values.at
│   │   │       │   │   │   ├── source-to-classes.tab_i
│   │   │       │   │   │   └── source-to-classes.tab_i.len
│   │   │       │   │   └── lookups/
│   │   │       │   │       ├── counters.tab
│   │   │       │   │       ├── file-to-id.tab
│   │   │       │   │       ├── file-to-id.tab.keystream
│   │   │       │   │       ├── file-to-id.tab.keystream.len
│   │   │       │   │       ├── file-to-id.tab.len
│   │   │       │   │       ├── file-to-id.tab.values.at
│   │   │       │   │       ├── file-to-id.tab_i
│   │   │       │   │       ├── file-to-id.tab_i.len
│   │   │       │   │       ├── id-to-file.tab
│   │   │       │   │       ├── id-to-file.tab.keystream
│   │   │       │   │       ├── id-to-file.tab.keystream.len
│   │   │       │   │       ├── id-to-file.tab.len
│   │   │       │   │       ├── id-to-file.tab.values.at
│   │   │       │   │       ├── id-to-file.tab_i
│   │   │       │   │       ├── id-to-file.tab_i.len
│   │   │       │   │       ├── lookups.tab
│   │   │       │   │       ├── lookups.tab.keystream
│   │   │       │   │       ├── lookups.tab.keystream.len
│   │   │       │   │       ├── lookups.tab.len
│   │   │       │   │       ├── lookups.tab.values.at
│   │   │       │   │       ├── lookups.tab_i
│   │   │       │   │       └── lookups.tab_i.len
│   │   │       │   └── last-build.bin
│   │   │       ├── classpath-snapshot/
│   │   │       │   └── shrunk-classpath-snapshot.bin
│   │   │       └── local-state/
│   │   ├── libs/
│   │   │   └── buildSrc.jar
│   │   ├── pluginDescriptors/
│   │   ├── pluginUnderTestMetadata/
│   │   ├── reports/tests/test/
│   │   │   ├── classes/
│   │   │   │   ├── com.miguelrodriguez19.safecube.buildlogic.AppVersionTest.html
│   │   │   │   └── com.miguelrodriguez19.safecube.buildlogic.ReleaseSigningConfigTest.html
│   │   │   ├── css/
│   │   │   │   ├── base-style.css
│   │   │   │   └── style.css
│   │   │   ├── js/
│   │   │   │   └── report.js
│   │   │   ├── packages/
│   │   │   │   └── com.miguelrodriguez19.safecube.buildlogic.html
│   │   │   └── index.html
│   │   ├── test-results/test/
│   │   │   ├── binary/
│   │   │   │   ├── output.bin
│   │   │   │   ├── output.bin.idx
│   │   │   │   └── results.bin
│   │   │   ├── TEST-com.miguelrodriguez19.safecube.buildlogic.AppVersionTest.xml
│   │   │   └── TEST-com.miguelrodriguez19.safecube.buildlogic.ReleaseSigningConfigTest.xml
│   │   └── tmp/
│   │       ├── jar/
│   │       │   └── MANIFEST.MF
│   │       └── test/
│   ├── src/
│   │   ├── main/kotlin/com/miguelrodriguez19/safecube/buildlogic/
│   │   │   ├── AppVersion.kt
│   │   │   └── ReleaseSigningConfig.kt
│   │   └── test/kotlin/com/miguelrodriguez19/safecube/buildlogic/
│   │       ├── AppVersionTest.kt
│   │       └── ReleaseSigningConfigTest.kt
│   ├── build.gradle.kts
│   └── settings.gradle.kts
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
│   │   │   │   │           ├── AccountSessionLifecycle.kt
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
│   ├── build/reports/cyclonedx-direct/
│   │   └── bom.json
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
│   │   │   │   │   │       ├── ListSecureItemChangesResponse.kt
│   │   │   │   │   │       ├── ListSecureItemsResponse.kt
│   │   │   │   │   │       ├── RefreshTokenRequest.kt
│   │   │   │   │   │       ├── RegisterAccountRequest.kt
│   │   │   │   │   │       ├── RegisterAccountResult.kt
│   │   │   │   │   │       ├── SecureItemChangeResponse.kt
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
│   │   │       ├── contract/
│   │   │       │   └── VaultSyncOpenApiContractTest.kt
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
│   │   │   ├── 5.json
│   │   │   ├── 6.json
│   │   │   └── 7.json
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/miguelrodriguez19/safecube/core/storage/
│   │   │   │   │   ├── di/
│   │   │   │   │   │   ├── StorageBindingsModule.kt
│   │   │   │   │   │   └── StorageModule.kt
│   │   │   │   │   ├── local/
│   │   │   │   │   │   ├── SecureItemDraftEntityMapper.kt
│   │   │   │   │   │   ├── SecureItemDraftLocalStorage.kt
│   │   │   │   │   │   └── SecureItemLocalStorage.kt
│   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   ├── SecureItemDao.kt
│   │   │   │   │   ├── SecureItemDraftDao.kt
│   │   │   │   │   ├── SecureItemDraftEntity.kt
│   │   │   │   │   ├── SecureItemDraftSyncStatusDb.kt
│   │   │   │   │   ├── SecureItemDraftTypeDb.kt
│   │   │   │   │   ├── SecureItemEntity.kt
│   │   │   │   │   ├── SecureItemSyncCheckpointDao.kt
│   │   │   │   │   ├── SecureItemSyncCheckpointEntity.kt
│   │   │   │   │   ├── SecureItemSyncStateDb.kt
│   │   │   │   │   ├── StorageMigrations.kt
│   │   │   │   │   └── StorageTypeConverters.kt
│   │   │   │   └── AndroidManifest.xml
│   │   │   └── test/
│   │   │       ├── java/com/miguelrodriguez19/safecube/core/storage/
│   │   │       │   ├── local/
│   │   │       │   │   ├── SecureItemDraftEntityMapperTest.kt
│   │   │       │   │   ├── SecureItemDraftLocalStorageTest.kt
│   │   │       │   │   ├── SecureItemLocalStorageTest.kt
│   │   │       │   │   └── SecureItemOfficializationIntegrationTest.kt
│   │   │       │   ├── SecureItemDaoIntegrationTest.kt
│   │   │       │   ├── SecureItemDraftDaoIntegrationTest.kt
│   │   │       │   ├── SecureItemDraftTypeDbTest.kt
│   │   │       │   ├── SecureItemSyncCheckpointDaoIntegrationTest.kt
│   │   │       │   ├── StorageMigrationsIntegrationTest.kt
│   │   │       │   └── StorageTypeConvertersTest.kt
│   │   │       └── resources/
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
│   └── vault/
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/com/miguelrodriguez19/safecube/core/vault/
│       │   │   │   ├── data/
│       │   │   │   │   ├── codec/
│       │   │   │   │   │   ├── JsonSecureItemContentCodec.kt
│       │   │   │   │   │   ├── NoteSecureItemContentJsonAdapter.kt
│       │   │   │   │   │   ├── PasswordSecureItemContentJsonAdapter.kt
│       │   │   │   │   │   └── SecureItemContentJsonAdapter.kt
│       │   │   │   │   ├── crypto/
│       │   │   │   │   │   ├── SecureItemCryptoContextProvider.kt
│       │   │   │   │   │   ├── SecureItemPayloadAadFactory.kt
│       │   │   │   │   │   ├── SecureItemPayloadEnvelopeIdentityReader.kt
│       │   │   │   │   │   ├── SecureItemPayloadEnvelopeV1Codec.kt
│       │   │   │   │   │   └── VaultItemCipher.kt
│       │   │   │   │   ├── local/
│       │   │   │   │   │   ├── EncryptedVaultKeyMaterialPrefs.kt
│       │   │   │   │   │   └── VaultKeyMaterialCache.kt
│       │   │   │   │   ├── remote/
│       │   │   │   │   │   ├── RemoteSecureItemDataSource.kt
│       │   │   │   │   │   └── RemoteVaultKeyMaterialDataSource.kt
│       │   │   │   │   └── session/
│       │   │   │   │       ├── LocalVaultDataCleanerImpl.kt
│       │   │   │   │       ├── VaultInMemoryKekStore.kt
│       │   │   │   │       └── VaultSessionManagerImpl.kt
│       │   │   │   ├── di/
│       │   │   │   │   └── VaultModule.kt
│       │   │   │   └── domain/
│       │   │   │       ├── codec/
│       │   │   │       │   ├── SecureItemContentCodec.kt
│       │   │   │       │   ├── SecureItemContentDecodeError.kt
│       │   │   │       │   └── SecureItemContentDecodeResult.kt
│       │   │   │       ├── config/
│       │   │   │       │   └── VaultCryptoDefaults.kt
│       │   │   │       ├── model/
│       │   │   │       │   ├── initialize/
│       │   │   │       │   │   ├── VaultInitializeError.kt
│       │   │   │       │   │   └── VaultInitializeResult.kt
│       │   │   │       │   ├── remote/
│       │   │   │       │   │   ├── request/
│       │   │   │       │   │   │   ├── RemoteCreateSecureItemRequest.kt
│       │   │   │       │   │   │   ├── RemoteDeleteSecureItemRequest.kt
│       │   │   │       │   │   │   └── RemoteUpdateSecureItemRequest.kt
│       │   │   │       │   │   ├── result/
│       │   │   │       │   │   │   ├── RemoteCreateSecureItemResult.kt
│       │   │   │       │   │   │   ├── RemoteDeleteSecureItemResult.kt
│       │   │   │       │   │   │   ├── RemoteUpdateSecureItemResult.kt
│       │   │   │       │   │   │   ├── SecureItemRemoteResult.kt
│       │   │   │       │   │   │   └── VaultKeyMaterialRemoteResult.kt
│       │   │   │       │   │   ├── RemoteListVaultItemsRequestParams.kt
│       │   │   │       │   │   ├── RemoteSecureItem.kt
│       │   │   │       │   │   ├── RemoteSecureItemChangesPage.kt
│       │   │   │       │   │   └── RemoteSecureItemSummary.kt
│       │   │   │       │   ├── secureitem/
│       │   │   │       │   │   ├── crud/
│       │   │   │       │   │   │   ├── ObserveSecureItemDetailResult.kt
│       │   │   │       │   │   │   ├── ObserveSecureItemDraftDetailResult.kt
│       │   │   │       │   │   │   ├── SecureItemCrudError.kt
│       │   │   │       │   │   │   ├── SecureItemDetail.kt
│       │   │   │       │   │   │   ├── SecureItemDraftDetail.kt
│       │   │   │       │   │   │   ├── SecureItemMutationResult.kt
│       │   │   │       │   │   │   ├── SecureNoteDraft.kt
│       │   │   │       │   │   │   ├── SecurePasswordDraft.kt
│       │   │   │       │   │   │   ├── VaultItemDraftSummary.kt
│       │   │   │       │   │   │   └── VaultItemSummary.kt
│       │   │   │       │   │   ├── itemcontent/
│       │   │   │       │   │   │   ├── NoteSecureItemContent.kt
│       │   │   │       │   │   │   ├── PasswordSecureItemContent.kt
│       │   │   │       │   │   │   └── SecureItemContent.kt
│       │   │   │       │   │   ├── EncodedSecureItemContent.kt
│       │   │   │       │   │   ├── SecureItem.kt
│       │   │   │       │   │   ├── SecureItemDraftSyncStatus.kt
│       │   │   │       │   │   ├── SecureItemDraftType.kt
│       │   │   │       │   │   ├── SecureItemSyncDraft.kt
│       │   │   │       │   │   ├── SecureItemSyncState.kt
│       │   │   │       │   │   └── SecureItemType.kt
│       │   │   │       │   ├── sync/
│       │   │   │       │   │   ├── draft/
│       │   │   │       │   │   │   ├── DiscardSecureItemDraftResult.kt
│       │   │   │       │   │   │   └── PrepareSecureItemDraftForSyncResult.kt
│       │   │   │       │   │   ├── pull/
│       │   │   │       │   │   │   ├── PullVaultDeltaApplyResults.kt
│       │   │   │       │   │   │   ├── PullVaultDeltaError.kt
│       │   │   │       │   │   │   └── PullVaultDeltaResult.kt
│       │   │   │       │   │   ├── push/
│       │   │   │       │   │   │   ├── PushLocalVaultChangesError.kt
│       │   │   │       │   │   │   ├── PushLocalVaultChangesInternal.kt
│       │   │   │       │   │   │   └── PushLocalVaultChangesResult.kt
│       │   │   │       │   │   ├── VaultSyncError.kt
│       │   │   │       │   │   └── VaultSyncResult.kt
│       │   │   │       │   ├── unlock/
│       │   │   │       │   │   ├── VaultUnlockError.kt
│       │   │   │       │   │   └── VaultUnlockResult.kt
│       │   │   │       │   ├── UnlockedKeyring.kt
│       │   │   │       │   ├── VaultKeyMaterial.kt
│       │   │   │       │   └── VaultState.kt
│       │   │   │       ├── repository/
│       │   │   │       │   ├── SecureItemDraftRepository.kt
│       │   │   │       │   ├── SecureItemRemoteRepository.kt
│       │   │   │       │   ├── SecureItemRepository.kt
│       │   │   │       │   ├── VaultKeyMaterialLocalRepository.kt
│       │   │   │       │   └── VaultKeyMaterialRemoteRepository.kt
│       │   │   │       ├── service/
│       │   │   │       │   ├── EncryptedSecureItemPayload.kt
│       │   │   │       │   ├── SecureItemCryptoError.kt
│       │   │   │       │   ├── SecureItemCryptoService.kt
│       │   │   │       │   ├── SecureItemDecryptionResult.kt
│       │   │   │       │   ├── SecureItemEncryptionResult.kt
│       │   │   │       │   └── SecureItemPayloadIdentityReader.kt
│       │   │   │       ├── session/
│       │   │   │       │   ├── LocalVaultDataCleaner.kt
│       │   │   │       │   └── VaultSessionManager.kt
│       │   │   │       └── usecase/
│       │   │   │           ├── secureitem/
│       │   │   │           │   ├── note/
│       │   │   │           │   │   ├── CreateSecureNoteUseCase.kt
│       │   │   │           │   │   ├── NoteDraftToContentMapper.kt
│       │   │   │           │   │   └── UpdateSecureNoteUseCase.kt
│       │   │   │           │   ├── password/
│       │   │   │           │   │   ├── CreateSecurePasswordUseCase.kt
│       │   │   │           │   │   ├── PasswordDraftToContentMapper.kt
│       │   │   │           │   │   └── UpdateSecurePasswordUseCase.kt
│       │   │   │           │   ├── CurrentInstantProvider.kt
│       │   │   │           │   ├── ObserveSecureItemDetailUseCase.kt
│       │   │   │           │   ├── ObserveSecureItemDraftDetailUseCase.kt
│       │   │   │           │   ├── ObserveVaultDraftSummariesUseCase.kt
│       │   │   │           │   ├── ObserveVaultItemSummariesUseCase.kt
│       │   │   │           │   ├── SecureItemDraftMutationCoordinator.kt
│       │   │   │           │   ├── SecureItemIdGenerator.kt
│       │   │   │           │   ├── SecureItemMutationIdGenerator.kt
│       │   │   │           │   └── SoftDeleteSecureItemUseCase.kt
│       │   │   │           ├── sync/
│       │   │   │           │   ├── draft/
│       │   │   │           │   │   ├── DiscardSecureItemDraftUseCase.kt
│       │   │   │           │   │   ├── PrepareSecureItemDraftForSyncUseCase.kt
│       │   │   │           │   │   ├── SecureItemDraftPolicyMappings.kt
│       │   │   │           │   │   └── SecureItemDraftSyncCoordinator.kt
│       │   │   │           │   ├── pull/
│       │   │   │           │   │   ├── PullVaultDeltaMappings.kt
│       │   │   │           │   │   └── PullVaultDeltaUseCase.kt
│       │   │   │           │   ├── push/
│       │   │   │           │   │   ├── PushLocalVaultChangesMappings.kt
│       │   │   │           │   │   └── PushLocalVaultChangesUseCase.kt
│       │   │   │           │   ├── ObserveVaultDirtyStateUseCase.kt
│       │   │   │           │   ├── ObserveVaultSyncingUseCase.kt
│       │   │   │           │   ├── SyncVaultNowUseCase.kt
│       │   │   │           │   ├── VaultSyncExecutionLock.kt
│       │   │   │           │   └── VaultSyncUseCase.kt
│       │   │   │           └── vault/
│       │   │   │               ├── VaultInitializeUseCase.kt
│       │   │   │               ├── VaultUnlocker.kt
│       │   │   │               └── VaultUnlockUseCase.kt
│       │   │   └── AndroidManifest.xml
│       │   └── test/java/com/miguelrodriguez19/safecube/core/vault/
│       │       ├── data/
│       │       │   ├── codec/
│       │       │   │   ├── JsonSecureItemContentCodecTest.kt
│       │       │   │   └── SecureItemContentJsonAdapterTest.kt
│       │       │   ├── crypto/
│       │       │   │   ├── SecureItemCryptoContextProviderTest.kt
│       │       │   │   ├── SecureItemPayloadAadFactoryTest.kt
│       │       │   │   ├── SecureItemPayloadEnvelopeV1CodecTest.kt
│       │       │   │   └── VaultItemCipherTest.kt
│       │       │   ├── local/
│       │       │   │   └── VaultKeyMaterialCacheTest.kt
│       │       │   ├── remote/
│       │       │   │   ├── RemoteSecureItemDataSourceIntegrationTest.kt
│       │       │   │   ├── RemoteSecureItemDataSourceTest.kt
│       │       │   │   ├── RemoteVaultKeyMaterialDataSourceIntegrationTest.kt
│       │       │   │   └── RemoteVaultKeyMaterialDataSourceTest.kt
│       │       │   └── session/
│       │       │       ├── LocalVaultDataCleanerImplTest.kt
│       │       │       ├── VaultInMemoryKekStoreTest.kt
│       │       │       └── VaultSessionManagerImplTest.kt
│       │       ├── domain/
│       │       │   ├── model/secureitem/
│       │       │   │   ├── itemcontent/
│       │       │   │   │   └── NoteSecureItemContentTest.kt
│       │       │   │   ├── SecureItemContentTest.kt
│       │       │   │   ├── SecureItemSyncDraftTest.kt
│       │       │   │   └── SecureItemTest.kt
│       │       │   └── usecase/
│       │       │       ├── draft/
│       │       │       │   ├── DiscardSecureItemDraftUseCaseTest.kt
│       │       │       │   ├── PrepareSecureItemDraftForSyncUseCaseTest.kt
│       │       │       │   └── SecureItemDraftSyncCoordinatorTest.kt
│       │       │       ├── note/
│       │       │       │   └── NoteDraftToContentMapperTest.kt
│       │       │       ├── password/
│       │       │       │   └── PasswordDraftToContentMapperTest.kt
│       │       │       ├── sync/pull/
│       │       │       │   └── PullVaultDeltaMappingsTest.kt
│       │       │       ├── ObserveSecureItemDetailUseCaseTest.kt
│       │       │       ├── ObserveSecureItemDraftDetailUseCaseTest.kt
│       │       │       ├── ObserveVaultDirtyStateUseCaseTest.kt
│       │       │       ├── ObserveVaultDraftSummariesUseCaseTest.kt
│       │       │       ├── PullVaultDeltaUseCaseTest.kt
│       │       │       ├── PushLocalVaultChangesUseCaseTest.kt
│       │       │       ├── SecureItemDraftMutationCoordinatorTest.kt
│       │       │       ├── VaultInitializeUseCaseTest.kt
│       │       │       ├── VaultSyncExecutionLockTest.kt
│       │       │       ├── VaultSyncUseCaseTest.kt
│       │       │       └── VaultUnlockUseCaseTest.kt
│       │       └── test/
│       │           └── DraftFirstTestFixtures.kt
│       └── build.gradle.kts
├── docs/
│   ├── architecture/
│   │   ├── adr/
│   │   │   ├── ADR-TEMPLATE.md
│   │   │   └── README.md
│   │   ├── historical/
│   │   │   ├── vault-sync-conflict-draft-resolution.md
│   │   │   └── vault-sync-v1.md
│   │   ├── crypto-v1.md
│   │   ├── openapi-auth-contract-integration.md
│   │   ├── openapi-vault-items-contract-integration.md
│   │   ├── openapi-vault-key-material-contract-integration.md
│   │   ├── secure-item-payload-v1.md
│   │   ├── storage_decision.md
│   │   └── vault-sync-versioning-v2.md
│   ├── package-structure/
│   │   └── package_structure.md
│   ├── release/
│   │   ├── conventional-commits.md
│   │   ├── release-policy.md
│   │   └── release-runbook.md
│   ├── roadmap/
│   │   ├── historical/
│   │   │   └── roadmap--fase-5-v1.md
│   │   ├── roadmap--fase-1.md
│   │   ├── roadmap--fase-2.md
│   │   ├── roadmap--fase-3.md
│   │   ├── roadmap--fase-4.md
│   │   ├── roadmap--fase-5.md
│   │   ├── roadmap--fase-6.md
│   │   └── roadmap--high-level.md
│   ├── sdd/
│   │   ├── agent-reports/
│   │   │   ├── EXTRA-CD-PIPELINE.md
│   │   │   ├── SCDK-M100.md
│   │   │   ├── SCDK-M101.md
│   │   │   ├── SCDK-M102--Build-and-verify-signed-APK-in-release-workflow.md
│   │   │   ├── SCDK-M103--Publish-APK-to-GitHub-Releases.md
│   │   │   ├── SCDK-M104--Create-release-runbook.md
│   │   │   ├── SCDK-M105--Automate-changelog.md
│   │   │   ├── SCDK-M106--Add-SBOM-and-provenance-attestation.md
│   │   │   ├── SCDK-M92.md
│   │   │   ├── SCDK-M93.md
│   │   │   ├── SCDK-M94.md
│   │   │   ├── SCDK-M95.md
│   │   │   ├── SCDK-M96.md
│   │   │   ├── SCDK-M97.md
│   │   │   ├── SCDK-M98.md
│   │   │   └── SCDK-M99.md
│   │   ├── agent-report-template.md
│   │   ├── agent-workflow.md
│   │   ├── definition-of-ready-done.md
│   │   ├── README.md
│   │   ├── spec-registry.md
│   │   ├── spec-template.md
│   │   ├── task-template.md
│   │   └── traceability-matrix.md
│   ├── security/
│   │   └── secret-scanning.md
│   ├── specs/product/
│   │   └── v1-product-brief.md
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
│   │   │   │   │   ├── welcome/ui/
│   │   │   │   │   │   └── WelcomeScreen.kt
│   │   │   │   │   └── AuthTestTags.kt
│   │   │   │   └── screens/
│   │   │   ├── res/values/
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   ├── build/reports/cyclonedx-direct/
│   │   └── bom.json
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
│       │   │   │   ├── settings/
│       │   │   │   │   ├── ui/
│       │   │   │   │   │   └── SettingsScreen.kt
│       │   │   │   │   └── viewmodel/
│       │   │   │   │       └── SettingsViewModel.kt
│       │   │   │   ├── shared/
│       │   │   │   │   ├── editor/
│       │   │   │   │   │   └── SecureItemEditorScaffold.kt
│       │   │   │   │   ├── error/
│       │   │   │   │   │   ├── SecureItemCrudErrorMessageMapper.kt
│       │   │   │   │   │   └── SecureItemDraftErrorMessageMapper.kt
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
│       │       │   ├── passwordeditor/viewmodel/
│       │       │   │   └── PasswordEditorViewModelTest.kt
│       │       │   └── settings/viewmodel/
│       │       │       └── SettingsViewModelTest.kt
│       │       └── test/
│       │           └── MainDispatcherRule.kt
│       └── build.gradle.kts
├── scripts/
│   ├── resources/com/safecube/tooling/
│   │   └── FolderTreeToFile.java
│   ├── create-immutable-release-tag.sh
│   ├── generate-changelog.sh
│   ├── run-folder-tree.sh
│   ├── verify-changelog-test.sh
│   ├── verify-create-immutable-release-tag.sh
│   ├── verify-gitleaks-fixture.sh
│   └── verify-release-sbom.sh
├── tests/fixtures/gitleaks/
│   └── allowed-synthetic-secret.txt
├── .gitignore
├── .gitleaks.toml
├── .gitleaksignore
├── AGENTS.md
├── build.gradle.kts
├── CHANGELOG.md
├── cliff.toml
├── gradlew
├── gradlew.bat
├── LICENSE
├── settings.gradle.kts
```
