package com.miguelrodriguez19.safecube.buildlogic

import java.io.File
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.xml.parsers.DocumentBuilderFactory
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.w3c.dom.Element

abstract class GenerateLogoVectorDrawablesTask : DefaultTask() {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val foregroundSvg: RegularFileProperty

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val monochromeSvg: RegularFileProperty

    @get:OutputFile
    abstract val foregroundVector: RegularFileProperty

    @get:OutputFile
    abstract val monochromeVector: RegularFileProperty

    @TaskAction
    fun generate() {
        LogoAssetContract.writeVector(
            source = foregroundSvg.get().asFile,
            destination = foregroundVector.get().asFile,
        )
        LogoAssetContract.writeVector(
            source = monochromeSvg.get().asFile,
            destination = monochromeVector.get().asFile,
        )
    }
}

abstract class VerifyLogoGeometryTask : DefaultTask() {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val assetFiles: ConfigurableFileCollection

    @TaskAction
    fun verify() {
        LogoAssetContract.verify(assetFiles.files)
    }
}

private object LogoAssetContract {
    private const val EXPECTED_PATH_COUNT = 37
    private const val EXPECTED_VIEWBOX = "0 0 1024 1024"
    private const val EXPECTED_MASTER_SHA256 = "79729c8e195797f6eac3708d6a1a65d953077252d3b13494fdaecacc3760c72a"
    private val transformPattern = Regex(
        """translate\(\s*([-+]?\d+(?:\.\d+)?)\s*,\s*([-+]?\d+(?:\.\d+)?)\s*\)\s+scale\(\s*([-+]?\d+(?:\.\d+)?)\s*,\s*([-+]?\d+(?:\.\d+)?)\s*\)""",
    )

    fun writeVector(source: File, destination: File) {
        val asset = parseSvg(source)
        require(asset.paths.isNotEmpty()) { "${source.path} contains no paths" }
        require(asset.transform != null) { "${source.path} has no launcher transform" }
        destination.parentFile.mkdirs()
        destination.writeText(renderVector(asset, source.name), StandardCharsets.UTF_8)
    }

    fun verify(files: Set<File>) {
        val byName = files.associateBy { it.name }
        val master = parseSvg(requireFile(byName, "master.svg"))
        val foreground = parseSvg(requireFile(byName, "adaptive-foreground.svg"))
        val monochrome = parseSvg(requireFile(byName, "monochrome-themed.svg"))
        val round = parseSvg(requireFile(byName, "round-preview.svg"))
        val foregroundVector = requireFile(byName, "ic_launcher_foreground.xml")
        val monochromeVector = requireFile(byName, "ic_launcher_monochrome.xml")
        val adaptiveBackground = parseSvg(requireFile(byName, "adaptive-background.svg"))

        check(master.paths.size == EXPECTED_PATH_COUNT) {
            "master.svg: expected $EXPECTED_PATH_COUNT paths, found ${master.paths.size}"
        }
        check(sha256(master.file) == EXPECTED_MASTER_SHA256) {
            "master.svg: checksum differs from the approved canonical asset"
        }
        listOf(foreground, monochrome, round).forEach { asset ->
            check(asset.viewBox == EXPECTED_VIEWBOX) {
                "${asset.file.name}: expected viewBox $EXPECTED_VIEWBOX, found ${asset.viewBox}"
            }
            check(asset.paths == master.paths) {
                "${asset.file.name}: path geometry differs from master.svg"
            }
            check(asset.transform != null) {
                "${asset.file.name}: launcher transform is missing"
            }
            check(asset.transform == foreground.transform) {
                "${asset.file.name}: launcher transform differs from adaptive-foreground.svg"
            }
        }
        check(foreground.fill == "#FFFFFF") {
            "adaptive-foreground.svg: expected white foreground fill"
        }
        check(monochrome.fill == "#000000") {
            "monochrome-themed.svg: expected black monochrome fill"
        }
        check(adaptiveBackground.paths.isEmpty()) {
            "adaptive-background.svg: background must not contain paths"
        }

        check(foregroundVector.readText(StandardCharsets.UTF_8) == renderVector(foreground, foreground.file.name)) {
            "ic_launcher_foreground.xml is out of date; run generateLogoVectorDrawables"
        }
        check(monochromeVector.readText(StandardCharsets.UTF_8) == renderVector(monochrome, monochrome.file.name)) {
            "ic_launcher_monochrome.xml is out of date; run generateLogoVectorDrawables"
        }
    }

    private fun parseSvg(file: File): SvgAsset {
        require(file.isFile) { "Missing logo asset: ${file.path}" }
        val factory = DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = false
            setFeature("http://xml.org/sax/features/external-general-entities", false)
            setFeature("http://xml.org/sax/features/external-parameter-entities", false)
            setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
            isXIncludeAware = false
            isExpandEntityReferences = false
        }
        val root = factory.newDocumentBuilder().parse(file).documentElement
        val group = root.getElementsByTagName("g").item(0) as? Element
        val transform = group?.getAttribute("transform")
            ?.takeIf(String::isNotBlank)
            ?.let { value ->
                val match = transformPattern.matchEntire(value)
                    ?: error("${file.name}: unsupported launcher transform '$value'")
                Transform(
                    translateX = match.groupValues[1],
                    translateY = match.groupValues[2],
                    scaleX = match.groupValues[3],
                    scaleY = match.groupValues[4],
                )
            }
        val paths = root.getElementsByTagName("path").let { nodes ->
            (0 until nodes.length).map { index ->
                (nodes.item(index) as Element).getAttribute("d").normalizePathData()
            }
        }

        return SvgAsset(
            file = file,
            viewBox = normalizeViewBox(root.getAttribute("viewBox")),
            transform = transform,
            fill = group?.getAttribute("fill")?.uppercase().orEmpty(),
            paths = paths,
        )
    }

    private fun renderVector(asset: SvgAsset, sourceName: String): String {
        val transform = requireNotNull(asset.transform) { "$sourceName: launcher transform is missing" }
        val fill = when (asset.fill) {
            "#FFFFFF" -> "#FFFFFFFF"
            "#000000" -> "#FF000000"
            else -> error("$sourceName: unsupported vector fill '${asset.fill}'")
        }
        return buildString {
            appendLine("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
            appendLine("<!-- Generated from docs/design/assets/logo/$sourceName; geometry preserved. -->")
            appendLine("<vector xmlns:android=\"http://schemas.android.com/apk/res/android\"")
            appendLine("    android:width=\"108dp\"")
            appendLine("    android:height=\"108dp\"")
            appendLine("    android:viewportWidth=\"1024\"")
            appendLine("    android:viewportHeight=\"1024\">")
            appendLine("    <group")
            appendLine("        android:translateX=\"${transform.translateX.toAndroidNumber()}\"")
            appendLine("        android:translateY=\"${transform.translateY.toAndroidNumber()}\"")
            appendLine("        android:scaleX=\"${transform.scaleX.toAndroidNumber()}\"")
            appendLine("        android:scaleY=\"${transform.scaleY.toAndroidNumber()}\">")
            asset.paths.forEach { path ->
                appendLine("        <path")
                appendLine("            android:fillColor=\"$fill\"")
                appendLine("            android:pathData=\"$path\" />")
            }
            appendLine("    </group>")
            appendLine("</vector>")
        }
    }

    private fun requireFile(files: Map<String, File>, name: String): File =
        files[name] ?: error("Logo verification input is missing: $name")

    private fun normalizeViewBox(value: String): String =
        value.trim().split(Regex("\\s+")).joinToString(" ") { token ->
            token.toBigDecimal().stripTrailingZeros().toPlainString()
        }

    private fun String.normalizePathData(): String = trim().replace(Regex("\\s+"), " ")

    private fun String.toAndroidNumber(): String =
        toBigDecimalOrNull()?.stripTrailingZeros()?.toPlainString() ?: this

    private fun sha256(file: File): String = MessageDigest
        .getInstance("SHA-256")
        .digest(file.readBytes())
        .joinToString("") { byte -> "%02x".format(byte) }

    private data class SvgAsset(
        val file: File,
        val viewBox: String,
        val transform: Transform?,
        val fill: String,
        val paths: List<String>,
    )

    private data class Transform(
        val translateX: String,
        val translateY: String,
        val scaleX: String,
        val scaleY: String,
    )
}
