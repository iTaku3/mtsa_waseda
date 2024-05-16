# For Developer

## Build with Docker

### 1. Move to source dir

```shell
cd maven-root/mtsa
```

### 2. Docker image build

```shell
docker compose build
```

### 3. Create Docker container

```shell
docker compose up --no-start
```

### 4. Copy built files from container to host

```shell
docker compose cp mtsa:/usr/local/src/mtsa/target ./target
```

### Operate with Make

current dir: `maven-root/mtsa`

### Build

```shell
make build
```

### Run

```shell
make run
```

### Debug with Intellij (Remote JVM Debug)

```shell
make debug
```
