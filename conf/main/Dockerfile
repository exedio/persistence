FROM debian:buster-20220418

ARG JDK
RUN \
	apt-get update && \
	apt-get install --no-install-recommends --assume-yes \
		${JDK}-jdk-headless && \
	rm -rf /var/lib/apt/lists/*

# TODO drop default once there is no JenkinsfileXxx anymore
ARG JENKINS_OWNER=1001:1001
RUN \
	mkdir /var/jenkins-build-survivor && \
	chown ${JENKINS_OWNER} /var/jenkins-build-survivor

# VaultFileServicePosixGroupTest requires these groups
RUN addgroup copevaultfilesv1
RUN addgroup copevaultfilesv2

LABEL purpose=jenkins
LABEL jdk=${JDK}
