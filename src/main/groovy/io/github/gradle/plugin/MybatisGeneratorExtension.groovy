package io.github.gradle.plugin

import groovy.transform.ToString
import io.github.gradle.utils.ResourceUtils
import org.gradle.api.Project

/**
 * @author zh
 */
@ToString(includeNames = true)
class MybatisGeneratorExtension {

    def verbose = true

    def overwrite = false

    def outputDirectory

    def folder = "generator"

    def configFile = "src/main/resources/generator/generatorConfig.xml"

    def propertyPath = "src/main/resources/generator/config.properties"

    Properties properties


    Project project

    MybatisGeneratorExtension(Project project) {
        this.project = project
        this.properties = new Properties()


        // default output directory
        this.outputDirectory = ResourceUtils.getMainResourcePath(project)
    }

    Properties getProperties() {
        def file = project.file(this.propertyPath)
        if (!file.exists()) {
            throw new GroovyRuntimeException(String.format("property %s file missing.", this.propertyPath))
        }
        this.properties.load(new FileInputStream(file))
        this.properties.put("generated.source.dir", this.outputDirectory)
        this.properties.put("mybatis.generator.outputDirectory", this.outputDirectory)
        this.properties
    }

}
