README

This app utilises the libraries Volley to create HTTP Requests and Gson to parse the JSON responses. It additonally uses the EventBus library to send messages to the view and picasso to download and display the online images.

The app doesn't fully handle all error cases, it's simply a proof of concept. Additionally, the app has not been setup to handle different screen resolutions, currently only HDPI has been tested on a Samsung Galaxy Note 2.

Please note that after syncing you will need to download Volley:

1) Clone this project

2) Open Android Studio, then choose to open an existing Android Studio project and select the location where you've cloned this repo.

3) Android studio will attempt to build and may give you an error. This is due to the fact that I cannot redistribute Volley.

4) git clone https://android.googlesource.com/platform/frameworks/volley 

5) From Android Studio - File, Import Module, select the location where you cloned Volley 

6) You may be asked if you wish to add certain XML files to 'Git'. Say no.

7) From Android Studio - Edit build.gradle (app) and add the line: compile project(':Volley') 

8) Click 'Sync Now'

9) Clean and rebuild
