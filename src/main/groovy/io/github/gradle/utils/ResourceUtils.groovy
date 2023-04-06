package io.github.gradle.utils

import org.gradle.api.Project
import org.gradle.api.tasks.Internal

class ResourceUtils {

    @Internal
    def static getMainResourcePath(Project project) {
        project.relativePath(getMainResourceFullPath(project))
    }

    @Internal
    def static getMainResourceFullPath(Project project) {
        project.sourceSets.main.resources.srcDirs.first().absolutePath
    }

    @Internal
    def static getMainJavaPath(Project project) {
        project.relativePath(getMainJavaFullPath(project))
    }

    @Internal
    def static getMainJavaFullPath(Project project) {
        project.sourceSets.main.java.srcDirs.first().absolutePath
    }
}
