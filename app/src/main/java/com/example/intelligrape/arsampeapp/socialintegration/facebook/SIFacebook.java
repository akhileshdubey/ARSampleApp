package com.example.intelligrape.arsampeapp.socialintegration.facebook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.LoginActivity;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * SIFacebook Class to use Facebook Integration in Android.
 * <p/>
 * <p/>
 * Steps to login and share:
 * <ol>
 * <li>Add latest Facebook SDK to your project</li>
 * <li>Create instance of this class at class-level<br />
 * <code>SIFacebook facebook = new SIFacebook(this);</code></li>
 * <li>Instantiate listener<br />
 * <code>SIFacebook.SIFacebookListener listener = facebook.new SIFacebookListener() {}
 *
 * @Override public void onLogIn(GraphUser graphUser) { }
 * @Override public void onConnectionFailure(String errorMessage) { }</code>
 * @Override public void onLogOut() { }; </li>
 * <p/>
 * <li>- Please register SIFacebookListener before calling login().
 * <p/>
 * </li><li> - Please define com.facebook.LoginActivity in
 * AndroidManifest.xml as <activity
 * android:name=\"com.facebook.LoginActivity\" />.
 * <p/>
 * </li><li> - Please provide Application ID in AndroidManifest.xml
 * <meta-data android:name=\"com.facebook.sdk.ApplicationId\"
 * android:value=\"@string/app_id\" /> inside <application>. </li><li>
 * Please override the onActivityResult() in your Activity and call
 * facebook.onActivityResult() in onActivityResult().
 * <p/>
 * </li><li> - Now, call facebook.login(true) to begin login process,
 * or call facebook.login(true,
 * Arrays.asList(Permissions_V2.USER_ABOUT_ME,
 * Permissions_V2.USER_BIRTHDAY) to login with extra permissions.
 * <p/>
 * </li><li> - To logout call facebook.logout() </li>
 * </ol>
 * </p>
 * <p>
 * Methods to share
 * <ul>
 * <li>facebook.shareMessage()</li>
 * <li>facebook.shareLink()</li>
 * <li>facebook.sharePhoto()</li>
 * <li>facebook.shareVideo()</li>
 * </ul>
 * </p>
 */
public class SIFacebook {

    private final String APPLICATION_ID_PROPERTY = "com.facebook.sdk.ApplicationId";
    private final String TAG = "SIFacebook";
    private final List<String> PERMISSIONS = Arrays
            .asList(Permissions.PUBLISH_ACTIONS);
    public boolean DEBUG = true;
    private Activity activity;
    private GraphUser graphUser;
    private SIFacebookListener siFacebookListener;

    private Request.GraphUserCallback graphUserCallback = new Request.GraphUserCallback() {

        @SuppressLint("NewApi")
        @Override
        public void onCompleted(GraphUser user, Response response) {
            if (user == null) {
                if (siFacebookListener != null)
                    siFacebookListener.onConnectionFailure(response.getError()
                            .toString());
            } else {
                SIFacebook.this.graphUser = user;

                showMsgInfo("log in success: " + user);

                siFacebookListener.onLogIn(user);
                siFacebookListener.jsonObject(user.getInnerJSONObject());
            }
        }
    };

    private Session.StatusCallback statusCallback = new Session.StatusCallback() {

        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            showMsgInfo("session isopen " + session.isOpened());
            showMsgInfo("session state " + state);
            if (session.isOpened()) {
                Request.newMeRequest(session, graphUserCallback).executeAsync();
            } else {
                showMsgError("Please Make sure you have override the onActivityResult() in your Activity.");
            }
            if (exception != null) {
                showMsgError(exception.toString());
            }
        }
    };

    /**
     * Constructor
     *
     * @param activity reference of Activity
     */
    public SIFacebook(Activity activity) {
        if (activity == null)
            throw new NullPointerException("Activity instance cannot be null");
        this.activity = activity;
    }

    public SIFacebook(Activity activity, SIFacebookListener listener) {
        if (activity == null)
            throw new NullPointerException("Activity instance cannot be null");
        this.activity = activity;
        if (listener == null)
            throw new NullPointerException("listener must not be null");
        this.siFacebookListener = listener;
    }

    /**
     * Set callback listener to listen success/failure of various events.
     *
     * @param siFacebookListener reference of SIFacebookListener
     */
    public void setSiFacebookListener(SIFacebookListener siFacebookListener) {
        if (siFacebookListener == null)
            throw new NullPointerException("listener must not be null");
        this.siFacebookListener = siFacebookListener;
    }

    /**
     * To get URL of Facebook profile picture of user.
     *
     * @return URL of Facebook profile picture of user
     */
    public URL getProfilePictureUrl() {
        try {
            return new URL("https://graph.facebook.com/" + graphUser.getId()
                    + "/picture?type=small");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * To download Facebook profile picture of user. Resulting picture will be
     * delivered as Bitmap via callback method of
     * SIFacebookListener#onGetProfilePictureBitmap().
     */
    @SuppressLint("NewApi")
    public void getProfilePictureBitmap() {

        URL imageUrl;
        try {
            imageUrl = new URL("https://graph.facebook.com/"
                    + graphUser.getId() + "/picture?type=small");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new DownloadAsync().executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR, imageUrl);
            } else {
                new DownloadAsync().execute(imageUrl);
            }
        } catch (MalformedURLException e) {
            showMsgError("exception in getProfilePictureBitmap: "
                    + e.toString());
        }
    }

    /**
     * Method to start facebook login process
     *
     * @param allowLoginUI If allowLoginUI is true, this will create a new Session, make
     *                     it active, and open it. If allowedLoginUI is false, this will
     *                     only create the active session and open it if it requires no
     *                     user interaction (i.e. the token cache is available and there
     *                     are cached tokens).
     */
    public void logIn(boolean allowLoginUI) {
        login(allowLoginUI, null);
    }

    /**
     * Method to start login to facebook process with a list of permissions.
     *
     * @param allowLoginUI   If allowLoginUI is true, this will create a new Session, make
     *                       it active, and open it. If allowedLoginUI is false, this will
     *                       only create the active session and open it if it requires no
     *                       user interaction (i.e. the token cache is available and there
     *                       are cached tokens).
     * @param permissionList List of permissions to access user information. use
     *                       SIFacebook.{@link com.example.intelligrape.arsampeapp.socialintegration.facebook.SIFacebook.Permissions_V1} for version 1 or SIFacebook.
     *                       {@link com.example.intelligrape.arsampeapp.socialintegration.facebook.SIFacebook.Permissions_V2} for version 2.
     */
    public void login(boolean allowLoginUI, List<String> permissionList) {

        if (siFacebookListener == null) {
            showMsgError("Please register SIFacebookListener before calling login().");
            return;
        }

        if (!isActivityDefined()) {
            showMsgError("Please define com.facebook.LoginActivity in AndroidManifest.xml as <activity android:name=\"com.facebook.LoginActivity\" />.");
            return;
        }

        if (!isMetadataDefined()) {
            showMsgError("Please provide Application ID in AndroidManifest.xml <meta-data android:name=\"com.facebook.sdk.ApplicationId\" android:value=\"@string/app_id\" /> inside <application>.");
            return;
        }
        if (permissionList != null) {
            showMsgInfo("entered in login with permission list..."
                    + permissionList.toString());
            showMsgInfo("opening session...");
            Session.openActiveSession(activity, allowLoginUI, permissionList,
                    statusCallback);

        } else {
            showMsgInfo("entered in login without permission list...");
            showMsgInfo("opening session...");
            Session.openActiveSession(activity, allowLoginUI, statusCallback);
        }
    }

    /**
     * To logout active facebook session
     */
    public void logOut() {
        Session.getActiveSession().closeAndClearTokenInformation();
        siFacebookListener.onLogOut();
    }

    /**
     * To check if a metadata tag is provided in AndroidManifest.xml
     *
     * @return true if mentioned, false otherwise
     */
    private boolean isMetadataDefined() {
        ApplicationInfo ai = null;
        try {
            ai = activity.getPackageManager().getApplicationInfo(
                    activity.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        String applicationId = null;
        try {
            if (ai != null) {
                applicationId = ai.metaData.getString(APPLICATION_ID_PROPERTY);
            }
        } catch (Exception ignored) {
        }

        return applicationId != null;
    }

    /**
     * To check if com.facebook.LoginActivity is defined in AndroidManifest.xml
     *
     * @return true if defined, false otherwise
     */
    private boolean isActivityDefined() {
        Intent intent = new Intent(activity, LoginActivity.class);
        List<ResolveInfo> list = activity.getPackageManager()
                .queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * To generate SHA KeyHash for a given package. The generated SHA key can be
     * provided to Facebook dashboard.
     *
     * @param packageName main package name of app
     */
    public void getKeyHash(String packageName) {
        try {
            PackageInfo info = activity.getPackageManager().getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                showMsgInfo("KeyHash:"
                        + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception ignored) {
            showMsgError("Exception in getKeyHash: " + ignored);
        }
    }

    /**
     * Must be called in onActivityResult inside Activity implementing
     * SIFacebook
     *
     * @param requestCode requestCode from Activity's onActivityResult
     * @param resultCode  resultCode from Activity's onActivityResult
     * @param data        data from Activity's onActivityResult
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == com.facebook.Session.DEFAULT_AUTHORIZE_ACTIVITY_CODE
                && resultCode == Activity.RESULT_OK) {

            Session.getActiveSession().onActivityResult(activity, requestCode,
                    resultCode, data);

        }
    }

    /**
     * To get extra information about user from Graph API. -Get version by
     * calling SIFacebook.GraphVersion and Get graphPath by calling
     * SIFacebook.GraphPath
     *
     * @param graphVersion use GraphVersion to provide Graph API version
     * @param graphPath    use GraphPath to provide Graph API path
     */
    public void getExtras(String graphVersion, String graphPath) {
        getExtras(graphVersion, "me", graphPath);
    }

    /**
     * To get extra information about user from Graph API using user id -Get
     * version by calling SIFacebook.GraphVersion and Get graphPath by calling
     * SIFacebook.GraphPath
     *
     * @param version   use GraphVersion to provide Graph API version
     * @param userId    provide user id
     * @param graphPath use GraphPath to provide Graph API path
     */
    public void getExtras(String version, String userId, String graphPath) {
        new Request(Session.getActiveSession(), version + userId + "/"
                + graphPath, null, HttpMethod.GET, new Request.Callback() {
            public void onCompleted(Response response) {
                siFacebookListener.onGetExtras(response);
            }
        }).executeAsync();
    }

    /**
     * To check if one list is subset of another. Used to check permission asked
     * by developer and permission required for action.
     *
     * @param subset   list to be inspected
     * @param superset list to inspect from
     * @return true if subset is found, false otherwise
     */
    private boolean isSubsetOf(Collection<String> subset,
                               Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }

    /**
     * To share a link to Facebook
     *
     * @param link        valid URL to be shared
     * @param name        title of URL
     * @param caption     subtitle of URL
     * @param description a snippet of text describing the content of the link
     * @param pictureUrl  the url of a thumbnail to associate with the post
     */
    public void shareLink(String link, String name, String caption,
                          String description, String pictureUrl) {
        // link must be passed
        Session session = Session.getActiveSession();
        if (session != null && session.getState().isOpened()
                && isPermitted(session)) {

            Bundle postParams = new Bundle();
            postParams.putString("link", link);
            postParams.putString("name", name);
            postParams.putString("caption", caption);
            postParams.putString("description", description);
            postParams.putString("picture", pictureUrl);

            Request.Callback callback = new Request.Callback() {
                public void onCompleted(Response response) {
                    siFacebookListener.onShareLink(response);
                }
            };

            Request request = new Request(session, "me/feed", postParams,
                    HttpMethod.POST, callback); // me/photos

            RequestAsyncTask task = new RequestAsyncTask(request);
            task.execute();
        }
    }

    /**
     * To share photo
     *
     * @param image       bitmap of photo to be shared
     * @param description caption of photo
     */
    public void sharePhoto(Bitmap image, String description) {
        // link must be passed
        Session session = Session.getActiveSession();
        if (session != null && session.getState().isOpened()
                && isPermitted(session)) {

            Request.Callback callback = new Request.Callback() {
                public void onCompleted(Response response) {
                    siFacebookListener.onSharePhoto(response);
                }
            };

            // 1st way
            Request photoRequest = Request.newUploadPhotoRequest(session,
                    image, callback);
            Bundle params = photoRequest.getParameters();
            params.putString("message", description);
            photoRequest.executeAsync();

            // 2nd way
            // Bundle parameters = new Bundle();
            // parameters.putParcelable("image", image);
            //
            // parameters.putString("message", description);
            //
            // Request request = new Request(session, "me/photos", parameters,
            // HttpMethod.POST, callback); // me/photos
            //
            // RequestAsyncTask task = new RequestAsyncTask(request);
            // task.execute();

        }
    }

    /**
     * To share photo
     *
     * @param image       file to be shared
     * @param description caption of image to be shared
     * @throws java.io.FileNotFoundException if file is not found
     */
    public void sharePhoto(File image, String description)
            throws FileNotFoundException {
        // link must be passed

        Session session = Session.getActiveSession();
        if (session != null && session.getState().isOpened()
                && isPermitted(session)) {

            Request.Callback callback = new Request.Callback() {
                public void onCompleted(Response response) {
                    siFacebookListener.onSharePhoto(response);
                }
            };
            Request photoRequest = Request.newUploadPhotoRequest(session,
                    image, callback);
            Bundle params = photoRequest.getParameters();
            params.putString("message", description);
            photoRequest.executeAsync();
        }
    }

    /**
     * To share video
     *
     * @param video       file to be shared
     * @param description description of video
     * @throws java.io.FileNotFoundException
     */
    public void shareVideo(File video, String description)
            throws FileNotFoundException {
        Session session = Session.getActiveSession();
        if (session != null && session.getState().isOpened()
                && isPermitted(session)) {
            Request.Callback callback = new Request.Callback() {
                @Override
                public void onCompleted(Response response) {
                    siFacebookListener.onShareVideo(response);

                }
            };
            Request videoRequest = Request.newUploadVideoRequest(session,
                    video, callback);
            Bundle params = videoRequest.getParameters();
            params.putString("message", description);
            params.putString("filename", video.getName());
            videoRequest.executeAsync();
        }

    }

    private boolean isPermitted(Session session) {
        // Check for share permissions
        List<String> permissions = session.getPermissions();

        if (!isSubsetOf(PERMISSIONS, permissions)) {
            Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
                    activity, PERMISSIONS);
            session.requestNewPublishPermissions(newPermissionsRequest);
            return false;
        }
        return true;
    }

    /**
     * To share message with link to Facebook
     *
     * @param message         status message to be shared
     * @param link            link to be attached to the status update (optional)
     * @param linkName        link title (optional)
     * @param linkDescription description of link to be attached (optional)
     * @param linkPicture     thumbnail image of URL
     */
    public void shareMessage(String message, String link, String linkName,
                             String linkDescription, String linkPicture) {
        Session session = Session.getActiveSession();
        if (session != null && session.getState().isOpened()
                && isPermitted(session)) {

            Request.Callback callback = new Request.Callback() {
                public void onCompleted(Response response) {
                    siFacebookListener.onShareMessage(response);
                }
            };

            Request request = Request.newStatusUpdateRequest(session, message,
                    callback);

            Bundle parameters = request.getParameters();
            if (link != null)
                parameters.putString("link", link);
            if (linkDescription != null)
                parameters.putString("description", linkDescription);
            if (linkName != null)
                parameters.putString("name", linkName);
            if (linkPicture != null)
                parameters.putString("picture", linkPicture);

            request.executeAsync();
        }
    }

    /**
     * To share message to Facebook
     *
     * @param message status message to be shared
     */
    public void shareMessage(String message) {
        Session session = Session.getActiveSession();
        if (session != null && session.getState().isOpened()
                && isPermitted(session)) {

            Request.Callback callback = new Request.Callback() {
                public void onCompleted(Response response) {
                    siFacebookListener.onShareMessage(response);
                }
            };

            Request request = Request.newStatusUpdateRequest(session, message,
                    callback);

            request.executeAsync();
        }
    }

    private void showMsgInfo(String string) {
        if (DEBUG)
            Log.i(TAG, string);
    }

    private void showMsgError(String string) {
        if (DEBUG)
            Log.e(TAG, string);
    }

    /**
     * List of Graph API versions
     */
    public interface GraphVersion {

        String V1 = "/";
        String V2 = "/v2.0/";

    }

    /**
     * List of Graph Api Paths
     */
    public interface GraphPath {

        public static final String FRIENDS = "friends";
        public static final String TAGGABLE_FRIENDS = "taggable_friends";
        public static final String INVITABLE_FRIENDS = "invitable_friends";
        public static final String ALBUMS = "albums";
        public static final String SCORES = "scores";
        public static final String APPREQUESTS = "apprequests";
        public static final String FEED = "feed";
        public static final String PHOTOS = "photos";
        public static final String VIDEOS = "videos";
        public static final String CHECKINS = "checkins";
        public static final String EVENTS = "events";
        public static final String GROUPS = "groups";
        public static final String POSTS = "posts";
        public static final String COMMENTS = "comments";
        public static final String LIKES = "likes";
        public static final String LINKS = "links";
        public static final String STATUSES = "statuses";
        public static final String TAGGED = "tagged";
        public static final String ACCOUNTS = "accounts";
        public static final String BOOKS = "books";
        public static final String MUSIC = "music";
        public static final String FAMILY = "family";
        public static final String MOVIES = "movies";
        public static final String GAMES = "games";
        public static final Object NOTIFICATIONS = "notifications";
        public static final String TELEVISION = "television";
        public static final String OBJECTS = "objects";

    }

    /**
     * List of Graph API permission for Graph API v1
     */
    public interface Permissions_V1 extends Permissions {
        String BASIC_INFO = "basic_info";
        String USER_CHECKINS = "user_checkins";
        String USER_NOTES = "user_notes";
        String USER_QUESTIONS = "user_questions";
        String USER_SUBSCRIPTIONS = "user_subscriptions";

        // Friends
        String FRIENDS_ABOUT_ME = "friends_about_me";
        String FRIENDS_ACTIVITIES = "friends_activities";
        String FRIENDS_BIRTHDAY = "friends_birthday";
        String FRIENDS_EDUCATION_HISTORY = "friends_education_history";
        String FRIENDS_EVENTS = "friends_events";
        String FRIENDS_GROUPS = "friends_groups";
        String FRIENDS_HOMETOWN = "friends_hometown";
        String FRIENDS_INTERESTS = "friends_interests";
        String FRIENDS_LIKES = "friends_likes";
        String FRIENDS_LOCATION = "friends_location";
        String FRIENDS_PHOTOS = "friends_photos";
        String FRIENDS_RELATIONSHIPS = "friends_relationships";
        String FRIENDS_RELATIONSHIP_DETAILS = "friends_relationship_details";
        String FRIENDS_RELIGION_POLITICS = "friends_religion_politics";
        String FRIENDS_STATUS = "friends_status";
        String FRIENDS_VIDEOS = "friends_videos";
        String FRIENDS_WEBSITE = "friends_website";
        String FRIENDS_WORK_HISTORY = "friends_work_history";

        // Extended permissions - Read
        String READ_REQUESTS = "read_requests";
        String XMPP_LOGIN = "xmpp_login";
        String USER_ONLINE_PRESENCE = "user_online_presence";
        String FRIENDS_ONLINE_PRESENCE = "friends_online_presence";

        // Extended Permissions - Publish
        String ADS_MANAGEMENT = "ads_management";
        String CREATE_EVENT = "create_event";
        String MANAGE_FRIENDLISTS = "manage_friendlists";
        String PUBLISH_STREAM = "publish_stream";

        // Open Graph Permissions
        String USER_GAMES_ACTIVITIES = "user_games_activities";

        String FRIENDS_GAMES_ACTIVITIES = "friends_games_activities";
        String FRIENDS_ACTIONS_MUSIC = "friends_actions.music";
        String FRIENDS_ACTIONS_NEWS = "friends_actions.news";
        String FRIENDS_ACTIONS_VIDEO = "friends_actions.video";
        String FRIENDS_ACTIONS_APP_NAMESPACE = "friends_actions:APP_NAMESPACE";
    }

    /**
     * List of Graph API Permissions for Graph API v2
     */
    public interface Permissions_V2 extends Permissions {

        // Open Graph Permissions
        String USER_ACTIONS_BOOKS = "user_actions.books";
        String USER_ACTIONS_FITNESS = "user_actions.fitness";

        // User Permissions
        String PUBLIC_PROFILE = "public_profile";
        // Friends
        String USER_FRIENDS = "user_friends";
        // Extented Profile Properties
        String USER_TAGGED_PLACES = "user_tagged_places";

    }

    /**
     * List of Graph API Permissions common in Graph API v1 and v2
     */
    private interface Permissions {

        String PUBLISH_ACTIONS = "publish_actions";
        // Open Graph Permissions
        String USER_ACTIONS_MUSIC = "user_actions.music";
        String USER_ACTIONS_NEWS = "user_actions.news";
        String USER_ACTIONS_VIDEO = "user_actions.video";
        String USER_ACTIONS_APP_NAMESPACE = "user_actions:" + "APP_NAMESPACE";

        // Email
        String EMAIL = "email";
        // Extented Profile Properties
        String USER_ABOUT_ME = "user_about_me";
        String USER_ACTIVITIES = "user_activities";
        String USER_BIRTHDAY = "user_birthday";
        String USER_EDUCATION_HISTORY = "user_education_history";
        String USER_EVENTS = "user_events";
        String USER_GROUPS = "user_groups";
        String USER_HOMETOWN = "user_hometown";
        String USER_INTERESTS = "user_interests";
        String USER_LIKES = "user_likes";
        String USER_LOCATION = "user_location";
        String USER_PHOTOS = "user_photos";
        String USER_RELATIONSHIPS = "user_relationships";
        String USER_RELATIONSHIP_DETAILS = "user_relationship_details";
        String USER_RELIGION_POLITICS = "user_religion_politics";
        String USER_STATUS = "user_status";
        String USER_VIDEOS = "user_videos";
        String USER_WEBSITE = "user_website";
        String USER_WORK_HISTORY = "user_work_history";

        // Extended permissions - Read
        String READ_FRIENDLISTS = "read_friendlists";
        String READ_INSIGHTS = "read_insights";
        String READ_MAILBOX = "read_mailbox";
        String READ_STREAM = "read_stream";
        // Extended permissions - Publish
        String MANAGE_NOTIFICATIONS = "manage_notfications";
        String RSVP_EVENTS = "rsvp_events";

        // Pages Permissions
        String MANAGE_PAGES = "manage_pages";
        String READ_PAGE_MAILBOXES = "read_page_mailboxes";
    }

    /**
     * Listener class to listen events and results
     */
    public static abstract class SIFacebookListener {
        /**
         * Called if Facebook login is success
         *
         * @param graphUser user information
         */
        public abstract void onLogIn(GraphUser graphUser);

        /**
         * Called if Facebook logout is success
         */
        public abstract void onLogOut();

        /**
         * Called if Facebook login is failed
         *
         * @param errorMessage short description of error
         */
        public abstract void onConnectionFailure(String errorMessage);

        /**
         * Called after downloading profile Picture
         *
         * @param imageBitmap bitmap of user profile image
         */
        public void onGetProfilePictureBitmap(Bitmap imageBitmap) {
        }

        /**
         * Called after executing getExtras
         *
         * @param response extra information received
         */
        public void onGetExtras(Response response) {
        }

        /**
         * Called after executing sharePhoto
         *
         * @param response information about share photo
         */
        public void onSharePhoto(Response response) {
        }

        /**
         * Called after executing shareVideo
         *
         * @param response information about shared video
         */
        public void onShareVideo(Response response) {
        }

        /**
         * Called after executing shareLink
         *
         * @param response information about shared link
         */
        public void onShareLink(Response response) {
        }

        /**
         * Called after executing shareMessage
         *
         * @param response information about shared status
         */
        public void onShareMessage(Response response) {
        }

        /**
         * Gets the underlying JSONObject representation of this graph object
         * after login() success.
         *
         * @param jsonObject the underlying JSONObject representation of this graph
         *                   object
         */
        public void jsonObject(JSONObject jsonObject) {

        }

    }

    /**
     * AsyncTask to download profile image. Resulting image will be sent as
     * Bitmap via callback method profileImage()
     */
    private class DownloadAsync extends AsyncTask<URL, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(URL... params) {
            return downloadImage(params[0]);
        }

        private Bitmap downloadImage(URL imageURL) {
            try {
                HttpURLConnection connection = (HttpURLConnection) imageURL
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();

                return BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (siFacebookListener != null) {
                siFacebookListener.onGetProfilePictureBitmap(result);
            }
        }
    }

}