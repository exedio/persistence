ARG VERSION
FROM postgres:${VERSION}-bookworm

ENV POSTGRES_PASSWORD password-JUST-FOR-SETUP

ARG VERSION
LABEL purpose=jenkins-postgresql
LABEL postgresql=${VERSION}
