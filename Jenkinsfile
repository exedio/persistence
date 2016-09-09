
timestamps
{
	//noinspection GroovyAssignabilityCheck
	node
	{
		try
		{
			abortable
			{
				stage 'Checkout'
				checkout scm
				sh 'git rev-parse HEAD > GIT_COMMIT'
				env.GIT_COMMIT = readFile('GIT_COMMIT').trim()
				sh "git cat-file -p HEAD | grep '^tree ' | sed -e 's/^tree //' > GIT_TREE"
				env.GIT_TREE = readFile('GIT_TREE').trim()

				stage 'Config'
				env.BUILD_TIMESTAMP = new Date().format("yyyy-MM-dd_HH-mm-ss");
				env.JAVA_HOME = "${tool 'jdk 1.8.0_60'}"
				env.PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
				def antHome = tool 'Ant version 1.8.2'

				sh "java -version"
				sh "${antHome}/bin/ant -version"

				def isRelease = env.BRANCH_NAME.toString().equals("master");

				properties([[$class: 'jenkins.model.BuildDiscarderProperty',
						strategy: [
								$class               : 'LogRotator',
								numToKeepStr         : isRelease ? '1000' : '15',
								artifactNumToKeepStr : isRelease ? '1000' :  '2' ]]])

				sh 'echo' +
						' HOSTNAME -${HOSTNAME}-' +
						' EXECUTOR_NUMBER -${EXECUTOR_NUMBER}-' +
						' GIT_COMMIT -${GIT_COMMIT}-' +
						' GIT_TREE -${GIT_TREE}-' +
						' BUILD_TIMESTAMP -${BUILD_TIMESTAMP}-' +
						' BRANCH_NAME -${BRANCH_NAME}-' +
						' BUILD_NUMBER -${BUILD_NUMBER}-' +
						' BUILD_ID -${BUILD_ID}-' +
						' isRelease=' + isRelease

				stage 'Build'
				sh "${antHome}/bin/ant clean jenkins" +
						' "-Dbuild.revision=${BUILD_NUMBER}"' +
						' "-Dbuild.tag=git ${BRANCH_NAME} ${GIT_COMMIT} ${GIT_TREE} jenkins ${BUILD_NUMBER} ${BUILD_TIMESTAMP}"' +
						' -Dinstrument.verify=true' +
						' -Dtomcat.port.shutdown=' + port(0) +
						' -Dtomcat.port.http=' + port(1) +
						' -Druntime.test.ClusterNetworkTest.multicast=' + multicastAddress() +
						' -Druntime.test.ClusterNetworkTest.port.send=' + port(2) +
						' -Druntime.test.ClusterNetworkTest.port.listen=' + port(3) +
						' -Dfindbugs.output=xml'

				stage 'Publish'
				step([$class: 'WarningsPublisher',
						canComputeNew: true,
						canResolveRelativePaths: true,
						consoleParsers: [[parserName: 'Java Compiler (javac)']],
						defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', messagesPattern: '', unHealthy: '',
						unstableTotalAll: '0',
						usePreviousBuildAsReference: false,
						useStableBuildAsReference: false])
				step([$class: 'FindBugsPublisher',
						canComputeNew: true,
						defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '',
						isRankActivated: false,
						pattern: 'build/findbugs.xml',
						unHealthy: '',
						unstableTotalAll: '0',
						usePreviousBuildAsReference: false,
						useStableBuildAsReference: false])
				archive 'build/success/*'
			}
		}
		catch(Exception e)
		{
			//todo handle script returned exit code 143
			throw e;
		}
		finally
		{
			// because junit failure aborts ant
			step([$class: 'JUnitResultArchiver',
					allowEmptyResults: false,
					testResults: 'build/testresults/*.xml'])

			archive 'build/testprotocol.*,build/*.log,tomcat/logs/*,build/testtmpdir'

			def to = emailextrecipients([
					[$class: 'CulpritsRecipientProvider'],
					[$class: 'RequesterRecipientProvider']
			])
			//TODO details
			step([$class: 'Mailer',
					recipients: to,
					attachLog: true,
					notifyEveryUnstableBuild: true])

			if('SUCCESS'.equals(currentBuild.result) ||
				'UNSTABLE'.equals(currentBuild.result))
			{
				echo("Delete working dir after " + currentBuild.result)
				deleteDir()
			}
		}
	}
}

def abortable(Closure body)
{
	try
	{
		body.call();
	}
	catch(hudson.AbortException e)
	{
		if(e.getMessage().contains("exit code 143"))
			return
		throw e;
	}
}

def port(int offset)
{
	return 28000 + 10*env.EXECUTOR_NUMBER.toInteger() + offset
}

def multicastAddress()
{
	String byHost
	if('hudson'.equals(env.HOSTNAME))
		byHost = '230.0.0.'
	else if('hudson1'.equals(env.HOSTNAME))
		byHost = '230.0.1.'
	else
		byHost = 'xxxx'

	return byHost + (1 + env.EXECUTOR_NUMBER.toInteger())
}
