#!/bin/bash 
redis-cli hset R:MASTERS 1 $1:6379
redis-cli hset R:SLAVES 1 $1:6379
redis-cli hset R:MASTERS 2 $1:6379
redis-cli hset R:SLAVES 2 $1:6379
redis-cli lpush R:WEIGHTS 10
redis-cli lpush R:WEIGHTS 10
