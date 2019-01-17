# kotlin-native-cocoapods

### Setup

```groovy
buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath 'com.alecstrong:cocoapods-gradle-plugin:0.1.0'
  }
}

// Cocoapods plugin is only applicable for multiplatform projects with Kotlin/Native
apply plugin: 'org.jetbrains.kotlin.multiplatform'
apply plugin: 'com.alecstrong.cocoapods'

// Optional configuration of plugin.
cocoapods {
  version = "1.0.0-LOCAL" // Defaults to "1.0.0-LOCAL"
  homepage = www.mywebsite.com  // Default to empty
  deploymentTarget = "10.0" // Defaults to "10.0"
  authors = "Ben Asher" // Defaults to empty
  license = "..." // Defaults to empty
  summary = "..." // Defaults to empty
}
```

From this the plugin will generate a task `generatePodspec` to create a `.podspec` file in that directory for the kotlin native project.

```
> Pods/Kotlin/gradlew -p Pods/Kotlin :common:generatePodspec
```

The above command is assuming a module structure where `Code/Kotlin` is the root of your gradle project, and `common` is a Kotlin Multiplatform module with iOS targets.

Then in your `Podfile` you can reference the module:

```ruby
source 'https://git.sqcorp.co/scm/ios/cocoapodspecs.git'

...

target 'MyProject' do
  ...
  pod 'common', :path => 'Code/Kotlin/common'
end
```

And that's it! From your iOS project you will be able to `import common`.