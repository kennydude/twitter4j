/*
 * Copyright 2007 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package twitter4j.internal.json;

import twitter4j.*;
import twitter4j.conf.Configuration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;

import static twitter4j.internal.util.z_T4JInternalParseUtil.*;

/**
 * A data class representing a Tweet in the search response
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
/*package*/ final class TweetJSONImpl implements Tweet, java.io.Serializable {
    private static final long serialVersionUID = 3019285230338056113L;
    private String text;
    private long toUserId = -1;
    private String toUser = null;
    private String toUserName = null;
    private String fromUser = null;
    private String fromUserName = null;
    private long id;
    private long fromUserId;
    private String isoLanguageCode = null;
    private String source;
    private long inReplyToStatusId = -1;
    private String profileImageUrl;
    private Date createdAt;
    private String location;
    private Place place;

    private GeoLocation geoLocation = null;
    private Annotations annotations = null;
    private UserMentionEntity[] userMentionEntities;
    private URLEntity[] urlEntities;
    private HashtagEntity[] hashtagEntities;
    private MediaEntity[] mediaEntities;

    /*package*/ TweetJSONImpl(JSONObject tweet) throws TwitterException {
        text = getUnescapedString("text", tweet);
        toUserId = getLong("to_user_id", tweet);
        toUser = getRawString("to_user", tweet);
        toUserName = getRawString("to_user_name", tweet);
        fromUser = getRawString("from_user", tweet);
        fromUserName = getRawString("from_user_name", tweet);
        id = getLong("id", tweet);
        fromUserId = getLong("from_user_id", tweet);
        isoLanguageCode = getRawString("iso_language_code", tweet);
        source = getUnescapedString("source", tweet);
        inReplyToStatusId = getLong("in_reply_to_status_id", tweet);
        profileImageUrl = getUnescapedString("profile_image_url", tweet);
        createdAt = getDate("created_at", tweet, "EEE, dd MMM yyyy HH:mm:ss z");
        location = getRawString("location", tweet);
        geoLocation = z_T4JInternalJSONImplFactory.createGeoLocation(tweet);
        if (!tweet.isNull("annotations")) {
            try {
                JSONArray annotationsArray = tweet.getJSONArray("annotations");
                annotations = new Annotations(annotationsArray);
            } catch (JSONException ignore) {
            }
        }
        if (!tweet.isNull("place")) {
            try {
                place = new PlaceJSONImpl(tweet.getJSONObject("place"));
            } catch (JSONException jsone) {
                throw new TwitterException(jsone);
            }
        } else {
            place = null;
        }
        if (!tweet.isNull("entities")) {
            try {
                JSONObject entities = tweet.getJSONObject("entities");
                int len;
                if (!entities.isNull("user_mentions")) {
                    JSONArray userMentionsArray = entities.getJSONArray("user_mentions");
                    len = userMentionsArray.length();
                    userMentionEntities = new UserMentionEntity[len];
                    for (int i = 0; i < len; i++) {
                        userMentionEntities[i] = new UserMentionEntityJSONImpl(userMentionsArray.getJSONObject(i));
                    }

                }
                if (!entities.isNull("urls")) {
                    JSONArray urlsArray = entities.getJSONArray("urls");
                    len = urlsArray.length();
                    urlEntities = new URLEntity[len];
                    for (int i = 0; i < len; i++) {
                        urlEntities[i] = new URLEntityJSONImpl(urlsArray.getJSONObject(i));
                    }
                }

                if (!entities.isNull("hashtags")) {
                    JSONArray hashtagsArray = entities.getJSONArray("hashtags");
                    len = hashtagsArray.length();
                    hashtagEntities = new HashtagEntity[len];
                    for (int i = 0; i < len; i++) {
                        hashtagEntities[i] = new HashtagEntityJSONImpl(hashtagsArray.getJSONObject(i));
                    }
                }

                if (!entities.isNull("media")) {
                    JSONArray mediaArray = entities.getJSONArray("media");
                    len = mediaArray.length();
                    mediaEntities = new MediaEntity[len];
                    for (int i = 0; i < len; i++) {
                        mediaEntities[i] = new MediaEntityJSONImpl(mediaArray.getJSONObject(i));
                    }
                }
            } catch (JSONException jsone) {
                throw new TwitterException(jsone);
            }
        }
    }

    /*package*/ TweetJSONImpl(JSONObject tweet, Configuration conf) throws TwitterException {
        this(tweet);
        if (conf.isJSONStoreEnabled()) {
            DataObjectFactoryUtil.registerJSONObject(this, tweet);
        }
    }

    @Override
    public int compareTo(Tweet that) {
        long delta = this.id - that.getId();
        if (delta < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        } else if (delta > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) delta;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText() {
        return text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getToUserId() {
        return toUserId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getToUser() {
        return toUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getToUserName() {
        return toUserName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFromUser() {
        return fromUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFromUserName() {
        return fromUserName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getFromUserId() {
        return fromUserId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIsoLanguageCode() {
        return isoLanguageCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSource() {
        return source;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getInReplyToStatusId() {
        return inReplyToStatusId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLocation() {
        return location;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Place getPlace() {
        return place;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserMentionEntity[] getUserMentionEntities() {
        return userMentionEntities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URLEntity[] getURLEntities() {
        return urlEntities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HashtagEntity[] getHashtagEntities() {
        return hashtagEntities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MediaEntity[] getMediaEntities() {
        return mediaEntities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Annotations getAnnotations() {
        return annotations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tweet)) return false;

        Tweet tweet = (Tweet) o;

        if (id != tweet.getId()) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = text != null ? text.hashCode() : 0;
        result = 31 * result + (int) (toUserId ^ (toUserId >>> 32));
        result = 31 * result + (toUser != null ? toUser.hashCode() : 0);
        result = 31 * result + (toUserName != null ? toUserName.hashCode() : 0);
        result = 31 * result + (fromUser != null ? fromUser.hashCode() : 0);
        result = 31 * result + (fromUserName != null ? fromUserName.hashCode() : 0);
        result = 31 * result + (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (fromUserId ^ (fromUserId >>> 32));
        result = 31 * result + (isoLanguageCode != null ? isoLanguageCode.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (int) (inReplyToStatusId ^ (inReplyToStatusId >>> 32));
        result = 31 * result + (profileImageUrl != null ? profileImageUrl.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (place != null ? place.hashCode() : 0);
        result = 31 * result + (geoLocation != null ? geoLocation.hashCode() : 0);
        result = 31 * result + (annotations != null ? annotations.hashCode() : 0);
        result = 31 * result + (userMentionEntities != null ? Arrays.hashCode(userMentionEntities) : 0);
        result = 31 * result + (urlEntities != null ? Arrays.hashCode(urlEntities) : 0);
        result = 31 * result + (hashtagEntities != null ? Arrays.hashCode(hashtagEntities) : 0);
        result = 31 * result + (mediaEntities != null ? Arrays.hashCode(mediaEntities) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TweetJSONImpl{" +
                "text='" + text + '\'' +
                ", toUserId=" + toUserId +
                ", toUser='" + toUser + '\'' +
                ", toUserName='" + toUserName + '\'' +
                ", fromUser='" + fromUser + '\'' +
                ", fromUserName='" + fromUserName + '\'' +
                ", id=" + id +
                ", fromUserId=" + fromUserId +
                ", isoLanguageCode='" + isoLanguageCode + '\'' +
                ", source='" + source + '\'' +
                ", inReplyToStatusId=" + inReplyToStatusId +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", createdAt=" + createdAt +
                ", location='" + location + '\'' +
                ", place=" + place +
                ", geoLocation=" + geoLocation +
                ", annotations=" + annotations +
                ", userMentionEntities=" + (userMentionEntities == null ? null : Arrays.asList(userMentionEntities)) +
                ", urlEntities=" + (urlEntities == null ? null : Arrays.asList(urlEntities)) +
                ", hashtagEntities=" + (hashtagEntities == null ? null : Arrays.asList(hashtagEntities)) +
                ", mediaEntities=" + (mediaEntities == null ? null : Arrays.asList(mediaEntities)) +
                '}';
    }
}
