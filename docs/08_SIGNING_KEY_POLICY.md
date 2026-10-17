# 署名鍵の扱い（このリポジトリには入れていません）

このリポジトリは**公開**です。そのため引き継ぎZIP `equipment-ledger-handoff-1.1.0-20261017.zip` に含まれていた次のファイルは、意図的にコミットしていません。

| 除外したもの | 理由 |
| --- | --- |
| `source/equipment-ledger/release-signing.jks` | APK署名用の秘密鍵。漏えいすると第三者が同じ署名のAPKを作れる |
| `source/equipment-ledger/signing.properties` | キーストアのエイリアスとパスワード |
| `originals/equipment-ledger-source-1.1.0.zip` | 上記の鍵とパスワードを含む元ソースZIP |

## 原本の保管

- 原本は引き継ぎZIPにあります。**鍵を失うと既存インストールへ上書き更新できなくなり、端末内の台帳はバックアップ経由でしか移行できません。**
- ZIP をパスワードマネージャーや個人用の非公開ストレージなど、2か所以上にバックアップしてください。
- 証明書 SHA-256（公開して問題ない値）: `0ac8f8044d5ec1774a220898e90dd8b657d0f6e0f05b840bcb871fd20d924c13`
- アプリ内データの暗号鍵（Android Keystore `equipment-v1`）は APK 署名鍵とは別物です。

## 手元で更新APKを作る

`app/build.gradle` は `signing.properties` が無い場合は署名設定を外して読み込むよう変更済みです（公開ソースで Gradle 同期・単体テストができるように）。リリース署名をする時だけ鍵を置きます。

```bash
cd source/equipment-ledger
cp /path/to/handoff/source/equipment-ledger/release-signing.jks .   # .gitignore 済み
cp /path/to/handoff/source/equipment-ledger/signing.properties .    # .gitignore 済み
# TEPRA-Print SDK 1.4.0 を docs/handoff/BUILD_AND_RELEASE.md の場所へ配置
./gradlew testDebugUnitTest assembleRelease
```

Windows は `gradlew.bat testDebugUnitTest assembleRelease`。出力は `app/build/outputs/apk/release/app-release.apk`。
`versionCode` は必ず 110 より大きくし、`releases/equipment-ledger-<version>.apk` としてコミット → Actions の `release-apk` を実行します。

## CI で署名する場合（構想 Phase 1）

GitHub の Settings → Secrets and variables → Actions に次を登録します（値はリポジトリに書かない）。

- `DEVICE_KEYSTORE_B64`：`base64 -w0 release-signing.jks` の出力（Mac は `base64 -i release-signing.jks`、Windows は `certutil -encodehex -f release-signing.jks out.txt 0x40000001`）
- `DEVICE_KEYSTORE_PASSWORD`、`DEVICE_KEY_ALIAS`、`DEVICE_KEY_PASSWORD`
- TEPRA-Print SDK は公開リポジトリに置けないため、非公開リポジトリ＋読み取りトークン等で別経路から取得する（構想 Phase 1）。
