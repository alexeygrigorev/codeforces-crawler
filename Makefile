build:
	mvn clean package
	docker build -t codeforces-scraper .
