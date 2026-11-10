# Package Structure
Updated: 10-11-2026 02:09:32

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
│   └── verifyKover.run.xml
├── app/
│   ├── src/
│   │   ├── androidTest/java/com/miguelrodriguez19/safecube/
│   │   │   ├── test/
│   │   │   ├── AppearanceThemeInstrumentedTest.kt
│   │   │   ├── AuthEntryRestorationTest.kt
│   │   │   ├── AuthEntrySemanticsTest.kt
│   │   │   ├── ComposeDesignSystemActionsTest.kt
│   │   │   ├── ComposeDesignSystemFeedbackTest.kt
│   │   │   ├── ComposeDesignSystemSecretTest.kt
│   │   │   ├── ComposeDesignSystemStructureTest.kt
│   │   │   ├── ComposeVisualRegressionCanarySemanticsTest.kt
│   │   │   ├── DesignSystemTestInputMode.kt
│   │   │   ├── DeviceCredentialIsolation.kt
│   │   │   ├── MainActivitySmokeTest.kt
│   │   │   ├── QuickUnlockDeviceCredentialTest.kt
│   │   │   ├── ResilienceInstrumentedTest.kt
│   │   │   ├── ResilienceUiStateTest.kt
│   │   │   ├── SafeCubeShellSemanticsTest.kt
│   │   │   └── ShellSystemBarsTest.kt
│   │   ├── debug/
│   │   │   ├── java/com/miguelrodriguez19/safecube/app/testsupport/
│   │   │   │   ├── visual/
│   │   │   │   │   ├── AuthVisualFixtures.kt
│   │   │   │   │   ├── DesignSystemCatalog.kt
│   │   │   │   │   ├── DesignSystemCatalogPreviews.kt
│   │   │   │   │   ├── SafeCubeMultiPreviews.kt
│   │   │   │   │   ├── ShellVisualFixtures.kt
│   │   │   │   │   ├── VisualRegressionCanaryFixtures.kt
│   │   │   │   │   ├── VisualRegressionCanaryPreviews.kt
│   │   │   │   │   ├── VisualRegressionCanarySurface.kt
│   │   │   │   │   └── VisualTokenCatalog.kt
│   │   │   │   ├── QuickUnlockColdStartProbeService.kt
│   │   │   │   └── QuickUnlockInstrumentationEntryPoint.kt
│   │   │   ├── res/
│   │   │   │   ├── values/
│   │   │   │   │   └── strings.xml
│   │   │   │   └── values-es/
│   │   │   │       └── strings.xml
│   │   │   └── AndroidManifest.xml
│   │   ├── main/
│   │   │   ├── java/com/miguelrodriguez19/safecube/app/
│   │   │   │   ├── appearance/
│   │   │   │   │   ├── AppAppearanceRepository.kt
│   │   │   │   │   ├── AppearanceBootstrapViewModel.kt
│   │   │   │   │   ├── AppearanceModule.kt
│   │   │   │   │   └── AppearanceSettingsCodec.kt
│   │   │   │   ├── entrypoint/
│   │   │   │   │   ├── MainActivity.kt
│   │   │   │   │   └── SafeCubeApp.kt
│   │   │   │   ├── presentation/
│   │   │   │   │   ├── navigation/
│   │   │   │   │   │   ├── gate/
│   │   │   │   │   │   │   ├── event/
│   │   │   │   │   │   │   │   └── PostLoginGateUiEvent.kt
│   │   │   │   │   │   │   ├── state/
│   │   │   │   │   │   │   │   └── PostLoginGateUiState.kt
│   │   │   │   │   │   │   ├── viewmodel/
│   │   │   │   │   │   │   │   └── PostLoginGateViewModel.kt
│   │   │   │   │   │   │   ├── NavigationGates.kt
│   │   │   │   │   │   │   └── NavigationGatesEntryPoint.kt
│   │   │   │   │   │   ├── host/
│   │   │   │   │   │   │   ├── ForcedLogoutNoticeDialog.kt
│   │   │   │   │   │   │   ├── NavigationBackPolicy.kt
│   │   │   │   │   │   │   ├── NavigationController.kt
│   │   │   │   │   │   │   ├── NavigationDependencies.kt
│   │   │   │   │   │   │   ├── NavigationGraph.kt
│   │   │   │   │   │   │   ├── NavigationSessionCoordinator.kt
│   │   │   │   │   │   │   ├── NavigationWrapper.kt
│   │   │   │   │   │   │   ├── SafeCubeNavigationHost.kt
│   │   │   │   │   │   │   ├── VaultExitBackPolicy.kt
│   │   │   │   │   │   │   └── VaultNavigationCoordinator.kt
│   │   │   │   │   │   └── route/
│   │   │   │   │   │       ├── RouteAccessPolicy.kt
│   │   │   │   │   │       └── Routes.kt
│   │   │   │   │   └── ui/
│   │   │   │   │       └── SplashGateScreen.kt
│   │   │   │   └── session/
│   │   │   │       ├── autolock/
│   │   │   │       │   ├── AutoLockClock.kt
│   │   │   │       │   ├── AutoLockModule.kt
│   │   │   │       │   ├── AutoLockScheduler.kt
│   │   │   │       │   ├── VaultAutoLockController.kt
│   │   │   │       │   └── VaultAutoLockCoordinator.kt
│   │   │   │       ├── AccountSessionLifecycleImpl.kt
│   │   │   │       ├── AccountSessionModule.kt
│   │   │   │       └── QuickUnlockAccountSessionValidatorImpl.kt
│   │   │   ├── res/
│   │   │   │   ├── drawable/
│   │   │   │   │   ├── ic_launcher_background.xml
│   │   │   │   │   ├── ic_launcher_foreground.xml
│   │   │   │   │   └── ic_launcher_monochrome.xml
│   │   │   │   ├── mipmap-anydpi-v26/
│   │   │   │   │   ├── ic_launcher.xml
│   │   │   │   │   └── ic_launcher_round.xml
│   │   │   │   ├── mipmap-anydpi-v33/
│   │   │   │   │   ├── ic_launcher.xml
│   │   │   │   │   └── ic_launcher_round.xml
│   │   │   │   ├── values/
│   │   │   │   │   ├── bools.xml
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── drawables.xml
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   └── styles.xml
│   │   │   │   ├── values-es/
│   │   │   │   │   └── strings.xml
│   │   │   │   ├── values-night/
│   │   │   │   │   ├── bools.xml
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   └── styles.xml
│   │   │   │   └── xml/
│   │   │   │       ├── backup_rules.xml
│   │   │   │       └── data_extraction_rules.xml
│   │   │   └── AndroidManifest.xml
│   │   ├── screenshotTest/java/com/miguelrodriguez19/safecube/visual/
│   │   │   ├── AuthScreenshotTest.kt
│   │   │   ├── DesignSystemCatalogScreenshotTest.kt
│   │   │   ├── ShellScreenshotTest.kt
│   │   │   └── VisualRegressionCanaryScreenshotTest.kt
│   │   ├── screenshotTestDebug/reference/com/miguelrodriguez19/safecube/
│   │   │   ├── visual/
│   │   │   │   ├── AuthScreenshotTestKt/
│   │   │   │   │   ├── authLoginIdleLightEnglishPreview_Login idle light English_79477c3f_0.png
│   │   │   │   │   ├── authLoginLoadingLightEnglishPreview_Login loading light English_bd3d2af4_0.png
│   │   │   │   │   ├── authLoginMediumLandscapeDarkSpanishPreview_Login medium landscape dark Spanish_4fe9100f_0.png
│   │   │   │   │   ├── authLoginRetryableDarkSpanishPreview_Login retryable dark Spanish_33b010da_0.png
│   │   │   │   │   ├── authLoginTerminalLightEnglishPreview_Login terminal light English_46e26788_0.png
│   │   │   │   │   ├── authSignupCompact200DarkSpanishPreview_Signup compact 200 percent dark Spanish_a33ede63_0.png
│   │   │   │   │   ├── authSignupValidationLightSpanishPreview_Signup validation light Spanish_dff0052e_0.png
│   │   │   │   │   ├── authWelcomeDarkSpanishPreview_Welcome dark Spanish_bd4badaa_0.png
│   │   │   │   │   ├── authWelcomeLightEnglishPreview_Welcome light English_31899fe6_0.png
│   │   │   │   │   └── authWelcomeLightSpanishCompact200Preview_Welcome light Spanish compact 200 percent_f7f19934_0.png
│   │   │   │   ├── DesignSystemCatalogScreenshotTestKt/
│   │   │   │   │   ├── designSystemCatalogActionsPreview_Dark English_7b10707e_0.png
│   │   │   │   │   ├── designSystemCatalogActionsPreview_Light English_73c2f133_0.png
│   │   │   │   │   ├── designSystemCatalogConfirmationFontScale200Preview_Confirmation compact 200 percent_cb45eb53_0.png
│   │   │   │   │   ├── designSystemCatalogConfirmationLoadingPreview_Confirmation loading_d76a9c51_0.png
│   │   │   │   │   ├── designSystemCatalogConfirmationPreview_Dark English_7b10707e_0.png
│   │   │   │   │   ├── designSystemCatalogConfirmationPreview_Light English_73c2f133_0.png
│   │   │   │   │   ├── designSystemCatalogFeedbackAvailabilityPreview_Dark English_7b10707e_0.png
│   │   │   │   │   ├── designSystemCatalogFeedbackAvailabilityPreview_Light English_73c2f133_0.png
│   │   │   │   │   ├── designSystemCatalogFeedbackLandscapePreview_Feedback medium landscape_5f6ff22d_0.png
│   │   │   │   │   ├── designSystemCatalogFeedbackRecoveryPreview_Dark English_7b10707e_0.png
│   │   │   │   │   ├── designSystemCatalogFeedbackRecoveryPreview_Light English_73c2f133_0.png
│   │   │   │   │   ├── designSystemCatalogFeedbackResolutionPreview_Dark English_7b10707e_0.png
│   │   │   │   │   ├── designSystemCatalogFeedbackResolutionPreview_Light English_73c2f133_0.png
│   │   │   │   │   ├── designSystemCatalogFeedbackStatusPreview_Dark English_7b10707e_0.png
│   │   │   │   │   ├── designSystemCatalogFeedbackStatusPreview_Light English_73c2f133_0.png
│   │   │   │   │   ├── designSystemCatalogFieldsFontScale200Preview_Fields compact 200 percent_edc7788c_0.png
│   │   │   │   │   ├── designSystemCatalogFieldsPreview_Dark English_7b10707e_0.png
│   │   │   │   │   ├── designSystemCatalogFieldsPreview_Light English_73c2f133_0.png
│   │   │   │   │   ├── designSystemCatalogFieldsSpanishPreview_Light Spanish_d259c1ae_0.png
│   │   │   │   │   ├── designSystemCatalogNavigationLandscapePreview_Navigation medium landscape_ed29a39a_0.png
│   │   │   │   │   ├── designSystemCatalogNavigationPreview_Dark English_7b10707e_0.png
│   │   │   │   │   └── designSystemCatalogNavigationPreview_Light English_73c2f133_0.png
│   │   │   │   ├── ShellScreenshotTestKt/
│   │   │   │   │   ├── shellSettingsLandscapeLightSpanishPreview_Shell settings landscape light Spanish_3eee61d9_0.png
│   │   │   │   │   ├── shellSettingsSelectedDarkSpanishPreview_Settings selected dark Spanish_fade5553_0.png
│   │   │   │   │   ├── shellSplashDarkEnglishPreview_Splash dark English_760548e9_0.png
│   │   │   │   │   ├── shellSplashLightEnglishPreview_Splash light English_9c668469_0.png
│   │   │   │   │   ├── shellVaultFontScale200DarkSpanishPreview_Shell vault 200 percent dark Spanish_56855144_0.png
│   │   │   │   │   └── shellVaultSelectedLightEnglishPreview_Vault selected light English_a1061180_0.png
│   │   │   │   ├── VisualRegressionCanaryScreenshotTestKt/
│   │   │   │   │   ├── visualRegressionCanaryConfigurationPreview_Compact portrait_47aeb6cc_0.png
│   │   │   │   │   ├── visualTokenCatalogDarkPreview_Dark tokens_293a429e_0.png
│   │   │   │   │   └── visualTokenCatalogLightPreview_Light tokens_ff2cccbf_0.png
│   │   └── test/java/com/miguelrodriguez19/safecube/
│   │       ├── app/
│   │       │   ├── appearance/
│   │       │   │   ├── AppAppearanceRepositoryTest.kt
│   │       │   │   ├── AppearanceBootstrapViewModelTest.kt
│   │       │   │   └── AppearanceSettingsCodecTest.kt
│   │       │   ├── presentation/navigation/
│   │       │   │   ├── gate/
│   │       │   │   │   ├── viewmodel/
│   │       │   │   │   │   └── PostLoginGateViewModelTest.kt
│   │       │   │   │   └── NavigationGatesTest.kt
│   │       │   │   └── host/
│   │       │   │       ├── NavigationBackPolicyTest.kt
│   │       │   │       ├── NavigationControllerTest.kt
│   │       │   │       ├── NavigationMotionPolicyTest.kt
│   │       │   │       ├── NavigationSessionCoordinatorTest.kt
│   │       │   │       └── VaultExitBackPolicyTest.kt
│   │       │   ├── security/
│   │       │   │   └── SensitiveSourcePolicyTest.kt
│   │       │   ├── session/
│   │       │   │   ├── autolock/
│   │       │   │   │   └── VaultAutoLockCoordinatorTest.kt
│   │       │   │   ├── AccountSessionLifecycleImplTest.kt
│   │       │   │   └── QuickUnlockAccountSessionValidatorImplTest.kt
│   │       │   └── test/
│   │       │       └── MainDispatcherRule.kt
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
│   │   │   │   │   ├── GenerateLogoVectorDrawablesTask.class
│   │   │   │   │   ├── LogoAssetContract$SvgAsset.class
│   │   │   │   │   ├── LogoAssetContract$Transform.class
│   │   │   │   │   ├── LogoAssetContract.class
│   │   │   │   │   ├── ReleaseSigningConfig.class
│   │   │   │   │   ├── ReleaseSigningCredentials.class
│   │   │   │   │   └── VerifyLogoGeometryTask.class
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
│   │   │   │   │   │   │   ├── source-to-classes.tab_i.len
│   │   │   │   │   │   │   ├── subtypes.tab
│   │   │   │   │   │   │   ├── subtypes.tab.keystream
│   │   │   │   │   │   │   ├── subtypes.tab.keystream.len
│   │   │   │   │   │   │   ├── subtypes.tab.len
│   │   │   │   │   │   │   ├── subtypes.tab.values.at
│   │   │   │   │   │   │   ├── subtypes.tab_i
│   │   │   │   │   │   │   ├── subtypes.tab_i.len
│   │   │   │   │   │   │   ├── supertypes.tab
│   │   │   │   │   │   │   ├── supertypes.tab.keystream
│   │   │   │   │   │   │   ├── supertypes.tab.keystream.len
│   │   │   │   │   │   │   ├── supertypes.tab.len
│   │   │   │   │   │   │   ├── supertypes.tab.values.at
│   │   │   │   │   │   │   ├── supertypes.tab_i
│   │   │   │   │   │   │   └── supertypes.tab_i.len
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
│   │   │   ├── LogoVectorGenerator.kt
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
│   │   │   │   │       │   ├── SessionState.kt
│   │   │   │   │       │   └── SessionTerminationReason.kt
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
│   │   │   │   │   │   │   ├── NetworkConfig.kt
│   │   │   │   │   │   │   └── NetworkFailure.kt
│   │   │   │   │   │   └── port/
│   │   │   │   │   │       ├── TokenProvider.kt
│   │   │   │   │   │       └── TokenRefreshHandler.kt
│   │   │   │   │   └── serialization/
│   │   │   │   │       ├── Base64ByteArraySerializer.kt
│   │   │   │   │       └── InstantIso8601Serializer.kt
│   │   │   │   └── AndroidManifest.xml
│   │   │   └── test/java/com/miguelrodriguez19/safecube/core/network/
│   │   │       ├── contract/
│   │   │       │   ├── VaultKeyMaterialOpenApiContractTest.kt
│   │   │       │   └── VaultSyncOpenApiContractTest.kt
│   │   │       ├── data/
│   │   │       │   ├── auth/
│   │   │       │   │   ├── AuthInterceptorTest.kt
│   │   │       │   │   ├── TokenRefreshAuthenticatorFlowIntegrationTest.kt
│   │   │       │   │   └── TokenRefreshAuthenticatorTest.kt
│   │   │       │   └── client/
│   │   │       │       └── NetworkClientFactoryIntegrationTest.kt
│   │   │       └── domain/model/
│   │   │           ├── NetworkConfigTest.kt
│   │   │           └── NetworkFailureClassifierTest.kt
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
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/miguelrodriguez19/safecube/core/ui/
│   │   │   │   │   ├── component/
│   │   │   │   │   │   ├── SafeCubeButton.kt
│   │   │   │   │   │   ├── SafeCubeFeedback.kt
│   │   │   │   │   │   ├── SafeCubeScaffold.kt
│   │   │   │   │   │   ├── SafeCubeStructure.kt
│   │   │   │   │   │   ├── SafeCubeTextField.kt
│   │   │   │   │   │   └── SecretOutlinedTextField.kt
│   │   │   │   │   ├── components/
│   │   │   │   │   │   └── .gitkeep
│   │   │   │   │   └── theme/
│   │   │   │   │       ├── Appearance.kt
│   │   │   │   │       ├── Color.kt
│   │   │   │   │       ├── SafeCubeIcons.kt
│   │   │   │   │       ├── SafeCubeTheme.kt
│   │   │   │   │       ├── Tokens.kt
│   │   │   │   │       └── Typography.kt
│   │   │   │   ├── res/
│   │   │   │   │   ├── drawable/
│   │   │   │   │   │   └── ic_safecube_mark.xml
│   │   │   │   │   ├── font/
│   │   │   │   │   │   ├── manrope_bold.ttf
│   │   │   │   │   │   ├── manrope_medium.ttf
│   │   │   │   │   │   ├── manrope_regular.ttf
│   │   │   │   │   │   └── manrope_semibold.ttf
│   │   │   │   │   ├── raw/
│   │   │   │   │   │   └── manrope_ofl.txt
│   │   │   │   │   ├── values/
│   │   │   │   │   │   ├── colors.xml
│   │   │   │   │   │   └── strings.xml
│   │   │   │   │   ├── values-es/
│   │   │   │   │   │   └── strings.xml
│   │   │   │   │   └── values-night/
│   │   │   │   │       └── colors.xml
│   │   │   │   └── AndroidManifest.xml
│   │   │   └── test/java/com/miguelrodriguez19/safecube/core/ui/
│   │   │       ├── component/
│   │   │       │   └── DesignSystemContractTest.kt
│   │   │       └── theme/
│   │   │           ├── SafeCubeThemeTest.kt
│   │   │           └── SafeCubeTokensTest.kt
│   │   └── build.gradle.kts
│   ├── vault/
│   │   ├── data/quickunlock/
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
│   │   │   │   │   │   │   ├── SecureItemPayloadEnvelopeIdentityReader.kt
│   │   │   │   │   │   │   ├── SecureItemPayloadEnvelopeV1Codec.kt
│   │   │   │   │   │   │   └── VaultItemCipher.kt
│   │   │   │   │   │   ├── local/
│   │   │   │   │   │   │   ├── AutoLockPreferences.kt
│   │   │   │   │   │   │   ├── AutoLockTimeoutRepositoryImpl.kt
│   │   │   │   │   │   │   ├── EncryptedVaultInitializationPrefs.kt
│   │   │   │   │   │   │   ├── EncryptedVaultKeyMaterialPrefs.kt
│   │   │   │   │   │   │   ├── PendingVaultInitializationCodec.kt
│   │   │   │   │   │   │   ├── PendingVaultInitializationStore.kt
│   │   │   │   │   │   │   └── VaultKeyMaterialCache.kt
│   │   │   │   │   │   ├── quickunlock/
│   │   │   │   │   │   │   ├── AndroidKeystoreQuickUnlockAdapter.kt
│   │   │   │   │   │   │   ├── AndroidQuickUnlockKeyStorePlatformImpl.kt
│   │   │   │   │   │   │   ├── QuickUnlockAliasFactory.kt
│   │   │   │   │   │   │   ├── QuickUnlockAndroidKeyStorePlatform.kt
│   │   │   │   │   │   │   ├── QuickUnlockEnvelopeCodec.kt
│   │   │   │   │   │   │   ├── QuickUnlockKeyStore.kt
│   │   │   │   │   │   │   ├── QuickUnlockManagerImpl.kt
│   │   │   │   │   │   │   ├── QuickUnlockPreferences.kt
│   │   │   │   │   │   │   └── QuickUnlockStore.kt
│   │   │   │   │   │   ├── remote/
│   │   │   │   │   │   │   ├── RemoteSecureItemDataSource.kt
│   │   │   │   │   │   │   └── RemoteVaultKeyMaterialDataSource.kt
│   │   │   │   │   │   └── session/
│   │   │   │   │   │       ├── LocalVaultDataCleanerImpl.kt
│   │   │   │   │   │       ├── PendingQuickUnlockEnrollmentStore.kt
│   │   │   │   │   │       ├── QuickUnlockKeyMaterialAccess.kt
│   │   │   │   │   │       ├── VaultInMemoryKekStore.kt
│   │   │   │   │   │       └── VaultSessionManagerImpl.kt
│   │   │   │   │   ├── di/
│   │   │   │   │   │   └── VaultModule.kt
│   │   │   │   │   ├── domain/
│   │   │   │   │   │   ├── codec/
│   │   │   │   │   │   │   ├── SecureItemContentCodec.kt
│   │   │   │   │   │   │   ├── SecureItemContentDecodeError.kt
│   │   │   │   │   │   │   └── SecureItemContentDecodeResult.kt
│   │   │   │   │   │   ├── config/
│   │   │   │   │   │   │   └── VaultCryptoDefaults.kt
│   │   │   │   │   │   ├── model/
│   │   │   │   │   │   │   ├── initialize/
│   │   │   │   │   │   │   │   ├── PendingVaultInitialization.kt
│   │   │   │   │   │   │   │   ├── PendingVaultInitializationStatus.kt
│   │   │   │   │   │   │   │   ├── PendingVaultRecoveryKeyResult.kt
│   │   │   │   │   │   │   │   ├── VaultInitializeError.kt
│   │   │   │   │   │   │   │   └── VaultInitializeResult.kt
│   │   │   │   │   │   │   ├── passphrase/
│   │   │   │   │   │   │   │   ├── ChangeVaultPassphraseError.kt
│   │   │   │   │   │   │   │   └── ChangeVaultPassphraseResult.kt
│   │   │   │   │   │   │   ├── quickunlock/
│   │   │   │   │   │   │   │   └── VaultUnlockProvenance.kt
│   │   │   │   │   │   │   ├── remote/
│   │   │   │   │   │   │   │   ├── request/
│   │   │   │   │   │   │   │   │   ├── RemoteCreateSecureItemRequest.kt
│   │   │   │   │   │   │   │   │   ├── RemoteDeleteSecureItemRequest.kt
│   │   │   │   │   │   │   │   │   └── RemoteUpdateSecureItemRequest.kt
│   │   │   │   │   │   │   │   ├── result/
│   │   │   │   │   │   │   │   │   ├── RemoteCreateSecureItemResult.kt
│   │   │   │   │   │   │   │   │   ├── RemoteDeleteSecureItemResult.kt
│   │   │   │   │   │   │   │   │   ├── RemoteUpdateSecureItemResult.kt
│   │   │   │   │   │   │   │   │   ├── SecureItemRemoteResult.kt
│   │   │   │   │   │   │   │   │   └── VaultKeyMaterialRemoteResult.kt
│   │   │   │   │   │   │   │   ├── MasterWrapperUpdateConfirmation.kt
│   │   │   │   │   │   │   │   ├── RemoteListVaultItemsRequestParams.kt
│   │   │   │   │   │   │   │   ├── RemoteSecureItem.kt
│   │   │   │   │   │   │   │   ├── RemoteSecureItemChangesPage.kt
│   │   │   │   │   │   │   │   ├── RemoteSecureItemSummary.kt
│   │   │   │   │   │   │   │   └── VersionedVaultKeyMaterial.kt
│   │   │   │   │   │   │   ├── secureitem/
│   │   │   │   │   │   │   │   ├── crud/
│   │   │   │   │   │   │   │   │   ├── ObserveSecureItemDetailResult.kt
│   │   │   │   │   │   │   │   │   ├── ObserveSecureItemDraftDetailResult.kt
│   │   │   │   │   │   │   │   │   ├── SecureItemCrudError.kt
│   │   │   │   │   │   │   │   │   ├── SecureItemDetail.kt
│   │   │   │   │   │   │   │   │   ├── SecureItemDraftDetail.kt
│   │   │   │   │   │   │   │   │   ├── SecureItemMutationResult.kt
│   │   │   │   │   │   │   │   │   ├── SecureNoteDraft.kt
│   │   │   │   │   │   │   │   │   ├── SecurePasswordDraft.kt
│   │   │   │   │   │   │   │   │   ├── VaultItemDraftSummary.kt
│   │   │   │   │   │   │   │   │   └── VaultItemSummary.kt
│   │   │   │   │   │   │   │   ├── itemcontent/
│   │   │   │   │   │   │   │   │   ├── NoteSecureItemContent.kt
│   │   │   │   │   │   │   │   │   ├── PasswordSecureItemContent.kt
│   │   │   │   │   │   │   │   │   └── SecureItemContent.kt
│   │   │   │   │   │   │   │   ├── EncodedSecureItemContent.kt
│   │   │   │   │   │   │   │   ├── SecureItem.kt
│   │   │   │   │   │   │   │   ├── SecureItemDraftSyncStatus.kt
│   │   │   │   │   │   │   │   ├── SecureItemDraftType.kt
│   │   │   │   │   │   │   │   ├── SecureItemSyncDraft.kt
│   │   │   │   │   │   │   │   ├── SecureItemSyncState.kt
│   │   │   │   │   │   │   │   └── SecureItemType.kt
│   │   │   │   │   │   │   ├── sync/
│   │   │   │   │   │   │   │   ├── draft/
│   │   │   │   │   │   │   │   │   ├── DiscardSecureItemDraftResult.kt
│   │   │   │   │   │   │   │   │   └── PrepareSecureItemDraftForSyncResult.kt
│   │   │   │   │   │   │   │   ├── pull/
│   │   │   │   │   │   │   │   │   ├── PullVaultDeltaApplyResults.kt
│   │   │   │   │   │   │   │   │   ├── PullVaultDeltaError.kt
│   │   │   │   │   │   │   │   │   └── PullVaultDeltaResult.kt
│   │   │   │   │   │   │   │   ├── push/
│   │   │   │   │   │   │   │   │   ├── PushLocalVaultChangesError.kt
│   │   │   │   │   │   │   │   │   ├── PushLocalVaultChangesInternal.kt
│   │   │   │   │   │   │   │   │   └── PushLocalVaultChangesResult.kt
│   │   │   │   │   │   │   │   ├── VaultSyncError.kt
│   │   │   │   │   │   │   │   └── VaultSyncResult.kt
│   │   │   │   │   │   │   ├── unlock/
│   │   │   │   │   │   │   │   ├── VaultUnlockError.kt
│   │   │   │   │   │   │   │   └── VaultUnlockResult.kt
│   │   │   │   │   │   │   ├── AutoLockTimeout.kt
│   │   │   │   │   │   │   ├── UnlockedKeyring.kt
│   │   │   │   │   │   │   ├── VaultKeyMaterial.kt
│   │   │   │   │   │   │   └── VaultState.kt
│   │   │   │   │   │   ├── quickunlock/
│   │   │   │   │   │   │   ├── QuickUnlockAccountSessionValidator.kt
│   │   │   │   │   │   │   └── QuickUnlockManager.kt
│   │   │   │   │   │   ├── repository/
│   │   │   │   │   │   │   ├── AutoLockTimeoutRepository.kt
│   │   │   │   │   │   │   ├── PendingVaultInitializationRepository.kt
│   │   │   │   │   │   │   ├── SecureItemDraftRepository.kt
│   │   │   │   │   │   │   ├── SecureItemRemoteRepository.kt
│   │   │   │   │   │   │   ├── SecureItemRepository.kt
│   │   │   │   │   │   │   ├── VaultKeyMaterialLocalReadResult.kt
│   │   │   │   │   │   │   ├── VaultKeyMaterialLocalRepository.kt
│   │   │   │   │   │   │   └── VaultKeyMaterialRemoteRepository.kt
│   │   │   │   │   │   ├── service/
│   │   │   │   │   │   │   ├── EncryptedSecureItemPayload.kt
│   │   │   │   │   │   │   ├── SecureItemCryptoError.kt
│   │   │   │   │   │   │   ├── SecureItemCryptoService.kt
│   │   │   │   │   │   │   ├── SecureItemDecryptionResult.kt
│   │   │   │   │   │   │   ├── SecureItemEncryptionResult.kt
│   │   │   │   │   │   │   └── SecureItemPayloadIdentityReader.kt
│   │   │   │   │   │   ├── session/
│   │   │   │   │   │   │   ├── LocalVaultDataCleaner.kt
│   │   │   │   │   │   │   ├── VaultKekProvider.kt
│   │   │   │   │   │   │   ├── VaultLockReason.kt
│   │   │   │   │   │   │   └── VaultSessionManager.kt
│   │   │   │   │   │   └── usecase/
│   │   │   │   │   │       ├── secureitem/
│   │   │   │   │   │       │   ├── note/
│   │   │   │   │   │       │   │   ├── CreateSecureNoteUseCase.kt
│   │   │   │   │   │       │   │   ├── NoteDraftToContentMapper.kt
│   │   │   │   │   │       │   │   └── UpdateSecureNoteUseCase.kt
│   │   │   │   │   │       │   ├── password/
│   │   │   │   │   │       │   │   ├── CreateSecurePasswordUseCase.kt
│   │   │   │   │   │       │   │   ├── PasswordDraftToContentMapper.kt
│   │   │   │   │   │       │   │   └── UpdateSecurePasswordUseCase.kt
│   │   │   │   │   │       │   ├── CurrentInstantProvider.kt
│   │   │   │   │   │       │   ├── ObserveSecureItemDetailUseCase.kt
│   │   │   │   │   │       │   ├── ObserveSecureItemDraftDetailUseCase.kt
│   │   │   │   │   │       │   ├── ObserveVaultDraftSummariesUseCase.kt
│   │   │   │   │   │       │   ├── ObserveVaultItemSummariesUseCase.kt
│   │   │   │   │   │       │   ├── SecureItemDraftMutationCoordinator.kt
│   │   │   │   │   │       │   ├── SecureItemIdGenerator.kt
│   │   │   │   │   │       │   ├── SecureItemMutationIdGenerator.kt
│   │   │   │   │   │       │   └── SoftDeleteSecureItemUseCase.kt
│   │   │   │   │   │       ├── sync/
│   │   │   │   │   │       │   ├── draft/
│   │   │   │   │   │       │   │   ├── DiscardSecureItemDraftUseCase.kt
│   │   │   │   │   │       │   │   ├── PrepareSecureItemDraftForSyncUseCase.kt
│   │   │   │   │   │       │   │   ├── SecureItemDraftPolicyMappings.kt
│   │   │   │   │   │       │   │   └── SecureItemDraftSyncCoordinator.kt
│   │   │   │   │   │       │   ├── pull/
│   │   │   │   │   │       │   │   ├── PullVaultDeltaMappings.kt
│   │   │   │   │   │       │   │   └── PullVaultDeltaUseCase.kt
│   │   │   │   │   │       │   ├── push/
│   │   │   │   │   │       │   │   ├── PushLocalVaultChangesMappings.kt
│   │   │   │   │   │       │   │   └── PushLocalVaultChangesUseCase.kt
│   │   │   │   │   │       │   ├── ObserveVaultDirtyStateUseCase.kt
│   │   │   │   │   │       │   ├── ObserveVaultSyncingUseCase.kt
│   │   │   │   │   │       │   ├── SyncVaultNowUseCase.kt
│   │   │   │   │   │       │   ├── VaultSyncExecutionLock.kt
│   │   │   │   │   │       │   └── VaultSyncUseCase.kt
│   │   │   │   │   │       └── vault/
│   │   │   │   │   │           ├── ChangeVaultPassphraseUseCase.kt
│   │   │   │   │   │           ├── VaultInitializeUseCase.kt
│   │   │   │   │   │           ├── VaultUnlocker.kt
│   │   │   │   │   │           └── VaultUnlockUseCase.kt
│   │   │   │   │   └── src/main/java/com/miguelrodriguez19/safecube/core/vault/domain/usecase/vault/
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
│   │   │       │   │   ├── AutoLockTimeoutRepositoryImplTest.kt
│   │   │       │   │   ├── PendingVaultInitializationCodecTest.kt
│   │   │       │   │   ├── PendingVaultInitializationStoreTest.kt
│   │   │       │   │   └── VaultKeyMaterialCacheTest.kt
│   │   │       │   ├── quickunlock/
│   │   │       │   │   ├── AndroidKeystoreQuickUnlockAdapterTest.kt
│   │   │       │   │   ├── QuickUnlockAliasFactoryTest.kt
│   │   │       │   │   ├── QuickUnlockEnvelopeCodecTest.kt
│   │   │       │   │   ├── QuickUnlockManagerImplTest.kt
│   │   │       │   │   └── QuickUnlockStoreTest.kt
│   │   │       │   ├── remote/
│   │   │       │   │   ├── RemoteSecureItemDataSourceIntegrationTest.kt
│   │   │       │   │   ├── RemoteSecureItemDataSourceTest.kt
│   │   │       │   │   ├── RemoteVaultKeyMaterialDataSourceIntegrationTest.kt
│   │   │       │   │   └── RemoteVaultKeyMaterialDataSourceTest.kt
│   │   │       │   └── session/
│   │   │       │       ├── LocalVaultDataCleanerImplTest.kt
│   │   │       │       ├── PendingQuickUnlockEnrollmentStoreTest.kt
│   │   │       │       ├── VaultInMemoryKekStoreTest.kt
│   │   │       │       └── VaultSessionManagerImplTest.kt
│   │   │       ├── domain/
│   │   │       │   ├── model/secureitem/
│   │   │       │   │   ├── itemcontent/
│   │   │       │   │   │   └── NoteSecureItemContentTest.kt
│   │   │       │   │   ├── SecureItemContentTest.kt
│   │   │       │   │   ├── SecureItemSyncDraftTest.kt
│   │   │       │   │   └── SecureItemTest.kt
│   │   │       │   ├── session/
│   │   │       │   └── usecase/
│   │   │       │       ├── draft/
│   │   │       │       │   ├── DiscardSecureItemDraftUseCaseTest.kt
│   │   │       │       │   ├── PrepareSecureItemDraftForSyncUseCaseTest.kt
│   │   │       │       │   └── SecureItemDraftSyncCoordinatorTest.kt
│   │   │       │       ├── note/
│   │   │       │       │   └── NoteDraftToContentMapperTest.kt
│   │   │       │       ├── password/
│   │   │       │       │   └── PasswordDraftToContentMapperTest.kt
│   │   │       │       ├── sync/pull/
│   │   │       │       │   └── PullVaultDeltaMappingsTest.kt
│   │   │       │       ├── vault/
│   │   │       │       │   └── ChangeVaultPassphraseUseCaseTest.kt
│   │   │       │       ├── ObserveSecureItemDetailUseCaseTest.kt
│   │   │       │       ├── ObserveSecureItemDraftDetailUseCaseTest.kt
│   │   │       │       ├── ObserveVaultDirtyStateUseCaseTest.kt
│   │   │       │       ├── ObserveVaultDraftSummariesUseCaseTest.kt
│   │   │       │       ├── PullVaultDeltaUseCaseTest.kt
│   │   │       │       ├── PushLocalVaultChangesUseCaseTest.kt
│   │   │       │       ├── SecureItemDraftMutationCoordinatorTest.kt
│   │   │       │       ├── VaultInitializeRecoveryUseCaseTest.kt
│   │   │       │       ├── VaultInitializeUseCaseTest.kt
│   │   │       │       ├── VaultSyncExecutionLockTest.kt
│   │   │       │       ├── VaultSyncUseCaseTest.kt
│   │   │       │       └── VaultUnlockUseCaseTest.kt
│   │   │       └── test/
│   │   │           └── DraftFirstTestFixtures.kt
│   │   └── build.gradle.kts
├── docs/
│   ├── architecture/
│   │   ├── adr/
│   │   │   ├── ADR-0001-VAULT-AUTO-LOCK.md
│   │   │   ├── ADR-0002-PASSPHRASE-REWRAP.md
│   │   │   ├── ADR-0003-SENSITIVE-DATA-SURFACES.md
│   │   │   ├── ADR-0004-DESIGN-SYSTEM-APPEARANCE.md
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
│   ├── design/
│   │   ├── assets/
│   │   │   ├── logo/
│   │   │   │   ├── adaptive-background.svg
│   │   │   │   ├── adaptive-foreground.svg
│   │   │   │   ├── in-app-mark.svg
│   │   │   │   ├── inverted.svg
│   │   │   │   ├── master.svg
│   │   │   │   ├── monochrome-themed.svg
│   │   │   │   ├── monochrome.svg
│   │   │   │   ├── primary.svg
│   │   │   │   ├── README.md
│   │   │   │   ├── round-preview.svg
│   │   │   │   ├── splash-dark.svg
│   │   │   │   └── splash-light.svg
│   │   │   └── type/manrope/
│   │   │       ├── Manrope-Bold.ttf
│   │   │       ├── Manrope-Medium.ttf
│   │   │       ├── Manrope-Regular.ttf
│   │   │       ├── Manrope-SemiBold.ttf
│   │   │       ├── OFL.txt
│   │   │       ├── README.md
│   │   │       └── README.upstream.txt
│   │   ├── mockups/
│   │   │   ├── generate_mockups.py
│   │   │   ├── index.html
│   │   │   ├── login-dark.svg
│   │   │   ├── login-light.svg
│   │   │   ├── README.md
│   │   │   ├── unlock-dark.svg
│   │   │   ├── unlock-light.svg
│   │   │   ├── vault-dark.svg
│   │   │   ├── vault-light.svg
│   │   │   ├── welcome-dark.svg
│   │   │   └── welcome-light.svg
│   │   ├── tools/
│   │   │   └── verify-brand-palette.py
│   │   ├── auth-entry-v1.md
│   │   ├── brand-guide-v1.md
│   │   ├── component-catalog-v1.md
│   │   └── shell-navigation-motion-v1.md
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
│   │   ├── roadmap--fase-7.md
│   │   ├── roadmap--fase-8.md
│   │   └── roadmap--high-level.md
│   ├── sdd/
│   │   ├── agent-report-template.md
│   │   ├── agent-workflow.md
│   │   ├── definition-of-ready-done.md
│   │   ├── local-storage-inventory.md
│   │   ├── README.md
│   │   ├── spec-registry.md
│   │   ├── spec-template.md
│   │   ├── task-template.md
│   │   └── traceability-matrix.md
│   ├── security/
│   │   └── secret-scanning.md
│   ├── specs/
│   │   ├── features/
│   │   │   ├── hardening-resilience-v1.md
│   │   │   └── ui-brand-motion-v1.md
│   │   ├── product/
│   │   │   └── v1-product-brief.md
│   ├── testing/
│   │   ├── phase-7-resilience-matrix.md
│   │   ├── testing.md
│   │   ├── TESTING_STANDARD.md
│   │   └── visual-regression-testing.md
│   └── README.md
├── feature/
│   ├── auth/
│   │   ├── src/
│   │   │   ├── debug/java/com/miguelrodriguez19/safecube/feature/auth/testsupport/
│   │   │   │   ├── AuthLoginContent.kt
│   │   │   │   └── AuthSignupContent.kt
│   │   │   ├── main/
│   │   │   │   ├── java/com/miguelrodriguez19/safecube/feature/auth/
│   │   │   │   │   ├── presentation/
│   │   │   │   │   │   ├── component/
│   │   │   │   │   │   │   ├── AuthActionLabel.kt
│   │   │   │   │   │   │   ├── AuthFeedback.kt
│   │   │   │   │   │   │   └── AuthScreenLayout.kt
│   │   │   │   │   │   ├── gate/ui/
│   │   │   │   │   │   │   └── PostLoginGateScreen.kt
│   │   │   │   │   │   ├── login/
│   │   │   │   │   │   │   ├── action/
│   │   │   │   │   │   │   │   └── LoginUiAction.kt
│   │   │   │   │   │   │   ├── event/
│   │   │   │   │   │   │   │   └── LoginUiEvent.kt
│   │   │   │   │   │   │   ├── state/
│   │   │   │   │   │   │   │   └── LoginUiState.kt
│   │   │   │   │   │   │   ├── ui/
│   │   │   │   │   │   │   │   └── LoginScreen.kt
│   │   │   │   │   │   │   └── viewmodel/
│   │   │   │   │   │   │       └── LoginViewModel.kt
│   │   │   │   │   │   ├── mapper/
│   │   │   │   │   │   │   └── AuthUiErrorMapper.kt
│   │   │   │   │   │   ├── signup/
│   │   │   │   │   │   │   ├── action/
│   │   │   │   │   │   │   │   └── SignupUiAction.kt
│   │   │   │   │   │   │   ├── event/
│   │   │   │   │   │   │   │   └── SignupUiEvent.kt
│   │   │   │   │   │   │   ├── state/
│   │   │   │   │   │   │   │   └── SignupUiState.kt
│   │   │   │   │   │   │   ├── ui/
│   │   │   │   │   │   │   │   └── SignupScreen.kt
│   │   │   │   │   │   │   └── viewmodel/
│   │   │   │   │   │   │       └── SignupViewModel.kt
│   │   │   │   │   │   ├── state/
│   │   │   │   │   │   │   └── AuthUiOperationState.kt
│   │   │   │   │   │   ├── welcome/ui/
│   │   │   │   │   │   │   └── WelcomeScreen.kt
│   │   │   │   │   │   └── AuthTestTags.kt
│   │   │   │   │   └── screens/
│   │   │   │   ├── res/
│   │   │   │   │   ├── values/
│   │   │   │   │   │   └── strings.xml
│   │   │   │   │   └── values-es/
│   │   │   │   │       └── strings.xml
│   │   │   │   └── AndroidManifest.xml
│   │   │   └── test/java/com/miguelrodriguez19/safecube/feature/auth/
│   │   │       ├── presentation/
│   │   │       │   ├── login/viewmodel/
│   │   │       │   │   └── LoginViewModelTest.kt
│   │   │       │   └── signup/viewmodel/
│   │   │       │       └── SignupViewModelTest.kt
│   │   │       └── test/
│   │   │           └── MainDispatcherRule.kt
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
│       │   ├── debug/java/com/miguelrodriguez19/safecube/feature/vault/testsupport/
│       │   │   └── ResilienceUiFixtures.kt
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
│       │   │   │   ├── editor/
│       │   │   │   │   ├── note/
│       │   │   │   │   │   ├── action/
│       │   │   │   │   │   │   └── NoteEditorUiAction.kt
│       │   │   │   │   │   ├── adapter/
│       │   │   │   │   │   │   └── NoteEditorMutationGateway.kt
│       │   │   │   │   │   ├── event/
│       │   │   │   │   │   │   └── NoteEditorUiEvent.kt
│       │   │   │   │   │   ├── state/
│       │   │   │   │   │   │   └── NoteEditorUiState.kt
│       │   │   │   │   │   ├── ui/
│       │   │   │   │   │   │   └── NoteEditorScreen.kt
│       │   │   │   │   │   └── viewmodel/
│       │   │   │   │   │       └── NoteEditorViewModel.kt
│       │   │   │   │   ├── password/
│       │   │   │   │   │   ├── action/
│       │   │   │   │   │   │   └── PasswordEditorUiAction.kt
│       │   │   │   │   │   ├── adapter/
│       │   │   │   │   │   │   └── PasswordEditorMutationGateway.kt
│       │   │   │   │   │   ├── event/
│       │   │   │   │   │   │   └── PasswordEditorUiEvent.kt
│       │   │   │   │   │   ├── state/
│       │   │   │   │   │   │   └── PasswordEditorUiState.kt
│       │   │   │   │   │   ├── ui/
│       │   │   │   │   │   │   └── PasswordEditorScreen.kt
│       │   │   │   │   │   └── viewmodel/
│       │   │   │   │   │       └── PasswordEditorViewModel.kt
│       │   │   │   │   └── shared/
│       │   │   │   │       ├── lifecycle/
│       │   │   │   │       │   └── SecureItemEditorLifecycleCoordinator.kt
│       │   │   │   │       ├── mutation/
│       │   │   │   │       │   ├── contract/
│       │   │   │   │       │   │   ├── SecureItemEditorMutationGateway.kt
│       │   │   │   │       │   │   └── SecureItemEditorMutationOperations.kt
│       │   │   │   │       │   ├── di/
│       │   │   │   │       │   │   ├── SecureItemEditorMutationGatewayModule.kt
│       │   │   │   │       │   │   ├── SecureItemEditorMutationModule.kt
│       │   │   │   │       │   │   └── SecureItemTypeKey.kt
│       │   │   │   │       │   ├── factory/
│       │   │   │   │       │   │   └── SecureItemEditorMutationGatewayFactory.kt
│       │   │   │   │       │   ├── model/
│       │   │   │   │       │   │   └── SecureItemEditorMutationRequest.kt
│       │   │   │   │       │   └── SecureItemEditorMutationCoordinator.kt
│       │   │   │   │       ├── observation/
│       │   │   │   │       │   ├── model/
│       │   │   │   │       │   │   ├── ObservationEvent.kt
│       │   │   │   │       │   │   ├── ObservationSnapshot.kt
│       │   │   │   │       │   │   └── SecureItemEditorObservationResult.kt
│       │   │   │   │       │   └── SecureItemEditorObservationCoordinator.kt
│       │   │   │   │       ├── state/
│       │   │   │   │       │   └── SecureItemEditorState.kt
│       │   │   │   │       └── ui/
│       │   │   │   │           └── SecureItemEditorScaffold.kt
│       │   │   │   ├── home/
│       │   │   │   │   ├── state/
│       │   │   │   │   │   └── VaultHomeUiState.kt
│       │   │   │   │   ├── ui/
│       │   │   │   │   │   └── VaultScreen.kt
│       │   │   │   │   └── viewmodel/
│       │   │   │   │       └── VaultHomeViewModel.kt
│       │   │   │   ├── passphrase/
│       │   │   │   │   ├── action/
│       │   │   │   │   │   └── ChangePassphraseUiAction.kt
│       │   │   │   │   ├── event/
│       │   │   │   │   │   └── ChangePassphraseUiEvent.kt
│       │   │   │   │   ├── state/
│       │   │   │   │   │   └── ChangePassphraseUiState.kt
│       │   │   │   │   ├── ui/
│       │   │   │   │   │   └── ChangePassphraseScreen.kt
│       │   │   │   │   └── viewmodel/
│       │   │   │   │       └── ChangePassphraseViewModel.kt
│       │   │   │   ├── quickunlock/
│       │   │   │   │   └── QuickUnlockPrompt.kt
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
│       │   │   │   │   ├── event/
│       │   │   │   │   │   └── SettingsUiEvent.kt
│       │   │   │   │   ├── state/
│       │   │   │   │   │   └── SettingsQuickUnlockUiState.kt
│       │   │   │   │   ├── ui/
│       │   │   │   │   │   └── SettingsScreen.kt
│       │   │   │   │   └── viewmodel/
│       │   │   │   │       └── SettingsViewModel.kt
│       │   │   │   ├── shared/
│       │   │   │   │   ├── error/
│       │   │   │   │   │   ├── SecureItemCrudErrorMessageMapper.kt
│       │   │   │   │   │   └── SecureItemDraftErrorMessageMapper.kt
│       │   │   │   │   ├── navigation/
│       │   │   │   │   │   └── NavigationBar.kt
│       │   │   │   │   └── sync/
│       │   │   │   │       ├── SyncIconButton.kt
│       │   │   │   │       ├── VaultSyncUiErrorCategory.kt
│       │   │   │   │       └── VaultSyncUiMapper.kt
│       │   │   │   ├── state/
│       │   │   │   │   └── VaultUiOperationState.kt
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
│       │   │   ├── res/
│       │   │   │   ├── values/
│       │   │   │   │   └── strings.xml
│       │   │   │   └── values-es/
│       │   │   │       └── strings.xml
│       │   │   └── AndroidManifest.xml
│       │   └── test/java/com/miguelrodriguez19/safecube/feature/vault/
│       │       ├── presentation/
│       │       │   ├── create/viewmodel/
│       │       │   │   └── CreateVaultViewModelTest.kt
│       │       │   ├── editor/
│       │       │   │   ├── note/viewmodel/
│       │       │   │   │   └── NoteEditorViewModelTest.kt
│       │       │   │   ├── password/viewmodel/
│       │       │   │   │   └── PasswordEditorViewModelTest.kt
│       │       │   │   └── shared/mutation/
│       │       │   │       ├── factory/
│       │       │   │       │   └── SecureItemEditorMutationGatewayFactoryTest.kt
│       │       │   │       ├── mutation/factory/
│       │       │   │       └── SecureItemEditorMutationCoordinatorTest.kt
│       │       │   ├── home/viewmodel/
│       │       │   │   └── VaultHomeViewModelTest.kt
│       │       │   ├── passphrase/viewmodel/
│       │       │   │   └── ChangePassphraseViewModelTest.kt
│       │       │   ├── recovery/viewmodel/
│       │       │   │   └── RecoveryKeyViewModelTest.kt
│       │       │   ├── settings/viewmodel/
│       │       │   │   └── SettingsViewModelTest.kt
│       │       │   ├── shared/editor/
│       │       │   └── unlock/viewmodel/
│       │       │       └── UnlockVaultViewModelTest.kt
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
├── .gitattributes
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
