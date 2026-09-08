# TestZombie + Selenium Java – Quickstart Demo

Dieses Beispielprojekt zeigt, wie du bestehende Selenium-Tests in wenigen Minuten mit **TestZombie** startest und anschließend direkt sehen kannst, wie sich die Tests bei veränderten Locatoren und DOM-Strukturen verhalten.

Der Fokus liegt bewusst auf einem einfachen Einstieg:

1. auf **testzombie.ai** registrieren
2. API Key und E-Mail des automatisch angelegten Demo-Projekts übernehmen
3. Credentials an `TestZombieDriver.setCredentials(...)` übergeben
4. Tests starten
5. Mutation Levels ausprobieren und Self-Healing beobachten

> Die Tests selbst enthalten absichtlich **keine eigenen Locator-Fallbacks und keine Self-Healing-Logik**. Wenn sich die Anwendung verändert, soll TestZombie die Reparatur übernehmen – nicht der Beispieltest.

---

## 1. Account erstellen

Registriere dich kostenlos unter:

**https://testzombie.ai**

Nach der Registrierung steht dir ein Demo-Projekt zur Verfügung. Dort findest du die Zugangsdaten, die für die TestZombie-Integration benötigt werden:

- **API Key**
- **E-Mail-Adresse deines TestZombie-Accounts**

---

## 2. Credentials setzen

Die Integration erfolgt im Beispiel über `TestZombieDriver`.

Öffne:

```text
src/test/java/ai/testzombie/demo/support/DriverFactory.java
```

Für einen schnellen lokalen Test kannst du deine Zugangsdaten direkt setzen:

```java
TestZombieDriver.setCredentials(
    "YOUR_API_KEY",
    "YOUR_EMAIL"
);
```

Danach werden die Browser nicht mehr direkt mit `new ChromeDriver()` oder `new FirefoxDriver()` erzeugt, sondern über TestZombie:

```java
return TestZombieDriver.createChrome(options);
```

Für Firefox und Edge funktioniert es entsprechend über:

```java
TestZombieDriver.createFirefox(options);
TestZombieDriver.createEdge(options);
```

### Empfehlung für echte Projekte

API Keys sollten nicht in Git eingecheckt werden. Verwende für CI/CD oder produktive Testprojekte besser System Properties, Environment Variables oder dein vorhandenes Secret Management.

Dieses Beispiel akzeptiert die Credentials auch als Maven Properties:

```bash
mvn test \
  -Dtestzombie.apikey=YOUR_API_KEY \
  -Dtestzombie.email=YOUR_EMAIL
```

---

## 3. Voraussetzungen

Du benötigst:

- Java 21+
- Maven 3.9+
- Chrome, Firefox oder Edge
- einen TestZombie-Account
- Zugriff auf die Demo-Anwendung unter `http://demoweb2.testzombie.ai`

Die WebDriver-Verwaltung übernimmt Selenium Manager.

---

## 4. Ersten Test starten

Im Projektverzeichnis genügt:

```bash
mvn test
```

Standardmäßig läuft die Demo mit sichtbarem Browser.

Nur den vollständigen End-to-End-Flow starten:

```bash
mvn -Dtest=OnboardingFlowTest test
```

Headless ausführen:

```bash
mvn test -Dheadless=true
```

Firefox verwenden:

```bash
mvn test -Dbrowser=firefox
```

Edge verwenden:

```bash
mvn test -Dbrowser=edge
```

Eine andere Testumgebung verwenden:

```bash
mvn test -DbaseUrl=https://your-environment.example
```

---

# Was zeigt die Demo?

Die Demo bildet einen vollständigen kleinen SaaS-Onboarding-Prozess ab. Der Test navigiert über mehrere Seiten und interagiert mit typischen UI-Elementen wie Eingabefeldern, Selects, Buttons, Checkboxen und Dialogen.

Der vollständige Flow umfasst:

```text
Projekt anlegen
    ↓
Projektverantwortlichen erfassen
    ↓
Projekttyp auswählen
    ↓
Plan auswählen
    ↓
Abrechnungsdaten erfassen
    ↓
Workspace konfigurieren
    ↓
Workspace aktivieren
    ↓
Abschlussdialog prüfen
```

Damit ist die Demo bewusst näher an einem echten End-to-End-Test als an einem isolierten „Hello World“.

---

## Mutation Levels

Die Demo-Anwendung kann ihre Oberfläche gezielt verändern. Dadurch lassen sich typische Probleme simulieren, die Selenium-Tests nach Frontend-Änderungen verursachen.

Verfügbar sind:

| Level | Zweck |
|---|---|
| `OFF` | unveränderte Baseline |
| `LIGHT` | leichte Änderungen |
| `REALISTIC` | realistische Änderungen einer weiterentwickelten UI |
| `HARD` | deutlich stärkere Änderungen |
| `EXTREME` | maximale Belastung für die Locator-Erkennung |

Mit steigender Stufe werden locator-relevante Eigenschaften und Strukturen der Demo stärker verändert. Dazu gehören insbesondere Änderungen an Attributen, IDs beziehungsweise Selektoren und an der DOM-Struktur.

Der eigentliche Testcode bleibt dabei unverändert.

Genau das ist der Punkt der Demo: **Ein Test sollte nicht jedes Mal manuell angepasst werden müssen, nur weil sich das Frontend verändert hat.**

---

# Was TestZombie dabei übernimmt

## Self-Healing statt Locator-Fallbacks im Test

Jedes Element im Beispiel besitzt bewusst nur **einen** Locator.

Beispiel:

```java
private static final By SAVE_OWNER = By.id("saveOwnerButton");
```

Es gibt im Testprojekt:

- keine Liste alternativer Selektoren
- keine `try/catch`-Kaskaden mit Ersatz-Locatoren
- keine testseitige KI-Logik
- keine versteckten JavaScript-Klicks als Fallback

Wenn ein Locator aufgrund einer Mutation nicht mehr passt, kann TestZombie das Element anhand der verfügbaren Elementinformationen erneut identifizieren und den Test weiterführen.

Das hält die Page Objects klein und den eigentlichen Testcode lesbar.

---

## Automatisches Scrolling

Bei realen UI-Tests befinden sich Elemente häufig außerhalb des aktuell sichtbaren Viewports.

Die TestZombie-/Selenium-Integration ist darauf ausgelegt, solche Interaktionen robust auszuführen und gefundene Elemente für die Interaktion in den sichtbaren Bereich zu bringen, anstatt dafür in jedem Page Object eigene Scroll-Logik zu benötigen.

Gerade bei längeren Seiten reduziert das zusätzlichen technischen Testcode.

---

## Overlay Detection

Die Demo kann außerdem blockierende Overlays simulieren. Solche Overlays sind ein typischer Grund für fehlschlagende UI-Tests, obwohl das gesuchte Element grundsätzlich korrekt gefunden wurde.

Damit lässt sich demonstrieren, wie TestZombie mit temporären beziehungsweise überlagernden UI-Zuständen umgehen kann.

Im vollständigen `OnboardingFlowTest` wird das zufällige Demo-Overlay aktuell bewusst deaktiviert:

```java
project.disableRandomOverlay();
```

Dadurch bleibt der Vergleich der Mutation Levels reproduzierbar.

Wenn du die Overlay-Erkennung separat demonstrieren möchtest, kannst du diese Zeile entfernen beziehungsweise das Overlay in der Demo aktivieren.

---

## Stabilität ohne „magische“ Testlogik

Das Beispiel trennt bewusst zwei Dinge:

**Normale Browser-Stabilisierung**

Selenium wartet auf sichtbare beziehungsweise klickbare Elemente und wiederholt einen nativen Klick kurzzeitig, wenn sich das DOM während der Interaktion bewegt.

**TestZombie Self-Healing**

TestZombie kommt ins Spiel, wenn der ursprüngliche Locator aufgrund einer Änderung der Anwendung nicht mehr zum richtigen Element führt.

Dadurch bleibt nachvollziehbar, welcher Teil normale Testautomatisierung ist und welcher Teil durch TestZombie geheilt wird.

---

# Projektstruktur

```text
src/test/java/ai/testzombie/demo/
├── pages/
│   ├── BasePage.java
│   ├── ProjectPage.java
│   ├── CheckoutPage.java
│   └── AdminPage.java
├── support/
│   ├── DriverFactory.java
│   ├── SeleniumActions.java
│   ├── MutationLevel.java
│   └── DemoLogger.java
└── tests/
    ├── BaseSeleniumTest.java
    ├── OnboardingFlowTest.java
    └── StepTests.java
```

### `DriverFactory`

Hier wird TestZombie mit Selenium verbunden. Für eine Integration in ein bestehendes Projekt ist das der wichtigste Einstiegspunkt.

### `pages/`

Klassische Page Objects mit bewusst einfachen, einzelnen Selenium-Locatoren.

### `OnboardingFlowTest`

Der komplette End-to-End-Flow. Er wird für alle Mutation Levels ausgeführt.

### `StepTests`

Kleinere Tests für einzelne Bereiche der Demo-Anwendung.

---

# Integration in ein bestehendes Selenium-Projekt

Wenn du TestZombie nicht nur mit dieser Demo, sondern mit deinem eigenen Projekt testen möchtest, ist der grundlegende Umbau klein.

Statt beispielsweise:

```java
WebDriver driver = new ChromeDriver(options);
```

verwendest du:

```java
TestZombieDriver.setCredentials("YOUR_API_KEY", "YOUR_EMAIL");
WebDriver driver = TestZombieDriver.createChrome(options);
```

Deine bestehenden Page Objects, `By`-Locator und Selenium-Tests können anschließend weiterverwendet werden.

Du musst also nicht zuerst dein gesamtes Testframework auf eine TestZombie-spezifische API umbauen.

---

# Empfohlener Demo-Ablauf

Wenn du TestZombie zum ersten Mal ausprobierst, empfehlen wir diesen Ablauf:

1. `OnboardingFlowTest` mit Mutation Level `OFF` ausführen.
2. Prüfen, dass der normale Selenium-Flow erfolgreich läuft.
3. Anschließend die höheren Mutation Levels beobachten.
4. In den Logs verfolgen, wann ein ursprünglicher Locator nicht mehr ausreicht und TestZombie übernimmt.
5. Optional das zufällige Overlay aktivieren, um auch diesen Fehlerfall zu demonstrieren.

So wird innerhalb weniger Minuten sichtbar, welchen Unterschied Self-Healing bei bestehenden Selenium-Tests macht.

---

## Kurz gesagt

```text
Registrieren → Credentials setzen → mvn test → Mutationen ausprobieren
```

Das Beispiel ist absichtlich einfach gehalten: **deine Selenium-Tests bleiben Selenium-Tests – TestZombie ergänzt die Healing-Schicht darunter.**
