#!/bin/bash

mongosh --host mongo1:27017 --eval <<EOF
var config = {
    "_id": "crackhash-mongo-set",
    "version": 1,
    "members": [
        {
            "_id": 1,
            "host": "mongo1:27017"
        },
        {
            "_id": 2,
            "host": "mongo2:27017"
        },
        {
            "_id": 3,
            "host": "mongo3:27017"
        }
    ]
};
rs.initiate(config);
EOF