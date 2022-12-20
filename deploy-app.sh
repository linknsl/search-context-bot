mvn clean -f search-flat/pom.xml
mvn package -f search-flat/pom.xml
docker build -t search-flat:latest -f - search-flat < Dockerfile
docker-compose --env-file ../search-context-config/env-vars.sh up -d