# 次のAIへ

1. 00_START_HERE.md、REQUIREMENTS_AND_CONCEPT.md、STATUS_AND_NEXT_STEPS.mdを読む。
2. source/equipment-ledger/の現在のコードを確認する。アプリ版は1.1.0、versionCode 110。
3. 本ZIP作成は梱包と資料整備のみ。ソース・APKの機能変更、再ビルド、実APIの再テストは行っていない。
4. Gemini APIキーはアプリ入力方式を守る。会話で昔提示された実キーを復元・埋め込みしない。GeminiLiveTestはGEMINI_TEST_CONFIGを明示した時のみ実行する設計。設定しない場合はスキップが正常。
5. ビルド前にTEPRA-Print SDK単体バイナリの欠落を解消する。入手・配置手順はBUILD_AND_RELEASE.md。別スレッドの古いscratch絶対パスには依存しない。
6. アプリID jp.yaegaki.equipment、署名、Keystore alias equipment-v1、DB/バックアップ互換性を維持する。アップデート時はversionCodeを110より上げる。
7. 署名秘密鍵は既存のrelease-signing.jksを使用する。signing.propertiesをログに表示しない。署名変更やアンインストールを当然の前提にしない。
8. 最優先の未確認事項は、実Android端末のカメラ／認証／写真追加とSR5900P実機印刷。過去の模擬テスト成功を実機成功と表現しない。
9. データバックアップは160MiBの復元上限があり、追加復元は重複を作る。大規模運用や同期はまだ未対応。
10. ユーザーから次の変更指示を受けたら、既存の機能とデータを維持して実装し、APK・ソース・説明書を一緒に更新する。

本ZIPには本人のAndroid端末内の機器データは存在しない。実データを必要とする修正では、端末からエクスポートしたバックアップを別途受け取る。バックアップパスワードもここにはない。
