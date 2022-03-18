pipeline {
    agent any
    environment {
            WS = "${WORKSPACE}"
            IMAGE_VERSION = "v1.0"
            }

    stages {
        stage('环境检查'){
            steps {
                sh 'printenv'
                echo "正在检测基本信息"
                sh 'java -version'
                sh 'git --version'
                sh 'docker version'
                sh 'pwd && ls -alh'
            }
        }
    }


    post {             //新增
        success {
            emailext (
                subject: "SUCCESSFUL: Job '${env.JOB_NAME}-${env.BUILD_NUMBER}'",
                from: 'sicheng_zhou@qq.com',
                body: '''
                  构建名称:${PROJECT_NAME}
                  构建结果:Successful
                  构建次数:${BUILD_NUMBER}
                  触发用户:${CAUSE}
                  变更概要:${CHANGES}

                  运维中心:https://hxx.sicheng.store
                  服务市场:https://hxx.sicheng.store/nacos
                  mapple-admin: https://hxx.sicheng.store/mapple


                  系统邮箱，不需回复！！！
                  友情提示项目自动迭代了一个小版本
                  如果现在是晚上，很抱歉打扰。在下也不知道邮箱何时发送.
                  最后祝大家生活愉快！！！

                                                                            YOURS:
                                                                            Lazy
                       ''',

                to: "sicheng_zhou@qq.com,2941176308@qq.com,2686028645@qq.com,860834338@qq.com,1123671761@qq.com",
            )
        }
    }
}
