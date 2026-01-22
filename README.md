# Travel Insurance Automation

## Overview
Selenium + TestNG automation for Harel Travel Insurance flow. The suite covers:
- Destination selection (e.g., Europe tile)
- Navigation to the Date step
- Date selection via inputs and via date picker
- Inclusive day count verification across months
- Validation when Next is clicked with missing dates

## Tech stack
- Java 11
- Selenium 4
- TestNG 7
- WebDriverManager
- Maven (Surefire)
- GitHub Actions (CI) + GitHub Pages (reports)
 - Custom Action driver utilities (wrapper around Selenium actions)

## Project structure
```
TravelInsuranceAutomation
├─ src
│  ├─ main
│  │  └─ java
│  │     ├─ Base
│  │     │  └─ BaseClass.java                │ WebDriver lifecycle, config, CI headless options
│  │     ├─ pagesObject
│  │     │  ├─ DestinationPage.java          │ Destination step actions (tiles, next, errors)
│  │     │  └─ components
│  │     │     └─ DateSection.java           │ Date step component: inputs, picker, next, days summary
│  │     ├─ utils
│  │     │  └─ DateUtils.java                │ Parse/format and inclusive/exclusive day calculations
│  │     └─ ActionDriver
│  │        └─ Action.java                   │ Reusable actions helper (click, waits, typing, timeouts)
│  └─ test
│     └─ java
│        └─ testCases
│           ├─ DestinationPageTest.java      │ Destination step tests
│           └─ DateSectionTest.java          │ Date step tests (picker, absolute ranges, validations)
│
├─ src
│  └─ test
│     └─ resources
│        └─ testng.xml                       │ TestNG suite entry
│
└─ .github
   └─ workflows
      └─ tests-and-pages.yml                 │ CI: run tests headless, publish HTML report to Pages
```

## Local setup
- JDK 11+
- Maven 3.8+
- Chrome installed (for local runs)

## Run tests locally
```
mvn -DskipTests=false test -Dsurefire.suiteXmlFiles=src/test/resources/testng.xml
```
Generate and open the HTML report locally:
```
mvn surefire-report:report
# then open target/site/surefire-report.html
```

## CI & Reports
- CI badge:
  - ![Tests and Pages](https://github.com/maramalasad/TravelInsuranceAutomation/actions/workflows/tests-and-pages.yml/badge.svg)
- Workflow: `.github/workflows/tests-and-pages.yml`
  - Runs on every push and manual dispatch
  - Uses `xvfb-run` for headless Chrome on Linux
  - Publishes `target/site` to GitHub Pages
  - Always publishes a report (even when tests fail) with a fallback page

- Pages URL:
  - Main report (root): https://maramalasad.github.io/TravelInsuranceAutomation/
  - Raw Surefire outputs: https://maramalasad.github.io/TravelInsuranceAutomation/surefire-reports/

## Key tests
- `datePicker_selectStartPlus7_andEndPlus30_thenProceed`
  - Selects start+7 and end+30 via picker
  - Verifies 31 inclusive days and navigates to next component
- `absoluteDates_27Jan_to_28Feb_show33Days`
- `absoluteDates_28Jan_to_27Feb_show31Days`
- `missingDates_clickNext_showsGeneralError`

## Troubleshooting
- CI fails before deploy
  - We set `-Dmaven.test.failure.ignore=true` so Pages still deploys
  - Check run artifacts: `test-reports` → `target/site/surefire-report.html`
- Chrome in CI errors
  - `BaseClass` configures headless Chrome with `--no-sandbox`, `--disable-dev-shm-usage`, and a fixed window size
- Pages 404 after deploy
  - Wait ~1–2 minutes and hard refresh

---
Links above are configured for this repository: `maramalasad/TravelInsuranceAutomation`
https://github.com/maramalasad/TravelInsuranceAutomation
