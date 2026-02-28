#!/bin/bash 

echo "Refreshing schema";
mysql --force -uroot -pabalone10KG fusion < /data/fiab/scripts/dev_db_schema.sql;
mysql --force -uroot -pabalone10KG regdb <  /data/fiab/scripts/reg_db_schema.sql;

