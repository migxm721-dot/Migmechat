#!/bin/bash 
read -p "This will drop your localhost fusion db and reinstate it from a snapshot. Continue? [Y/n]" prompt;
if [ $prompt == "y" ] || [ $prompt == "Y" ]
then
    stty -echo;
    read -p "Enter root password for localhost db:" dbroot;
    stty echo ;
    printf \\n;
    echo "Setting up credentials";
    mysql -uroot -p$dbroot -e "drop user 'fusion'@'%';";
    mysql -uroot -p$dbroot -e "drop user 'fusion'@'localhost';";
    mysql -uroot -p$dbroot -e "drop database fusion;";
    mysql -uroot -p$dbroot -e "drop database ods;";
    mysql -uroot -p$dbroot -e "drop database regdb;";
    mysql -uroot -p$dbroot -e "CREATE USER 'fusion'@'%' IDENTIFIED by 'abalone5KG';";
    mysql -uroot -p$dbroot -e "CREATE USER 'fusion'@'localhost' IDENTIFIED by 'abalone5KG';";
    mysql -uroot -p$dbroot -e "create database fusion;";
    mysql -uroot -p$dbroot -e "GRANT ALL PRIVILEGES on fusion.* to 'fusion'@'%' with GRANT OPTION;";
    mysql -uroot -p$dbroot -e "GRANT ALL PRIVILEGES on fusion.* to 'fusion'@'localhost' with GRANT OPTION;";
    mysql -uroot -p$dbroot -e "create database ods;";
    mysql -uroot -p$dbroot -e "GRANT ALL PRIVILEGES on ods.* to 'fusion'@'%' with GRANT OPTION;";
    mysql -uroot -p$dbroot -e "GRANT ALL PRIVILEGES on ods.* to 'fusion'@'localhost' with GRANT OPTION;";
    mysql -uroot -p$dbroot -e "create database regdb;";
    mysql -uroot -p$dbroot -e "GRANT ALL PRIVILEGES on regdb.* to 'fusion'@'%' with GRANT OPTION;";
    mysql -uroot -p$dbroot -e "GRANT ALL PRIVILEGES on regdb.* to 'fusion'@'localhost' with GRANT OPTION;";
    echo "Importing schema";
    mysql -uroot -p$dbroot fusion < dev_db_schema.sql;
    mysql -uroot -p$dbroot regdb <  reg_db_schema.sql;
    echo "Importing ... some data";
    mysql -uroot -p$dbroot fusion < dev_db_data.sql;
else
    echo "aborting.";
fi;

