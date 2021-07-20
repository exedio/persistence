#!'groovy'

def projectName = env.JOB_NAME.substring(0, env.JOB_NAME.indexOf("/")) // depends on name and location of multibranch pipeline in jenkins
def jdk = 'openjdk-8'
def idea = '2020.3.1'
def ideaSHA256 = '06abca33b240b24f447dada437f5ce7387b47644c76378230254d6163882a42a'
def isRelease = env.BRANCH_NAME=="master"
def dockerNamePrefix = env.JOB_NAME.replace("/", "-").replace(" ", "_") + "-" + env.BUILD_NUMBER
def dockerDate = new Date().format("yyyyMMdd")

properties([
		gitLabConnection(env.GITLAB_CONNECTION),
		buildDiscarder(logRotator(
				numToKeepStr         : isRelease ? '1000' : '30',
				artifactNumToKeepStr : isRelease ?   '20' :  '2'
		))
])

try
{
	parallel "Main": { // trailing brace suppresses Syntax error in idea

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
			def bridge = sh ( script:
					"docker network create " +
							dockerName + "-net " +
							"--driver bridge " +
							"--internal",
					returnStdout: true).trim()
			try
			{
				mainImage.inside(
						"--name '" + dockerName + "' " +
						"--cap-drop all " +
						"--security-opt no-new-privileges " +
						"--network " + bridge)
				{
					shSilent "ant/bin/ant -noinput clean jenkins" +
							' "-Dbuild.revision=${BUILD_NUMBER}"' +
							' "-Dbuild.tag=' + buildTag + '"' +
							' -Dbuild.status=' + (isRelease?'release':'integration') +
							' -Dinstrument.verify=true' +
							' -Ddisable-ansi-colors=true' +
							' -Druntime.test.ClusterNetworkTest.multicast=' + multicastAddress() +
							' -Druntime.test.ClusterNetworkTest.port.A=' + port(0) +
							' -Druntime.test.ClusterNetworkTest.port.B=' + port(1) +
							' -Druntime.test.ClusterNetworkTest.port.C=' + port(2)
				}
			}
			finally
			{
				sh "docker network rm " + bridge
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
	},
	"Idea": { // trailing brace suppresses Syntax error in idea

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
			shSilent "rm idea-inspection-output/SpellCheckingInspection.xml"
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
	},
	"Ivy": { // trailing brace suppresses Syntax error in idea

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
				shSilent "ant/bin/ant -noinput" +
					" -buildfile ivy" +
					" -Divy.user.home=/var/jenkins-build-survivor"
			}
			archiveArtifacts 'ivy/artifacts/report/**'

			def gitStatus = sh (script: "git status --porcelain --untracked-files=normal", returnStdout: true).trim()
			if(gitStatus!='')
			{
				echo 'FAILURE because fetching dependencies produces git diff'
				echo gitStatus
				currentBuild.result = 'FAILURE'
			}
		}
	}
}
finally
{
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
