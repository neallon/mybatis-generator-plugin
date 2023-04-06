package io.github.gradle.tasks

import io.github.gradle.plugin.MybatisGeneratorExtension
import io.github.gradle.utils.ResourceUtils
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.ConventionTask
import org.gradle.api.internal.project.IsolatedAntBuilder
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction

import java.nio.file.Paths

class MybatisGeneratorTask extends ConventionTask {
    @InputFiles
    FileCollection mybatisGeneratorClasspath

    @Input
    MybatisGeneratorExtension options

    @TaskAction
    void executeAction() {
        logger.lifecycle "execute Generator..."

        options = getOptions()

        services.get(IsolatedAntBuilder)
                .withClasspath(getMybatisGeneratorClasspath())
                .execute {
                    ant.taskdef(name: 'mbgenerator', classname: 'org.mybatis.generator.ant.GeneratorAntTask')

                    def mybatisProperties = options.getProperties()
                    def modelPackage = mybatisProperties.getProperty("java-model-target-package").replace('.', '/')
                    def project = getProject()
                    def javaPath = ResourceUtils.getMainJavaPath(project)
                    def modelDirectory = project.file(Paths.get(javaPath, modelPackage))
                    if (!modelDirectory.exists()) modelDirectory.mkdirs()

                    mybatisProperties.each {
                        ant.project.setProperty(it.key.toString(), it.value.toString())
                    }

                    ant.mbgenerator(
                            overwrite: options.getOverwrite(),
                            configfile: options.getConfigFile(),
                            verbose: options.getVerbose()) {

                        propertyset {
                            mybatisProperties.each {
                                propertyref(name: it.key)
                            }
                        }
                    }
                }
    }
}
