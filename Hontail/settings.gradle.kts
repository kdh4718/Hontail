pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // 카카오 레포지토리 추가
        maven {
            url = uri("https://devrepo.kakao.com/nexus/content/groups/public/") // uri 함수로 URL 설정
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "Hontail"
include(":app")
