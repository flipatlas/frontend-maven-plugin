FROM maven:3.9.9-amazoncorretto-17-debian-bookworm

SHELL ["/bin/bash", "-o", "pipefail", "-c"]

ENV NVM_DIR /usr/local/nvm
RUN mkdir $NVM_DIR

RUN apt-get update && apt-get install -y curl
RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.1/install.sh | bash

CMD ["/bin/bash"]