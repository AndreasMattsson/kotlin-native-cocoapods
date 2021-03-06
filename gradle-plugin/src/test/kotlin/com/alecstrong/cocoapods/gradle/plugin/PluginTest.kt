package com.alecstrong.cocoapods.gradle.plugin

import com.google.common.truth.Truth.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.Test
import java.io.File

class PluginTest {
  @Test
  fun `running generatePodspec in an empty repo works correctly`() {
    val fixtureName = "sample"
    val fixtureRoot = File("src/test/$fixtureName")
    val runner = GradleRunner.create()
        .withProjectDir(fixtureRoot)
        .withPluginClasspath()

    val podspec = File(fixtureRoot, "$fixtureName.podspec")
    assertThat(podspec.exists()).isFalse()

    runner.withArguments("generatePodspec", "--stacktrace").build()

    assertThat(podspec.exists()).isTrue()
    podspec.delete()
  }

  @Test
  fun `running initializeFramework successfully copies the dummy framework over`() {
    val fixtureName = "sample"
    val fixtureRoot = File("src/test/$fixtureName")
    val runner = GradleRunner.create()
        .withProjectDir(fixtureRoot)
        .withPluginClasspath()


    val framework = File(fixtureRoot, "build/sample.framework").apply { deleteRecursively() }
    val dsym = File(fixtureRoot, "build/sample.framework.dSYM").apply { deleteRecursively() }

    runner.withArguments(
        "-P${InitializeFrameworkTask.FRAMEWORK_PROPERTY}=$fixtureName.framework", "initializeFramework", "--stacktrace"
    ).build()

    assertThat(framework.exists()).isTrue()
    assertThat(dsym.exists()).isTrue()

    framework.deleteRecursively()
    dsym.deleteRecursively()
  }

  @Test
  fun `run createIosDebugArtifacts`() {
    val fixtureName = "sample"
    val fixtureRoot = File("src/test/$fixtureName")
    val runner = GradleRunner.create()
        .withProjectDir(fixtureRoot)
        .withPluginClasspath()
        .forwardOutput()

    val framework = File(fixtureRoot, "build/sample.framework").apply { deleteRecursively() }
    val dsym = File(fixtureRoot, "build/sample.framework.dSYM").apply { deleteRecursively() }

    runner.withArguments("createIosDebugArtifacts", "--stacktrace", "--info").build()

    assertThat(framework.exists()).isTrue()
    assertThat(dsym.exists()).isTrue()

    val plist = File(framework, "Info.plist")
    assertThat(plist.exists()).isTrue()

    assertThat(plist.readText()).apply {
      contains("iPhoneOS")
    }

    framework.deleteRecursively()
    dsym.deleteRecursively()
  }

  @Test
  fun `run createIosDebugArtifacts without preset`() {
    val fixtureName = "sample-no-preset"
    val fixtureRoot = File("src/test/$fixtureName")
    val runner = GradleRunner.create()
        .withProjectDir(fixtureRoot)
        .withPluginClasspath()
        .forwardOutput()

    val framework = File(fixtureRoot, "build/sample.framework").apply { deleteRecursively() }
    val dsym = File(fixtureRoot, "build/sample.framework.dSYM").apply { deleteRecursively() }

    runner.withArguments("createIosDebugArtifacts", "--stacktrace", "--info").build()

    assertThat(framework.exists()).isTrue()
    assertThat(dsym.exists()).isTrue()

    val plist = File(framework, "Info.plist")
    assertThat(plist.exists()).isTrue()

    assertThat(plist.readText()).apply {
      contains("iPhoneOS")
      doesNotContain("UIRequiredDeviceCapabilities")
    }

    framework.deleteRecursively()
    dsym.deleteRecursively()
  }

  @Test
  fun `run iosTest`() {
    val fixtureName = "sample"
    val fixtureRoot = File("src/test/$fixtureName")
    val runner = GradleRunner.create()
        .withProjectDir(fixtureRoot)
        .withPluginClasspath()
        .forwardOutput()

    val result = runner.withArguments("iosTest", "--stacktrace", "--info").build()

    assertThat(result.output)
        .contains("[  PASSED  ] 1 tests.")
  }

  @Test
  fun `build with no simulator warns`() {
    val fixtureName = "sample-no-simulator"
    val fixtureRoot = File("src/test/$fixtureName")
    val runner = GradleRunner.create()
        .withProjectDir(fixtureRoot)
        .withPluginClasspath()
        .forwardOutput()

    runner.withArguments("tasks", "--stacktrace", "--info")
        .build()
        .output
        .let { output ->
          assertThat(output).doesNotContain("iosTest ")
          assertThat(output).contains("No architecture provided for framework to be run on a simulator." +
              " This means no test task was added.")
        }
  }
}
