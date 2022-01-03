#!/bin/bash

curl -F "file=@./src/test/resources/test-person-data.csv;type=text/csv" http://127.0.0.1:8080/api/v1/person/csv
