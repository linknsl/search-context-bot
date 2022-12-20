mvn clean -f config-server/pom.xml
mvn package -f config-server/pom.xml
docker build -t config-server:latest -f - config-server < Dockerfile
docker-compose -f ./common-service.yml --env-file ../search-context-config/env-vars.sh up -d

mvn clean -f search-flat/pom.xml
mvn package -f search-flat/pom.xml
docker build -t search-flat:latest -f - search-flat < Dockerfile
docker-compose --env-file ../search-context-config/env-vars.sh up -d