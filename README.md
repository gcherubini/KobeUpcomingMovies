# KobeUpcomingMovies
Application to show the list of upcoming movies throught The Movie Database (TMDb) API. You can also search for movies with this application and take a look into the details of some modelMovie of your interest.

There is a background task that is responsible to synchronize upcoming movies from TMDb API, this task persist inside local database the results, this way the user can visualize upcoming movies even when he lost their internet connection. Otherwise, there is another task just to search movies in TMDb database, this one will just update the list view, not saving anymore into local database.

There is a view restoration code implemented using Android Shared Preferences storage, this way if user left the movies view for some reason, the context of this view will be restored properly when the user comes back to application. There is also a pagination feature implemented in order to facilitate the visualization of the whole results shown.


Android libraries used:

RecyclerView:
Advanced list view that gives a better experience on reloading items and reusing rows

Retrofit:
Network communication with HTTP calls

JacksonConverter:
Used to serialize/deserialize Retrofit network responses automatically into object models

Picasso:
Manage image loading, used to load movies poster's from network

Realm:
Local database used to shown upcoming movies even the user lost  internet connection 

SharedPreferences:
Default native storage used to storage session data


Future Improvements:

- Implement "loading..." UI feedback
- Increase code documentation
- Implement unit tests and UI tests
- Improve UI
