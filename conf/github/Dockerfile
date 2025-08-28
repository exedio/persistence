ARG DEBIAN_RELEASE
ARG DEBIAN_SNAPSHOT
FROM debian:${DEBIAN_RELEASE}-${DEBIAN_SNAPSHOT}

# corresponds to .github/workflows/ant.yml
ARG JDK
RUN <<EOF
set -e
apt-get update
apt-get install --no-install-recommends --assume-yes \
	${JDK}-jdk-headless
rm -rf /var/lib/apt/lists/*
EOF
