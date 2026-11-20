# John's Drop Tweaks — Minecraft Java 1.21.11 (Fabric)

Dieses Projekt ist auf **Minecraft 1.21.11** festgelegt. Es behält die ursprüngliche Funktion bei: ausgewählte Hotbar-Items werden beim Drücken der Drop-Taste geschützt; Drops im Inventar sollen davon unberührt bleiben.

## GitHub Actions (empfohlener Build-Weg)

1. Erstelle ein GitHub-Repository.
2. Entpacke diese ZIP-Datei auf deinem PC. **Wichtig:** Lade den INHALT dieses Ordners hoch, sodass `build.gradle`, `gradle.properties`, `src/` und `.github/` direkt im Repository-Hauptordner liegen. Nicht den äußeren Ordner als Unterordner hochladen.
3. Committe die Dateien. Die Action `Build John's Drop Tweaks (Minecraft 1.21.11)` startet bei Push automatisch.
4. Öffne im Repository den Reiter **Actions**, wähle den erfolgreichen Lauf und lade unter **Artifacts** `johns-drop-tweaks-minecraft-1.21.11` herunter. Darin liegt die fertige `.jar`.

Die Action installiert Java 21 und Gradle 9.2.1 selbst. Ein `gradlew`-Wrapper ist deshalb nicht erforderlich. Gradle 9.2.1 ist absichtlich fest eingetragen, weil Fabric Loom 1.14 Gradle 9.2 oder neuer benötigt.

## Lokal bauen (Windows)

Voraussetzungen: JDK 21 und Gradle 9.2.1 oder neuer installiert. Öffne in diesem Ordner ein Terminal und führe aus:

```powershell
gradle build --no-daemon
```

Die JAR liegt danach unter `build\libs\johns-drop-tweaks-1.1.0.jar`.

## Installation

Lege die JAR in den `mods`-Ordner deiner **Fabric-Installation für Minecraft 1.21.11**. Fabric API wird benötigt. Mod Menu ist optional; damit erscheint die Konfigurationsseite im Mod-Menü. Alternativ kannst du im Spiel `/droptweaks` eingeben.

## Enthaltene Anpassungen

- Zielversion Minecraft 1.21.11.
- Classic Gray bleibt erhalten.
- Zusätzliche Designs: Neon Pulse, Woodland, Crimson, Galaxy, Emerald und Royal Gold.
- Animierte Rahmen-, Glanz- und Hover-Effekte.
- Die bestehenden Drop-Schutz- und GUI-Funktionen wurden im Quellcode beibehalten.

## Hinweis zum Teststatus

Die ZIP-Struktur und die Build-Konfiguration wurden korrigiert. In dieser Umgebung konnte kein vollständiger Gradle-Build und kein Minecraft-Ingame-Test ausgeführt werden. Ein grüner GitHub-Actions-Lauf ist daher weiterhin der notwendige Nachweis, dass die JAR erfolgreich kompiliert wurde.
