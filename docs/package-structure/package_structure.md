# Package Structure
Updated: 20-02-2026 01:32:19

```
safecube-android/
├── .git/... # Skipped Content
├── .gradle/... # Skipped Content
├── .idea/... # Skipped Content
├── .kotlin/... # Skipped Content
├── app/
│   ├── build/... # Skipped Content
│   ├── src/
│   │   ├── androidTest/
│   │   │   └── java/
│   │   │       └── com/
│   │   │           └── miguelrodriguez19/
│   │   │               └── safecube/
│   │   │                   └── ExampleInstrumentedTest.kt
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── miguelrodriguez19/
│   │   │   │           └── safecube/
│   │   │   │               ├── ui/
│   │   │   │               │   └── theme/
│   │   │   │               │       ├── Color.kt
│   │   │   │               │       ├── Theme.kt
│   │   │   │               │       └── Type.kt
│   │   │   │               └── MainActivity.kt
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
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   └── themes.xml
│   │   │   │   └── xml/
│   │   │   │       ├── backup_rules.xml
│   │   │   │       └── data_extraction_rules.xml
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   │       └── java/
│   │           └── com/
│   │               └── miguelrodriguez19/
│   │                   └── safecube/
│   │                       └── ExampleUnitTest.kt
│   ├── .gitignore
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build/
│   └── reports/
│       └── problems/
│           └── problems-report.html
├── core/
│   ├── auth/
│   │   ├── build/
│   │   │   ├── .transforms/
│   │   │   │   └── b574b43e43b6fe3c25ab06b9333bcbcb/
│   │   │   │       ├── transformed/
│   │   │   │       │   └── bundleLibRuntimeToDirDebug/
│   │   │   │       │       └── desugar_graph.bin
│   │   │   │       └── results.bin
│   │   │   ├── generated/
│   │   │   │   ├── res/
│   │   │   │   │   └── pngs/
│   │   │   │   │       └── debug/
│   │   │   │   └── updated_navigation_xml/
│   │   │   │       └── debug/
│   │   │   ├── intermediates/
│   │   │   │   ├── aapt_friendly_merged_manifests/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── processDebugManifest/
│   │   │   │   │           └── aapt/
│   │   │   │   │               ├── AndroidManifest.xml
│   │   │   │   │               └── output-metadata.json
│   │   │   │   ├── aar_metadata/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── writeDebugAarMetadata/
│   │   │   │   │           │   │   │   │   ├── annotation_processor_list/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── javaPreCompileDebug/
│   │   │   │   │           └── annotationProcessors.json
│   │   │   │   ├── assets/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── mergeDebugAssets/
│   │   │   │   ├── compile_library_classes_jar/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── bundleLibCompileToJarDebug/
│   │   │   │   │           └── classes.jar
│   │   │   │   ├── compile_r_class_jar/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── generateDebugRFile/
│   │   │   │   │           └── R.jar
│   │   │   │   ├── compile_symbol_list/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── generateDebugRFile/
│   │   │   │   │           └── R.txt
│   │   │   │   ├── compiled_local_resources/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── compileDebugLibraryResources/
│   │   │   │   │           └── out/
│   │   │   │   ├── data_binding_layout_info_type_package/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── packageDebugResources/
│   │   │   │   │           └── out/
│   │   │   │   ├── incremental/
│   │   │   │   │   ├── debug/
│   │   │   │   │   │   └── packageDebugResources/
│   │   │   │   │   │       ├── merged.dir/
│   │   │   │   │   │       ├── stripped.dir/
│   │   │   │   │   │       │   │   │   │   │   │       └── merger.xml
│   │   │   │   │   ├── mergeDebugAssets/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   └── mergeDebugJniLibFolders/
│   │   │   │   │       └── merger.xml
│   │   │   │   ├── library_jni/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── copyDebugJniLibsProjectOnly/
│   │   │   │   │           └── jni/
│   │   │   │   ├── local_only_symbol_list/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── parseDebugLocalResources/
│   │   │   │   │           └── R-def.txt
│   │   │   │   ├── manifest_merge_blame_file/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── processDebugManifest/
│   │   │   │   │           └── manifest-merger-blame-debug-report.txt
│   │   │   │   ├── merged_jni_libs/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── mergeDebugJniLibFolders/
│   │   │   │   │           └── out/
│   │   │   │   ├── merged_manifest/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── processDebugManifest/
│   │   │   │   │           └── AndroidManifest.xml
│   │   │   │   ├── navigation_json/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── extractDeepLinksDebug/
│   │   │   │   │           └── navigation.json
│   │   │   │   ├── nested_resources_validation_report/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── generateDebugResources/
│   │   │   │   │           └── nestedResourcesValidationReport.txt
│   │   │   │   ├── packaged_res/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── packageDebugResources/
│   │   │   │   ├── public_res/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── packageDebugResources/
│   │   │   │   ├── runtime_library_classes_dir/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── bundleLibRuntimeToDirDebug/
│   │   │   │   └── symbol_list_with_package_name/
│   │   │   │       └── debug/
│   │   │   │           └── generateDebugRFile/
│   │   │   │               └── package-aware-r.txt
│   │   │   └── outputs/
│   │   │       └── logs/
│   │   │           └── manifest-merger-debug-report.txt
│   │   ├── src/
│   │   │   └── main/
│   │   │       └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   ├── crypto/
│   │   ├── build/
│   │   │   ├── .transforms/
│   │   │   │   └── 2ca3dfe76acec6f9eed806e96d094c8f/
│   │   │   │       ├── transformed/
│   │   │   │       │   └── bundleLibRuntimeToDirDebug/
│   │   │   │       │       └── desugar_graph.bin
│   │   │   │       └── results.bin
│   │   │   ├── generated/
│   │   │   │   ├── res/
│   │   │   │   │   └── pngs/
│   │   │   │   │       └── debug/
│   │   │   │   └── updated_navigation_xml/
│   │   │   │       └── debug/
│   │   │   ├── intermediates/
│   │   │   │   ├── aapt_friendly_merged_manifests/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── processDebugManifest/
│   │   │   │   │           └── aapt/
│   │   │   │   │               ├── AndroidManifest.xml
│   │   │   │   │               └── output-metadata.json
│   │   │   │   ├── aar_metadata/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── writeDebugAarMetadata/
│   │   │   │   │           │   │   │   │   ├── annotation_processor_list/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── javaPreCompileDebug/
│   │   │   │   │           └── annotationProcessors.json
│   │   │   │   ├── assets/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── mergeDebugAssets/
│   │   │   │   ├── compile_library_classes_jar/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── bundleLibCompileToJarDebug/
│   │   │   │   │           └── classes.jar
│   │   │   │   ├── compile_r_class_jar/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── generateDebugRFile/
│   │   │   │   │           └── R.jar
│   │   │   │   ├── compile_symbol_list/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── generateDebugRFile/
│   │   │   │   │           └── R.txt
│   │   │   │   ├── compiled_local_resources/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── compileDebugLibraryResources/
│   │   │   │   │           └── out/
│   │   │   │   ├── data_binding_layout_info_type_package/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── packageDebugResources/
│   │   │   │   │           └── out/
│   │   │   │   ├── incremental/
│   │   │   │   │   ├── debug/
│   │   │   │   │   │   └── packageDebugResources/
│   │   │   │   │   │       ├── merged.dir/
│   │   │   │   │   │       ├── stripped.dir/
│   │   │   │   │   │       │   │   │   │   │   │       └── merger.xml
│   │   │   │   │   ├── mergeDebugAssets/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   └── mergeDebugJniLibFolders/
│   │   │   │   │       └── merger.xml
│   │   │   │   ├── library_jni/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── copyDebugJniLibsProjectOnly/
│   │   │   │   │           └── jni/
│   │   │   │   ├── local_only_symbol_list/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── parseDebugLocalResources/
│   │   │   │   │           └── R-def.txt
│   │   │   │   ├── manifest_merge_blame_file/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── processDebugManifest/
│   │   │   │   │           └── manifest-merger-blame-debug-report.txt
│   │   │   │   ├── merged_jni_libs/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── mergeDebugJniLibFolders/
│   │   │   │   │           └── out/
│   │   │   │   ├── merged_manifest/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── processDebugManifest/
│   │   │   │   │           └── AndroidManifest.xml
│   │   │   │   ├── navigation_json/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── extractDeepLinksDebug/
│   │   │   │   │           └── navigation.json
│   │   │   │   ├── nested_resources_validation_report/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── generateDebugResources/
│   │   │   │   │           └── nestedResourcesValidationReport.txt
│   │   │   │   ├── packaged_res/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── packageDebugResources/
│   │   │   │   ├── public_res/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── packageDebugResources/
│   │   │   │   ├── runtime_library_classes_dir/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── bundleLibRuntimeToDirDebug/
│   │   │   │   └── symbol_list_with_package_name/
│   │   │   │       └── debug/
│   │   │   │           └── generateDebugRFile/
│   │   │   │               └── package-aware-r.txt
│   │   │   └── outputs/
│   │   │       └── logs/
│   │   │           └── manifest-merger-debug-report.txt
│   │   ├── src/
│   │   │   └── main/
│   │   │       └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   ├── network/
│   │   ├── build/
│   │   │   ├── .transforms/
│   │   │   │   └── 38581485626d412b38ff26f0f50b5012/
│   │   │   │       ├── transformed/
│   │   │   │       │   └── bundleLibRuntimeToDirDebug/
│   │   │   │       │       └── desugar_graph.bin
│   │   │   │       └── results.bin
│   │   │   ├── generated/
│   │   │   │   ├── res/
│   │   │   │   │   └── pngs/
│   │   │   │   │       └── debug/
│   │   │   │   └── updated_navigation_xml/
│   │   │   │       └── debug/
│   │   │   ├── intermediates/
│   │   │   │   ├── aapt_friendly_merged_manifests/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── processDebugManifest/
│   │   │   │   │           └── aapt/
│   │   │   │   │               ├── AndroidManifest.xml
│   │   │   │   │               └── output-metadata.json
│   │   │   │   ├── aar_metadata/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── writeDebugAarMetadata/
│   │   │   │   │           │   │   │   │   ├── annotation_processor_list/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── javaPreCompileDebug/
│   │   │   │   │           └── annotationProcessors.json
│   │   │   │   ├── assets/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── mergeDebugAssets/
│   │   │   │   ├── compile_library_classes_jar/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── bundleLibCompileToJarDebug/
│   │   │   │   │           └── classes.jar
│   │   │   │   ├── compile_r_class_jar/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── generateDebugRFile/
│   │   │   │   │           └── R.jar
│   │   │   │   ├── compile_symbol_list/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── generateDebugRFile/
│   │   │   │   │           └── R.txt
│   │   │   │   ├── compiled_local_resources/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── compileDebugLibraryResources/
│   │   │   │   │           └── out/
│   │   │   │   ├── data_binding_layout_info_type_package/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── packageDebugResources/
│   │   │   │   │           └── out/
│   │   │   │   ├── incremental/
│   │   │   │   │   ├── debug/
│   │   │   │   │   │   └── packageDebugResources/
│   │   │   │   │   │       ├── merged.dir/
│   │   │   │   │   │       ├── stripped.dir/
│   │   │   │   │   │       │   │   │   │   │   │       └── merger.xml
│   │   │   │   │   ├── mergeDebugAssets/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   └── mergeDebugJniLibFolders/
│   │   │   │   │       └── merger.xml
│   │   │   │   ├── library_jni/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── copyDebugJniLibsProjectOnly/
│   │   │   │   │           └── jni/
│   │   │   │   ├── local_only_symbol_list/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── parseDebugLocalResources/
│   │   │   │   │           └── R-def.txt
│   │   │   │   ├── manifest_merge_blame_file/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── processDebugManifest/
│   │   │   │   │           └── manifest-merger-blame-debug-report.txt
│   │   │   │   ├── merged_jni_libs/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── mergeDebugJniLibFolders/
│   │   │   │   │           └── out/
│   │   │   │   ├── merged_manifest/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── processDebugManifest/
│   │   │   │   │           └── AndroidManifest.xml
│   │   │   │   ├── navigation_json/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── extractDeepLinksDebug/
│   │   │   │   │           └── navigation.json
│   │   │   │   ├── nested_resources_validation_report/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── generateDebugResources/
│   │   │   │   │           └── nestedResourcesValidationReport.txt
│   │   │   │   ├── packaged_res/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── packageDebugResources/
│   │   │   │   ├── public_res/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── packageDebugResources/
│   │   │   │   ├── runtime_library_classes_dir/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── bundleLibRuntimeToDirDebug/
│   │   │   │   └── symbol_list_with_package_name/
│   │   │   │       └── debug/
│   │   │   │           └── generateDebugRFile/
│   │   │   │               └── package-aware-r.txt
│   │   │   └── outputs/
│   │   │       └── logs/
│   │   │           └── manifest-merger-debug-report.txt
│   │   ├── src/
│   │   │   └── main/
│   │   │       └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   └── storage/
│       ├── build/
│       │   ├── .transforms/
│       │   │   └── 66d07e258dbe65733fe4082e53db4c6e/
│       │   │       ├── transformed/
│       │   │       │   └── bundleLibRuntimeToDirDebug/
│       │   │       │       └── desugar_graph.bin
│       │   │       └── results.bin
│       │   ├── generated/
│       │   │   ├── res/
│       │   │   │   └── pngs/
│       │   │   │       └── debug/
│       │   │   └── updated_navigation_xml/
│       │   │       └── debug/
│       │   ├── intermediates/
│       │   │   ├── aapt_friendly_merged_manifests/
│       │   │   │   └── debug/
│       │   │   │       └── processDebugManifest/
│       │   │   │           └── aapt/
│       │   │   │               ├── AndroidManifest.xml
│       │   │   │               └── output-metadata.json
│       │   │   ├── aar_metadata/
│       │   │   │   └── debug/
│       │   │   │       └── writeDebugAarMetadata/
│       │   │   │           │       │   │   ├── annotation_processor_list/
│       │   │   │   └── debug/
│       │   │   │       └── javaPreCompileDebug/
│       │   │   │           └── annotationProcessors.json
│       │   │   ├── assets/
│       │   │   │   └── debug/
│       │   │   │       └── mergeDebugAssets/
│       │   │   ├── compile_library_classes_jar/
│       │   │   │   └── debug/
│       │   │   │       └── bundleLibCompileToJarDebug/
│       │   │   │           └── classes.jar
│       │   │   ├── compile_r_class_jar/
│       │   │   │   └── debug/
│       │   │   │       └── generateDebugRFile/
│       │   │   │           └── R.jar
│       │   │   ├── compile_symbol_list/
│       │   │   │   └── debug/
│       │   │   │       └── generateDebugRFile/
│       │   │   │           └── R.txt
│       │   │   ├── compiled_local_resources/
│       │   │   │   └── debug/
│       │   │   │       └── compileDebugLibraryResources/
│       │   │   │           └── out/
│       │   │   ├── data_binding_layout_info_type_package/
│       │   │   │   └── debug/
│       │   │   │       └── packageDebugResources/
│       │   │   │           └── out/
│       │   │   ├── incremental/
│       │   │   │   ├── debug/
│       │   │   │   │   └── packageDebugResources/
│       │   │   │   │       ├── merged.dir/
│       │   │   │   │       ├── stripped.dir/
│       │   │   │   │       │       │   │   │   │       └── merger.xml
│       │   │   │   ├── mergeDebugAssets/
│       │   │   │   │   └── merger.xml
│       │   │   │   └── mergeDebugJniLibFolders/
│       │   │   │       └── merger.xml
│       │   │   ├── library_jni/
│       │   │   │   └── debug/
│       │   │   │       └── copyDebugJniLibsProjectOnly/
│       │   │   │           └── jni/
│       │   │   ├── local_only_symbol_list/
│       │   │   │   └── debug/
│       │   │   │       └── parseDebugLocalResources/
│       │   │   │           └── R-def.txt
│       │   │   ├── manifest_merge_blame_file/
│       │   │   │   └── debug/
│       │   │   │       └── processDebugManifest/
│       │   │   │           └── manifest-merger-blame-debug-report.txt
│       │   │   ├── merged_jni_libs/
│       │   │   │   └── debug/
│       │   │   │       └── mergeDebugJniLibFolders/
│       │   │   │           └── out/
│       │   │   ├── merged_manifest/
│       │   │   │   └── debug/
│       │   │   │       └── processDebugManifest/
│       │   │   │           └── AndroidManifest.xml
│       │   │   ├── navigation_json/
│       │   │   │   └── debug/
│       │   │   │       └── extractDeepLinksDebug/
│       │   │   │           └── navigation.json
│       │   │   ├── nested_resources_validation_report/
│       │   │   │   └── debug/
│       │   │   │       └── generateDebugResources/
│       │   │   │           └── nestedResourcesValidationReport.txt
│       │   │   ├── packaged_res/
│       │   │   │   └── debug/
│       │   │   │       └── packageDebugResources/
│       │   │   ├── public_res/
│       │   │   │   └── debug/
│       │   │   │       └── packageDebugResources/
│       │   │   ├── runtime_library_classes_dir/
│       │   │   │   └── debug/
│       │   │   │       └── bundleLibRuntimeToDirDebug/
│       │   │   └── symbol_list_with_package_name/
│       │   │       └── debug/
│       │   │           └── generateDebugRFile/
│       │   │               └── package-aware-r.txt
│       │   └── outputs/
│       │       └── logs/
│       │           └── manifest-merger-debug-report.txt
│       ├── src/
│       │   └── main/
│       │       └── AndroidManifest.xml
│       └── build.gradle.kts
├── docs/
│   ├── package-structure/
│   │   └── package_structure.md
│   └── README.md
├── feature/
│   ├── auth/
│   │   ├── build/
│   │   │   ├── .transforms/
│   │   │   │   └── 159eb3405ab740720324cb4ef9a87030/
│   │   │   │       ├── transformed/
│   │   │   │       │   └── bundleLibRuntimeToDirDebug/
│   │   │   │       │       └── desugar_graph.bin
│   │   │   │       └── results.bin
│   │   │   ├── generated/
│   │   │   │   ├── res/
│   │   │   │   │   └── pngs/
│   │   │   │   │       └── debug/
│   │   │   │   └── updated_navigation_xml/
│   │   │   │       └── debug/
│   │   │   ├── intermediates/
│   │   │   │   ├── aapt_friendly_merged_manifests/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── processDebugManifest/
│   │   │   │   │           └── aapt/
│   │   │   │   │               ├── AndroidManifest.xml
│   │   │   │   │               └── output-metadata.json
│   │   │   │   ├── aar_metadata/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── writeDebugAarMetadata/
│   │   │   │   │           │   │   │   │   ├── annotation_processor_list/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── javaPreCompileDebug/
│   │   │   │   │           └── annotationProcessors.json
│   │   │   │   ├── assets/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── mergeDebugAssets/
│   │   │   │   ├── compile_library_classes_jar/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── bundleLibCompileToJarDebug/
│   │   │   │   │           └── classes.jar
│   │   │   │   ├── compile_r_class_jar/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── generateDebugRFile/
│   │   │   │   │           └── R.jar
│   │   │   │   ├── compile_symbol_list/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── generateDebugRFile/
│   │   │   │   │           └── R.txt
│   │   │   │   ├── compiled_local_resources/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── compileDebugLibraryResources/
│   │   │   │   │           └── out/
│   │   │   │   ├── data_binding_layout_info_type_package/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── packageDebugResources/
│   │   │   │   │           └── out/
│   │   │   │   ├── incremental/
│   │   │   │   │   ├── debug/
│   │   │   │   │   │   └── packageDebugResources/
│   │   │   │   │   │       ├── merged.dir/
│   │   │   │   │   │       ├── stripped.dir/
│   │   │   │   │   │       │   │   │   │   │   │       └── merger.xml
│   │   │   │   │   ├── mergeDebugAssets/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   └── mergeDebugJniLibFolders/
│   │   │   │   │       └── merger.xml
│   │   │   │   ├── library_jni/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── copyDebugJniLibsProjectOnly/
│   │   │   │   │           └── jni/
│   │   │   │   ├── local_only_symbol_list/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── parseDebugLocalResources/
│   │   │   │   │           └── R-def.txt
│   │   │   │   ├── manifest_merge_blame_file/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── processDebugManifest/
│   │   │   │   │           └── manifest-merger-blame-debug-report.txt
│   │   │   │   ├── merged_jni_libs/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── mergeDebugJniLibFolders/
│   │   │   │   │           └── out/
│   │   │   │   ├── merged_manifest/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── processDebugManifest/
│   │   │   │   │           └── AndroidManifest.xml
│   │   │   │   ├── navigation_json/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── extractDeepLinksDebug/
│   │   │   │   │           └── navigation.json
│   │   │   │   ├── nested_resources_validation_report/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── generateDebugResources/
│   │   │   │   │           └── nestedResourcesValidationReport.txt
│   │   │   │   ├── packaged_res/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── packageDebugResources/
│   │   │   │   ├── public_res/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── packageDebugResources/
│   │   │   │   ├── runtime_library_classes_dir/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── bundleLibRuntimeToDirDebug/
│   │   │   │   └── symbol_list_with_package_name/
│   │   │   │       └── debug/
│   │   │   │           └── generateDebugRFile/
│   │   │   │               └── package-aware-r.txt
│   │   │   └── outputs/
│   │   │       └── logs/
│   │   │           └── manifest-merger-debug-report.txt
│   │   ├── src/
│   │   │   └── main/
│   │   │       └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   ├── profile/
│   │   ├── build/
│   │   │   ├── .transforms/
│   │   │   │   └── 3082755d8af2ea601b1d3011c72d5a79/
│   │   │   │       ├── transformed/
│   │   │   │       │   └── bundleLibRuntimeToDirDebug/
│   │   │   │       │       └── desugar_graph.bin
│   │   │   │       └── results.bin
│   │   │   ├── generated/
│   │   │   │   ├── res/
│   │   │   │   │   └── pngs/
│   │   │   │   │       └── debug/
│   │   │   │   └── updated_navigation_xml/
│   │   │   │       └── debug/
│   │   │   ├── intermediates/
│   │   │   │   ├── aapt_friendly_merged_manifests/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── processDebugManifest/
│   │   │   │   │           └── aapt/
│   │   │   │   │               ├── AndroidManifest.xml
│   │   │   │   │               └── output-metadata.json
│   │   │   │   ├── aar_metadata/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── writeDebugAarMetadata/
│   │   │   │   │           │   │   │   │   ├── annotation_processor_list/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── javaPreCompileDebug/
│   │   │   │   │           └── annotationProcessors.json
│   │   │   │   ├── assets/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── mergeDebugAssets/
│   │   │   │   ├── compile_library_classes_jar/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── bundleLibCompileToJarDebug/
│   │   │   │   │           └── classes.jar
│   │   │   │   ├── compile_r_class_jar/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── generateDebugRFile/
│   │   │   │   │           └── R.jar
│   │   │   │   ├── compile_symbol_list/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── generateDebugRFile/
│   │   │   │   │           └── R.txt
│   │   │   │   ├── compiled_local_resources/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── compileDebugLibraryResources/
│   │   │   │   │           └── out/
│   │   │   │   ├── data_binding_layout_info_type_package/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── packageDebugResources/
│   │   │   │   │           └── out/
│   │   │   │   ├── incremental/
│   │   │   │   │   ├── debug/
│   │   │   │   │   │   └── packageDebugResources/
│   │   │   │   │   │       ├── merged.dir/
│   │   │   │   │   │       ├── stripped.dir/
│   │   │   │   │   │       │   │   │   │   │   │       └── merger.xml
│   │   │   │   │   ├── mergeDebugAssets/
│   │   │   │   │   │   └── merger.xml
│   │   │   │   │   └── mergeDebugJniLibFolders/
│   │   │   │   │       └── merger.xml
│   │   │   │   ├── library_jni/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── copyDebugJniLibsProjectOnly/
│   │   │   │   │           └── jni/
│   │   │   │   ├── local_only_symbol_list/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── parseDebugLocalResources/
│   │   │   │   │           └── R-def.txt
│   │   │   │   ├── manifest_merge_blame_file/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── processDebugManifest/
│   │   │   │   │           └── manifest-merger-blame-debug-report.txt
│   │   │   │   ├── merged_jni_libs/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── mergeDebugJniLibFolders/
│   │   │   │   │           └── out/
│   │   │   │   ├── merged_manifest/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── processDebugManifest/
│   │   │   │   │           └── AndroidManifest.xml
│   │   │   │   ├── navigation_json/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── extractDeepLinksDebug/
│   │   │   │   │           └── navigation.json
│   │   │   │   ├── nested_resources_validation_report/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── generateDebugResources/
│   │   │   │   │           └── nestedResourcesValidationReport.txt
│   │   │   │   ├── packaged_res/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── packageDebugResources/
│   │   │   │   ├── public_res/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── packageDebugResources/
│   │   │   │   ├── runtime_library_classes_dir/
│   │   │   │   │   └── debug/
│   │   │   │   │       └── bundleLibRuntimeToDirDebug/
│   │   │   │   └── symbol_list_with_package_name/
│   │   │   │       └── debug/
│   │   │   │           └── generateDebugRFile/
│   │   │   │               └── package-aware-r.txt
│   │   │   └── outputs/
│   │   │       └── logs/
│   │   │           └── manifest-merger-debug-report.txt
│   │   ├── src/
│   │   │   └── main/
│   │   │       └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   └── vault/
│       ├── build/
│       │   ├── .transforms/
│       │   │   └── 717e4635debfdeaec981353de4ee428f/
│       │   │       ├── transformed/
│       │   │       │   └── bundleLibRuntimeToDirDebug/
│       │   │       │       └── desugar_graph.bin
│       │   │       └── results.bin
│       │   ├── generated/
│       │   │   ├── res/
│       │   │   │   └── pngs/
│       │   │   │       └── debug/
│       │   │   └── updated_navigation_xml/
│       │   │       └── debug/
│       │   ├── intermediates/
│       │   │   ├── aapt_friendly_merged_manifests/
│       │   │   │   └── debug/
│       │   │   │       └── processDebugManifest/
│       │   │   │           └── aapt/
│       │   │   │               ├── AndroidManifest.xml
│       │   │   │               └── output-metadata.json
│       │   │   ├── aar_metadata/
│       │   │   │   └── debug/
│       │   │   │       └── writeDebugAarMetadata/
│       │   │   │           │       │   │   ├── annotation_processor_list/
│       │   │   │   └── debug/
│       │   │   │       └── javaPreCompileDebug/
│       │   │   │           └── annotationProcessors.json
│       │   │   ├── assets/
│       │   │   │   └── debug/
│       │   │   │       └── mergeDebugAssets/
│       │   │   ├── compile_library_classes_jar/
│       │   │   │   └── debug/
│       │   │   │       └── bundleLibCompileToJarDebug/
│       │   │   │           └── classes.jar
│       │   │   ├── compile_r_class_jar/
│       │   │   │   └── debug/
│       │   │   │       └── generateDebugRFile/
│       │   │   │           └── R.jar
│       │   │   ├── compile_symbol_list/
│       │   │   │   └── debug/
│       │   │   │       └── generateDebugRFile/
│       │   │   │           └── R.txt
│       │   │   ├── compiled_local_resources/
│       │   │   │   └── debug/
│       │   │   │       └── compileDebugLibraryResources/
│       │   │   │           └── out/
│       │   │   ├── data_binding_layout_info_type_package/
│       │   │   │   └── debug/
│       │   │   │       └── packageDebugResources/
│       │   │   │           └── out/
│       │   │   ├── incremental/
│       │   │   │   ├── debug/
│       │   │   │   │   └── packageDebugResources/
│       │   │   │   │       ├── merged.dir/
│       │   │   │   │       ├── stripped.dir/
│       │   │   │   │       │       │   │   │   │       └── merger.xml
│       │   │   │   ├── mergeDebugAssets/
│       │   │   │   │   └── merger.xml
│       │   │   │   └── mergeDebugJniLibFolders/
│       │   │   │       └── merger.xml
│       │   │   ├── library_jni/
│       │   │   │   └── debug/
│       │   │   │       └── copyDebugJniLibsProjectOnly/
│       │   │   │           └── jni/
│       │   │   ├── local_only_symbol_list/
│       │   │   │   └── debug/
│       │   │   │       └── parseDebugLocalResources/
│       │   │   │           └── R-def.txt
│       │   │   ├── manifest_merge_blame_file/
│       │   │   │   └── debug/
│       │   │   │       └── processDebugManifest/
│       │   │   │           └── manifest-merger-blame-debug-report.txt
│       │   │   ├── merged_jni_libs/
│       │   │   │   └── debug/
│       │   │   │       └── mergeDebugJniLibFolders/
│       │   │   │           └── out/
│       │   │   ├── merged_manifest/
│       │   │   │   └── debug/
│       │   │   │       └── processDebugManifest/
│       │   │   │           └── AndroidManifest.xml
│       │   │   ├── navigation_json/
│       │   │   │   └── debug/
│       │   │   │       └── extractDeepLinksDebug/
│       │   │   │           └── navigation.json
│       │   │   ├── nested_resources_validation_report/
│       │   │   │   └── debug/
│       │   │   │       └── generateDebugResources/
│       │   │   │           └── nestedResourcesValidationReport.txt
│       │   │   ├── packaged_res/
│       │   │   │   └── debug/
│       │   │   │       └── packageDebugResources/
│       │   │   ├── public_res/
│       │   │   │   └── debug/
│       │   │   │       └── packageDebugResources/
│       │   │   ├── runtime_library_classes_dir/
│       │   │   │   └── debug/
│       │   │   │       └── bundleLibRuntimeToDirDebug/
│       │   │   └── symbol_list_with_package_name/
│       │   │       └── debug/
│       │   │           └── generateDebugRFile/
│       │   │               └── package-aware-r.txt
│       │   └── outputs/
│       │       └── logs/
│       │           └── manifest-merger-debug-report.txt
│       ├── src/
│       │   └── main/
│       │       └── AndroidManifest.xml
│       └── build.gradle.kts
├── gradle/
│   ├── wrapper/... # Skipped Content
│   └── libs.versions.toml
├── scripts/
│   ├── .build/... # Skipped Content
│   ├── resources/
│   │   └── com/
│   │       └── safecube/
│   │           └── tooling/
│   │               └── FolderTreeToFile.java
│   └── run-folder-tree.sh
├── .gitignore
├── build.gradle.kts
├── LICENSE
└── settings.gradle.kts
```
