#!'groovy'

def projectName = env.JOB_NAME.substring(0, env.JOB_NAME.indexOf("/")) // depends on name and location of multibranch pipeline in jenkins
def jdk = 'openjdk-11'
def idea = '2023.1'
def ideaSHA256 = 'e6fe45c9df8e763ee3278444b5fb1003910c436752e83221e0303a62c5e81eaa'
def databaseMysql56 = '5.6.33'
def databaseMysql57 = '5.7.37'
def databaseMysql80 = '8.0.28'
def databasePostgresql = '11.12'
def isRelease = env.BRANCH_NAME=="master"
def dockerNamePrefix = env.JOB_NAME.replace("/", "-").replace(" ", "_") + "-" + env.BUILD_NUMBER
def dockerDate = new Date().format("yyyyMMdd")
def ant = 'ant/bin/ant -noinput'

properties([
		gitLabConnection(env.GITLAB_CONNECTION),
		buildDiscarder(logRotator(
				numToKeepStr         : isRelease ? '50' : '30',
				artifactNumToKeepStr : isRelease ? '10' :  '2'
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
						"--network-alias=test-apache-host " +
						"--mount type=bind,src=" + env.WORKSPACE + "/VaultHttpServiceDocumentRoot/myContent,target=/usr/local/apache2/htdocs " +
						"--dns-opt timeout:1 --dns-opt attempts:1") // fail faster
				{ a ->

				mainImage.inside(
						"--name '" + dockerName + "' " +
						"--group-add copevaultfilesv1 " + // VaultFileServicePosixGroupTest#testGroupFile
						"--group-add copevaultfilesv2 " + // VaultFileServicePosixGroupTest#testGroupDirectory
						"--cap-drop all " +
						"--security-opt no-new-privileges " +
						"--network " + bridge + " " +
						// avoids 20s dns timeout when probes do futilely try to connect
						"--add-host VaultHttpServicePropertiesTest.invalid:0.0.0.0 " +
						"--dns-opt timeout:1 --dns-opt attempts:1") // fail faster
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
							' -Druntime.test.VaultHttpServiceTest.url=http://test-apache-host' +
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

	parallelBranches["Github"] =
	{
		//noinspection GroovyAssignabilityCheck
		nodeCheckoutAndDelete
		{
			def dockerName = dockerNamePrefix + "-Github"
			def mainImage = docker.build(
					'exedio-jenkins:' + dockerName + '-' + dockerDate,
					'--build-arg JDK=' + jdk + ' ' +
					'conf/github')

			withBridge(dockerName + "-net")
			{
				bridge ->

				mainImage.inside(
						"--name '" + dockerName + "' " +
						"--cap-drop all " +
						"--security-opt no-new-privileges " +
						"--network " + bridge)
				{
					// corresponds to .github/workflows/ant.yml
					shSilent ant + " -Dgithub=true clean jenkins"
				}
			}

			junit(
					allowEmptyResults: false,
					testResults: 'build/testresults/**/*.xml',
					skipPublishingChecks: true
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

	if(branchConsidersDatabase("Mysql"))
	parallelBranches["Mysql56"] =
	{
		//noinspection GroovyAssignabilityCheck
		nodeCheckoutAndDelete
		{
			scmResult ->
			def buildTag = makeBuildTag(scmResult)

			sh 'rm -f conf/environment/*.properties'

			envMysql56(
					'my56',
					''
			)
			envMysql56(
					'my56-legacy',
					'dialect.longConstraintNames=false\n' +
					'dialect.smallIntegerTypes=false\n' +
					'dialect.utf8mb4=false\n' +
					'disableSupport.nativeDate=true\n'
			)
			envMysql56(
					'my56-nprep',
					'disableSupport.preparedStatements=true\n'
			)
			envMysql56(
					'my56-nprep-legacy',
					'dialect.longConstraintNames=false\n' +
					'dialect.smallIntegerTypes=false\n' +
					'dialect.utf8mb4=false\n' +
					'disableSupport.preparedStatements=true\n'
			)
			envMysql56(
					'my56-nstmp',
					'cache.stamps=false\n'
			)
			envMysql56(
					'my56-nstmp-sq',
					'cache.stamps=false\n' +
					'schema.primaryKeyGenerator=sequence\n'
			)
			envMysql56(
					'my56-sq',
					'schema.primaryKeyGenerator=sequence\n'
			)
			envMysql56(
					'my56-sqb',
					'schema.primaryKeyGenerator=batchedSequence\n'
			)
			envMysql56(
					'my56-unique',
					'disableSupport.uniqueViolation=true\n'
			)

			def dockerName = dockerNamePrefix + "-Mysql56"
			def mainImage = docker.build(
					'exedio-jenkins:' + dockerName + '-' + dockerDate,
					'--build-arg JDK=' + jdk + ' ' +
					'conf/main')
			def dbImage = docker.build(
					'exedio-jenkins:' + dockerName + '-' + dockerDate + '-db',
					'--build-arg VERSION=' + databaseMysql56 + ' ' +
					'--build-arg CONF=my56.cnf ' +
					'conf/mysql')

			withBridge(dockerName + "-net")
			{
				bridge ->

				dbImage.withRun(
						"--name '" + dockerName + "-db' " +
						"--cap-drop all " +
						"--cap-add CHOWN " +
						"--cap-add SETGID " +
						"--cap-add SETUID " +
						"--security-opt no-new-privileges " +
						"--tmpfs /var/lib/mysql:rw " +
						"--network " + bridge + " " +
						"--network-alias=test-db-host " +
						"--dns-opt timeout:1 --dns-opt attempts:1") // fail faster
				{ c ->
					mainImage.inside(
							"--name '" + dockerName + "' " +
							"--cap-drop all " +
							"--security-opt no-new-privileges " +
							"--hostname mydockerhostname " +
							"--network " + bridge + " " +
							"--dns-opt timeout:1 --dns-opt attempts:1") // fail faster
					{
						shSilent ant + " clean testWithEnv" +
								' "-Dbuild.tag=' + buildTag + '"' +
								' -Dskip.instrument=true' + // already verified in branch Main
								' -Druntime.test.withEnv.setup.mysql.url=jdbc:mysql://test-db-host/' +
								' -Druntime.test.withEnv.setup.mysql.sql=conf/setup-mysql56.sql' +
								' -Ddisable-ansi-colors=true'
					}
					sh "docker logs " + c.id + " &> db-Mysql56.log"
					archiveArtifacts 'db-Mysql56.log'
				}
			}
			junit(
					allowEmptyResults: false,
					testResults: 'build/testresults/**/*.xml',
					skipPublishingChecks: true
			)
			sh "mv build buildMysql56"
			archiveArtifacts(
					'buildMysql56/testprotocol.*,' +
					'buildMysql56/classes/runtime/src/com/exedio/cope/testprotocol.properties,' +
					'buildMysql56/*.log,' +
					'buildMysql56/testtmpdir'
			)
		}
	}

	if(branchConsidersDatabase("Mysql"))
	parallelBranches["Mysql57"] =
	{
		//noinspection GroovyAssignabilityCheck
		nodeCheckoutAndDelete
		{
			scmResult ->
			def buildTag = makeBuildTag(scmResult)

			sh 'rm -f conf/environment/*.properties'

			envMysql57(
					'my57',
					'mysql-connector-java',
					''
			)
			envMysql57(
					'my57-compress',
					'mysql-connector-java',
					'dialect.rowFormat=COMPRESSED\n'
			)
			envMysql57(
					'my57-legacy',
					'mysql-connector-java',
					'disableSupport.nativeDate=true\n'
			)
			envMysql57(
					'my57-nprep',
					'mysql-connector-java',
					'disableSupport.preparedStatements=true\n'
			)
			envMysql57(
					'my57-nstmp',
					'mysql-connector-java',
					'cache.stamps=false\n'
			)
			envMysql57(
					'my57-nstmp-sq',
					'mysql-connector-java',
					'cache.stamps=false\n' +
					'schema.primaryKeyGenerator=sequence\n'
			)
			envMysql57(
					'my57-sq',
					'mysql-connector-java',
					'dialect.connection.compress=true\n' +
					'schema.primaryKeyGenerator=sequence\n'
			)
			envMysql57(
					'my57-sqb',
					'mysql-connector-java',
					'schema.primaryKeyGenerator=batchedSequence\n'
			)
			envMysql57(
					'my57-unique',
					'mysql-connector-java',
					'disableSupport.uniqueViolation=true\n'
			)
			envMysql57(
					'my57-vault',
					'mysql-connector-java',
					'vault=true\n' +
					'vault.service=com.exedio.cope.vaultmock.VaultMockService\n' +
					'vault.isAppliedToAllFields=true\n' +
					'schema.primaryKeyGenerator=sequence\n'
			)
			envMysql57(
					'my57m',
					'mariadb-java-client',
					''
			)
			envMysql57(
					'my57m-legacy',
					'mariadb-java-client',
					'disableSupport.nativeDate=true\n'
			)
			envMysql57(
					'my57m-nprep',
					'mariadb-java-client',
					'disableSupport.preparedStatements=true\n'
			)
			envMysql57(
					'my57m-nstmp',
					'mariadb-java-client',
					'cache.stamps=false\n'
			)
			envMysql57(
					'my57m-nstmp-sq',
					'mariadb-java-client',
					'cache.stamps=false\n' +
					'schema.primaryKeyGenerator=sequence\n'
			)
			envMysql57(
					'my57m-sq',
					'mariadb-java-client',
					'dialect.connection.compress=true\n' +
					'schema.primaryKeyGenerator=sequence\n'
			)
			envMysql57(
					'my57m-vault',
					'mariadb-java-client',
					'vault=true\n' +
					'vault.service=com.exedio.cope.vaultmock.VaultMockService\n' +
					'vault.isAppliedToAllFields=true\n' +
					'schema.primaryKeyGenerator=sequence\n'
			)

			def dockerName = dockerNamePrefix + "-Mysql57"
			def mainImage = docker.build(
					'exedio-jenkins:' + dockerName + '-' + dockerDate,
					'--build-arg JDK=' + jdk + ' ' +
					'conf/main')
			def dbImage = docker.build(
					'exedio-jenkins:' + dockerName + '-' + dockerDate + '-db',
					'--build-arg VERSION=' + databaseMysql57 + ' ' +
					'--build-arg CONF=my57.cnf ' +
					'conf/mysql')

			withBridge(dockerName + "-net")
			{
				bridge ->

				dbImage.withRun(
						"--name '" + dockerName + "-db' " +
						"--cap-drop all " +
						"--cap-add CHOWN " +
						"--cap-add SETGID " +
						"--cap-add SETUID " +
						"--security-opt no-new-privileges " +
						"--tmpfs /var/lib/mysql:rw " +
						"--network " + bridge + " " +
						"--network-alias=test-db-host " +
						"--dns-opt timeout:1 --dns-opt attempts:1") // fail faster
				{ c ->
					mainImage.inside(
							"--name '" + dockerName + "' " +
							"--cap-drop all " +
							"--security-opt no-new-privileges " +
							"--hostname mydockerhostname " +
							"--network " + bridge + " " +
							"--dns-opt timeout:1 --dns-opt attempts:1") // fail faster
					{
						shSilent ant + " clean testWithEnv" +
								' "-Dbuild.tag=' + buildTag + '"' +
								' -Dskip.instrument=true' + // already verified in branch Main
								' -Druntime.test.withEnv.setup.mysql.url=jdbc:mysql://test-db-host/' +
								' -Druntime.test.withEnv.setup.mysql.sql=conf/setup-mysql57.sql' +
								' -Ddisable-ansi-colors=true'
					}
					sh "docker logs " + c.id + " &> db-Mysql57.log"
					archiveArtifacts 'db-Mysql57.log'
				}
			}
			junit(
					allowEmptyResults: false,
					testResults: 'build/testresults/**/*.xml',
					skipPublishingChecks: true
			)
			sh "mv build buildMysql57"
			archiveArtifacts(
					'buildMysql57/testprotocol.*,' +
					'buildMysql57/classes/runtime/src/com/exedio/cope/testprotocol.properties,' +
					'buildMysql57/*.log,' +
					'buildMysql57/testtmpdir'
			)
		}
	}

	if(branchConsidersDatabase("Mysql"))
	parallelBranches["Mysql57CR"] =
	{
		//noinspection GroovyAssignabilityCheck
		nodeCheckoutAndDelete
		{
			scmResult ->
			def buildTag = makeBuildTag(scmResult)

			sh 'rm -f conf/environment/*.properties'

			envMysql57(
					'my57-stampsA',
					'mysql-connector-java',
					'cache.stamps=true'
			)
			envMysql57(
					'my57-stampsB',
					'mysql-connector-java',
					'cache.stamps=false'
			)
			envMysql57(
					'my57m-stampsA',
					'mariadb-java-client',
					'cache.stamps=true'
			)
			envMysql57(
					'my57m-stampsB',
					'mariadb-java-client',
					'cache.stamps=false'
			)

			def dockerName = dockerNamePrefix + "-Mysql57CR"
			def mainImage = docker.build(
					'exedio-jenkins:' + dockerName + '-' + dockerDate,
					'--build-arg JDK=' + jdk + ' ' +
					'conf/main')
			def dbImage = docker.build(
					'exedio-jenkins:' + dockerName + '-' + dockerDate + '-db',
					'--build-arg VERSION=' + databaseMysql57 + ' ' +
					'--build-arg CONF=my57.cnf ' +
					'conf/mysql')

			withBridge(dockerName + "-net")
			{
				bridge ->

				dbImage.withRun(
						"--name '" + dockerName + "-db' " +
						"--cap-drop all " +
						"--cap-add CHOWN " +
						"--cap-add SETGID " +
						"--cap-add SETUID " +
						"--security-opt no-new-privileges " +
						"--tmpfs /var/lib/mysql:rw " +
						"--network " + bridge + " " +
						"--network-alias=test-db-host " +
						"--dns-opt timeout:1 --dns-opt attempts:1") // fail faster
				{ c ->
					mainImage.inside(
							"--name '" + dockerName + "' " +
							"--cap-drop all " +
							"--security-opt no-new-privileges " +
							"--hostname mydockerhostname " +
							"--network " + bridge + " " +
							"--dns-opt timeout:1 --dns-opt attempts:1") // fail faster
					{
						shSilent ant + " clean testWithEnv" +
								' "-Dbuild.tag=' + buildTag + '"' +
								' -Dskip.instrument=true' + // already verified in branch Main
								' -Druntime.test.withEnv=com.exedio.cope.CacheReadPoisoningBruteForceTest' +
								' -Druntime.test.withEnv.setup.mysql.url=jdbc:mysql://test-db-host/' +
								' -Druntime.test.withEnv.setup.mysql.sql=conf/setup-mysql57.sql' +
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

	if(branchConsidersDatabase("Mysql"))
	parallelBranches["Mysql80"] =
	{
		//noinspection GroovyAssignabilityCheck
		nodeCheckoutAndDelete
		{
			scmResult ->
			def buildTag = makeBuildTag(scmResult)

			sh 'rm -f conf/environment/*.properties'

			envMysql80(
					'my80',
					'mysql-connector-java',
					''
			)
			envMysql80(
					'my80-compress',
					'mysql-connector-java',
					'dialect.rowFormat=COMPRESSED\n'
			)
			envMysql80(
					'my80-legacy',
					'mysql-connector-java',
					'disableSupport.nativeDate=true\n'
			)
			envMysql80(
					'my80-nprep',
					'mysql-connector-java',
					'disableSupport.preparedStatements=true\n'
			)
			envMysql80(
					'my80-nstmp',
					'mysql-connector-java',
					'cache.stamps=false\n'
			)
			envMysql80(
					'my80-nstmp-sq',
					'mysql-connector-java',
					'cache.stamps=false\n' +
					'schema.primaryKeyGenerator=sequence\n'
			)
			envMysql80(
					'my80-sq',
					'mysql-connector-java',
					'dialect.connection.compress=true\n' +
					'schema.primaryKeyGenerator=sequence\n'
			)
			envMysql80(
					'my80-sqb',
					'mysql-connector-java',
					'schema.primaryKeyGenerator=batchedSequence\n'
			)
			envMysql80(
					'my80-unique',
					'mysql-connector-java',
					'disableSupport.uniqueViolation=true\n'
			)
			envMysql80(
					'my80-vault',
					'mysql-connector-java',
					'vault=true\n' +
					'vault.service=com.exedio.cope.vaultmock.VaultMockService\n' +
					'vault.isAppliedToAllFields=true\n' +
					'schema.primaryKeyGenerator=sequence\n'
			)
			envMysql80(
					'my80m',
					'mariadb-java-client',
					''
			)
			envMysql80(
					'my80m-legacy',
					'mariadb-java-client',
					'disableSupport.nativeDate=true\n'
			)
			envMysql80(
					'my80m-nprep',
					'mariadb-java-client',
					'disableSupport.preparedStatements=true\n'
			)
			envMysql80(
					'my80m-nstmp',
					'mariadb-java-client',
					'cache.stamps=false\n'
			)
			envMysql80(
					'my80m-nstmp-sq',
					'mariadb-java-client',
					'cache.stamps=false\n' +
					'schema.primaryKeyGenerator=sequence\n'
			)
			envMysql80(
					'my80m-sq',
					'mariadb-java-client',
					'dialect.connection.compress=true\n' +
					'schema.primaryKeyGenerator=sequence\n'
			)
			envMysql80(
					'my80m-vault',
					'mariadb-java-client',
					'vault=true\n' +
					'vault.service=com.exedio.cope.vaultmock.VaultMockService\n' +
					'vault.isAppliedToAllFields=true\n' +
					'schema.primaryKeyGenerator=sequence\n'
			)

			def dockerName = dockerNamePrefix + "-Mysql80"
			def mainImage = docker.build(
					'exedio-jenkins:' + dockerName + '-' + dockerDate,
					'--build-arg JDK=' + jdk + ' ' +
					'conf/main')
			def dbImage = docker.build(
					'exedio-jenkins:' + dockerName + '-' + dockerDate + '-db',
					'--build-arg VERSION=' + databaseMysql80 + ' ' +
					'--build-arg CONF=my80.cnf ' +
					'conf/mysql')

			withBridge(dockerName + "-net")
			{
				bridge ->

				dbImage.withRun(
						"--name '" + dockerName + "-db' " +
						"--cap-drop all " +
						"--cap-add CHOWN " +
						"--cap-add SETGID " +
						"--cap-add SETUID " +
						"--security-opt no-new-privileges " +
						"--tmpfs /var/lib/mysql:rw " +
						"--network " + bridge + " " +
						"--network-alias=test-db-host " +
						"--dns-opt timeout:1 --dns-opt attempts:1") // fail faster
				{ c ->
					mainImage.inside(
							"--name '" + dockerName + "' " +
							"--cap-drop all " +
							"--security-opt no-new-privileges " +
							"--hostname mydockerhostname " +
							"--network " + bridge + " " +
							"--dns-opt timeout:1 --dns-opt attempts:1") // fail faster
					{
						shSilent ant + " clean testWithEnv" +
								' "-Dbuild.tag=' + buildTag + '"' +
								' -Dskip.instrument=true' + // already verified in branch Main
								' -Druntime.test.withEnv.setup.mysql.url=jdbc:mysql://test-db-host/' +
								' -Druntime.test.withEnv.setup.mysql.sql=conf/setup-mysql57.sql' +
								' -Ddisable-ansi-colors=true'
					}
					sh "docker logs " + c.id + " &> db-Mysql80.log"
					archiveArtifacts 'db-Mysql80.log'
				}
			}
			junit(
					allowEmptyResults: false,
					testResults: 'build/testresults/**/*.xml',
					skipPublishingChecks: true
			)
			sh "mv build buildMysql80"
			archiveArtifacts(
					'buildMysql80/testprotocol.*,' +
					'buildMysql80/classes/runtime/src/com/exedio/cope/testprotocol.properties,' +
					'buildMysql80/*.log,' +
					'buildMysql80/testtmpdir'
			)
		}
	}

	if(branchConsidersDatabase("Postgresql"))
	parallelBranches["Postgresql"] =
	{
		//noinspection GroovyAssignabilityCheck
		nodeCheckoutAndDelete
		{
			scmResult ->
			def buildTag = makeBuildTag(scmResult)

			sh 'rm -f conf/environment/*.properties'

			envPostgresql(
					'pg11',
					''
			)
			envPostgresql(
					'pg11-mysql',
					'disableSupport.nativeDate=true\n'
			)
			envPostgresql(
					'pg11-nprep',
					'disableSupport.preparedStatements=true\n'
			)
			envPostgresql(
					'pg11-nstmp',
					'cache.stamps=false\n'
			)
			envPostgresql(
					'pg11-public',
					'dialect.connection.schema=public\n' +
					'schema.primaryKeyGenerator=sequence\n'
			)
			envPostgresql(
					'pg11-sq',
					'schema.primaryKeyGenerator=sequence\n' +
					'dialect.pgcryptoSchema=<disabled>\n'
			)
			envPostgresql(
					'pg11-vault',
					'vault=true\n' +
					'vault.service=com.exedio.cope.vaultmock.VaultMockService\n' +
					'vault.isAppliedToAllFields=true\n' +
					'schema.primaryKeyGenerator=sequence\n'
			)

			def dockerName = dockerNamePrefix + "-Postgresql"
			def mainImage = docker.build(
					'exedio-jenkins:' + dockerName + '-' + dockerDate,
					'--build-arg JDK=' + jdk + ' ' +
					'conf/main')
			def dbImage = docker.build(
					'exedio-jenkins:' + dockerName + '-' + dockerDate + '-db',
					'--build-arg VERSION=' + databasePostgresql + ' ' +
					'conf/postgresql')

			withBridge(dockerName + "-net")
			{
				bridge ->

				dbImage.withRun(
						"--name '" + dockerName + "-db' " +
						"--cap-drop all " +
						"--cap-add CHOWN " +
						"--cap-add SETGID " +
						"--cap-add SETUID " +
						"--security-opt no-new-privileges " +
						"--tmpfs /var/lib/postgresql/data:rw " +
						"--network " + bridge + " " +
						"--network-alias=test-db-host " +
						"--dns-opt timeout:1 --dns-opt attempts:1") // fail faster
				{ c ->
					mainImage.inside(
							"--name '" + dockerName + "' " +
							"--cap-drop all " +
							"--security-opt no-new-privileges " +
							"--hostname mydockerhostname " +
							"--network " + bridge + " " +
							"--dns-opt timeout:1 --dns-opt attempts:1") // fail faster
					{
						shSilent ant + " clean testWithEnv" +
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

	// workaround for Mailer plugin: set result status explicitly to SUCCESS if empty, otherwise no mail will be triggered if a build gets successful after a previous unsuccessful build
	if(currentBuild.result==null)
		currentBuild.result = 'SUCCESS'
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

def branchConsidersDatabase(name)
{
	return env.BRANCH_NAME.contains(name) || env.BRANCH_NAME.contains("AllDB") || env.BRANCH_NAME.contains("master");
}

def envMysql56(name, text)
{
	writeFile(
			file: 'conf/environment/' + name + '.properties',
			text:
					'connection.url=jdbc:mysql://test-db-host/test_db_schema\n' +
					'connection.username=test_db_user\n' +
					'connection.password=test_db_password\n' +
					'x-build.schemasavepoint=' +
							'OK:' +
							' SHOW MASTER STATUS' +
							/ Gtid=\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}:\\d+-\\d+/ +
							/ mysql-bin.\\d{6}:\\d+/ +
							' doDB=binlogtest' +
							'\n' +
					'x-build.dialect=mysql\n' +
					'x-build.driver=mysql-connector-java.jar\n' +
					text
		)
}

def envMysql57(name, driver, text)
{
	writeFile(
			file: 'conf/environment/' + name + '.properties',
			text:
					'connection.url=jdbc:' + (driver=='mariadb-java-client' ? 'mariadb' : 'mysql') + '://test-db-host/test_db_schema\n' +
					'connection.username=test_db_user\n' +
					'connection.password=test_db_password\n' +
					'x-build.schemasavepoint=' +
							'OK:' +
							' SHOW MASTER STATUS' +
							/ Gtid=\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}:\\d+-\\d+/ +
							/ mysql-bin.\\d{6}:\\d+/ +
							' doDB=binlogtest' +
							'\n' +
					'x-build.dialect=mysql\n' +
					'x-build.driver=' + driver + '.jar\n' +
					text
		)
}

def envMysql80(name, driver, text)
{
	writeFile(
			file: 'conf/environment/' + name + '.properties',
			text:
					'connection.url=jdbc:' + (driver=='mariadb-java-client' ? 'mariadb' : 'mysql') + '://test-db-host/test_db_schema\n' +
					'connection.username=test_db_user\n' +
					'connection.password=test_db_password\n' +
					'x-build.schemasavepoint=' +
							'OK:' +
							' SHOW MASTER STATUS' +
							/ Gtid=\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}:\\d+-\\d+/ +
							/ binlog.\\d{6}:\\d+/ +
							' doDB=binlogtest' +
							'\n' +
					'x-build.dialect=mysql\n' +
					'x-build.driver=' + driver + '.jar\n' +
					'x-build.mysql80=true\n' +
					text
		)
}

def envPostgresql(name, text)
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
					'x-build.driver=postgresql.jar\n' +
					text
		)
}
