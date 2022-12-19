# find-flat-telegram-bot
### Телеграм бот по поиску объявлений квартир на сайте крыша кз
Бот создает уведомление на телеграм канал о том что на сайте п
оявилось новое объявление по заданным фильтрам. Поиск происходит через 
извлечение данных с html страницы на сайте https://krisha.kz выставляем 
все необходимые фильтры для поиска квартиры Добавляем в переменную среды 
полученный результат из адресной строки у меня получился такой 

krisha.url: "https://krisha.kz/arenda/kvartiry/almaty/?das[_sys.hasphoto]=1&das[live.furniture][]=1&das[live.furniture][]=2&das[price][from]=100000&das[price][to]=250000&das[who]=1&areas=p43.266981,76.956176,43.265601,76.922187,43.259704,76.902103,43.252176,76.887683,43.249792,76.885108,43.232599,76.884937,43.222557,76.893691,43.219544,76.913776,43.224063,76.946220,43.226197,76.954460,43.235862,76.967849,43.253055,76.976089,43.264472,76.969566,43.268235,76.964588,43.268737,76.959781,43.265977,76.947593,43.267231,76.940555,43.266981,76.956176&zoom=12&lat=43.23684&lon=76.87713"

##### 1.Команды
~~~~
start - старт мониторинга
end - конец мониторинга
alive - проверка что бот продолжает мониторинг
last  - показать последние объявление
~~~~
### Запуск в docker
~~~~
mvn clean package
docker build --tag=search-flat:latest .
docker run -p9000:8080 find-flat-telegram-bot:latest --spring.profiles.active=test
~~~~
### Запуск в docker compose
~~~~
Чтобы удалить все данные о бд нужно выполнить 
docker-compose down --volumes
после
docker-compose up
~~~~
### Развертывание на linux ubuntu 20 (hosting VPS) 
#####Docker установка
~~~~Большая часть информации взята из данной статьи https://totaku.ru/ustanovka-docker-i-docker-compose-na-ubuntu-20-04
sudo apt update
sudo apt install apt-transport-https ca-certificates curl software-properties-common
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu focal stable"
sudo apt update
sudo apt-cache policy docker-ce
sudo apt install docker-ce
sudo usermod -aG docker ${user}
~~~~
#####Docker-compose установка
~~~~
curl -L "https://github.com/docker/compose/releases/download/1.26.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose
docker-compose --version
~~~~
#####java maven установка
~~~~ 
sudo apt install openjdk-8-jdk
sudo apt install maven
~~~~
##### сборка и развертывание
~~~~
mkdir ./app
git clone https://${UserGITHUB}:${TOKENGITHUB}@${URL}
export SPRING_PROFILES_ACTIVE=prod
. ./env-vars.sh

mvn clean -f config-server/pom.xml
mvn package -f config-server/pom.xml
docker build -t config-server:latest -f - config-server < Dockerfile
docker compose -f ./common-service.yml --env-file ../search-context-config/env-vars.sh up -d

mvn clean -f search-flat/pom.xml
mvn package -f search-flat/pom.xml
docker build -t search-flat:latest -f - search-flat < Dockerfile

windows
docker compose --env-file ./env-vars.sh up -d
linux
docker compose --env-file ../search-context-config/env-vars.sh up -d

~~~~
##### Создание Backup postgresql https://stackoverflow.com/questions/24718706/backup-restore-a-dockerized-postgresql-database
docker exec -t db pg_dumpall -c -U ${DATASOURCE_USERNAME} > dump_`date +%d-%m-%Y"_"%H_%M_%S`.sql
##### Восстановление postgresql
cat your_dump.sql | docker exec -i db psql -U postgres

###Swagger для ознакомления с api:
http://**/swagger-ui.html

##### 3. работа с миграциями
Для автоматического  обновления при старте необходимо в файле application.yml
установить   liquibase.enabled: true
Для автоматического обновления
mvn -pl . liquibase:update
mvn -pl . liquibase:rollback -D liquibase.rollbackCount=1


### config-server
Создание сети

docker network create -d bridge superapp

Config server

Собираем с помощью build.config.bat
Запускаем один раз

docker run -dit -p 8888:80 -e "JAVA_OPTS=-Xmx64m" --network superapp --restart=unless-stopped --name config-server config-server:latest --spring.profiles.active=dev
docker run -dit -p 8080:8080 -e "JAVA_OPTS=-Xmx64m" --network superapp --restart=unless-stopped --name search-flat search-flat:latest --spring.profiles.active=dev,db
java -jar D:\design\search-context-bot\search-flat\target\search-flat-1.0.4.jar --spring.profiles.active=dev,db