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
                body: '''<!DOCTYPE html>
                                         <html>
                                         <head>
                                         <meta charset="UTF-8">
                                         <title>${ENV, var="JOB_NAME"}-第${BUILD_NUMBER}次构建日志</title>
                                         </head>

                                         <body leftmargin="8" marginwidth="0" topmargin="8" marginheight="4"
                                             offset="0">
                                             <table width="95%" cellpadding="0" cellspacing="0"  style="font-size: 11pt; font-family: Tahoma, Arial, Helvetica, sans-serif">
                                         <h3>本邮件由系统自动发出，请勿回复！</h3>
                                                 <tr>
                                                    <br/>
                                                     各位同事，大家好，以下为${PROJECT_NAME }项目构建信息</br>
                                                     <td><font color="#CC0000">构建结果 - ${BUILD_STATUS}</font></td>
                                                 </tr>
                                                 <tr>
                                                     <td><br />
                                                     <b><font color="#0B610B">构建信息</font></b>
                                                     <hr size="2" width="100%" align="center" /></td>
                                                 </tr>
                                                 <tr>
                                                     <td>
                                                         <ul>
                                                             <li>项目名称 ： ${PROJECT_NAME}</li>
                                                             <li>构建编号 ： 第${BUILD_NUMBER}次构建</li>
                                                             <li>触发原因： ${CAUSE}</li>
                                                             <li>构建状态： ${BUILD_STATUS}</li>
                                                             <li>构建日志： <a href="${BUILD_URL}console">${BUILD_URL}console</a></li>
                                                             <li>构建  Url ： <a href="${BUILD_URL}">${BUILD_URL}</a></li>
                                                             <li>工作目录 ： <a href="${PROJECT_URL}ws">${PROJECT_URL}ws</a></li>
                                                             <li>项目  Url ： <a href="${PROJECT_URL}">${PROJECT_URL}</a></li>
                                                         </ul>


                                         <h4><font color="#0B610B">最近提交</font></h4>
                                         <ul>
                                         <hr size="2" width="100%" />
                                         ${CHANGES_SINCE_LAST_SUCCESS, reverse=true, format="%c", changesFormat="<li>%d [%a] %m</li>"}
                                         </ul>
                                         详细提交: <a href="${PROJECT_URL}changes">${PROJECT_URL}changes</a><br/>

                                                     </td>
                                                 </tr>
                                             </table>
                                         </body>
                                         </html>''',

                to: "sicheng_zhou@qq.com,2941176308@qq.com,2686028645@qq.com,860834338@qq.com,1123671761@qq.com",
            )
        }
    }
}
