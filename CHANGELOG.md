# Changelog

All notable changes to TugaOBD will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.0] - 2026-05-17

### Added
- Initial release as part of the Tuga ecosystem v0.2.0 utility bundle for Geely Tugella head unit (Android 5.1, API 22).
- Demo dashboard with sin-wave generated metrics: speed, RPM, coolant temperature, fuel level, battery voltage.
- DTC (Diagnostic Trouble Codes) status bar.
- "ПОДКЛЮЧИТЬ" (Connect) button with stub dialog — real ELM327 / Bluetooth OBD-II integration deferred to a later phase.
- Landscape-locked UI matching head unit hardware.
- Shared design tokens (colours, typography, drawables) consumed from `tuga-design` workspace library (added in Phase 1).
- Package: `com.miktuga.obd` (renamed from `com.example.tugastore.obd` in Phase 1).
- Signed with the shared `_signing/tuga-release.jks` keystore (v1+v2+v3) so it installs as a sibling alongside TugaStore without signature conflicts.
