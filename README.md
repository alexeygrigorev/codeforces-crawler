## Build & Run

For using this package you need maven and docker.
Docker is optional, but simplifies the setup: without docker you
need to install redis and mysql yourself.


### Building

Building:

    mvn package
    docker build -t codeforces-scraper .

### Redis

Setting up a redis instance with `docker`:

    REDIS_CONTAINER_NAME="redis"

    docker run --rm -d \
        --name $REDIS_CONTAINER_NAME \
        redis:3.2.11

    REDIS_HOST=`docker inspect ${REDIS_CONTAINER_NAME} \
       --format='{{.NetworkSettings.IPAddress}}'`

Checking that redis works:

    docker exec redis redis-cli ping


### MySQL

Setting up a mysql instance with `docker`:

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

    docker exec -i mysql mysql \
        -u"$MYSQL_USER" \
        -p"$MYSQL_PASSWORD" \
        $MYSQL_DATABASE \
        < db/schema.sql


### Running

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

Check that the tasks are added to the queue:

    docker exec -it redis redis-cli LLEN tasks

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

    for i in 0 1 2 3 4 5; do
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

    for i in 0 1 2 3 4 5; do
        docker logs -f --tail=30 codeforces-scraper-$i \
            | sed -e 's/^/container-'$i': /' &
    done


### Results

You can check how many submission are already scraped by counting rows in the `submission` table:

    docker exec mysql mysql \
        -u"$MYSQL_USER" \
        -p"$MYSQL_PASSWORD" \
        -e 'select count(1) from submissions' \
        $MYSQL_DATABASE

It is also possible to check how many problems are still to be scraped: 

    docker exec redis redis-cli llen tasks


Once you have scraped enough, you can dump the data:

    docker exec mysql mysqldump \
        -u"$MYSQL_USER" \
        -p"$MYSQL_PASSWORD" \
        $MYSQL_DATABASE \
        | gzip -c \
        > codeforces_dump.sql.gz


## Dependencies

For scraping it uses Selenium via Geckodriver v0.20.0. The `geckodriver` binary is already downloaded and put to the `bin` folder. It was downloaded this way:

    # from https://github.com/mozilla/geckodriver/releases
    wget https://github.com/mozilla/geckodriver/releases/download/v0.20.0/geckodriver-v0.20.0-linux64.tar.gz
    tar xzf geckodriver-v0.20.0-linux64.tar.gz
    rm geckodriver-v0.20.0-linux64.tar.gz
    mv geckodriver bin/ 
