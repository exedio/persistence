
timestamps
{
	//noinspection GroovyAssignabilityCheck
	node('GitCloneExedio && OpenJdk18Debian9')
	{
		try
		{
			abortable
			{
				echo("Delete working dir before build")
				deleteDir()

				def scmResult = checkout scm
				computeGitTree(scmResult)

				env.BUILD_TIMESTAMP = new Date().format("yyyy-MM-dd_HH-mm-ss")
				env.JAVA_HOME = "${tool 'openjdk 1.8 debian9'}"
				env.PATH = "${env.JAVA_HOME}/bin:${env.PATH}"

				def isRelease = env.BRANCH_NAME.toString().equals("master")

				properties([
						buildDiscarder(logRotator(
								numToKeepStr         : isRelease ? '1000' : '30',
								artifactNumToKeepStr : isRelease ? '1000' :  '2'
						))
				])

				sh 'echo' +
						' HOSTNAME -${HOSTNAME}-' +
						' EXECUTOR_NUMBER -${EXECUTOR_NUMBER}-' +
						' scmResult=' + scmResult +
						' BUILD_TIMESTAMP -${BUILD_TIMESTAMP}-' +
						' BRANCH_NAME -${BRANCH_NAME}-' +
						' BUILD_NUMBER -${BUILD_NUMBER}-' +
						' BUILD_ID -${BUILD_ID}-' +
						' isRelease=' + isRelease

				sh "ant/bin/ant clean jenkins" +
						' "-Dbuild.revision=${BUILD_NUMBER}"' +
						' "-Dbuild.tag=git ${BRANCH_NAME} ' + scmResult.GIT_COMMIT + ' ' + scmResult.GIT_TREE + ' jenkins ${BUILD_NUMBER} ${BUILD_TIMESTAMP}"' +
						' -Dbuild.status=' + (isRelease?'release':'integration') +
						' -Dinstrument.verify=true' +
						' -Ddisable-ansi-colors=true' +
						' -Dtomcat.port.shutdown=' + port(0) +
						' -Dtomcat.port.http=' + port(1) +
						' -Druntime.test.ClusterNetworkTest.multicast=' + multicastAddress() +
						' -Druntime.test.ClusterNetworkTest.port.send=' + port(2) +
						' -Druntime.test.ClusterNetworkTest.port.listen=' + port(3) +
						' -Dfindbugs.output=xml'

				recordIssues(
						enabledForFailure: true,
						ignoreFailedBuilds: false,
						qualityGates: [[threshold: 1, type: 'TOTAL', unstable: true]],
						tools: [
							java(),
							spotBugs(pattern: 'build/findbugs.xml', useRankAsPriority: true),
						],
				)
				archiveArtifacts 'build/success/*'
				plot(
						csvFileName: 'plots.csv',
						exclZero: false,
						keepRecords: false,
						group: 'Sizes',
						title: '1: exedio-cope.jar',
						numBuilds: '150',
						style: 'line',
						useDescr: false,
						propertiesSeries: [
							[ file: 'build/exedio-cope.jar-plot.properties',     label: 'exedio-cope.jar' ],
							[ file: 'build/exedio-cope-src.zip-plot.properties', label: 'exedio-cope-src.zip' ],
						],
				)
				plot(
						csvFileName: 'plots-dialect.csv',
						exclZero: false,
						keepRecords: false,
						group: 'Sizes',
						title: '2: exedio-cope-dialect.jar',
						numBuilds: '150',
						style: 'line',
						useDescr: false,
						propertiesSeries: [
							[ file: 'build/exedio-cope-hsqldb.jar-plot.properties',     label: 'exedio-cope-hsqldb.jar' ],
							[ file: 'build/exedio-cope-mysql.jar-plot.properties',      label: 'exedio-cope-mysql.jar' ],
							[ file: 'build/exedio-cope-oracle.jar-plot.properties',     label: 'exedio-cope-oracle.jar' ],
							[ file: 'build/exedio-cope-postgresql.jar-plot.properties', label: 'exedio-cope-postgresql.jar' ],
						],
				)
				plot(
						csvFileName: 'plots-instrument.csv',
						exclZero: false,
						keepRecords: false,
						group: 'Sizes',
						title: '3: exedio-cope-instrument.jar',
						numBuilds: '150',
						style: 'line',
						useDescr: false,
						propertiesSeries: [[
							file: 'build/exedio-cope-instrument.jar-plot.properties',
							label:      'exedio-cope-instrument.jar',
						]],
				)
				plot(
						csvFileName: 'plots-instrument-annotations.csv',
						exclZero: false,
						keepRecords: false,
						group: 'Sizes',
						title: '4: exedio-cope-instrument-annotations.jar',
						numBuilds: '150',
						style: 'line',
						useDescr: false,
						propertiesSeries: [[
							file: 'build/exedio-cope-instrument-annotations.jar-plot.properties',
							label:      'exedio-cope-instrument-annotations.jar',
						]],
				)
				plot(
						csvFileName: 'plots-instrument-completion.csv',
						exclZero: false,
						keepRecords: false,
						group: 'Sizes',
						title: '5: exedio-cope-instrument-completion.jar',
						numBuilds: '150',
						style: 'line',
						useDescr: false,
						propertiesSeries: [[
							file: 'build/exedio-cope-instrument-completion.jar-plot.properties',
							label:      'exedio-cope-instrument-completion.jar',
						]],
				)
			}
		}
		catch(Exception e)
		{
			//todo handle script returned exit code 143
			throw e
		}
		finally
		{
			// because junit failure aborts ant
			junit(
					allowEmptyResults: false,
					testResults: 'build/testresults/**/*.xml',
			)
			archiveArtifacts(
					'build/ThumbnailTest/*,' +
					'build/testprotocol.*,' +
					'build/classes/runtime/src/com/exedio/cope/testprotocol.properties,' +
					'build/*.log,' +
					'tomcat/logs/*,' +
					'build/testtmpdir'
			)
			def to = emailextrecipients([culprits(), requestor()])
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
		body.call()
	}
	catch(hudson.AbortException e)
	{
		if(e.getMessage().contains("exit code 143"))
			return
		throw e
	}
}

def computeGitTree(scmResult)
{
	sh "git cat-file -p " + scmResult.GIT_COMMIT + " | grep '^tree ' | sed -e 's/^tree //' > .git/jenkins-head-tree"
	scmResult.GIT_TREE = readFile('.git/jenkins-head-tree').trim()
}

def port(int offset)
{
	return 28000 + 10*env.EXECUTOR_NUMBER.toInteger() + offset
}

def multicastAddress()
{
	return env.MULTICAST_ADDRESS_PREFIX + (1 + env.EXECUTOR_NUMBER.toInteger())
}
