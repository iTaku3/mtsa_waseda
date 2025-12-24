.PHONY: all clean build run

# デフォルトのターゲット
all: build run

# ビルドとインストール
build:
	mvn clean install -DskipTests

# 実行
run:
	mvn exec:java

# クリーンのみ実行
clean:
	mvn clean

# ヘルプ
help:
	@echo "使用可能なコマンド:"
	@echo "  make        - ビルドして実行"
	@echo "  make build  - mvn clean install -DskipTests を実行"
	@echo "  make run    - mvn exec:java を実行"
	@echo "  make clean  - mvn clean を実行"
