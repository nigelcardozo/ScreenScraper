README

This app utilises the libraries Volley to create HTTP Requests and Gson to parse the JSON responses. 
It additonally uses the EventBus library to send messages to the view and picasso to download and display the online images.

The app doesn't fully handle all error cases, it's simply a proof of concept.
Additionally, the app has not been setup to handle different screen resolutions, currently only HDPI has been tested on a Samsung Galaxy Note 2.


Please note that after syncing you will need to download Volley:

1) git clone https://android.googlesource.com/platform/frameworks/volley
2) From Android Studio - File, Import Module, select the location where you cloned Volley
3) From Android Studio - Edit build.gradle (app) and add the line:
	compile project(':Volley')
4) Clean and rebuild

You may also need Gson:
https://github.com/google/gson




