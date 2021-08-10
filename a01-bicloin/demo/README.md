# Guião de Demonstração

## 1. Preparação do sistema

Para testar o sistema e todos os seus componentes, é necessário preparar um ambiente com dados para proceder à verificação dos testes.

### 1.1. Lançar o *registry*

Para lançar o *ZooKeeper*, ir à pasta `zookeeper/bin` e correr o comando  
`./zkServer.sh start` (Linux) ou `zkServer.cmd` (Windows).

É possível também lançar a consola de interação com o *ZooKeeper*, novamente na pasta `zookeeper/bin` e correr `./zkCli.sh` (Linux) ou `zkCli.cmd` (Windows).

### 1.2. Compilar o projeto

Primeiramente, é necessário compilar e instalar todos os módulos e suas dependências --  *rec*, *hub*, *app*, etc.
Para isso, basta ir à pasta *root* do projeto e correr o seguinte comando:

```sh
$ mvn clean install -DskipTests
```

### 1.3. Lançar e testar o *rec*

Para proceder aos testes, é preciso em primeiro lugar lançar o servidor *rec* .
Para isso basta ir à pasta *rec* e executar:

```sh
$ mvn compile exec:java
```

Este comando vai colocar o *rec* no endereço *localhost* e na porta *8091*.

Para confirmar o funcionamento do servidor com um *ping*, fazer:

```sh
$ cd rec-tester
$ mvn compile exec:java
```

Para executar toda a bateria de testes de integração, fazer:

```sh
$ mvn verify
```

Todos os testes devem ser executados sem erros.

### 1.4. Lançar e testar o *hub*

Para proceder aos testes, é preciso em primeiro lugar lançar o servidor *hub* .
Para isso basta ir à pasta *hub* e executar:

```sh
$ mvn compile exec:java
```

Este comando vai colocar o *hub* no endereço *localhost* e na porta *8081*.

Para confirmar o funcionamento do servidor com um *ping*, fazer:

```sh
$ cd hub-tester
$ mvn compile exec:java
```

Para executar toda a bateria de testes de integração, fazer:

```sh
$ mvn verify
```

Todos os testes devem ser executados sem erros.

### 1.5. *App*

Iniciar a aplicação com a utilizadora alice:

```sh
$ app localhost 2181 alice +35191102030 38.7380 -9.3000
```

**Nota:** Para poder correr o script *app* diretamente é necessário fazer `mvn install` e adicionar ao *PATH* ou utilizar diretamente os executáveis gerados na pasta `target/appassembler/bin/`.

Depois de lançar todos os componentes, tal como descrito acima, já temos o que é necessário para usar o sistema através dos comandos.

## 2. Teste dos comandos

Nesta secção vamos correr os comandos necessários para testar todas as operações do sistema.
Cada subsecção é respetiva a cada operação presente no *hub*.

### 2.1. *balance*
```sh
> balance
alice 0 BIC
> balance #username inválido
ERRO user não existe
```

### 2.2 *top-up*
```sh
> top-up 15
alice 150 BIC
> top-up -15 #valor inválido
ERRO valor inválido
```

### 2.3 *tag*
```sh
> tag 38.7376 -9.3031 loc1
OK
> tag 92.345 -9.3031 #coordenadas inválidas
ERRO coordenadas invalidas
```

### 2.4 *move*
```sh
> move 38.7376 -9.3031
alice em https://www.google.com/maps/place/38.7376,-9.3031
> move loc1
alice em https://www.google.com/maps/place/38.7376,-9.3031
> move 92.345 -9.3031 #coordenadas inválidas
ERRO coordenadas invalidas
> move loc2
ERRO tag não existe
```

### 2.5 *at*
```sh
> at 38.7376 -9.3031 loc1
alice em https://www.google.com/maps/place/38.7376,-9.3031
```

### 2.6 *scan*
```sh
> scan 3
istt, lat 38.7372, -9.3023 long, 20 docas, 4 BIC prémio, 12 bicicletas, a 82 metros
stao, lat 38.6867, -9.3124 long, 30 docas, 3 BIC prémio, 20 bicicletas, a 5717 metros
jero, lat 38.6972, -9.2064 long, 30 docas, 3 BIC prémio, 20 bicicletas, a 9517 metros
> scan 0 #valor inválido
ERRO valor inválido
```

### 2.7 *info*
```sh
> info istt
IST Taguspark, lat 38.7372, -9.3023 long, 20 docas, 4 BIC prémio, 12 bicicletas, 22 levantamentos, 7 devoluções, https://www.google.com/maps/place/38.7372,-9.3023
> info abbb #estação não existe
ERRO estação não existe
```

### 2.8 *bike-up*
```sh
> bike-up istt
OK
> bike-up jero #fora do alcance
ERRO fora do alcance
```

### 2.9 *bike-down*
```sh
> bike-down istt
OK
> bike-down jero #fora do alcance
ERRO fora do alcance
```

----

## 3. Replicação e Tolerância a Faltas

Nesta secção vamos preparar o sistema para utilizar várias réplicas do servidor *rec*.

### 3.1 Lançar réplicas _rec_

Para lançar uma nova réplica _rec_ deve executar-se o seguinte comando na pasta _rec_:

```sh
$ mvn exec:java -Drec.i=j # j corresponde ao número da réplica que se quer lançar
```

### 3.2 Falta num servidor *rec*

Para causar uma falta no servidor executar uma das seguintes alternativas:
- Fechar o terminal de um *rec*
- Fazer ```Ctrl+Z``` para pausar a execução de um *rec*

### 3.3 Recuperar um servidor *rec*

Para recuperar um servidor executar uma das seguintes opções:
- Seguir os passos em 3.1
- Fazer ```fg``` para retomar a execução de um *rec*

----

## 4. Considerações Finais

Estes testes não cobrem tudo, pelo que devem ter sempre em conta os testes de integração e o código.
