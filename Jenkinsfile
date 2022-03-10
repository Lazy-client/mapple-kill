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
        stage('Maven编译打包') {
            agent {
                 docker {
                       image 'maven:3-alpine'
                       args '-v /var/jenkins_home/appconfig/maven/mvnrepo:/root/mvnrepo'
                    }
                 }

                steps {
                    //打包，jar.。默认是从maven中央仓库下载。
                    //jenkins目录+容器目录；-s指定容器内位置
                    sh "echo 默认的工作目录：${WS}"
                    //每一行指令都是基于当前环境信息。和上下指令无关
                    sh 'cd ${WS} && mvn clean package -s "/var/jenkins_home/appconfig/maven/settings.xml"  -Dmaven.test.skip=true '
                }
        }

        stage('制作Docker镜像') {

                    steps {
                        sh 'cd ${WS}/mapple-admin && docker build -t registry-vpc.cn-beijing.aliyuncs.com/sicheng/mapple-admin .'
                        sh 'cd ${WS}/mapple-gateway && docker build -t registry-vpc.cn-beijing.aliyuncs.com/sicheng/mapple-gateway .'
                        sh 'cd ${WS}/mapple-seckill && docker build -t registry-vpc.cn-beijing.aliyuncs.com/sicheng/mapple-seckill .'
                    }
        }

        stage('部署应用') {
            steps {
                 echo "部署..."
                 sh 'docker rm -f mapple-admin mapple-gateway mapple-seckill'
            }
        }
        stage('推送制品到阿里云制品库') {
                steps {
                     echo "push..."
                     sh 'docker login --username=userlazy -p wise5201314 registry-vpc.cn-beijing.aliyuncs.com'
                     sh 'docker push registry-vpc.cn-beijing.aliyuncs.com/sicheng/mapple-admin'
                     sh 'docker rmi registry-vpc.cn-beijing.aliyuncs.com/sicheng/mapple-admin'
                     sh 'docker push registry-vpc.cn-beijing.aliyuncs.com/sicheng/mapple-gateway'
                     sh 'docker rmi registry-vpc.cn-beijing.aliyuncs.com/sicheng/mapple-gateway'
                     sh 'docker push registry-vpc.cn-beijing.aliyuncs.com/sicheng/mapple-seckill'
                     sh 'docker rmi registry-vpc.cn-beijing.aliyuncs.com/sicheng/mapple-seckill'
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
                  构建地址:${BUILD_URL}
                  构建日志:${BUILD_URL}
                  请运维人员及时按实例更新,在此之前删除老版本镜像和容器:
                  docker run -di -p 8001:3333 -e server_port="3333" --name mapple-admin registry.cn-beijing.aliyuncs.com/sicheng/mapple-admin
                       ''',
                to: "sicheng_zhou@qq.com",
            )
        }
    }
}
