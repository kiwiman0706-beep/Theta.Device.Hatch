# Theta.xxx.Hatch 要件に合わせる次の構想（案）

作成：2026-10-17。対象：機器台帳 1.1.0（versionCode 110, package `jp.yaegaki.equipment`）を Theta.Device.Hatch リポジトリで継続開発するための構想。
**これは提案です。** 確定要望は `docs/handoff/REQUIREMENTS_AND_CONCEPT.md` と `docs/handoff/REQUEST_HISTORY.md` だけです。
LAN内機器一覧（Fing風）の構想は別紙 [10_LAN_SCAN_PLAN.md](10_LAN_SCAN_PLAN.md)。

## 1. 「Theta.xxx.Hatch」の要件をどう読み取ったか

明文化された要件書は見つからなかったため、同日に引き継いだ兄弟リポジトリ（Theta.Item.Hatch / Theta.Label.Hatch / Theta.Kamigata.Hatch / Theta.DoorPhoto.Hatch）の
`09_THETA_HATCH_PLAN.md` で使われている共通規約 T1〜T12（先行作 DeskHatch から抽出されたもの）に揃えた。

| # | シリーズ共通の規約 | Theta.Device.Hatch での要件（案） | 1.1.0 の現状 |
| --- | --- | --- | --- |
| T1 | 識別子 `Theta.<Name>Hatch`、Publisher `Theta` | 表示名を「Device Hatch（機器台帳）」系に。package は維持 | 表示名「機器台帳」 |
| T2 | 公開リポジトリ＋GitHub Releases で配布 | APK を GitHub Releases で配布 | **v1.1.0 で対応**（release-apk ワークフロー） |
| T3 | タグ or 手動実行で Actions がビルド→Release | CI でビルド・署名・公開まで自動化 | 署名済みAPKの検証・公開のみ |
| T4 | stable／beta（`-beta` タグ＝プレリリース） | 同じ2チャネル運用 | ワークフローは `-` 付きタグをプレリリース扱い済み |
| T5 | アプリ内自動更新 | Releases を確認→取得→検証→インストール | 未対応 |
| T6 | パッケージマネージャー登録 | Android 相当：Obtainium 対応 | 未対応 |
| T7 | 日本語／英語 UI | 日英 UI | 日本語のみ。文字列が Java コードに直書き |
| T8 | README（バッジ・手順）、CHANGELOG、PUBLISHING.md | 同等の文書一式 | README・CHANGELOG・署名方針のみ |
| T9 | プライバシーポリシー、GitHub Pages LP・デモ | 同等。デモはブラウザ版ラベルプレビュー | 未対応 |
| T10 | 単体＋E2E テストを CI で実行 | 既存 Robolectric テスト 12 件＋ビルドを CI 化 | テストはあるが CI なし。TEPRA SDK 無しではコンパイル不可 |
| T11 | MIT License | ライセンス明示（SDK・ML Kit は各社条件で別扱い） | 未設定 |
| T12 | 署名鍵を Secrets で受けて CI 署名 | 同左 | **鍵は手元にある**（引き継ぎZIP）。Secrets 未登録 |

### 兄弟リポジトリとの違い（Device.Hatch 固有の事情）
- **扱うデータが機密。** ID/PW・管理画面URL・社内IPを保存するアプリなので、Theta 化（自動更新・LP・テレメトリ等）で**外部送信を増やさない**ことを最優先の非機能要件にする。
- **署名鍵が残っている。** package `jp.yaegaki.equipment` と鍵 alias・Keystore alias `equipment-v1` を維持し、上書き更新でデータを守る。
- **TEPRA-Print SDK が公開リポジトリに置けない。** CI ビルドには別経路が必要（Label.Hatch と同じ課題。共通化できる）。
- **DB の `onUpgrade` が未実装（例外を投げる）。** LAN スキャン等でスキーマを変える前に移行の仕組みが必須。
- **コードが極端に密。** Java 8 ファイル計 225 行（1 行が非常に長い）。機能追加の前に整形・分割が必要。

## 2. 最初に決めること（ユーザー判断が必要）

1. **実機テストを先にするか。** 推奨は「はい」。v1.1.0 で「1台登録→写真3種→再起動後表示→編集→Gemini候補反映→24mm 1枚印刷→QR読取→.eqbk バックアップ/復元」を一巡し、結果を Issue 化。
2. **表示名。** 例：「Device Hatch（機器台帳）」。package は維持（変えると別アプリ扱いでデータが移らない）。
3. **TEPRA SDK を CI に渡す方法。**
   - 案A（推奨）：非公開リポジトリ（例 `Theta.Hatch-sdk`）に SDK を置き、読み取り専用トークン（Secrets）で CI から取得。Label.Hatch と共用できる。
   - 案B：CI では `Printer.java` を除いた「SDK なしフレーバー」でテストのみ、署名済みAPKは手元ビルド→`releases/` コミット→現行ワークフローで公開。
4. **ライセンス**（アプリ自身のコードを MIT にするか）。
5. **LAN 機器一覧を入れるか・どこまでか**（別紙の L1〜L4 のどこまで）。

## 3. 段階的な構想

### Phase 0 — 済（今回）
- 引き継ぎ一式を登録（`docs/handoff/` は原文、`source/equipment-ledger/` はソース）。鍵・署名設定・鍵入りの元ソースZIPは除外。
- `app/build.gradle`：`signing.properties` が無い時は署名設定を外して読み込む（公開ソースで Gradle 同期・テストを可能に）。
- `release-apk` ワークフロー：同梱APKを apksigner で検証し、証明書 SHA-256・package・versionName が一致し、APIキーらしき文字列が無い時だけ Release を作る。
- v1.1.0 APK を GitHub Release として公開。

### Phase 1 — 実機確定と開発基盤（T3, T4, T10, T12）— 1.1.x / 1.2.0
- 実機チェックリスト（`docs/REAL_DEVICE_TEST_CHECKLIST.md` を新設）を実施し、不具合を 1.1.x で修正。
- **コード整形と分割**（挙動は変えない）：`MainActivity` を「一覧」「詳細/編集」「写真」「ラベル」「設定/バックアップ」に分割、文字列を `res/values/strings.xml` へ抽出（T7 の前提）。
- **DB 移行の枠組み**：`onUpgrade` に段階的マイグレーション（v1→v2…）とテスト。旧 DB を含む `.eqbk` 復元互換テストを追加。
- `build.yml`：push／PR ごとにビルド・単体テスト・lint（SDK 経路は §2-3 の決定に従う）。
- `release.yml`：Secrets `DEVICE_KEYSTORE_B64` 等から鍵を復元→ビルド→署名→証明書ピン留め確認→Release。versionCode は `major*10000+minor*100+patch` を推奨（1.2.0→10200。現行 110 より必ず大きい）。`v1.2.0-beta.1` はプレリリース。
- バックアップの既知課題：エクスポート時にも 160MiB 上限の事前検査、復元時の重複検出（同一 ID / MAC をスキップ or 上書き選択）。

### Phase 2 — LAN 内機器一覧（ユーザー希望）— 1.3.0
- 別紙 [10_LAN_SCAN_PLAN.md](10_LAN_SCAN_PLAN.md) の L1〜L2（スキャン・識別・台帳との突き合わせ・未登録機器の登録）。
- 台帳アプリの価値に直結する部分（「未登録の機器が LAN にいる」「台帳の IP が変わった」）を優先。

### Phase 3 — 配布と更新（T5, T6）
- 設定に「アップデートを確認」：GitHub API `releases/latest`（beta 選択時は prerelease 含む）→ versionCode 比較 → APK ダウンロード → SHA-256 と**署名証明書の一致を検証** → `PackageInstaller`（`REQUEST_INSTALL_PACKAGES`）。自動確認は既定オフ（機密アプリのため外部通信は明示操作のみ）。
- Obtainium 用に APK 命名規則を固定（`DeviceHatch-x.y.z.apk` へ移行するなら CHANGELOG とワークフローを同時に変更）。
- Google Play は TEPRA SDK の国内限定条件・機密データの扱い（データセーフティ申告）を確認してから別途判断。

### Phase 4 — Theta ブランドと文書（T1, T7, T8, T9, T11）
- 表示名変更、アイコン調整（シリーズ共通トーン）。
- 日英 UI（`values-en/`）。TEPRA SDK は国内利用前提なので英語は在日ユーザー向け。
- README をシリーズ形式に（バッジ、スクリーンショット、対応機種、インストール手順）、`docs/PUBLISHING.md`、`docs/privacy.html`（台帳・写真・ID/PW は端末内のみ。外部送信は「Gemini 認識でユーザーが選んだ写真」「更新確認」だけ、を明記）。
- GitHub Pages：LP＋「項目とテープ幅を選ぶとラベル見本とQRが出る」ブラウザ版プレビュー（`Labels.java` の規則を JS に移植）。

### Phase 5 — 元要望の範囲での拡張（候補）
- Google 画像検索／Web 検索による型番補助（要望にあり未実装）：Gemini の Google Search grounding を使う案。外部送信が増えるため既定オフ・明示同意。
- 他プリンター対応・PC 版・同期は要望が出てから。

## 4. 回帰させないこと（Theta 化でも維持）
package `jp.yaegaki.equipment`・署名鍵・Keystore alias `equipment-v1`・DB/`.eqbk` 互換、Gemini キーはアプリ内入力（埋め込まない・バックアップしない）、
端末ロック認証とスクリーンショット抑止、ID/PW をラベル初期選択に入れない、QR は機器の管理画面 URL のみ、
`Powerted by KING JIM TEPRA-Print SDK` 表記、鍵・SDK バイナリ・実APIキーを公開リポジトリに入れないこと。
