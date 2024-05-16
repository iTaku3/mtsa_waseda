FROM maven:3.9.6 AS development

WORKDIR /usr/local/src/mtsa

RUN apt-get update -y \
    && apt-get install wget -y

COPY . .

ARG DEPENDENCIES_VERSION=v0.0.0-dependencies
RUN wget https://github.com/ITK13201/mtsa/releases/download/PCS_Problem-splittingControllerSynthesis_${DEPENDENCIES_VERSION}/dependencies.tar.gz \
    && tar xzvf ./dependencies.tar.gz \
    && cp -rv ./dependencies/* ./lib \
    && rm -rf ./dependencies \
    && rm -f ./dependencies.tar.gz

RUN mvn clean

# Build with cached dependencies
RUN --mount=type=cache,target=/root/.m2 mvn install -DskipTests=true
