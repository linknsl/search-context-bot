call mvn clean -f config-server/pom.xml
call mvn package -f config-server/pom.xml ^
  && docker build -t config-server:latest -f - config-server < Dockerfile
call mvn clean -f search-flat/pom.xml
call mvn package -f search-flat/pom.xml ^
  && docker build -t search-flat:latest -f - search-flat < Dockerfile
exit /B 1