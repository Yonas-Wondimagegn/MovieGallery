==============================================
  MOVIES GALLERY - Information Management System
==============================================

SETUP STEPS
-----------
1. Start XAMPP (Apache + MySQL).

2. Open phpMyAdmin at http://localhost/phpmyadmin

3. Run the file: database_setup.sql
   (Import it via phpMyAdmin > Import tab)

4. Default admin login: admin / 1234

5. Add mysql-connector-java JAR to your project's classpath.
   Download: https://dev.mysql.com/downloads/connector/j/

6. Compile all .java files from the src/ folder.

7. Run: Main.java


MOVIE POSTER IMAGES
-------------------
- When you add a movie, you assign it a Movie ID like M001, M002, etc.
- Place the movie poster image at:  src/pics/M001.jpg
- The Member Gallery view will automatically load and show the poster.
- If the image is missing, the gallery tile shows a placeholder.


MOVIE CONTENT (Full + Highlights)
----------------------------------
When adding a movie, fill:
  - Full Content: enter video quality, e.g. "1080p" or "4K"
  - Highlight 1: first trailer/highlight description
  - Highlight 2: second trailer/highlight description

The system stores one full version and two highlight versions per movie.

On the Member Gallery:
  - Subscribed members see the full movie ("Avatar now playing with 1080p quality")
  - Non-subscribed members see only highlights ("playing the highlight (trailer)")
  - Non-subscribed members can subscribe instantly from the gallery


MEMBER SUBSCRIPTION
--------------------
- Add members from the Members panel.
- Toggle subscription on/off with "Toggle Subscribe".
- Members can also self-subscribe from the gallery view.


REVIEWS (Viewer Activity)
--------------------------
- Only subscribed members can write reviews.
- Non-subscribed members are prompted to subscribe first.
- Reviews are stored in the database with date and member name.


FILE STRUCTURE
--------------
MovieGallery/
  src/
    *.java            (all source files)
    pics/
      M001.jpg        (poster for movie M001)
      M002.jpg        (poster for movie M002)
      ...
  database_setup.sql
  README.txt


WHAT WAS CHANGED FROM ORIGINAL
--------------------------------
- EmojiFont.java removed; all emojis removed from UI
- Movie table now has movieCode column (M001, M002, ...)
- MovieContent now has fullVersion + highlight1 + highlight2 (two highlights)
- Member table now has subscribed column
- New Review table for viewer activity
- MovieFrame upgraded: tabbed layout with Admin view + Member Gallery view
- Gallery view shows poster tiles loaded from src/pics/<movieCode>.jpg
- Watch button plays full or highlight based on subscription status
- Review button opens review dialog (subscribe prompt for non-members)
- MemberFrame: toggle subscription button added
- DashboardFrame: no emojis, shows logged-in admin name
- LoginFrame: no emojis
