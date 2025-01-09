import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.amazonEC2CloudImage
import jetbrains.buildServer.configs.kotlin.amazonEC2CloudProfile
import jetbrains.buildServer.configs.kotlin.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.projectFeatures.activeStorage
import jetbrains.buildServer.configs.kotlin.projectFeatures.awsConnection
import jetbrains.buildServer.configs.kotlin.projectFeatures.s3Storage
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2024.12"

project {

    vcsRoot(HttpsGithubComSocksdevilTeamcityAwsLambdaPluginExampleRefsHeadsMain)
    vcsRoot(HttpsGithubComSocksdevilTeamcityAwsLambdaPluginExampleRefsHeadsMain1)

    buildType(Build)

    features {
        awsConnection {
            id = "AmazonWebServicesAws_2"
            name = "Static Credentials"
            credentialsType = static {
                accessKeyId = "AKIA5JH2VERVOJZASKLS"
                secretAccessKey = "credentialsJSON:1448a0c5-1c49-48a5-980d-7a2612a7da20"
                useSessionCredentials = false
            }
            allowInBuilds = true
        }
        awsConnection {
            id = "IamRoleFromRoot"
            name = "IAM Role from Root"
            credentialsType = iamRole {
                roleArn = "arn:aws:iam::913206223978:role/acherenkov-test-s3-role"
                awsConnectionId = "StaticCredentialsInRoot"
            }
            allowInBuilds = false
        }
        s3Storage {
            id = "PROJECT_EXT_14"
            storageName = "Meow"
            bucketName = "artifacts-evierocha"
            forceVirtualHostAddressing = true
            cloudFrontEnabled = true
            cloudFrontUploadDistribution = "E320MMPZP8V38K"
            cloudFrontDownloadDistribution = "E2T6E3HJ8C3FDB"
            cloudFrontPublicKeyId = "KWYVF3I5SQPP0"
            cloudFrontPrivateKey = "credentialsJSON:faed04d0-38b0-4c8f-88ad-286d080976d8"
            awsEnvironment = default {
            }
            connectionId = "AmazonWebServicesAws_2"
        }
        amazonEC2CloudImage {
            id = "PROJECT_EXT_24"
            profileId = "amazon-2"
            vpcSubnetId = "subnet-058761d1c673583c8,subnet-07277bd24d3261745"
            instanceType = "t2.medium"
            securityGroups = listOf("sg-0e46a3411dd87de47")
            instanceTags = mapOf(
                "Owner" to "evie.rocha@jetbrains.com"
            )
            source = Source("ami-09c358ba71fe4ee8b")
        }
        activeStorage {
            id = "PROJECT_EXT_6"
            activeStorageID = "PROJECT_EXT_14"
        }
        amazonEC2CloudProfile {
            id = "amazon-2"
            name = "Test"
            terminateIdleMinutes = 0
            region = AmazonEC2CloudProfile.Regions.EU_WEST_DUBLIN
            authType = accessKey {
                keyId = "credentialsJSON:eb866efa-5965-49be-b5be-6ddeb4c55c2f"
                secretKey = "credentialsJSON:1448a0c5-1c49-48a5-980d-7a2612a7da20"
            }
        }
    }
}

object Build : BuildType({
    name = "Build"

    artifactRules = "README.md"

    vcs {
        root(HttpsGithubComSocksdevilTeamcityAwsLambdaPluginExampleRefsHeadsMain)
    }

    steps {
        script {
            id = "gradle_runner"
            enabled = false
            scriptContent = """
                #!/bin/bash
                set -euo pipefail
                
                for iteration in ${'$'}(seq 1 3); do
                    d="iteration-${'$'}iteration/nested"
                    mkdir -p "${'$'}d"
                    echo "Generating files"
                    for file in ${'$'}(seq 1 10000); do
                	  dd if=/dev/random of="${'$'}d/${'$'}file" bs=1M count=1 status=none
                	done
                    echo "Issuing TeamCity command"
                    echo "##teamcity[publishArtifacts '${'$'}d => ${'$'}iteration.zip']"
                    for file in ${'$'}(seq 1 50 10000); do
                      echo "##teamcity[publishArtifacts '${'$'}d/${'$'}file => ${'$'}d/']"
                    done
                    echo "Sleep 1"
                    sleep 15
                    echo "Trimming files"
                    for file in ${'$'}(seq 10000 -1 1); do
                      truncate -s 0 "${'$'}d/${'$'}file" ||:
                    done
                    echo "Sleep 2"
                    sleep 15
                    echo "Remove dir"
                    rm -rf "${'$'}d"
                done
            """.trimIndent()
        }
        gradle {
            id = "gradle_runner_1"
            tasks = "clean build"
            gradleWrapperPath = ""
        }
    }

    triggers {
        vcs {
        }
    }
})

object HttpsGithubComSocksdevilTeamcityAwsLambdaPluginExampleRefsHeadsMain : GitVcsRoot({
    name = "https://github.com/socksdevil/teamcity-aws-lambda-plugin-example#refs/heads/main"
    url = "https://github.com/socksdevil/teamcity-aws-lambda-plugin-example"
    branch = "refs/heads/main"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "SocksDevil"
        password = "credentialsJSON:2091df62-b0a6-494c-9dd1-b03db1aaf9c6"
    }
})

object HttpsGithubComSocksdevilTeamcityAwsLambdaPluginExampleRefsHeadsMain1 : GitVcsRoot({
    name = "https://github.com/socksdevil/teamcity-aws-lambda-plugin-example#refs/heads/main (1)"
    url = "https://github.com/socksdevil/teamcity-aws-lambda-plugin-example"
    branch = "refs/heads/main"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "SocksDevil"
        password = "credentialsJSON:2091df62-b0a6-494c-9dd1-b03db1aaf9c6"
    }
})
