# Any ARG before the first FROM can be used in all FROM instruction of the Dockerfile.
# https://docs.docker.com/reference/dockerfile/#understand-how-arg-and-from-interact
ARG DEBIAN_RELEASE
ARG DEBIAN_SNAPSHOT

FROM alpine:3.22.1 AS idea-fetcher
RUN apk --no-cache add curl
ARG IDEA
ARG IDEA_SHA256
RUN <<EOF
set -e
curl \
	--output /tmp/ideaIC.tar.gz \
	https://ivyartifacts.exedio.com/idea/ideaIC-${IDEA}.tar.gz
(echo "${IDEA_SHA256}  /tmp/ideaIC.tar.gz" | sha256sum -c -)
EOF


FROM debian:${DEBIAN_RELEASE}-${DEBIAN_SNAPSHOT}

ARG JDK
RUN <<EOF
set -e
apt-get update
apt-get install --no-install-recommends --assume-yes \
	${JDK}-jdk-headless
rm -rf /var/lib/apt/lists/*
EOF

COPY --from=idea-fetcher /tmp/ideaIC.tar.gz /tmp
RUN <<EOF
set -e
mkdir /opt/idea
tar \
	-xf /tmp/ideaIC.tar.gz \
	--directory /opt/idea \
	--strip-components=1
rm /tmp/ideaIC.tar.gz
EOF

# keeping idea files  out of workspace, where they are scanned by idea inspections
RUN echo "-Duser.home=/tmp/idea" >> /opt/idea/bin/idea64.vmoptions

ARG IDEA
LABEL purpose=jenkins-idea
LABEL jdk=${JDK}
LABEL idea=${IDEA}
LABEL idea.properties=yes
