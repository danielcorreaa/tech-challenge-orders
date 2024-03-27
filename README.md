
# Microsserviço tech-challenge-orders

Microsserviço responsável pelo gerenciamento de pedidos


## Autores

- [@danielcorreaa](https://github.com/danielcorreaa)


## Stack utilizada


**Back-end:** Java, Spring Boot, Mongodb, Kafka


## Documentação da API

### Criar, Buscar pedidos

#### Checkout do pedido 

```http
  POST orders/api/v1/checkout
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `orderId` | `string` |  Identificador do pedido, se não for informado o sistema vai gerar um código |
| `cpf` | `string` |   Identificador do cliente |
| `products` | `string` | **Obrigatório**  Identificador dos produtos selecionados para o pedido |

#### Buscar pedido 

```http
  GET orders/api/v1/find/{id}
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `orderId` | `string` | **Obrigatório**  identificador do pedido |


#### Buscar todos os pedidos

```http
  GET orders/api/v1
```
| Parâmetro   | Tipo       |  Default|Descrição                                   |
| :---------- | :--------- | :------------------------------------------ |:--- |
| `page`      | `int` | 0| Valor 0 retornara do primeiro registro até o valor  do size|
| `size`      | `int` | 10 |Quantidade de registro que retornaram na resposta|


#### Buscar todos os pedidos ordernados por data de criação, em que o status seja diferente de FINALIZADO

```http
  GET orders/api/v1/sorted
```

## OWASP ZAP
*Realizei ataque na api usando o OWASP Zap, e deu apenas um alerta de nível baixo, fiz a correção, segue links com o antes e depois*

- [@report-before](https://danielcorreaa.github.io/tech-challenge-orders/before/pedido/report.html)


- [@report-after](https://danielcorreaa.github.io/tech-challenge-orders/after/pedido/report.html)

## Relatório RIPD
*RELATÓRIO DE IMPACTO À PROTEÇÃO DE DADOS PESSOAIS*

- [@RIPD](https://danielcorreaa.github.io/tech-challenge-orders/RIPD.pdf)

## Documentação Saga

### Padrão escolhido: Coreografia 

#### Razão de utilizar a coreografia
*Escolhi o padrão coreografado para evitar deixar tudo centralizado no serviço de pedidos, no caso de acontecer alguma falha no serviço de pedidos toda a operação de notificar cliente e enviar os pedidos pagos para a cozinha seria paralizada, com a coreografia mesmo que tenha algum problema com o serviço de pedidos, a cozinha ainda recebe os pedidos com pagamentos aprovados, nao parando a produção de pedidos pagos, e os clientes recebem notificaçao de problemas com o pagamento.*

#### Desenhos

- [@Desenho Padrão Saga coreografado.](https://danielcorreaa.github.io/tech-challenge-orders/images/saga-diagrama.png)


- [@Desenho arquitetura.](https://danielcorreaa.github.io/tech-challenge-orders/images/diagrama-arquitetura.png)



## Rodando localmente

Clone o projeto

```bash
  git clone https://github.com/danielcorreaa/tech-challenge-orders.git
```

Entre no diretório do projeto

```bash
  cd tech-challenge-orders
```

Docker

```bash
  docker compose up -d
```

No navegador

```bash
  http://localhost:8085/
```



## Deploy

### Para subir a aplicação usando kubernetes

#### Infraestrutura:

Clone o projeto com a infraestrutura

```bash
  git clone https://github.com/danielcorreaa/tech-challenge-infra-terraform-kubernetes.git
```
Entre no diretório do projeto

```bash
  cd tech-challenge-infra-terraform-kubernetes/
````

Execute os comandos

```bash   
- run: kubectl apply -f kubernetes/metrics.yaml     
- run: kubectl apply -f kubernetes/mongo/mongo-secrets.yaml 
- run: kubectl apply -f kubernetes/mongo/mongo-configmap.yaml 
- run: kubectl apply -f kubernetes/mongo/mongo-pvc.yaml 
- run: kubectl apply -f kubernetes/mongo/mongo-service.yaml 
- run: kubectl apply -f kubernetes/mongo/mongo-statefulset.yaml

- run: kubectl apply -f kubernetes/kafka/kafka-configmap.yaml
- run: kubectl apply -f kubernetes/kafka/zookeeper-deployment.yaml
- run: kubectl apply -f kubernetes/kafka/zookeeper-service.yaml
- run: kubectl apply -f kubernetes/kafka/kafka-deployment.yaml
- run: kubectl apply -f kubernetes/kafka/kafka-service.yaml
- run: kubectl apply -f kubernetes/kafka/kafka-ui-deployment.yaml

````

#### Aplicação:

docker hub [@repositorio](https://hub.docker.com/r/daniel36/tech-challenge-orders/tags)

Clone o projeto

```bash
  git clone https://github.com/danielcorreaa/tech-challenge-orders.git
```

Entre no diretório do projeto

```bash
  cd tech-challenge-orders
```

Execute os comandos
```bash   
- run: kubectl apply -f k8s/orders-deployment.yaml
- run: kubectl apply -f k8s/orders-service.yaml
- run: kubectl apply -f k8s/orders-hpa.yaml
- run: kubectl get svc

````

Depedências

[@tech-challenge-product](https://github.com/danielcorreaa/tech-challenge-product)

[@tech-challenge-customer](https://github.com/danielcorreaa/tech-challenge-customer)





