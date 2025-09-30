import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.amazonEC2CloudImage
import jetbrains.buildServer.configs.kotlin.amazonEC2CloudProfile
import jetbrains.buildServer.configs.kotlin.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.projectFeatures.activeStorage
import jetbrains.buildServer.configs.kotlin.projectFeatures.awsConnection
import jetbrains.buildServer.configs.kotlin.projectFeatures.dockerECRRegistry
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

version = "2025.07"

project {

    vcsRoot(HttpsGithubComSocksdevilTeamcityAwsLambdaPluginExampleRefsHeadsMain)
    vcsRoot(HttpsGithubComSocksdevilTeamcityAwsLambdaPluginExampleRefsHeadsMain1)
    vcsRoot(HttpsGithubComSocksDevilTeamcityAwsLambdaPluginExampleRefsHeadsMain_2)

    buildType(Build)
    buildType(ImageBuilder)

    params {
        param("teamcity.internal.ec2.dsl.expireOnUpdate", "false")
    }

    features {
        awsConnection {
            id = "AmazonWebServicesAws_2"
            name = "Static Credentials"
            regionName = "eu-west-1"
            credentialsType = static {
                accessKeyId = "AKIA5JH2VERVOJZASKLS"
                secretAccessKey = "credentialsJSON:1448a0c5-1c49-48a5-980d-7a2612a7da20"
                useSessionCredentials = false
            }
            allowInBuilds = true
            stsEndpoint = "https://sts.eu-west-1.amazonaws.com"
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
            awsEnvironment = default {
            }
            connectionId = "AmazonWebServicesAws_2"
        }
        amazonEC2CloudImage {
            id = "PROJECT_EXT_15"
            profileId = "amazon-2"
            agentPoolId = "21"
            name = "Agent"
            vpcSubnetId = "subnet-07277bd24d3261745"
            instanceType = "t2.micro"
            securityGroups = listOf("sg-04e78bc7b27d01c70")
            userScript = ""
            instanceTags = mapOf(
                "Owner" to "evie.rocha@jetbrains.com"
            )
            source = Source("ami-0516047fd694c7fd6")
        }
        amazonEC2CloudImage {
            id = "PROJECT_EXT_22"
            profileId = "amazon-2"
            agentPoolId = "-2"
            name = "Non-existing AMI"
            vpcSubnetId = "subnet-07277bd24d3261745"
            instanceType = "t4g.small"
            securityGroups = listOf("sg-04e78bc7b27d01c70")
            source = Source("fake-id")
        }
        dockerECRRegistry {
            id = "PROJECT_EXT_36"
            displayName = "Amazon ECR"
            ecrType = ecrPublic()
            registryId = "913206223978"
            credentialsProvider = accessKey {
                accessKeyId = "AKIA5JH2VERVAYHV5RNO"
                secretAccessKey = "credentialsJSON:5f77bcdc-20b7-4d84-8971-f14dc824a8e7"
            }
            regionCode = "eu-central-1"
            credentialsType = accessKeys()
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

    subProject(Subproject)
}

object Build : BuildType({
    name = "Build"

    artifactRules = """
        big_file.txt
        folder/*.vsix => folder/
    """.trimIndent()

    params {
        param("teamcity.internal.artifactUpload.webPublisher.enableRetrier", "true")
    }

    vcs {
        root(HttpsGithubComSocksdevilTeamcityAwsLambdaPluginExampleRefsHeadsMain)
    }

    steps {
        gradle {
            id = "gradle_runner_1"
            tasks = "clean build"
            gradleWrapperPath = ""
        }
        script {
            id = "gradle_runner"
            scriptContent = """
                #!/bin/bash
                mkdir folder
                dd if=/dev/urandom bs=786438000 count=1 | base64 > build/test_file.vsix
                dd if=/dev/urandom bs=786438000 count=1 | base64 > build/test_file_2.vsix
                zip -r folder/meow.vsix build/*
            """.trimIndent()
        }
    }

    triggers {
        vcs {
        }
    }
})

object ImageBuilder : BuildType({
    name = "Image Builder"

    vcs {
        root(HttpsGithubComSocksDevilTeamcityAwsLambdaPluginExampleRefsHeadsMain_2)
    }

    triggers {
        vcs {
        }
    }
})

object HttpsGithubComSocksDevilTeamcityAwsLambdaPluginExampleRefsHeadsMain_2 : GitVcsRoot({
    name = "https://github.com/SocksDevil/teamcity-aws-lambda-plugin-example#refs/heads/main"
    url = "https://github.com/SocksDevil/teamcity-aws-lambda-plugin-example"
    branch = "refs/heads/main"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "SocksDevil"
        password = "credentialsJSON:2091df62-b0a6-494c-9dd1-b03db1aaf9c6"
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


object Subproject : Project({
    name = "Subproject"

    vcsRoot(Subproject_HttpsGithubComSocksDevilTeamcityAwsLambdaPluginExampleRefsHeadsMain)

    buildType(Subproject_Build)
})

object Subproject_Build : BuildType({
    name = "Build"

    artifactRules = "LICENSE"

    vcs {
        root(Subproject_HttpsGithubComSocksDevilTeamcityAwsLambdaPluginExampleRefsHeadsMain)
    }

    steps {
        gradle {
            id = "gradle_runner"
            tasks = "clean build"
            gradleWrapperPath = ""
        }
    }

    triggers {
        vcs {
        }
    }
})

object Subproject_HttpsGithubComSocksDevilTeamcityAwsLambdaPluginExampleRefsHeadsMain : GitVcsRoot({
    name = "https://github.com/SocksDevil/teamcity-aws-lambda-plugin-example#refs/heads/main"
    url = "https://github.com/SocksDevil/teamcity-aws-lambda-plugin-example"
    branch = "refs/heads/main"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "SocksDevil"
        password = "credentialsJSON:2091df62-b0a6-494c-9dd1-b03db1aaf9c6"
    }
})
