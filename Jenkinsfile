#!'groovy'

def projectName = env.JOB_NAME.substring(0, env.JOB_NAME.indexOf("/")) // depends on name and location of multibranch pipeline in jenkins
def jdk = 'openjdk-8'
def idea = '2022.1'
def ideaSHA256 = '0400e6152fa0173e4e9a514c6398eef8f19150893298658c0b3eb1427e5bcbe5'
def isRelease = env.BRANCH_NAME=="master"
def dockerNamePrefix = env.JOB_NAME.replace("/", "-").replace(" ", "_") + "-" + env.BUILD_NUMBER
def dockerDate = new Date().format("yyyyMMdd")
def ant = 'ant/bin/ant -noinput'

properties([
		gitLabConnection(env.GITLAB_CONNECTION),
		buildDiscarder(logRotator(
				numToKeepStr         : isRelease ?  '500' : '30',
				artifactNumToKeepStr : isRelease ?   '20' :  '2'
		))
])

tryCompleted = false
try
{
	def parallelBranches = [:]

	parallelBranches["Main"] =
	{
		//noinspection GroovyAssignabilityCheck
		nodeCheckoutAndDelete
		{
			scmResult ->
			def buildTag = makeBuildTag(scmResult)

			def dockerName = dockerNamePrefix + "-Main"
			def mainImage = docker.build(
					'exedio-jenkins:' + dockerName + '-' + dockerDate,
					'--build-arg JDK=' + jdk + ' ' +
					'--build-arg JENKINS_OWNER=' + env.JENKINS_OWNER + ' ' +
					'conf/main')
			def apacheImage = docker.build(
					'exedio-jenkins:' + dockerName + '-' + dockerDate + '-apache',
					'conf/apache')
			shSilent "mkdir VaultHttpServiceDocumentRoot"
			shSilent "mkdir VaultHttpServiceDocumentRoot/myContent"

			withBridge(dockerName + "-net")
			{
				bridge ->

				apacheImage.withRun(
						"--name '" + dockerName + "-apache' " +
						"--cap-drop all " +
						"--cap-add SETGID " + // in apache.log get rid of: [unixd:alert] (1)Operation not permitted: AH02156: setgid: unable to set group id to Group 33
						"--cap-add SETUID " + // in apache.log fixes: [unixd:alert] (1)Operation not permitted: AH02162: setuid: unable to change to uid: 33
						"--security-opt no-new-privileges " +
						"--network " + bridge + " " +
						"--network-alias=test_apache_host " +
						"--mount type=bind,src=" + env.WORKSPACE + "/VaultHttpServiceDocumentRoot/myContent,target=/usr/local/apache2/htdocs " +
						"--dns-opt timeout:1 --dns-opt attempts:1") // fail faster
				{ a ->

				mainImage.inside(
						"--name '" + dockerName + "' " +
						"--group-add copevaultfilesv1 " + // VaultFileServicePosixGroupTest#testGroupFile
						"--group-add copevaultfilesv2 " + // VaultFileServicePosixGroupTest#testGroupDirectory
						"--cap-drop all " +
						"--security-opt no-new-privileges " +
						"--network " + bridge)
				{
					shSilent ant + " clean jenkins" +
							' "-Dbuild.revision=${BUILD_NUMBER}"' +
							' "-Dbuild.tag=' + buildTag + '"' +
							' -Dbuild.status=' + (isRelease?'release':'integration') +
							' -Dinstrument.verify=true' +
							' -Ddisable-ansi-colors=true' +
							' -Druntime.test.ClusterNetworkTest.multicast=' + multicastAddress() +
							' -Druntime.test.ClusterNetworkTest.port.A=' + port(0) +
							' -Druntime.test.ClusterNetworkTest.port.B=' + port(1) +
							' -Druntime.test.ClusterNetworkTest.port.C=' + port(2) +
							' -Druntime.test.VaultHttpServiceTest.url=http://test_apache_host' +
							' -Druntime.test.VaultHttpServiceTest.dir=VaultHttpServiceDocumentRoot'
				}
					sh "docker logs " + a.id + " &> apache.log"
					archiveArtifacts 'apache.log'
				}
			}

			recordIssues(
					failOnError: true,
					enabledForFailure: true,
					ignoreFailedBuilds: false,
					qualityGates: [[threshold: 1, type: 'TOTAL', unstable: true]],
					tools: [
						java(),
					],
					skipPublishingChecks: true,
			)
			junit(
					allowEmptyResults: false,
					testResults: 'build/testresults/**/*.xml',
					skipPublishingChecks: true
			)
			archiveArtifacts(
					'build/ThumbnailTest/*,' +
					'build/testprotocol.*,' +
					'build/classes/runtime/src/com/exedio/cope/testprotocol.properties,' +
					'build/*.log,' +
					'tomcat/logs/*,' +
					'build/testtmpdir'
			)
			if(isRelease || env.BRANCH_NAME.contains("archiveSuccessArtifacts"))
				archiveArtifacts fingerprint: true, artifacts: 'build/success/*'
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

	parallelBranches["Idea"] =
	{
		//noinspection GroovyAssignabilityCheck
		nodeCheckoutAndDelete
		{
			recordIssues(
					failOnError: true,
					enabledForFailure: true,
					ignoreFailedBuilds: false,
					qualityGates: [[threshold: 1, type: 'TOTAL_HIGH', unstable: true]],
					tools: [
							taskScanner(
									excludePattern:
											'.git/**,lib/**,ant/**,' +
											'**/*.jar,**/*.zip,**/*.tgz,**/*.jpg,**/*.gif,**/*.png,**/*.tif,**/*.webp,**/*.pdf,**/*.eot,**/*.ttf,**/*.woff,**/*.woff2,**/keystore', // binary file types
									highTags: 'FIX' + 'ME', // causes build to become unstable, concatenation prevents matching this line
									normalTags: 'TODO', // does not cause build to become unstable
									ignoreCase: true),
					],
			)

			shSilent "source conf/replaceToolJar"

			def dockerName = dockerNamePrefix + "-Idea"
			docker.
				build(
					'exedio-jenkins:' + dockerName + '-' + dockerDate,
					'--build-arg JDK=' + jdk + ' ' +
					'--build-arg IDEA=' + idea + ' ' +
					'--build-arg IDEA_SHA256=' + ideaSHA256 + ' ' +
					'conf/idea').
				inside(
					"--name '" + dockerName + "' " +
					"--cap-drop all " +
					"--security-opt no-new-privileges " +
					"--network none")
				{
					shSilent "/opt/idea/bin/inspect.sh " + env.WORKSPACE + " 'Project Default' idea-inspection-output"
				}
			archiveArtifacts 'idea-inspection-output/**'
			// replace project dir to prevent UnsupportedOperationException - will not be exposed in artifacts
			shSilent "find idea-inspection-output -name '*.xml' | xargs --no-run-if-empty sed --in-place -- 's=\\\$PROJECT_DIR\\\$="+env.WORKSPACE+"=g'"
			recordIssues(
					failOnError: true,
					enabledForFailure: true,
					ignoreFailedBuilds: false,
					qualityGates: [[threshold: 1, type: 'TOTAL', unstable: true]],
					tools: [
						ideaInspection(pattern: 'idea-inspection-output/**'),
					],
			)
		}
	}

	parallelBranches["Ivy"] =
	{
		def cache = 'jenkins-build-survivor-' + projectName + "-Ivy"
		//noinspection GroovyAssignabilityCheck
		lockNodeCheckoutAndDelete(cache)
		{
			def dockerName = dockerNamePrefix + "-Ivy"
			def mainImage = docker.build(
					'exedio-jenkins:' + dockerName + '-' + dockerDate,
					'--build-arg JDK=' + jdk + ' ' +
					'--build-arg JENKINS_OWNER=' + env.JENKINS_OWNER + ' ' +
					'conf/main')
			mainImage.inside(
					"--name '" + dockerName + "' " +
					"--cap-drop all " +
					"--security-opt no-new-privileges " +
					"--mount type=volume,src=" + cache + ",target=/var/jenkins-build-survivor")
			{
				shSilent ant +
					" -buildfile ivy" +
					" -Divy.user.home=/var/jenkins-build-survivor"
			}
			archiveArtifacts 'ivy/artifacts/report/**'

			def gitStatus = sh (script: "git status --porcelain --untracked-files=normal", returnStdout: true).trim()
			if(gitStatus!='')
			{
				error 'FAILURE because fetching dependencies produces git diff:\n' + gitStatus
			}
		}
	}

	parallel parallelBranches

	tryCompleted = true
}
finally
{
	if(!tryCompleted)
		currentBuild.result = 'FAILURE'

	node('email')
	{
		step([$class: 'Mailer',
				recipients: emailextrecipients([isRelease ? culprits() : developers(), requestor()]),
				notifyEveryUnstableBuild: true])
	}
	updateGitlabCommitStatus state: currentBuild.resultIsBetterOrEqualTo("SUCCESS") ? "success" : "failed" // https://docs.gitlab.com/ee/api/commits.html#post-the-build-status-to-a-commit
}

def lockNodeCheckoutAndDelete(resource, Closure body)
{
	lock(resource)
	{
		nodeCheckoutAndDelete(body)
	}
}

def nodeCheckoutAndDelete(Closure body)
{
	node('GitCloneExedio && docker')
	{
		env.JENKINS_OWNER =
			sh (script: "id --user",  returnStdout: true).trim() + ':' +
			sh (script: "id --group", returnStdout: true).trim()
		try
		{
			deleteDir()
			def scmResult = checkout scm
			updateGitlabCommitStatus state: 'running'

			body.call(scmResult)
		}
		finally
		{
			deleteDir()
		}
	}
}

def withBridge(name, Closure body)
{
	def bridge = sh ( script:
			"docker network create " +
					name + " " +
					"--driver bridge " +
					"--internal",
			returnStdout: true).trim()
	try
	{
		body.call(bridge)
	}
	finally
	{
		sh "docker network rm " + bridge
	}
}

def makeBuildTag(scmResult)
{
	return 'build ' +
			env.BRANCH_NAME + ' ' +
			env.BUILD_NUMBER + ' ' +
			new Date().format("yyyy-MM-dd") + ' ' +
			scmResult.GIT_COMMIT + ' ' +
			sh (script: "git cat-file -p " + scmResult.GIT_COMMIT + " | grep '^tree ' | sed -e 's/^tree //'", returnStdout: true).trim()
}

def shSilent(script)
{
	try
	{
		sh script
	}
	catch(Exception ignored)
	{
		currentBuild.result = 'FAILURE'
	}
}

def port(int offset)
{
	return 28000 + 10*env.EXECUTOR_NUMBER.toInteger() + offset
}

def multicastAddress()
{
	// Multicast address for Local subnetwork (Not routable, 224.0.0.0 to 224.0.0.255).
	// Not one of the "Notable addresses":
	// https://en.wikipedia.org/wiki/Multicast_address
	return "224.0.0." + (60 + env.EXECUTOR_NUMBER_HOST_OFFSET.toInteger() + env.EXECUTOR_NUMBER.toInteger())
}
