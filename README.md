# demoji

Discord のスラッシュコマンドから文字入りカスタム絵文字を作成する bot です。

`/emo` を実行すると入力モーダルが開き、絵文字名、テキスト、文字色、背景色を指定できます。
送信後にフォントを選択すると 128x128 の PNG プレビューが生成され、確認後にサーバーへ絵文字として登録されます。

## Requirements

- Java 21
- Docker / Docker Compose（Docker で起動する場合）
- Discord bot token
- bot を登録する Discord guild ID

## Environment Variables

| Name                                | Required | Description                                                        |
|-------------------------------------|----------|--------------------------------------------------------------------|
| `DISCORD_BOT_TOKEN`                 | Yes      | Discord bot token                                                   |
| `DISCORD_GUILD_ID`                  | Yes      | スラッシュコマンドを登録する guild ID。カンマ区切りで複数指定できます。 |
| `OTEL_EXPORTER_OTLP_TRACES_ENDPOINT` | No       | OpenTelemetry traces の OTLP HTTP endpoint。未設定時はログに出力します。 |
| `MACKEREL_API_KEY`                  | No       | Mackerel OTLP endpoint に送信する場合の API key                    |

`.env.example` をコピーして利用できます。

```shell
cp .env.example .env
```

## Run Locally

```shell
export DISCORD_BOT_TOKEN="your-bot-token"
export DISCORD_GUILD_ID="123456789012345678"

./gradlew run
```

## Run with Docker

```shell
docker build -t demoji .
docker run --rm --env-file .env demoji
```

Docker Compose を使う場合は、`compose.yaml` の `DISCORD_BOT_TOKEN` と `DISCORD_GUILD_ID` に値を設定してから起動します。

```shell
docker compose up --build
```

## Usage

1. Discord で `/emo` を実行します。
2. モーダルに以下を入力します。
   - 名前: 絵文字名（2 文字以上 32 文字以下、コロンなし）
   - テキスト: 絵文字画像に入れる文字列
   - 文字色: `#EC71A1` のような 16 進カラーコード
   - 背景色: `#FFFFFF` のような 16 進カラーコード。`transparent` で透過背景
3. フォントを選択します。
4. プレビューを確認し、`登録` を押すと guild に絵文字が追加されます。

## Fonts

現在は `fonts/NotoSansMonoCJKjp-Bold.otf` を同梱し、フォント選択では `Noto Sans Mono CJK JP Bold` を利用できます。

## Development

```shell
./gradlew test
./gradlew build
```

Java のバージョン管理に mise を使う場合は、`mise.toml` に Java 21 が定義されています。
