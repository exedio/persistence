ARG DEBIAN_RELEASE
ARG DEBIAN_SNAPSHOT
FROM debian:${DEBIAN_RELEASE}-${DEBIAN_SNAPSHOT}

ARG JDK
RUN <<EOF
set -e
apt-get update
apt-get install --no-install-recommends --assume-yes \
	${JDK}-jdk-headless
rm -rf /var/lib/apt/lists/*
EOF

ARG JENKINS_OWNER
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
