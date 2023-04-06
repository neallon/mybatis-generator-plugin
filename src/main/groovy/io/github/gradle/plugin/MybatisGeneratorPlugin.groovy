package io.github.gradle.plugin

import io.github.gradle.tasks.MybatisGenerateConfigTask
import io.github.gradle.tasks.MybatisGeneratorTask
import org.gradle.api.Plugin
import org.gradle.api.internal.project.ProjectInternal

/**
 * @author zh
 */
class MybatisGeneratorPlugin implements Plugin<ProjectInternal> {
    def MYBATIS_GENERATOR_RUNTIME_CONFIGURATION = "mybatisGenerator"
    def MYBATIS_GENERATOR_RUNTIME_ACTION = "generateMybatis"
    def MYBATIS_GENERATOR_CONFIG_RUNTIME_ACTION = "generateMybatisConfigFile"

    def MYBATIS_GENERATOR_ACTION_DESCRIPTION = "MyBatis Generator for gradle plugin"
    def MYBATIS_GENERATOR_CONFIG_ACTION_DESCRIPTION = "generate mybatis config files"

    @Override
    void apply(ProjectInternal project) {
        project.logger.info "Configuring MyBatis Generator for project: $project.name"

        MybatisGeneratorTask generatorTask = project.tasks.create(MYBATIS_GENERATOR_RUNTIME_ACTION, MybatisGeneratorTask)
                .configure {
                    group = MYBATIS_GENERATOR_RUNTIME_CONFIGURATION
                    description = MYBATIS_GENERATOR_ACTION_DESCRIPTION
                }

        MybatisGenerateConfigTask generateConfigTask = project.tasks.create(MYBATIS_GENERATOR_CONFIG_RUNTIME_ACTION, MybatisGenerateConfigTask)
                .configure {
                    group = MYBATIS_GENERATOR_RUNTIME_CONFIGURATION
                    description = MYBATIS_GENERATOR_CONFIG_ACTION_DESCRIPTION
                }


        project.configurations.create(MYBATIS_GENERATOR_RUNTIME_CONFIGURATION)
                .with {
                    description = "The MyBatis Generator to be used for this project."
                }

        def extension = project.extensions.create(MYBATIS_GENERATOR_RUNTIME_CONFIGURATION, MybatisGeneratorExtension, project)

        [generateConfigTask, generatorTask].forEach(task -> {
            task.conventionMapping.with {
                mybatisGeneratorClasspath = {
                    def config = project.configurations[MYBATIS_GENERATOR_RUNTIME_CONFIGURATION]
                    if (config.dependencies.empty) {
                        project.logger.info "The mybatisGenerator not config dependencies, so use default: \n\tmybatis-generator-core:1.3.7 \n\tmysql-connector-java:8.0.15 \n\tmapper-generator:4.2.2"
                        project.dependencies {
                            mybatisGenerator 'org.mybatis.generator:mybatis-generator-core:1.4.2'
                            mybatisGenerator 'com.mysql:mysql-connector-j:8.0.32'
                            mybatisGenerator 'tk.mybatis:mapper-generator:4.2.2'
                        }
                    }
                    config
                }

                options = { extension }
            }
        })
    }
}
