#!/usr/bin/env groovy
@Library('GlobalPipelineLibrary')
@Library('PipelineLibrary') _
brePipeline {
    defaultBuildGoals = 'clean checkstyle:checkstyle install'
    xlReleaseTechnology = 'Microservice'
    junit = ''
    checkstyle = [canComputeNew: false, defaultEncoding: '', healthy: '', pattern: '', unHealthy: '']
    findbugs = [canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', pattern: '', shouldDetectModules: true, unHealthy: '']
    jacoco = [classPattern: '**/target/classes']
}