Команды для докера

docker run -d --name tgBankDb --restart always -e POSTGRES_DB=tgBank -e POSTGRES_USER=pg -e POSTGRES_PASSWORD=pg -p 5432:5432  --network tgBank_net postgres

docker run -d --name redis -p 6379:6379 -v redis_data:/data -e REDIS_PASSWORD=my-password -e REDIS_PORT=6379 -e REDIS_DATABASES=16  --network tgBank_net  redis

docker run --name tgBank -p 9200:9200 -e datasource.url='jdbc:postgresql://tgBankDb:5432/tgBank' -e datasource.username='pg' -e datasource.password='pg' -e redis.host='redis' -e redis.port='6379' -e redis.password='my-password' --network tgBank_net signorerome/tg-bank:latest