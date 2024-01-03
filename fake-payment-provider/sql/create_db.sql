CREATE ROLE "fake-payment-provider" WITH SUPERUSER LOGIN PASSWORD 'payment-provider';
CREATE DATABASE "fake-payment-provider" WITH OWNER "payment-provider";