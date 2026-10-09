FREE WAY TO GET THE APK (no Android Studio, no money)
1. Make a free account on github.com
2. New repository, name it bubblevpn, Private, Create.
3. Click "uploading an existing file", unzip this zip on your PC, drag ALL files and folders
   from inside the BubbleVPN folder into the page, click Commit changes.
4. Make sure .github/workflows/build.yml exists. If not: Add file > Create new file,
   type the name  .github/workflows/build.yml  and paste the text from that file.
5. Open Actions tab > Build APK (about 5 minutes). If it does not start: Run workflow.
6. When it has a green tick, click it, Artifacts, download BubbleVPN-APK, unzip,
   install app-debug.apk on your phone.

USE (no password)
1. Open BubbleVPN, tap the power button, allow All files access, tap again.
2. On PC File Explorer type:  ftp://192.168.1.101:2222
   If it asks for a login, choose "Log on anonymously".
