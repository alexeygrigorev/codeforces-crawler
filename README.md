## Build & Run

For using this package you need maven and docker.
Docker is optional, but simplifies running the code.

Building:

    mvn package
    docker build -t codeforces-scraper .

Setting up mysql instance:

    MYSQL_CONTAINER_NAME=mysql
    MYSQL_DATABASE=code
    MYSQL_USER=code
    MYSQL_PASSWORD=heynottoorough

    docker run -d \
        --name $MYSQL_CONTAINER_NAME \
        -e MYSQL_DATABASE="$MYSQL_DATABASE" \
        -e MYSQL_USER="$MYSQL_USER" \
        -e MYSQL_PASSWORD="$MYSQL_PASSWORD" \
        -p 3306 \
        mysql/mysql-server:5.7


    MYSQL_HOST=`docker inspect ${MYSQL_CONTAINER_NAME} \
       --format='{{.NetworkSettings.IPAddress}}'`

    mysql --host="$MYSQL_HOST" \
        -u"$MYSQL_USER" \
        -p"$MYSQL_PASSWORD" \
        $MYSQL_DATABASE \
        < db/schema.sql


TODO: add parameters for populating the task table

Running




## Dependencies

It already has geckodriver in bin. It was downloaded this way:

    # from https://github.com/mozilla/geckodriver/releases
    wget https://github.com/mozilla/geckodriver/releases/download/v0.20.0/geckodriver-v0.20.0-linux64.tar.gz
    tar xzf geckodriver-v0.20.0-linux64.tar.gz
    rm geckodriver-v0.20.0-linux64.tar.gz
    mv geckodriver bin/
