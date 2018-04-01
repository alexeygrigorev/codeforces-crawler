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

If you don't have a mysql client, you can do it from inside the container:

    docker exec -i mysql mysql \
        -u"$MYSQL_USER" \
        -p"$MYSQL_PASSWORD" \
        $MYSQL_DATABASE \
        < db/schema.sql

Now run the app.

First add the tasks:

    docker run -it --rm \
        -e MYSQL_HOST="$MYSQL_HOST" \
        -e MYSQL_DATABASE="$MYSQL_DATABASE" \
        -e MYSQL_USER="$MYSQL_USER" \
        -e MYSQL_PASSWORD="$MYSQL_PASSWORD" \
        codeforces-scraper get-tasks

Next, let it scrape:

    docker run -it --rm \
        --name codeforces-scraper-0 \
        -e MYSQL_HOST="$MYSQL_HOST" \
        -e MYSQL_DATABASE="$MYSQL_DATABASE" \
        -e MYSQL_USER="$MYSQL_USER" \
        -e MYSQL_PASSWORD="$MYSQL_PASSWORD" \
        -e NUM_EXECUTORS="3" \
        codeforces-scraper scrape


    docker run -it --rm \
        --name codeforces-scraper-0 \
        -e MYSQL_HOST="$MYSQL_HOST" \
        -e MYSQL_DATABASE="$MYSQL_DATABASE" \
        -e MYSQL_USER="$MYSQL_USER" \
        -e MYSQL_PASSWORD="$MYSQL_PASSWORD" \
        -e NUM_EXECUTORS="3" \
        --entrypoint="/bin/bash" \
        codeforces-scraper

You can see the logs:

    docker logs codeforces-scraper-0 -f


## Dependencies

It already has `geckodriver` in `bin`. It was downloaded this way:

    # from https://github.com/mozilla/geckodriver/releases
    wget https://github.com/mozilla/geckodriver/releases/download/v0.20.0/geckodriver-v0.20.0-linux64.tar.gz
    tar xzf geckodriver-v0.20.0-linux64.tar.gz
    rm geckodriver-v0.20.0-linux64.tar.gz
    mv geckodriver bin/
