# Kafka producer and consumer test task

The producer service requests a short poem from an external LLM API and publishes it to Kafka. The consumer service reads the message and logs the result.

## Run locally

1. Copy `.env.example` to `.env` and set `LLM_API_URL`, `LLM_API_KEY` and `LLM_API_SECRET` for the API you use. Do not commit `.env`.
2. Run `docker compose up --build -d`.
3. Call `curl -X POST http://localhost:8080/send-poem`.
4. View the result with `docker compose logs -f consumer-service`.

The example credentials are placeholders; generating a poem requires valid API credentials.
