#!'groovy'
import groovy.transform.Field
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType

@Field
String jdk = 'openjdk-17'
@Field
String idea = '2023.2'
@Field
String ideaSHA256 = 'b1a5c267ca86850764b0541bee0c27af7d2082e55516e95a0c8d30539571735c'
@Field
String databaseMysql57 = '5.7.40'
@Field
String databaseMysql80 = '8.0.36'
@Field
String databaseMysql84 = '8.4.0'
@Field
String databasePostgresql = '15.6'

String projectName = env.JOB_NAME.substring(0, env.JOB_NAME.indexOf("/")) // depends on name and location of multibranch pipeline in jenkins
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
					sh "docker logs " + c.id + " &> apache.log"
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
		}
	}

	parallelBranches["Github"] =
	{
		//noinspection GroovyAssignabilityCheck
		nodeCheckoutAndDelete
		{
			def mainImage = docker.build(
				imageName('Github'),
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
		}
	}

	parallelBranches["Idea"] = {
		nodeCheckoutAndDelete {
			def ideaImage = docker.build(
				imageName('Idea'),
				'--build-arg JDK=' + jdk + ' ' +
				'--build-arg IDEA=' + idea + ' ' +
				'--build-arg IDEA_SHA256=' + ideaSHA256 + ' ' +
				'conf/idea')
			ideaImage.inside(dockerRunDefaults()) {
				shSilent "/opt/idea/bin/inspect.sh " + env.WORKSPACE + " 'Project Default' idea-inspection-output"
			}
			archiveArtifacts 'idea-inspection-output/**'
			// replace project dir to prevent UnsupportedOperationException - will not be exposed in artifacts
			shSilent "find idea-inspection-output -name '*.xml' | " +
			         "xargs --no-run-if-empty sed --in-place -- 's=\\\$PROJECT_DIR\\\$=" + env.WORKSPACE + "=g'"
			recordIssues(
				*: recordIssuesDefaults,
				tools: [ideaInspection(pattern: 'idea-inspection-output/**')],
			)
		}
	}

	if(branchConsidersDatabase("Mysql"))
	{
		branchMysql(parallelBranches, '57', databaseMysql57, 'my57.cnf', '/var/log/mysql',       'mysql-bin')
		branchMysql(parallelBranches, '80', databaseMysql80, 'my80.cnf', '/var/lib/mysql-files', 'binlog')
		branchMysql(parallelBranches, '84', databaseMysql84, 'my80.cnf', '/var/lib/mysql-files', 'binlog')
	}

	if(branchConsidersDatabase("Mysql"))
	parallelBranches["Mysql57CR"] = {
		nodeCheckoutAndDelete { scmResult ->
			String buildTag = makeBuildTag(scmResult)

			sh 'rm -f conf/environment/*.properties'

			envMysql(
				'my57-stampsA',
				'mysql',
				'mysql-bin',
				'cache.stamps=true'
			)
			envMysql(
				'my57-stampsB',
				'mysql',
				'mysql-bin',
				'cache.stamps=false'
			)
			envMysql(
				'my57m-stampsA',
				'mariadb',
				'mysql-bin',
				'cache.stamps=true'
			)
			envMysql(
				'my57m-stampsB',
				'mariadb',
				'mysql-bin',
				'cache.stamps=false'
			)

			def mainImage = mainImage(imageName("Mysql57CR"))
			def dbImage = docker.build(
				imageName('Mysql57CR', 'db'),
				'--build-arg VERSION=' + databaseMysql57 + ' ' +
				'--build-arg CONF=my57.cnf ' +
				'conf/mysql')

			withBridge("Mysql57CR-db") { dbBridge ->
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
						    ' -Druntime.test.withEnv.setup.mysql.sql=conf/setup-mysql.sql' +
						    ' -Ddisable-ansi-colors=true'
					}
					sh "docker logs " + c.id + " &> db-Mysql57CR.log"
					archiveArtifacts 'db-Mysql57CR.log'
				}
			}
			junit(
				allowEmptyResults: false,
				testResults: 'build/testresults/**/*.xml',
				skipPublishingChecks: true
			)
			sh "mv build buildMysql57CR"
			archiveArtifacts(
				'buildMysql57CR/testprotocol.*,' +
				'buildMysql57CR/classes/runtime/src/com/exedio/cope/testprotocol.properties,' +
				'buildMysql57CR/*.log,' +
				'buildMysql57CR/testtmpdir'
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
				'dialect.connection.schema=public\n' +
				'schema.primaryKeyGenerator=sequence\n'
			)
			envPostgresql(
				'pg-sq',
				'schema.primaryKeyGenerator=sequence\n' +
				'dialect.pgcryptoSchema=<disabled>\n'
			)
			envPostgresql(
				'pg-vault',
				'vault=true\n' +
				'vault.default.service=com.exedio.cope.vaultmock.VaultMockService\n' +
				'vault.isAppliedToAllFields=true\n' +
				'schema.primaryKeyGenerator=sequence\n'
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
					sh "docker logs " + c.id + " &> db-Postgresql.log"
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
		def cache = 'jenkins-build-survivor-' + projectName + "-Ivy"
		lockNodeCheckoutAndDelete(cache) {
			mainImage(imageName('Ivy')).inside(
				dockerRunDefaults('bridge') +
				"--mount type=volume,src=" + cache + ",target=/var/jenkins-build-survivor") {
				ant "-buildfile ivy" +
				    " -Divy.user.home=/var/jenkins-build-survivor"
			}
			archiveArtifacts 'ivy/artifacts/report/**'

			assertGitUnchanged()

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

void lockNodeCheckoutAndDelete(String resource, Closure body)
{
	lock(resource) {
		nodeCheckoutAndDelete(body)
	}
}

void nodeCheckoutAndDelete(@ClosureParams(value = SimpleType, options = ["Map<String, String>"]) Closure body)
{
	node('GitCloneExedio && docker') {
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

void ant(String script)
{
	shSilent 'java -jar lib/ant/ant-launcher.jar -noinput ' + script
}

void assertGitUnchanged()
{
	String gitStatus = shStdout "git status --porcelain --untracked-files=normal"
	if (gitStatus != '')
	{
		error 'FAILURE because fetching dependencies produces git diff:\n' + gitStatus
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
		String conf,
		String tmpfsDir,
		String binlogName)
{
	parallelBranches["Mysql" + code] = {
		nodeCheckoutAndDelete { scmResult ->
			String buildTag = makeBuildTag(scmResult)

			sh 'rm -f conf/environment/*.properties'

			envMysql(
				'my' + code,
				'mysql',
				binlogName,
				''
			)
			envMysql(
				'my' + code + '-compress',
				'mysql',
				binlogName,
				'dialect.rowFormat=COMPRESSED\n'
			)
			envMysql(
				'my' + code + '-legacy',
				'mysql',
				binlogName,
				'disableSupport.semicolon=true\n' +
				'disableSupport.nativeDate=true\n'
			)
			envMysql(
				'my' + code + '-nprep',
				'mysql',
				binlogName,
				'disableSupport.preparedStatements=true\n'
			)
			envMysql(
				'my' + code + '-nstmp',
				'mysql',
				binlogName,
				'cache.stamps=false\n'
			)
			envMysql(
				'my' + code + '-nstmp-sq',
				'mysql',
				binlogName,
				'cache.stamps=false\n' +
				'schema.primaryKeyGenerator=sequence\n'
			)
			envMysql(
				'my' + code + '-sq',
				'mysql',
				binlogName,
				'dialect.connection.compress=true\n' +
				'disableSupport.checkConstraint=true\n' +
				'schema.primaryKeyGenerator=sequence\n'
			)
			envMysql(
				'my' + code + '-sqb',
				'mysql',
				binlogName,
				'schema.primaryKeyGenerator=batchedSequence\n'
			)
			envMysql(
				'my' + code + '-unique',
				'mysql',
				binlogName,
				'disableSupport.uniqueViolation=true\n'
			)
			envMysql(
				'my' + code + '-vault',
				'mysql',
				binlogName,
				'vault=true\n' +
				'vault.default.service=com.exedio.cope.vaultmock.VaultMockService\n' +
				'vault.isAppliedToAllFields=true\n' +
				'schema.primaryKeyGenerator=sequence\n'
			)
			envMysql(
				'my' + code + 'm',
				'mariadb',
				binlogName,
				''
			)
			envMysql(
				'my' + code + 'm-legacy',
				'mariadb',
				binlogName,
				'disableSupport.semicolon=true\n' +
				'disableSupport.nativeDate=true\n'
			)
			envMysql(
				'my' + code + 'm-nprep',
				'mariadb',
				binlogName,
				'disableSupport.preparedStatements=true\n'
			)
			envMysql(
				'my' + code + 'm-nstmp',
				'mariadb',
				binlogName,
				'cache.stamps=false\n'
			)
			envMysql(
				'my' + code + 'm-nstmp-sq',
				'mariadb',
				binlogName,
				'cache.stamps=false\n' +
				'schema.primaryKeyGenerator=sequence\n'
			)
			envMysql(
				'my' + code + 'm-sq',
				'mariadb',
				binlogName,
				'dialect.connection.compress=true\n' +
				'disableSupport.checkConstraint=true\n' +
				'schema.primaryKeyGenerator=sequence\n'
			)
			envMysql(
				'my' + code + 'm-vault',
				'mariadb',
				binlogName,
				'vault=true\n' +
				'vault.default.service=com.exedio.cope.vaultmock.VaultMockService\n' +
				'vault.isAppliedToAllFields=true\n' +
				'schema.primaryKeyGenerator=sequence\n'
			)

			def mainImage = mainImage(imageName("Mysql" + code))
			def dbImage = docker.build(
				imageName('Mysql' + code, 'db'),
				'--build-arg VERSION=' + version + ' ' +
				'--build-arg CONF=' + conf + ' ' +
				'conf/mysql')

			withBridge("Mysql" + code + "-db") { dbBridge ->
				dbImage.withRun(
					dockerRunDefaults(dbBridge, 'test-db-host') +
					"--cap-add CHOWN " +
					"--cap-add SETGID " +
					"--cap-add SETUID " +
					"--cap-add DAC_OVERRIDE " +
					"--tmpfs /var/lib/mysql:rw " +
					"--tmpfs " + tmpfsDir + ":rw "
				) { c ->
					mainImage.inside(
						dockerRunDefaults(dbBridge) +
						"--hostname mydockerhostname "
					) {
						ant 'clean testWithEnv' +
						    ' "-Dbuild.tag=' + buildTag + '"' +
						    ' -Dskip.instrument=true' + // already verified in branch Main
						    ' -Druntime.test.withEnv.setup.mysql.url=jdbc:mysql://test-db-host/' +
						    ' -Druntime.test.withEnv.setup.mysql.sql=conf/setup-mysql.sql' +
						    ' -Ddisable-ansi-colors=true'
					}
					sh "docker logs " + c.id + " &> db-Mysql" + code + ".log"
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

def envMysql(String name, String driver, String binlogName, String text)
{
	writeFile(
			file: 'conf/environment/' + name + '.properties',
			text:
					'connection.url=jdbc:' + driver + '://test-db-host/test_db_schema\n' +
					'connection.username=test_db_user\n' +
					'connection.password=test_db_password\n' +
					'x-build.schemasavepoint=' +
							'OK:' +
							' SHOW MASTER STATUS' +
							/ Gtid=\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}:\\d+-\\d+/ +
							/ / + binlogName + /.\\d{6}:\\d+/ +
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
