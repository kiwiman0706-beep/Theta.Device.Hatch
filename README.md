# Theta.Device.Hatch — 機器台帳

PC・電子機器の本体／銘板・MAC 表示／設置風景をスマートフォンで撮影し、そのまま**機器台帳**を作る Android アプリ。
OCR と Gemini 画像認識でメーカー・型番などの転記を補助し、管理画面 URL・ログイン情報・設置場所を写真と一緒に暗号化保存。
KING JIM「テプラ」PRO **SR5900P** から、管理画面 URL の QR 付き管理ラベルを印刷できます。

- 最新版：[GitHub Releases](https://github.com/kiwiman0706-beep/Theta.Device.Hatch/releases)（`equipment-ledger-1.1.0.apk`）
- Android 11以降 / package `jp.yaegaki.equipment` / 署名証明書 SHA-256 `0ac8f804…0d924c13`
- **実機でのカメラ・端末認証・写真追加、SR5900P 実印刷は未検証です。**

## 最初に読むファイル

1. [docs/handoff/00_START_HERE.md](docs/handoff/00_START_HERE.md)：引き継ぎ資料の入口（原文のまま収録）
2. [docs/handoff/REQUIREMENTS_AND_CONCEPT.md](docs/handoff/REQUIREMENTS_AND_CONCEPT.md)：要望とコンセプト
3. [docs/handoff/ARCHITECTURE_AND_DATA.md](docs/handoff/ARCHITECTURE_AND_DATA.md)：構造・データ形式・暗号化
4. [docs/handoff/STATUS_AND_NEXT_STEPS.md](docs/handoff/STATUS_AND_NEXT_STEPS.md)：検証範囲と未確認事項
5. [docs/09_THETA_HATCH_PLAN.md](docs/09_THETA_HATCH_PLAN.md)：Theta.xxx.Hatch 規約に合わせる構想
6. [docs/10_LAN_SCAN_PLAN.md](docs/10_LAN_SCAN_PLAN.md)：LAN 内機器一覧（Fing 風）機能の構想
7. [docs/08_SIGNING_KEY_POLICY.md](docs/08_SIGNING_KEY_POLICY.md)：署名鍵をこのリポジトリに置かない理由と更新APKの作り方

## 構成

| 場所 | 内容 |
| --- | --- |
| `releases/equipment-ledger-1.1.0.apk` | 配布済みの署名済みAPK（バイト列無改変） |
| `source/equipment-ledger/` | 1.1.0 のソース。署名鍵・TEPRA SDK は含まない |
| `docs/handoff/` | 前スレッドの引き継ぎ資料（原文）。文中の `source/equipment-ledger/` は本リポジトリでも同じ場所 |
| `docs/release-notes/` | GitHub Release 本文 |
| `.github/workflows/release-apk.yml` | APK の署名証明書・package・version・APIキー非混入を検証して Release を作成 |

## リリース手順（現状）

1. `releases/equipment-ledger-<version>.apk` と `docs/release-notes/v<version>.md` をコミット
2. Actions → `release-apk` → Run workflow（`version` に `v<version>`）。`-` 付きはプレリリース

## 注意

- このリポジトリは公開です。**署名鍵（`release-signing.jks`）・`signing.properties`・TEPRA SDK バイナリ・Gemini API キーはコミットしないでください**（`.gitignore` 済み）。
- Gemini API キーはアプリ内で入力し端末内で暗号化保存します。APK・ソースには含まれません。
- Powerted by KING JIM TEPRA-Print SDK（SDK 指定表記）。KING JIM、テプラ、TEPRA は株式会社キングジムの商標または登録商標です。
