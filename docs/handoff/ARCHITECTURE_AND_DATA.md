# 実装とデータ形式

## 基本

JavaのネイティブAndroidアプリ。Activityと標準Viewを使ったUI。WebView/PWA/Reactではない。

| ファイル | 役割 |
|---|---|
| MainActivity.java | 一覧、検索、詳細、編集、カメラ、写真選択、OCR、ラベル画面、端末認証、バックアップ操作 |
| Store.java | 暗号化SQLiteレコード、暗号化写真、下書き、AI設定、バックアップ・復元 |
| Crypto.java | AES-GCM、PBKDF2-HMAC-SHA256 |
| Extract.java | 端末内OCR文字列からのIP/MAC/メーカー/型番等の抽出 |
| Gemini.java | 選択写真のREST送信、JSON Schema指定、応答の検査、キャンセル、HTTPエラー案内 |
| GeminiUi.java | APIキー設定、送信写真の選択、AI候補の確認・反映 |
| Labels.java | 文字・QRのラスタ画像作成、小さすぎる文字/QRや過長ラベルの抑止 |
| Printer.java | 公式SDKでのSR5900P探索、状態取得、テープ幅検査、印刷 |

## データモデル

SQLite database: equipment.db / version 1

`devices(id TEXT PRIMARY KEY, data BLOB NOT NULL, updated INTEGER NOT NULL)`

dataはJSONをUTF-8化してAES-GCM暗号化したBLOB。JSONの主なキー：

| キー | 意味 |
|---|---|
| id | UUID |
| name | 機器名（必須） |
| asset | 管理番号 |
| category | 分類 |
| maker / model / serial | メーカー / 型番 / 製造番号 |
| location | 設置場所 |
| ip / mac | 複数値は改行区切り |
| price / date | 価格 / 購入日 |
| url | 機器の管理画面URL |
| login / password | 認証情報 |
| memo | 自由メモ |
| photos | [{file: UUID.jpg, kind: 写真分類}, ...] |

写真はfiles/photos/の暗号化ファイル。拡張子.jpgでも保存時の実体は暗号文。鍵aliasはAndroid Keystoreのequipment-v1。暗号文は12バイトIVにAES-GCMの暗号化結果（認証タグを含む）を続けたもの。

下書きはfiles/draftのAtomicFileで暗号化保存。AI設定はfiles/ai-settingsに暗号化保存し、JSONキーはkey/model。キーの再表示はせず、新規入力・置換・削除を提供。

通常の検索は復号したレコードを端末内で検索。サーバーDBやインデックス同期はない。DBのonUpgradeは未実装で例外を投げるので、DBバージョンを上げる際は移行処理が必要。

## バックアップ

拡張子.eqbk。ヘッダはASCII `EQBK01`（6バイト）、salt（16バイト）、IV（12バイト）、AES-GCM暗号文。鍵導出はPBKDF2-HMAC-SHA256、210000回、256bit。暗号文の中身はZIPで、manifest.json（format=1, devices配列）とphotos/以下の写真。

API設定と未保存の下書きは含めない。復元は認証検証後に新しい機器IDと写真IDで追加し、既存レコードを上書きしない。SQLite transactionで追加失敗時はロールバックし、作成写真も削除する。再復元は重複する。

復元上限は入力160MiB、展開合計160MiB、1エントリ16MiB。多数ファイルにも上限チェックがある。インポートはメモリ上に読み込む。エクスポート側は同じサイズ上限を事前検査しないため、大きな台帳では復元できないサイズの出力が生じ得る。大規模化時の改善対象。

## OCR / Gemini

ML Kit text-recognition-japanese 16.0.1を同梱。通常OCRは端末内処理。写真は取り込み時に向き補正・縮小・JPEG化。銘板写真はOCRを起動し、候補をユーザー確認後に反映。

GeminiはHTTPS REST `https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent`。APIキーはヘッダx-goog-api-key。標準モデルgemini-2.5-flash、設定で変更可能。モデルの将来の提供状況は開発再開時に必要なら確認する。

選択したJPEG 1〜4枚のみ送信。写真の合計12MiB上限。入力済み台帳テキスト・ID/PWを自動添付しない。写真に写った情報自体は外部送信される。

responseSchemaでfields[{key,value,confidence,evidence}]とsummaryを要求。許可キーはname/category/maker/model/serial/location/ip/mac/price/url。資格情報は候補から除外。IP/MAC/URL/価格を検査し、STOP以外の未完了応答は反映しない。Web検索やGoogle画像検索は行っていない。認識候補の確度はAI自己評価で保証ではない。

## 印刷

SR5900Pのみを対象として公式TEPRA-Print SDK 1.4.0を組み込む。Wi-Fi側からネットワーク探索するか、プリンターのIPv4を手入力。手入力のポートは9100。

Labels.javaで文字とZXing QRのBitmapを作り、SDKへ渡す。QRには機器URLだけ。実際の印字領域・解像度はプリンターの取得値を使用。UIは12/18/24/36mm、1回1枚。プレビューは360dpiを前提に144/216/288/384ドット高。

ID/PWのチェック状態は次回の初期選択に引き継がない。QRの極小化や文字の詰め込みを検知し、項目削減または広いテープを促す。実機の余白・読取品質・通信は未検証。
