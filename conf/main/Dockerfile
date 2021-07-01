ARG RELEASE=stretch
FROM debian:${RELEASE}-20210511

ARG JDK
RUN \
	apt-get update && \
	apt-get install --no-install-recommends --assume-yes \
		${JDK}-jdk-headless && \
	rm -rf /var/lib/apt/lists/*

# needed for path to tools.jar when compiling instrumentor
ARG JAVAHOME=/usr/lib/jvm/java-8-openjdk-amd64
ENV JAVA_HOME ${JAVAHOME}

# TODO drop default once there is no JenkinsfileXxx anymore
ARG JENKINS_OWNER=1001:1001
RUN \
	mkdir /var/jenkins-build-survivor && \
	chown ${JENKINS_OWNER} /var/jenkins-build-survivor

LABEL purpose=jenkins
LABEL jdk=${JDK}
