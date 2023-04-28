FROM debian:buster-20220418

ARG JDK
RUN <<EOF
set -e
apt-get update
apt-get install --no-install-recommends --assume-yes \
	${JDK}-jdk-headless
rm -rf /var/lib/apt/lists/*
EOF

# TODO drop default once there is no JenkinsfileXxx anymore
ARG JENKINS_OWNER=1001:1001
RUN <<EOF
set -e
mkdir /var/jenkins-build-survivor
chown ${JENKINS_OWNER} /var/jenkins-build-survivor
EOF

# VaultFileServicePosixGroupTest requires these groups
RUN <<EOF
set -e
addgroup copevaultfilesv1
addgroup copevaultfilesv2
EOF

LABEL purpose=jenkins
LABEL jdk=${JDK}
