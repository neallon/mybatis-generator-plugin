package io.github.gradle.tasks

import io.github.gradle.plugin.MybatisGeneratorExtension

import org.gradle.api.file.FileCollection
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.yaml.snakeyaml.Yaml

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

/**
 * generate config.properties & generatorConfig.xml
 *
 */
class MybatisGenerateConfigTask extends ConventionTask {
    @Input
    MybatisGeneratorExtension options

    @InputFiles
    FileCollection mybatisGeneratorClasspath

    @TaskAction
    void executeAction() {
        MybatisGeneratorExtension options = getOptions()

        def folder = options.folder

        createDirectoryIfNotExists(Paths.get(options.getOutputDirectory() as String, folder))

        def loader = getClass().getClassLoader()
        generateConfigFile(loader, String.join("/", folder, "config.properties"))
        generateGeneratorConfigFile(loader, String.join("/", folder, "generatorConfig.xml"))

    }

    static def createDirectoryIfNotExists(Path path) {
        if (!Files.exists(path)) {
            Files.createDirectories(path)
        }
    }

    @Internal
    def getDatabaseProperties() {
        def properties = new Properties()
        def resourcesPath = getOptions().getOutputDirectory() as String
        def fs = ['application.yml', 'application.yaml', 'application-dev.yml', 'application-dev.yaml']
                .collect { resourcesPath + "/" + it }
        def files = project.files(fs).findAll({ it.exists() })
        if (!files.isEmpty()) {
            def file = files.first() as File
            Yaml parser = new Yaml()
            def all = parser.load(file.text)
            def datasource = all.spring.datasource as LinkedHashMap<String, String>
            datasource.forEach((k, v) -> properties.setProperty(k, v))
        }

        if (properties.isEmpty()) {
            fs = ['application.properties', 'application-dev.properties']
                    .collect { resourcesPath + "/" + it }
            files = project.files(fs).findAll({ it.exists() })
            def prefix = "spring.datasource"
            if (!files.isEmpty()) {
                def file = files.first() as File
                def springProperties = new Properties()
                springProperties.load(new FileInputStream(file))
                springProperties.keys().findAll { it.startsWith(prefix) }.toList()
                        .forEach(k ->
                                properties.setProperty(k.getAt(prefix.length() + 1..-1), springProperties.getProperty(k))
                        )
            }
        }

        properties
    }

    @Internal
    def getGeneratorProperties() {
        def properties = new Properties()
        def basePackage = "com.zh." + project.name.replace('-', '')
        properties.setProperty("java-client-target-package", basePackage + ".mapper")
        properties.setProperty("java-model-target-package", basePackage + ".model")
        properties.setProperty("sql-map-package", "mapper")
        return properties
    }

    void generateConfigFile(ClassLoader loader, String path) {
        def stream = loader.getResourceAsStream(path)
        def properties = new Properties()
        properties.load(stream)
        properties << getGeneratorProperties() << getDatabaseProperties()

        properties.store(Files.newOutputStream(Paths.get(getOptions().getOutputDirectory() as String, path)), "generate by generator plugin")
    }

    void generateGeneratorConfigFile(ClassLoader loader, String path) {
        def stream = loader.getResourceAsStream(path)
        Files.copy(stream, Paths.get(getOptions().getOutputDirectory() as String, path), StandardCopyOption.REPLACE_EXISTING)
    }
}
