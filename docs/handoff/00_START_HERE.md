# 機器台帳アプリ：別スレッドへの引き継ぎ

引き継ぎ作成日：2026-10-17（日本時間）
最新納品版：1.1.0 / versionCode 110 / Android 11以降

このZIPを新しいスレッドへ添付して、以下を伝えてください。

> このZIPの00_START_HERE.mdとdocs/AI_HANDOFF.mdから読み、機器台帳Androidアプリの開発を引き継いでください。ユーザー要望と現在の実装・未実装を区別してください。既存の台帳を維持し、同じアプリIDと署名で更新可能なAPKを作ってください。Gemini APIキーはコードに埋め込まずアプリ内入力の方式を守ってください。次に変更したい内容は、この後に伝えます。

## 同梱内容

- apk/equipment-ledger-1.1.0.apk：最新納品APK（再ビルドしていない原本）
- source/equipment-ledger/：Javaソース、Androidリソース、Gradle Wrapper、設定、テスト、使用説明
- source/equipment-ledger/release-signing.jks：既存アプリの更新用署名秘密鍵
- source/equipment-ledger/signing.properties：署名用設定。値は本文には転載しない
- docs/REQUIREMENTS_AND_CONCEPT.md：ユーザー要望・コンセプト・最終決定
- docs/REQUEST_HISTORY.md：要望の時系列（APIキーの値は省略）
- docs/ARCHITECTURE_AND_DATA.md：構造、データ形式、暗号化、API、印刷
- docs/BUILD_AND_RELEASE.md：開発再開・ビルド・署名・SDK配置手順
- docs/STATUS_AND_NEXT_STEPS.md：履歴、検証範囲、制限、次に確認すること
- docs/AI_HANDOFF.md：次のAI向けの作業開始メモ
- docs/USER_GUIDE.md：最新の使用説明
- originals/equipment-ledger-source-1.1.0.zip：回収した元のソースZIP
- SHA256SUMS.txt / INVENTORY.json：同梱ファイルの整合性と一覧

## 大事な区別

ユーザーから提供されたGemini APIキーは含まない。最終仕様はアプリ内入力・暗号化保存であり、以前の「内包してよい」は撤回済み。

このZIPには**APKの更新に必要な署名秘密鍵と署名用パスワード設定**を含む。公開GitHubや第三者へそのまま公開せず、本人用の引き継ぎ資料として保管する。公開リポジトリにする際はこれらを除外する。

TEPRA-Print SDK 1.4.0の単体バイナリは元のソースZIPに含まれていない。公式SDKを取得して配置しないとソースからビルドできない。取得先・配置先はdocs/BUILD_AND_RELEASE.md。納品APKには実行に必要な印刷SDKが組み込まれている。

ユーザーがAndroid端末に登録した実際の機器台帳・写真・パスワードは、この開発資料には含まれない。移行が必要ならアプリから .eqbk バックアップを別途作成する。
