## Build & Run

For using this package you need maven and docker.
Docker is optional, but simplifies running the code.

Building:

    mvn package
    docker build -t codeforces-scraper .

Setting up a redis instance:

    REDIS_CONTAINER_NAME="redis"

    docker run --rm -d \
        --name $REDIS_CONTAINER_NAME \
        redis:3.2.11

    REDIS_HOST=`docker inspect ${REDIS_CONTAINER_NAME} \
       --format='{{.NetworkSettings.IPAddress}}'`

Checking that redis works:

    docker exec -it redis redis-cli

    127.0.0.1:6379> ping
    PONG

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
        codeforces-scraper scrape-tasks

Then add them to the redis queue:

    docker run -it --rm \
        -e MYSQL_HOST="$MYSQL_HOST" \
        -e MYSQL_DATABASE="$MYSQL_DATABASE" \
        -e MYSQL_USER="$MYSQL_USER" \
        -e MYSQL_PASSWORD="$MYSQL_PASSWORD" \
        -e REDIS_HOST="$REDIS_HOST" \
        codeforces-scraper enqueue-tasks


Next, let it scrape:

    docker run -d --rm \
        --name codeforces-scraper-0 \
        -e MYSQL_HOST="$MYSQL_HOST" \
        -e MYSQL_DATABASE="$MYSQL_DATABASE" \
        -e MYSQL_USER="$MYSQL_USER" \
        -e MYSQL_PASSWORD="$MYSQL_PASSWORD" \
        -e REDIS_HOST="$REDIS_HOST" \
        codeforces-scraper scrape-code


Checking logs:

    docker logs codeforces-scraper-0 -f


You can run multiple scrapers:

    for i in 1 2 3 4 5
    do
        docker run -d --rm \
            --name codeforces-scraper-$i \
            -e MYSQL_HOST="$MYSQL_HOST" \
            -e MYSQL_DATABASE="$MYSQL_DATABASE" \
            -e MYSQL_USER="$MYSQL_USER" \
            -e MYSQL_PASSWORD="$MYSQL_PASSWORD" \
            -e REDIS_HOST="$REDIS_HOST" \
            codeforces-scraper scrape-code
    done

Logging from all containers at once:

    for i in 0 1 2 3 4 5
    do
        docker logs -f --tail=30 codeforces-scraper-$i \
            | sed -e 's/^/container-'$i': /' &
    done


## Dependencies

It already has `geckodriver` in `bin`. It was downloaded this way:

    # from https://github.com/mozilla/geckodriver/releases
    wget https://github.com/mozilla/geckodriver/releases/download/v0.20.0/geckodriver-v0.20.0-linux64.tar.gz
    tar xzf geckodriver-v0.20.0-linux64.tar.gz
    rm geckodriver-v0.20.0-linux64.tar.gz
    mv geckodriver bin/
