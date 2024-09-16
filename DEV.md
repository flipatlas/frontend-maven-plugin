### Build docker image

docker build --no-cache -t nvm-sandbox:1 -f ./docker/Dockerfile .

### Run docker image shell

docker run -it nvm-sandbox:1

### Run single invoker test

mvn invoker:run -Dinvoker.test=node-version-manager-install-with-nvm

### Run mvn from docker 

docker run -v "$(pwd)":/usr/src/frontend-maven-plugin -w /usr/src/frontend-maven-plugin nvm-sandbox:1  mvn clean install -DskipTests
