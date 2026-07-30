#!'groovy'
import groovy.transform.Field
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

@Field
String projectNamePattern = "^exedio/([a-z]*)/.*" // depends on location of multibranch pipeline in jenkins
@Field
String debianRelease = 'bullseye'
@Field
String debianSnapshot = '20240904'
@Field
String jdk = 'openjdk-17'
@Field
String idea = '2025.3.3-PATCHED'
@Field
String ideaSHA256 = '38a4392fe2117185c3991d9bc8dd83bacdcfa0e1e223ce33c082cbef618d99a9'
@Field
String databaseMysql80 = '8.0.36'
@Field
String databaseMysql84 = '8.4.8'
@Field
String databaseMysql97 = '9.7.0'
@Field
String databasePostgresql = '17.7'

boolean isRelease = env.BRANCH_NAME=="master"

Map<String, ?> recordIssuesDefaults = [
	failOnError         : true,
	enabledForFailure   : true,
	ignoreFailedBuilds  : false,
	skipPublishingChecks: true,
	qualityGates        : [[threshold: 1, type: 'TOTAL', unstable: true]],
]

properties([
		gitLabConnection(env.GITLAB_CONNECTION),
		buildDiscarder(
			logRotator(
				numToKeepStr         : isRelease ? '150' : '30', // 150 corresponds to plot numBuilds
				artifactNumToKeepStr : isRelease ?  '10' :  '2'
		))
])

boolean tryCompleted = false
try
{
	Map<String, Closure<?>> parallelBranches = [:]

	parallelBranches["Main"] = {
		nodeCheckoutAndDelete { scmResult ->
			def buildTag = makeBuildTag(scmResult)

			def mainImage = mainImage(imageName("Main"))
			def dbImage = docker.build(
				imageName('Main', 'apache'),
				'--build-arg DEBIAN_RELEASE=' + debianRelease + ' ' +
				'conf/apache')
			shSilent "mkdir VaultHttpServiceDocumentRoot"
			shSilent "mkdir VaultHttpServiceDocumentRoot/myContent"

			withBridge("Main-db") { dbBridge ->
				dbImage.withRun(
					dockerRunDefaults(dbBridge, 'test-apache-host') +
					"--cap-add SETGID " + // in apache.log get rid of: [unixd:alert] (1)Operation not permitted: AH02156: setgid: unable to set group id to Group 33
					"--cap-add SETUID " + // in apache.log fixes: [unixd:alert] (1)Operation not permitted: AH02162: setuid: unable to change to uid: 33
					"--mount type=bind,src=" + env.WORKSPACE + "/VaultHttpServiceDocumentRoot/myContent,target=/usr/local/apache2/htdocs "
				) { c ->
					mainImage.inside(
						dockerRunDefaults(dbBridge) +
						"--group-add copevaultfilesv1 " + // VaultFileServicePosixGroupTest#testGroupFile
						"--group-add copevaultfilesv2 " + // VaultFileServicePosixGroupTest#testGroupDirectory
						// avoids 20s dns timeout when probes do futilely try to connect
						"--add-host VaultHttpServicePropertiesTest.invalid:0.0.0.0 "
					) {
						ant 'clean jenkins' +
						    ' "-Dbuild.revision=${BUILD_NUMBER}"' +
						    ' "-Dbuild.tag=' + buildTag + '"' +
						    ' -Dbuild.status=' + (isRelease?'release':'integration') +
						    ' -Dinstrument.verify=true' +
						    ' -Ddisable-ansi-colors=true' +
						    ' -Druntime.test.ClusterNetworkTest.skipMulticast=true' +
						    ' -Druntime.test.ClusterNetworkTest.listenInterface=eth0' +
						    ' -Druntime.test.ClusterNetworkTest.port.A=' + port(0) +
						    ' -Druntime.test.ClusterNetworkTest.port.B=' + port(1) +
						    ' -Druntime.test.ClusterNetworkTest.port.C=' + port(2) +
						    ' -Druntime.test.VaultHttpServiceTest.url=http://test-apache-host' +
						    ' -Druntime.test.VaultHttpServiceTest.dir=VaultHttpServiceDocumentRoot'
					}
					sh "docker logs " + c.id + " > apache.log 2>&1"
					archiveArtifacts 'apache.log'
				}
			}

			recordIssues(
				*: recordIssuesDefaults,
				tools: [
					java(),
				],
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

			if (isRelease || env.BRANCH_NAME.contains("archiveSuccessArtifacts"))
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
			assertGitUnchanged()
		}
	}

	parallelBranches["Network"] = {
		nodeCheckoutAndDelete {
			def mainImage = mainImage(imageName("Network"))
			mainImage.inside(
				"--cap-drop all " +
				"--security-opt no-new-privileges " +
				"--dns-opt timeout:1 " + // seconds; default is 5
				"--dns-opt attempts:1 " // default is 2
			) {
				ant 'clean runtime.test.withoutEnv' +
				    ' -Dskip.instrument=true' + // already verified in branch Main
				    ' -Dgithub=true' +
				    ' -Ddisable-ansi-colors=true' +
				    ' -Druntime.test.ClusterNetworkTest.multicast=' + multicastAddress() +
				    ' -Druntime.test.ClusterNetworkTest.listenInterface=eth0' +
				    ' -Druntime.test.ClusterNetworkTest.port.A=' + port(5) +
				    ' -Druntime.test.ClusterNetworkTest.port.B=' + port(6) +
				    ' -Druntime.test.ClusterNetworkTest.port.C=' + port(7)
			}
			junit(
				allowEmptyResults: false,
				testResults: 'build/testresults/**/*.xml',
				skipPublishingChecks: true
			)
			assertGitUnchanged()
		}
	}

	parallelBranches["Github"] = {
		nodeCheckoutAndDelete {
			def mainImage = docker.build(
				imageName('Github'),
				'--build-arg DEBIAN_RELEASE=' + debianRelease + ' ' +
				'--build-arg DEBIAN_SNAPSHOT=' + debianSnapshot + ' ' +
				'--build-arg JDK=' + jdk + ' ' +
				'conf/github')

			withBridge("Github-db") { bridge ->
				mainImage.inside(dockerRunDefaults(bridge))
				{
					// corresponds to .github/workflows/ant.yml
					ant 'clean jenkins' +
					    ' -Dgithub=true' +
					    ' -Druntime.test.ClusterNetworkTest.skipMulticast=true' +
					    ' -Druntime.test.ClusterNetworkTest.listenInterface=eth0'
				}
			}

			junit(
				allowEmptyResults: false,
				testResults: 'build/testresults/**/*.xml',
				skipPublishingChecks: true
			)
			assertGitUnchanged()
		}
	}

	parallelBranches["Forensic"] = {
		nodeCheckoutAndDelete {
			discoverGitReferenceBuild()
			gitDiffStat()
			if (isRelease || env.BRANCH_NAME.contains("forensic")) mineRepository()

			recordIssues(
				*: recordIssuesDefaults,
				qualityGates: [[threshold: 1, type: 'TOTAL_HIGH', unstable: true]],
				tools: [
					taskScanner(
						excludePattern:
							'.git/**,lib/**,' +
							// binary file types
							'**/*.jar,**/*.zip,**/*.tgz,**/*.jpg,**/*.jpeg,**/*.gif,**/*.png,**/*.tif,**/*.webp,**/*.pdf,**/*.eot,**/*.ttf,**/*.woff,**/*.woff2,**/keystore,**/*.ico,**/*.xls,**/*.kdbx,**/*.bcmap,**/*.dat,**/*.cur,**/*.otf,**/*.zargo,**/*.gz',
						// causes build to become unstable, concatenation prevents matching this line
						highTags: 'FIX' + 'ME',
						// does not cause build to become unstable
						normalTags: 'TODO',
						ignoreCase: true),
				],
			)
			assertGitUnchanged()
		}
	}

	parallelBranches["Idea"] = {
		nodeCheckoutAndDelete {
			def ideaImage = docker.build(
				imageName('Idea'),
				'--build-arg DEBIAN_RELEASE=' + debianRelease + ' ' +
				'--build-arg DEBIAN_SNAPSHOT=' + debianSnapshot + ' ' +
				'--build-arg JDK=' + jdk + ' ' +
				'--build-arg IDEA=' + idea + ' ' +
				'--build-arg IDEA_SHA256=' + ideaSHA256 + ' ' +
				'conf/idea')
			ideaImage.inside(dockerRunDefaults()) {
				shSilent "/opt/idea/bin/inspect.sh " + env.WORKSPACE + " 'Project Default' idea-inspection-output"
				shSilent 'cp /tmp/idea/.cache/JetBrains/*/log/idea.log .'
			}
			archiveArtifacts 'idea-inspection-output/**,idea.log'
			shSilent "rm idea-inspection-output/GrazieInspection.xml" // Grammar
			shSilent "rm idea-inspection-output/GrazieStyle.xml"
			shSilent "rm idea-inspection-output/SpellCheckingInspection.xml"
			// replace project dir to prevent UnsupportedOperationException - will not be exposed in artifacts
			shSilent "find idea-inspection-output -name '*.xml' | " +
			         "xargs --no-run-if-empty sed --in-place -- 's=\\\$PROJECT_DIR\\\$=" + env.WORKSPACE + "=g'"
			recordIssues(
				*: recordIssuesDefaults,
				tools: [ideaInspection(pattern: 'idea-inspection-output/**')],
			)
			assertGitUnchanged()
		}
	}

	if(branchConsidersDatabase("Mysql"))
	{
		branchMysql(parallelBranches, '80', databaseMysql80, 'MASTER'    )
		branchMysql(parallelBranches, '84', databaseMysql84, 'BINARY LOG')
		branchMysql(parallelBranches, '97', databaseMysql97, 'BINARY LOG')
	}

	if(branchConsidersDatabase("Mysql"))
	parallelBranches["Mysql80CR"] = {
		nodeCheckoutAndDelete { scmResult ->
			String buildTag = makeBuildTag(scmResult)

			sh 'rm -f conf/environment/*.properties'

			envMysql(
				'my80-stampsA',
				'mysql',
				'MASTER',
				'cache.stamps=true'
			)
			envMysql(
				'my80-stampsB',
				'mysql',
				'MASTER',
				'cache.stamps=false'
			)
			envMysql(
				'my80m-stampsA',
				'mariadb',
				'MASTER',
				'cache.stamps=true'
			)
			envMysql(
				'my80m-stampsB',
				'mariadb',
				'MASTER',
				'cache.stamps=false'
			)

			def mainImage = mainImage(imageName("Mysql80CR"))
			def dbImage = docker.build(
				imageName('Mysql80CR', 'db'),
				'--build-arg VERSION=' + databaseMysql80 + ' ' +
				'conf/mysql')

			withBridge("Mysql80CR-db") { dbBridge ->
				dbImage.withRun(
					dockerRunDefaults(dbBridge, 'test-db-host') +
					"--cap-add CHOWN " +
					"--cap-add SETGID " +
					"--cap-add SETUID " +
					"--cap-add DAC_OVERRIDE " +
					"--tmpfs /var/lib/mysql:rw " +
					"--tmpfs /var/log/mysql:rw "
				) { c ->
					mainImage.inside(
						dockerRunDefaults(dbBridge) +
						"--hostname mydockerhostname "
					) {
						ant 'clean testWithEnv' +
						    ' "-Dbuild.tag=' + buildTag + '"' +
						    ' -Dskip.instrument=true' + // already verified in branch Main
						    ' -Druntime.test.withEnv=com.exedio.cope.CacheReadPoisoningBruteForceTest' +
						    ' -Druntime.test.withEnv.setup.mysql.url=jdbc:mysql://test-db-host/' +
						    ' -Ddisable-ansi-colors=true'
					}
					sh "docker logs " + c.id + " > db-Mysql80CR.log 2>&1"
					archiveArtifacts 'db-Mysql80CR.log'
				}
			}
			junit(
				allowEmptyResults: false,
				testResults: 'build/testresults/**/*.xml',
				skipPublishingChecks: true
			)
			sh "mv build buildMysql80CR"
			archiveArtifacts(
				'buildMysql80CR/testprotocol.*,' +
				'buildMysql80CR/classes/runtime/src/com/exedio/cope/testprotocol.properties,' +
				'buildMysql80CR/*.log,' +
				'buildMysql80CR/testtmpdir'
			)
		}
	}

	if(branchConsidersDatabase("Postgresql"))
	parallelBranches["Postgresql"] = {
		nodeCheckoutAndDelete { scmResult ->
			String buildTag = makeBuildTag(scmResult)

			sh 'rm -f conf/environment/*.properties'

			envPostgresql(
				'pg',
				''
			)
			envPostgresql(
				'pg-mysql',
				'disableSupport.semicolon=true\n' +
				'disableSupport.nativeDate=true\n'
			)
			envPostgresql(
				'pg-nprep',
				'disableSupport.preparedStatements=true\n'
			)
			envPostgresql(
				'pg-nstmp',
				'cache.stamps=false\n'
			)
			envPostgresql(
				'pg-public',
				'connection.username=test_db_user_public\n' +
				'connection.password=test_db_password_public\n' +
				'dialect.connection.schema=public\n'
			)
			envPostgresql(
				'pg-mem',
				'schema.primaryKeyGenerator=memory\n' +
				'dialect.pgcryptoSchema=<disabled>\n'
			)
			envPostgresql(
				'pg-vault',
				'vault=true\n' +
				'vault.default.service=com.exedio.cope.vaultmock.VaultMockService\n' +
				'vault.isAppliedToAllFields=true\n'
			)

			def mainImage = mainImage(imageName("Postgresql"))
			def dbImage = docker.build(
				imageName('Postgresql', 'db'),
				'--build-arg VERSION=' + databasePostgresql + ' ' +
				'conf/postgresql')

			withBridge("Postgresql-db") { dbBridge ->
				dbImage.withRun(
					dockerRunDefaults(dbBridge, 'test-db-host') +
					"--cap-add CHOWN " +
					"--cap-add SETGID " +
					"--cap-add SETUID " +
					"--tmpfs /var/lib/postgresql/data:rw "
				) { c ->
					mainImage.inside(
						dockerRunDefaults(dbBridge) +
						"--hostname mydockerhostname "
					) {
						ant 'clean testWithEnv' +
						    ' "-Dbuild.tag=' + buildTag + '"' +
						    ' -Dskip.instrument=true' + // already verified in branch Main
						    ' -Druntime.test.withEnv.setup.postgresql.url=jdbc:postgresql://test-db-host/' +
						    ' -Ddisable-ansi-colors=true'
					}
					sh "docker logs " + c.id + " > db-Postgresql.log 2>&1"
					archiveArtifacts 'db-Postgresql.log'
				}
			}
			junit(
				allowEmptyResults: false,
				testResults: 'build/testresults/**/*.xml',
				skipPublishingChecks: true
			)
			sh "mv build buildPostgresql"
			archiveArtifacts(
				'buildPostgresql/testprotocol.*,' +
				'buildPostgresql/classes/runtime/src/com/exedio/cope/testprotocol.properties,' +
				'buildPostgresql/*.log,' +
				'buildPostgresql/testtmpdir'
			)
		}
	}

	parallelBranches["Ivy"] = {
		def cache = 'jenkins-build-survivor-' + projectName() + "-Ivy"
		lockNodeCheckoutAndDelete(cache, "jenkins-build-survivor") {
			mainImage(imageName('Ivy')).inside(
				dockerRunDefaults('bridge') +
				"--mount type=volume,src=" + cache + ",target=/var/jenkins-build-survivor") {
				ant "-buildfile ivy" +
				    " -Divy.user.home=/var/jenkins-build-survivor",
				    "-Divy.xml.allow-doctype-processing=true -Divy.xml.external-resources=PROHIBIT"
			}
			archiveArtifacts 'ivy/artifacts/report/**'

			// There should be an assertIvyExtends for each <conf name="abc" extends="def" /> in ivy/ivy.xml.
			assertIvyExtends("servlet", "runtime")
			assertIvyExtends("instrument", "runtime")
			assertIvyExtends("test", "runtime")
			assertIvyExtends("test", "servlet")
			assertIvyExtends("hsqldb", "runtime")
			assertIvyExtends("mysql", "runtime")
			assertIvyExtends("mariadb", "runtime")
			assertIvyExtends("postgresql", "runtime")
			assertIvyExtends("testhsqldb",     "test")
			assertIvyExtends("testmysql",      "test")
			assertIvyExtends("testmariadb",    "test")
			assertIvyExtends("testpostgresql", "test")
			assertIvyExtends("testhsqldb",     "hsqldb")
			assertIvyExtends("testmysql",      "mysql")
			assertIvyExtends("testmariadb",    "mariadb")
			assertIvyExtends("testpostgresql", "postgresql")
			assertIvyExtends("ide", "runtime")
			assertIvyExtends("ide", "test")
			assertIvyExtends("ide", "ant")
			assertIvyExtends("ide", "hsqldb")
			assertIvyExtends("ide", "mysql")
			assertIvyExtends("ide", "postgresql")
			assertGitUnchanged()
		}
	}

	parallel parallelBranches

	tryCompleted = true
}
finally
{
	if (!tryCompleted)
		currentBuild.result = 'FAILURE'

	// workaround for Mailer plugin: set result status explicitly to SUCCESS if empty, otherwise no mail will be triggered if a build gets successful after a previous unsuccessful build
	if (currentBuild.result == null)
		currentBuild.result = 'SUCCESS'
	node('email') {
		step(
			[$class                  : 'Mailer',
			 recipients              : emailextrecipients([isRelease ? culprits() : developers(), requestor()]),
			 notifyEveryUnstableBuild: true])
	}
	// https://docs.gitlab.com/ee/api/commits.html#post-the-build-status-to-a-commit
	updateGitlabCommitStatus state: currentBuild.resultIsBetterOrEqualTo("SUCCESS") ? "success" : "failed"
}

// ------------------- LIBRARY ----------------------------
// The code below is meant to be equal across all projects.

String projectName()
{
	String jobName = env.JOB_NAME
	java.util.regex.Matcher m = java.util.regex.Pattern.compile(projectNamePattern).
			matcher(jobName)
	if(!m.matches())
		error "illegal jobName >" + jobName + "<, must match " + projectNamePattern

	String result = m.group(1)
	echo("project name >" + result + "< computed from >" + jobName + "<")
	return result
}

void lockNodeCheckoutAndDelete(String resource, String additionalLabel = '', Closure body)
{
	lock(resource) {
		nodeCheckoutAndDelete(additionalLabel, body)
	}
}

void nodeCheckoutAndDelete(
	String additionalLabel = '',
	@ClosureParams(value = SimpleType, options = ["Map<String, String>"]) Closure body)
{
	node('GitCloneExedio && docker' + (additionalLabel.isEmpty() ? '' : " && " + additionalLabel)) {
		env.JENKINS_OWNER = shStdout("id --user") + ':' + shStdout("id --group")
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

String jobNameAndBuildNumber()
{
	env.JOB_NAME.replace("/", "-").replace(" ", "_") + "-" + env.BUILD_NUMBER
}

void withBridge(String namePart, @ClosureParams(value = SimpleType, options = ["String"]) Closure<?> body)
{
	String name = jobNameAndBuildNumber() + "-" + namePart
	String bridge = shStdout(
		"docker network create " +
		// using hashCode because name of network may become too long for long branch names
		"network" + name.hashCode() + " " +
		"--label JenkinsfileName=" + name + " " +
		"--driver bridge " +
		"--internal")
	try
	{
		body.call(bridge)
	}
	finally
	{
		sh "docker network rm " + bridge
	}
}

def mainImage(String imageName)
{
	return docker.build(
		imageName,
		'--build-arg DEBIAN_RELEASE=' + debianRelease + ' ' +
		'--build-arg DEBIAN_SNAPSHOT=' + debianSnapshot + ' ' +
		'--build-arg JDK=' + jdk + ' ' +
		'--build-arg JENKINS_OWNER=' + env.JENKINS_OWNER + ' ' +
		'conf/main')
}

String imageName(String pipelineBranch, String subImage = '')
{
	String isoToday = new Date().format("yyyyMMdd")
	String name = 'exedio-jenkins:' + jobNameAndBuildNumber() + '-' + pipelineBranch + '-' + isoToday
	if (!subImage.isBlank()) name += '-' + subImage
	return name
}

static String dockerRunDefaults(String network = 'none', String hostname = '')
{
	return "--cap-drop all " +
	       "--security-opt no-new-privileges " +
	       "--network " + network + " " +
	       (hostname != '' ? "--network-alias " + hostname + " " : "") +
	       "--dns-opt timeout:1 " + // seconds; default is 5
	       "--dns-opt attempts:1 " // default is 2
}

String makeBuildTag(Map<String, String> scmResult)
{
	String treeHash = shStdout "git cat-file -p " + scmResult.GIT_COMMIT + " | grep '^tree ' | sed -e 's/^tree //'"
	return 'build ' +
	       env.BRANCH_NAME + ' ' +
	       env.BUILD_NUMBER + ' ' +
	       new Date().format("yyyy-MM-dd") + ' ' +
	       scmResult.GIT_COMMIT + ' ' +
	       treeHash
}

void assertIvyExtends(String extendingConf, String parentConf)
{
	int status = shStatus(
		"LC_ALL=C" +
		" diff --recursive lib/" + parentConf + " lib/" + extendingConf +
		" | grep --invert-match '^Only in lib/" + extendingConf + ": '" +
		" > ivy/artifacts/ivyExtends" + extendingConf + ".txt")
	if (status!=0 && status!=1) // https://www.man7.org/linux/man-pages/man1/diff.1.html
	{
		error 'FAILURE because diff had trouble'
	}
	String result = readFile "ivy/artifacts/ivyExtends" + extendingConf + ".txt"
	if (result != '')
	{
		error 'FAILURE because ivy conf "' + extendingConf + '" does not just add jar-files to "' + parentConf + '":\n' +
		      result
	}
}

void shSilent(String script)
{
	try
	{
		sh script
	}
	catch (Exception ignored)
	{
		currentBuild.result = 'FAILURE'
	}
}

int shStatus(String script)
{
	return (int) sh(script: script, returnStatus: true)
}

String shStdout(String script)
{
	return ((String) sh(script: script, returnStdout: true)).trim()
}

void ant(String script, String jvmargs = '')
{
	shSilent 'java ' + jvmargs + ' -jar lib/ant/ant-launcher.jar -noinput ' + script
}

void assertGitUnchanged()
{
	String gitStatus = shStdout "git -c core.excludesFile=.gitignore-jenkins status --porcelain --untracked-files=normal"
	if (gitStatus != '')
	{
		error 'FAILURE because build produces git diff:\n' + gitStatus
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

def branchConsidersDatabase(String name)
{
	return env.BRANCH_NAME.contains(name) || env.BRANCH_NAME.contains("AllDB") || env.BRANCH_NAME.contains("master")
}

void branchMysql(
		Map<String, Closure<?>> parallelBranches,
		String code,
		String version,
		String binlogSql)
{
	parallelBranches["Mysql" + code] = {
		nodeCheckoutAndDelete { scmResult ->
			String buildTag = makeBuildTag(scmResult)

			sh 'rm -f conf/environment/*.properties'

			envMysql(
				'my' + code,
				'mysql',
				binlogSql,
				''
			)
			envMysql(
				'my' + code + '-compress',
				'mysql',
				binlogSql,
				'dialect.rowFormat=COMPRESSED\n'
			)
			envMysql(
				'my' + code + '-legacy',
				'mysql',
				binlogSql,
				'disableSupport.semicolon=true\n' +
				'disableSupport.nativeDate=true\n' +
				(code!='97' ? 'dialect.legacyHashesAllowed=true\n' : '') +
				'schema.redundantUnq=true\n'
			)
			envMysql(
				'my' + code + '-nprep',
				'mysql',
				binlogSql,
				'disableSupport.preparedStatements=true\n'
			)
			envMysql(
				'my' + code + '-nstmp',
				'mysql',
				binlogSql,
				'cache.stamps=false\n'
			)
			envMysql(
				'my' + code + '-nstmp-mem',
				'mysql',
				binlogSql,
				'cache.stamps=false\n' +
				'schema.primaryKeyGenerator=memory\n'
			)
			envMysql(
				'my' + code + '-mem',
				'mysql',
				binlogSql,
				'dialect.connection.compress=true\n' +
				'disableSupport.checkConstraint=true\n' +
				'schema.primaryKeyGenerator=memory\n'
			)
			envMysql(
				'my' + code + '-sqb',
				'mysql',
				binlogSql,
				'schema.primaryKeyGenerator=batchedSequence\n'
			)
			envMysql(
				'my' + code + '-unique',
				'mysql',
				binlogSql,
				'disableSupport.uniqueViolation=true\n'
			)
			envMysql(
				'my' + code + '-vault',
				'mysql',
				binlogSql,
				'vault=true\n' +
				'vault.default.service=com.exedio.cope.vaultmock.VaultMockService\n' +
				'vault.isAppliedToAllFields=true\n'
			)
			envMysql(
				'my' + code + 'm',
				'mariadb',
				binlogSql,
				''
			)
			envMysql(
				'my' + code + 'm-legacy',
				'mariadb',
				binlogSql,
				'disableSupport.semicolon=true\n' +
				'disableSupport.nativeDate=true\n' +
				(code!='97' ? 'dialect.legacyHashesAllowed=true\n' : '') +
				'schema.redundantUnq=true\n'
			)
			envMysql(
				'my' + code + 'm-nprep',
				'mariadb',
				binlogSql,
				'disableSupport.preparedStatements=true\n'
			)
			envMysql(
				'my' + code + 'm-nstmp',
				'mariadb',
				binlogSql,
				'cache.stamps=false\n'
			)
			envMysql(
				'my' + code + 'm-nstmp-mem',
				'mariadb',
				binlogSql,
				'cache.stamps=false\n' +
				'schema.primaryKeyGenerator=memory\n'
			)
			envMysql(
				'my' + code + 'm-mem',
				'mariadb',
				binlogSql,
				'dialect.connection.compress=true\n' +
				'disableSupport.checkConstraint=true\n' +
				'schema.primaryKeyGenerator=memory\n'
			)
			envMysql(
				'my' + code + 'm-vault',
				'mariadb',
				binlogSql,
				'vault=true\n' +
				'vault.default.service=com.exedio.cope.vaultmock.VaultMockService\n' +
				'vault.isAppliedToAllFields=true\n'
			)

			def mainImage = mainImage(imageName("Mysql" + code))
			def dbImage = docker.build(
				imageName('Mysql' + code, 'db'),
				'--build-arg VERSION=' + version + ' ' +
				'conf/mysql')

			withBridge("Mysql" + code + "-db") { dbBridge ->
				dbImage.withRun(
					dockerRunDefaults(dbBridge, 'test-db-host') +
					"--cap-add CHOWN " +
					"--cap-add SETGID " +
					"--cap-add SETUID " +
					"--cap-add DAC_OVERRIDE " +
					"--tmpfs /var/lib/mysql:rw " +
					"--tmpfs /var/lib/mysql-files:rw "
				) { c ->
					mainImage.inside(
						dockerRunDefaults(dbBridge) +
						"--hostname mydockerhostname "
					) {
						ant 'clean testWithEnv' +
						    ' "-Dbuild.tag=' + buildTag + '"' +
						    ' -Dskip.instrument=true' + // already verified in branch Main
						    ' -Druntime.test.withEnv.setup.mysql.url=jdbc:mysql://test-db-host/' +
						    ' -Ddisable-ansi-colors=true'
					}
					sh "docker logs " + c.id + " > db-Mysql" + code + ".log 2>&1"
					archiveArtifacts 'db-Mysql' + code + '.log'
				}
			}
			junit(
				allowEmptyResults: false,
				testResults: 'build/testresults/**/*.xml',
				skipPublishingChecks: true
			)
			sh "mv build buildMysql" + code
			archiveArtifacts(
				'buildMysql' + code + '/testprotocol.*,' +
				'buildMysql' + code + '/classes/runtime/src/com/exedio/cope/testprotocol.properties,' +
				'buildMysql' + code + '/*.log,' +
				'buildMysql' + code + '/testtmpdir'
			)
		}
	}
}

def envMysql(String name, String driver, String binlogSql, String text)
{
	writeFile(
			file: 'conf/environment/' + name + '.properties',
			text:
					'connection.url=jdbc:' + driver + '://test-db-host/test_db_schema\n' +
					'connection.username=test_db_user\n' +
					'connection.password=test_db_password\n' +
					'x-build.schemasavepoint=' +
							'OK:' +
							' SHOW ' + binlogSql + ' STATUS' +
							/ Gtid=\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}:\\d+-\\d+/ +
							/ binlog.\\d{6}:\\d+/ +
							' doDB=binlogtest' +
							'\n' +
					'x-build.dialect=mysql\n' +
					'x-build.driver=' + driver + '\n' +
					text
		)
}

def envPostgresql(String name, String text)
{
	writeFile(
			file: 'conf/environment/' + name + '.properties',
			text:
					'connection.url=jdbc:postgresql://test-db-host/test_db_database\n' +
					'connection.username=test_db_user\n' +
					'connection.password=test_db_password\n' +
					'dialect.connection.schema=test_db_schema\n' +
					'dialect.pgcryptoSchema=test_db_pgcrypto_schema\n' +
					'x-build.dialect=postgresql\n' +
					'x-build.driver=postgresql\n' +
					text
		)
}
