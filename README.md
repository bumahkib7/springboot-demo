# Runix Spring Boot Demo

A Spring Boot app demonstrating PostgreSQL, Kafka, and S3 (MinIO) integration — deployed on [Runix](https://runixcloud.dev) with zero configuration.

## Features

- **Notes** — CRUD with PostgreSQL persistence + S3 file attachments
- **Kafka** — Publish/consume messages with a live message log
- **S3 Browser** — Upload, list, and delete objects in MinIO
- **Dashboard** — Real-time service health and stats

## Environment Variables

| Variable | Description |
|---|---|
| `DATABASE_URL` | PostgreSQL JDBC URL |
| `DATABASE_USER` | PostgreSQL username |
| `DATABASE_PASSWORD` | PostgreSQL password |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka broker address |
| `S3_ENDPOINT` | MinIO/S3 endpoint URL |
| `S3_ACCESS_KEY` | S3 access key |
| `S3_SECRET_KEY` | S3 secret key |
| `S3_BUCKET` | S3 bucket name |
| `PORT` | Server port (default: 8080) |

## Run Locally

```bash
mvn spring-boot:run
```

## Deploy on Runix

Push to GitHub, connect the repo in Runix, and deploy. Runix auto-detects Spring Boot, generates a Dockerfile, and provisions all services.
