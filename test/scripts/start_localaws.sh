#!/bin/bash

#docker-compose -p localaws up -d
docker-compose up -d

# waits for localstack docker container initialization
sleep 5

# s3
#aws --endpoint-url=http://localhost:4566 s3 mb s3://local-s3-bucket

