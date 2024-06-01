NAME = audio-to-text
VERSION = $(shell mvn help:evaluate -Dexpression='project.version' -q -DforceStdout)
IMAGE = $(NAME):$(VERSION)

build: docker
package:
	mvn clean package
docker:
	docker build -t $(IMAGE) .
	docker image prune -f
run:
	docker volume create uploads
	docker run --rm --name $(NAME) -p 8080:8080 -v uploads:/app/uploads -it $(IMAGE)
start:
	docker volume create uploads
	docker run --rm --name $(NAME) -p 8080:8080 -v uploads:/app/uploads -dit $(IMAGE)
stop:
	docker stop $(NAME)
sh:
	docker exec -it $(NAME) /bin/sh
log:
	docker logs -f $(NAME) --tail 50