# QR Attendance — Web App Edition

**Flow:** Instructor's screen shows a QR code → student scans it with
their own phone camera → phone opens a one-field page → student types
their Student ID and taps Submit → Student ID + Date + Time is saved
to your Google Sheet automatically.

---

## What was wrong before, and what changed

The old version made the QR code point at `http://<your-computer's-local-IP>:8080/`,
served by a small web server running inside the Java program itself.
That only works if:
- the phone and PC are on the exact same Wi-Fi, **and**
- that Wi-Fi allows devices to talk to each other directly (many
  school/office/guest networks turn this off — it's called "client
  isolation" or "AP isolation" — even though both devices show the
  same Wi-Fi name), **and**
- the PC's firewall allows inbound connections on port 8080.

Any one of those failing looks exactly like "the page won't load on
my phone." That's almost certainly what happened.

**The fix:** the phone no longer talks to your computer at all. The
QR code now encodes your **deployed Google Apps Script Web App URL**
— a normal `https://script.google.com/...` address hosted by Google.
The Java program's only job is to draw that URL as a QR code; Apps
Script itself serves the Student ID form *and* saves the row to your
Sheet. Any phone with internet access (Wi-Fi or mobile data, same
network as the PC or not) can open it.

### Project structure
```
qr-attendance-webscan/
├── pom.xml
├── google-apps-script/
│   └── Code.gs               # paste into Apps Script — serves the page + saves to Sheets
└── src/main/java/attendance/
    └── Main.java              # the whole Java app — just shows the QR code
```

---

## Part 1 — Google Sheets + Apps Script setup (do this first)

1. Create (or open) a Google Sheet.
2. In the Sheet, go to **Extensions → Apps Script**.
3. Delete anything in the editor and paste in the entire contents of
   `google-apps-script/Code.gs` from this project.
4. Click **Deploy → New deployment**.
   - Click the gear icon next to "Select type" and choose **Web app**.
   - Execute as: **Me**
   - Who has access: **Anyone**
   - Click **Deploy**, then **Authorize access** and approve the
     permissions (you'll see an "unverified app" warning — click
     **Advanced → Go to (project name)** — that's normal for your own
     script).
5. Copy the **Web app URL** shown (it ends in `/exec`).

You do **not** need to create the "Attendance" tab or header row
yourself — the script creates them automatically on the first
submission if they're missing.

> **If your Google account is a school/work Workspace account:** some
> organizations block "Anyone" access for Apps Script web apps. If
> deployment fails or students get a permissions error, either ask
> your IT admin to allow it, or create the Sheet on a personal Gmail
> account instead.

---

## Part 2 — Java setup

1. Open the `qr-attendance-webscan` folder as a Maven project in
   NetBeans, IntelliJ, or Eclipse (or any editor).
2. Open `src/main/java/attendance/Main.java` and replace the
   placeholder URL with the real Web app URL you copied in Part 1:

   ```java
   private static final String WEB_APP_URL =
           "https://script.google.com/macros/s/AKfyc.../exec";
   ```

3. Run `Main.java`. A window appears with one large QR code — that's
   the whole program.

### Or from a terminal:
```bash
mvn package
java -jar target/qr-attendance-webscan.jar
```

---

## Try it

Point any phone's regular camera app at the QR code. It should show a
banner or link to open it — tap it, type any Student ID, tap Submit.
Within a couple of seconds a new row (`StudentID | Date | Time`)
appears in your Sheet's "Attendance" tab.

---

## Troubleshooting

- **Nothing happens when scanning / "can't find app to open link":**
  Use the phone's built-in Camera app, not a third-party QR scanner —
  regular cameras show a tappable link banner automatically.
- **Page shows a Google error / permission screen:** Deployment
  access is probably not set to "Anyone." Go to **Deploy → Manage
  deployments** and check the setting.
- **You edited `Code.gs` but nothing changed:** Apps Script does not
  update the live URL automatically. Go to **Deploy → Manage
  deployments → click the pencil (edit) icon → Version: New version →
  Deploy**.
- **QR code opens the old placeholder / a broken link:** You likely
  edited `Main.java` but didn't restart the program (or didn't
  rebuild the jar). Re-run it after saving.
- **Still nothing saved to the Sheet:** Open the Sheet's Apps Script
  project, click **Executions** (clock icon on the left) to see if
  `doPost` ran and whether it logged an error.
- **Phone has no signal/Wi-Fi at all:** the phone just needs *some*
  internet connection (Wi-Fi or mobile data) — it no longer needs to
  share a network with the instructor's PC.

---

## Notes

- Every submission appends a new row — the script doesn't check
  whether a student already scanned today. If you'd like it to skip
  duplicate scans for the same day, that's a small addition to
  `Code.gs` — just ask.
- Because access is set to "Anyone," anyone with the exact URL could
  technically submit an entry. The URL is never shown to students
  (only encoded in the QR image), which is a reasonable trade-off for
  a simple classroom tool.
