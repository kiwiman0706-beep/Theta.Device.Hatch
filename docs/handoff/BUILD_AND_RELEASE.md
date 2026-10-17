# ビルド・更新の引き継ぎ

## 既存構成

- applicationId / namespace: jp.yaegaki.equipment
- versionName: 1.1.0 / versionCode: 110
- minSdk 30（Android 11）/ compileSdk・targetSdk 35
- Java 17 / Gradle 8.13 / Android Gradle Plugin 8.13.1
- Gradle Wrapperとテストソースを同梱
- Android SDK・JDK・Gradle本体・Mavenキャッシュは同梱しない。各環境で導入・取得する

## SDKの配置（ビルド前の必須作業）

元ソースに印刷SDKの単体バイナリは入っていない。以下は元の開発記録に基づく取得先。今回、配布サイトの変更確認やSDKの再ダウンロードはしていない。

- 開発者向け情報: https://www.kingjim.co.jp/sp/tepra_creator/developer/
- SDK: https://www.kingjim.co.jp/download/tepra/sdk/
- 使用許諾: https://www.kingjim.co.jp/download/tepra/sdk/agree.html

TEPRA-Print SDK for Android 1.4.0を公式配布元から使用条件に従って取得し、元のモジュールを改変せず配置する。

- `source/equipment-ledger/app/libs/TepraPrint.jar`
- `source/equipment-ledger/app/src/main/jniLibs/arm64-v8a/libTepraPrint.so`
- `source/equipment-ledger/app/src/main/jniLibs/armeabi-v7a/libTepraPrint.so`
- `source/equipment-ledger/app/src/main/jniLibs/x86/libTepraPrint.so`
- `source/equipment-ledger/app/src/main/jniLibs/x86_64/libTepraPrint.so`

元のTHIRD_PARTY_NOTICES.mdとSDK利用条件を確認する。APKから逆コンパイルしてSDKを再構成する手順ではない。納品済みAPKはそのままインストールできる。

## ローカルでのビルド

JDK17とAndroid SDK platform 35/build-tools 35.0.0を用意する。ANDROID_HOMEを設定するか、プロジェクト直下のlocal.propertiesにその環境のsdk.dirを設定する。古い /workspace/scratch/... のSDKパスをコピーしない。

プロジェクトディレクトリで：

```bash
chmod +x gradlew
./gradlew testDebugUnitTest assembleRelease
```

Windowsでは `gradlew.bat testDebugUnitTest assembleRelease`。
出力は `app/build/outputs/apk/release/app-release.apk`。

GeminiLiveTestは環境変数GEMINI_TEST_CONFIGがないとスキップする。実APIテストを明示的に行う場合だけ、作業用の非公開propertiesファイルにapiKeyを設定して、その絶対パスを環境変数に指定する。課金と外部送信が伴い得る。実キーをソース・成果ZIP・ログへ含めない。通常の残り12件のテストには実APIキーは不要。

## 署名と更新

同梱のrelease-signing.jksとsigning.propertiesをそのまま使用する。キーストアaliasはequipment。署名証明書SHA-256：

`0ac8f8044d5ec1774a220898e90dd8b657d0f6e0f05b840bcb871fd20d924c13`

署名設定の値は表示・転載せずファイルから読み込む。Git公開時には署名ファイルを除外する（既存.gitignoreにも登録済み）。APK更新時にはversionCodeを110より大きくし、applicationIdと署名を維持する。可能ならユーザーへバックアップを案内してから上書き更新する。

アプリ内のデータ暗号鍵とAPKの署名秘密鍵は別物。署名ファイルだけでユーザーの端末内暗号化DBを復号できるわけではない。端末移行は .eqbk を用いる。

## リリース時確認

- リリースAPKの署名、アプリID、minSdk、versionCodeを検査
- 旧版と署名証明書が一致することを確認
- 実APIキーがソース/APKに埋め込まれていないことを検査
- 写真追加、再起動後のデータ保持、下書き、編集、削除を確認
- ラベル選択、テープ幅違い、QR読取、SR5900Pの1枚実印字を確認
- バックアップ・正しい/誤ったパスワード・既存データ保持を確認

今回の引き継ぎ作成では再ビルドしない。元のAPKとソースを保存し、次の修正の基点とする。
