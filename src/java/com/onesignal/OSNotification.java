package com.onesignal;

import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONArray;
import java.util.ArrayList;
import androidx.core.app.NotificationCompat$Extender;
import org.json.JSONObject;
import java.util.List;

public class OSNotification
{
    private List<ActionButton> actionButtons;
    private JSONObject additionalData;
    private int androidNotificationId;
    private BackgroundImageLayout backgroundImageLayout;
    private String bigPicture;
    private String body;
    private String collapseId;
    private String fromProjectNumber;
    private String groupKey;
    private String groupMessage;
    private List<OSNotification> groupedNotifications;
    private String largeIcon;
    private String launchURL;
    private String ledColor;
    private int lockScreenVisibility;
    private NotificationCompat$Extender notificationExtender;
    private String notificationId;
    private int priority;
    private String rawPayload;
    private long sentTime;
    private String smallIcon;
    private String smallIconAccentColor;
    private String sound;
    private String templateId;
    private String templateName;
    private String title;
    private int ttl;
    
    protected OSNotification() {
        this.lockScreenVisibility = 1;
    }
    
    protected OSNotification(final OSNotification osNotification) {
        this.lockScreenVisibility = 1;
        this.notificationExtender = osNotification.notificationExtender;
        this.groupedNotifications = osNotification.groupedNotifications;
        this.androidNotificationId = osNotification.androidNotificationId;
        this.notificationId = osNotification.notificationId;
        this.templateName = osNotification.templateName;
        this.templateId = osNotification.templateId;
        this.title = osNotification.title;
        this.body = osNotification.body;
        this.additionalData = osNotification.additionalData;
        this.smallIcon = osNotification.smallIcon;
        this.largeIcon = osNotification.largeIcon;
        this.bigPicture = osNotification.bigPicture;
        this.smallIconAccentColor = osNotification.smallIconAccentColor;
        this.launchURL = osNotification.launchURL;
        this.sound = osNotification.sound;
        this.ledColor = osNotification.ledColor;
        this.lockScreenVisibility = osNotification.lockScreenVisibility;
        this.groupKey = osNotification.groupKey;
        this.groupMessage = osNotification.groupMessage;
        this.actionButtons = osNotification.actionButtons;
        this.fromProjectNumber = osNotification.fromProjectNumber;
        this.backgroundImageLayout = osNotification.backgroundImageLayout;
        this.collapseId = osNotification.collapseId;
        this.priority = osNotification.priority;
        this.rawPayload = osNotification.rawPayload;
        this.sentTime = osNotification.sentTime;
        this.ttl = osNotification.ttl;
    }
    
    OSNotification(final List<OSNotification> groupedNotifications, final JSONObject jsonObject, final int androidNotificationId) {
        this.lockScreenVisibility = 1;
        this.initPayloadData(jsonObject);
        this.groupedNotifications = groupedNotifications;
        this.androidNotificationId = androidNotificationId;
    }
    
    OSNotification(final JSONObject jsonObject) {
        this(null, jsonObject, 0);
    }
    
    private void initPayloadData(final JSONObject backgroundImageLayout) {
        try {
            final JSONObject customJSONObject = NotificationBundleProcessor.getCustomJSONObject(backgroundImageLayout);
            final long currentTimeMillis = OneSignal.getTime().getCurrentTimeMillis();
            if (backgroundImageLayout.has("google.ttl")) {
                this.sentTime = backgroundImageLayout.optLong("google.sent_time", currentTimeMillis) / 1000L;
                this.ttl = backgroundImageLayout.optInt("google.ttl", 259200);
            }
            else if (backgroundImageLayout.has("hms.ttl")) {
                this.sentTime = backgroundImageLayout.optLong("hms.sent_time", currentTimeMillis) / 1000L;
                this.ttl = backgroundImageLayout.optInt("hms.ttl", 259200);
            }
            else {
                this.sentTime = currentTimeMillis / 1000L;
                this.ttl = 259200;
            }
            this.notificationId = customJSONObject.optString("i");
            this.templateId = customJSONObject.optString("ti");
            this.templateName = customJSONObject.optString("tn");
            this.rawPayload = backgroundImageLayout.toString();
            this.additionalData = customJSONObject.optJSONObject("a");
            this.launchURL = customJSONObject.optString("u", (String)null);
            this.body = backgroundImageLayout.optString("alert", (String)null);
            this.title = backgroundImageLayout.optString("title", (String)null);
            this.smallIcon = backgroundImageLayout.optString("sicon", (String)null);
            this.bigPicture = backgroundImageLayout.optString("bicon", (String)null);
            this.largeIcon = backgroundImageLayout.optString("licon", (String)null);
            this.sound = backgroundImageLayout.optString("sound", (String)null);
            this.groupKey = backgroundImageLayout.optString("grp", (String)null);
            this.groupMessage = backgroundImageLayout.optString("grp_msg", (String)null);
            this.smallIconAccentColor = backgroundImageLayout.optString("bgac", (String)null);
            this.ledColor = backgroundImageLayout.optString("ledc", (String)null);
            final String optString = backgroundImageLayout.optString("vis", (String)null);
            if (optString != null) {
                this.lockScreenVisibility = Integer.parseInt(optString);
            }
            this.fromProjectNumber = backgroundImageLayout.optString("from", (String)null);
            this.priority = backgroundImageLayout.optInt("pri", 0);
            final String optString2 = backgroundImageLayout.optString("collapse_key", (String)null);
            if (!"do_not_collapse".equals((Object)optString2)) {
                this.collapseId = optString2;
            }
            try {
                this.setActionButtons();
            }
            finally {
                final Throwable t;
                OneSignal.Log(OneSignal.LOG_LEVEL.ERROR, "Error assigning OSNotificationReceivedEvent.actionButtons values!", t);
            }
            try {
                this.setBackgroundImageLayout(backgroundImageLayout);
            }
            finally {
                final Throwable t2;
                OneSignal.Log(OneSignal.LOG_LEVEL.ERROR, "Error assigning OSNotificationReceivedEvent.backgroundImageLayout values!", t2);
            }
        }
        finally {
            final Throwable t3;
            OneSignal.Log(OneSignal.LOG_LEVEL.ERROR, "Error assigning OSNotificationReceivedEvent payload values!", t3);
        }
    }
    
    private void setActionButtons() throws Throwable {
        final JSONObject additionalData = this.additionalData;
        if (additionalData != null && additionalData.has("actionButtons")) {
            final JSONArray jsonArray = this.additionalData.getJSONArray("actionButtons");
            this.actionButtons = (List<ActionButton>)new ArrayList();
            for (int i = 0; i < jsonArray.length(); ++i) {
                final JSONObject jsonObject = jsonArray.getJSONObject(i);
                final ActionButton actionButton = new ActionButton();
                actionButton.id = jsonObject.optString("id", (String)null);
                actionButton.text = jsonObject.optString("text", (String)null);
                actionButton.icon = jsonObject.optString("icon", (String)null);
                this.actionButtons.add((Object)actionButton);
            }
            this.additionalData.remove("actionId");
            this.additionalData.remove("actionButtons");
        }
    }
    
    private void setBackgroundImageLayout(JSONObject jsonObject) throws Throwable {
        final String optString = jsonObject.optString("bg_img", (String)null);
        if (optString != null) {
            jsonObject = new JSONObject(optString);
            (this.backgroundImageLayout = new BackgroundImageLayout()).image = jsonObject.optString("img");
            this.backgroundImageLayout.titleTextColor = jsonObject.optString("tc");
            this.backgroundImageLayout.bodyTextColor = jsonObject.optString("bc");
        }
    }
    
    private void setSentTime(final long sentTime) {
        this.sentTime = sentTime;
    }
    
    private void setTtl(final int ttl) {
        this.ttl = ttl;
    }
    
    OSNotification copy() {
        return new OSNotificationBuilder().setNotificationExtender(this.notificationExtender).setGroupedNotifications(this.groupedNotifications).setAndroidNotificationId(this.androidNotificationId).setNotificationId(this.notificationId).setTemplateName(this.templateName).setTemplateId(this.templateId).setTitle(this.title).setBody(this.body).setAdditionalData(this.additionalData).setSmallIcon(this.smallIcon).setLargeIcon(this.largeIcon).setBigPicture(this.bigPicture).setSmallIconAccentColor(this.smallIconAccentColor).setLaunchURL(this.launchURL).setSound(this.sound).setLedColor(this.ledColor).setLockScreenVisibility(this.lockScreenVisibility).setGroupKey(this.groupKey).setGroupMessage(this.groupMessage).setActionButtons(this.actionButtons).setFromProjectNumber(this.fromProjectNumber).setBackgroundImageLayout(this.backgroundImageLayout).setCollapseId(this.collapseId).setPriority(this.priority).setRawPayload(this.rawPayload).setSenttime(this.sentTime).setTTL(this.ttl).build();
    }
    
    public List<ActionButton> getActionButtons() {
        return this.actionButtons;
    }
    
    public JSONObject getAdditionalData() {
        return this.additionalData;
    }
    
    public int getAndroidNotificationId() {
        return this.androidNotificationId;
    }
    
    public BackgroundImageLayout getBackgroundImageLayout() {
        return this.backgroundImageLayout;
    }
    
    public String getBigPicture() {
        return this.bigPicture;
    }
    
    public String getBody() {
        return this.body;
    }
    
    public String getCollapseId() {
        return this.collapseId;
    }
    
    public String getFromProjectNumber() {
        return this.fromProjectNumber;
    }
    
    public String getGroupKey() {
        return this.groupKey;
    }
    
    public String getGroupMessage() {
        return this.groupMessage;
    }
    
    public List<OSNotification> getGroupedNotifications() {
        return this.groupedNotifications;
    }
    
    public String getLargeIcon() {
        return this.largeIcon;
    }
    
    public String getLaunchURL() {
        return this.launchURL;
    }
    
    public String getLedColor() {
        return this.ledColor;
    }
    
    public int getLockScreenVisibility() {
        return this.lockScreenVisibility;
    }
    
    public NotificationCompat$Extender getNotificationExtender() {
        return this.notificationExtender;
    }
    
    public String getNotificationId() {
        return this.notificationId;
    }
    
    public int getPriority() {
        return this.priority;
    }
    
    public String getRawPayload() {
        return this.rawPayload;
    }
    
    public long getSentTime() {
        return this.sentTime;
    }
    
    public String getSmallIcon() {
        return this.smallIcon;
    }
    
    public String getSmallIconAccentColor() {
        return this.smallIconAccentColor;
    }
    
    public String getSound() {
        return this.sound;
    }
    
    public String getTemplateId() {
        return this.templateId;
    }
    
    public String getTemplateName() {
        return this.templateName;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public int getTtl() {
        return this.ttl;
    }
    
    boolean hasNotificationId() {
        return this.androidNotificationId != 0;
    }
    
    public OSMutableNotification mutableCopy() {
        return new OSMutableNotification(this);
    }
    
    void setActionButtons(final List<ActionButton> actionButtons) {
        this.actionButtons = actionButtons;
    }
    
    void setAdditionalData(final JSONObject additionalData) {
        this.additionalData = additionalData;
    }
    
    protected void setAndroidNotificationId(final int androidNotificationId) {
        this.androidNotificationId = androidNotificationId;
    }
    
    void setBackgroundImageLayout(final BackgroundImageLayout backgroundImageLayout) {
        this.backgroundImageLayout = backgroundImageLayout;
    }
    
    void setBigPicture(final String bigPicture) {
        this.bigPicture = bigPicture;
    }
    
    void setBody(final String body) {
        this.body = body;
    }
    
    void setCollapseId(final String collapseId) {
        this.collapseId = collapseId;
    }
    
    void setFromProjectNumber(final String fromProjectNumber) {
        this.fromProjectNumber = fromProjectNumber;
    }
    
    void setGroupKey(final String groupKey) {
        this.groupKey = groupKey;
    }
    
    void setGroupMessage(final String groupMessage) {
        this.groupMessage = groupMessage;
    }
    
    void setGroupedNotifications(final List<OSNotification> groupedNotifications) {
        this.groupedNotifications = groupedNotifications;
    }
    
    void setLargeIcon(final String largeIcon) {
        this.largeIcon = largeIcon;
    }
    
    void setLaunchURL(final String launchURL) {
        this.launchURL = launchURL;
    }
    
    void setLedColor(final String ledColor) {
        this.ledColor = ledColor;
    }
    
    void setLockScreenVisibility(final int lockScreenVisibility) {
        this.lockScreenVisibility = lockScreenVisibility;
    }
    
    protected void setNotificationExtender(final NotificationCompat$Extender notificationExtender) {
        this.notificationExtender = notificationExtender;
    }
    
    void setNotificationId(final String notificationId) {
        this.notificationId = notificationId;
    }
    
    void setPriority(final int priority) {
        this.priority = priority;
    }
    
    void setRawPayload(final String rawPayload) {
        this.rawPayload = rawPayload;
    }
    
    void setSmallIcon(final String smallIcon) {
        this.smallIcon = smallIcon;
    }
    
    void setSmallIconAccentColor(final String smallIconAccentColor) {
        this.smallIconAccentColor = smallIconAccentColor;
    }
    
    void setSound(final String sound) {
        this.sound = sound;
    }
    
    void setTemplateId(final String templateId) {
        this.templateId = templateId;
    }
    
    void setTemplateName(final String templateName) {
        this.templateName = templateName;
    }
    
    void setTitle(final String title) {
        this.title = title;
    }
    
    public JSONObject toJSONObject() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("androidNotificationId", this.androidNotificationId);
            final JSONArray jsonArray = new JSONArray();
            final List<OSNotification> groupedNotifications = this.groupedNotifications;
            if (groupedNotifications != null) {
                final Iterator iterator = groupedNotifications.iterator();
                while (iterator.hasNext()) {
                    jsonArray.put((Object)((OSNotification)iterator.next()).toJSONObject());
                }
            }
            jsonObject.put("groupedNotifications", (Object)jsonArray);
            jsonObject.put("notificationId", (Object)this.notificationId);
            jsonObject.put("templateName", (Object)this.templateName);
            jsonObject.put("templateId", (Object)this.templateId);
            jsonObject.put("title", (Object)this.title);
            jsonObject.put("body", (Object)this.body);
            jsonObject.put("smallIcon", (Object)this.smallIcon);
            jsonObject.put("largeIcon", (Object)this.largeIcon);
            jsonObject.put("bigPicture", (Object)this.bigPicture);
            jsonObject.put("smallIconAccentColor", (Object)this.smallIconAccentColor);
            jsonObject.put("launchURL", (Object)this.launchURL);
            jsonObject.put("sound", (Object)this.sound);
            jsonObject.put("ledColor", (Object)this.ledColor);
            jsonObject.put("lockScreenVisibility", this.lockScreenVisibility);
            jsonObject.put("groupKey", (Object)this.groupKey);
            jsonObject.put("groupMessage", (Object)this.groupMessage);
            jsonObject.put("fromProjectNumber", (Object)this.fromProjectNumber);
            jsonObject.put("collapseId", (Object)this.collapseId);
            jsonObject.put("priority", this.priority);
            final JSONObject additionalData = this.additionalData;
            if (additionalData != null) {
                jsonObject.put("additionalData", (Object)additionalData);
            }
            if (this.actionButtons != null) {
                final JSONArray jsonArray2 = new JSONArray();
                final Iterator iterator2 = this.actionButtons.iterator();
                while (iterator2.hasNext()) {
                    jsonArray2.put((Object)((ActionButton)iterator2.next()).toJSONObject());
                }
                jsonObject.put("actionButtons", (Object)jsonArray2);
            }
            jsonObject.put("rawPayload", (Object)this.rawPayload);
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObject;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OSNotification{notificationExtender=");
        sb.append((Object)this.notificationExtender);
        sb.append(", groupedNotifications=");
        sb.append((Object)this.groupedNotifications);
        sb.append(", androidNotificationId=");
        sb.append(this.androidNotificationId);
        sb.append(", notificationId='");
        sb.append(this.notificationId);
        sb.append("', templateName='");
        sb.append(this.templateName);
        sb.append("', templateId='");
        sb.append(this.templateId);
        sb.append("', title='");
        sb.append(this.title);
        sb.append("', body='");
        sb.append(this.body);
        sb.append("', additionalData=");
        sb.append((Object)this.additionalData);
        sb.append(", smallIcon='");
        sb.append(this.smallIcon);
        sb.append("', largeIcon='");
        sb.append(this.largeIcon);
        sb.append("', bigPicture='");
        sb.append(this.bigPicture);
        sb.append("', smallIconAccentColor='");
        sb.append(this.smallIconAccentColor);
        sb.append("', launchURL='");
        sb.append(this.launchURL);
        sb.append("', sound='");
        sb.append(this.sound);
        sb.append("', ledColor='");
        sb.append(this.ledColor);
        sb.append("', lockScreenVisibility=");
        sb.append(this.lockScreenVisibility);
        sb.append(", groupKey='");
        sb.append(this.groupKey);
        sb.append("', groupMessage='");
        sb.append(this.groupMessage);
        sb.append("', actionButtons=");
        sb.append((Object)this.actionButtons);
        sb.append(", fromProjectNumber='");
        sb.append(this.fromProjectNumber);
        sb.append("', backgroundImageLayout=");
        sb.append((Object)this.backgroundImageLayout);
        sb.append(", collapseId='");
        sb.append(this.collapseId);
        sb.append("', priority=");
        sb.append(this.priority);
        sb.append(", rawPayload='");
        sb.append(this.rawPayload);
        sb.append("'}");
        return sb.toString();
    }
    
    public static class ActionButton
    {
        private String icon;
        private String id;
        private String text;
        
        public ActionButton() {
        }
        
        public ActionButton(final String id, final String text, final String icon) {
            this.id = id;
            this.text = text;
            this.icon = icon;
        }
        
        public ActionButton(final JSONObject jsonObject) {
            this.id = jsonObject.optString("id");
            this.text = jsonObject.optString("text");
            this.icon = jsonObject.optString("icon");
        }
        
        public String getIcon() {
            return this.icon;
        }
        
        public String getId() {
            return this.id;
        }
        
        public String getText() {
            return this.text;
        }
        
        public JSONObject toJSONObject() {
            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", (Object)this.id);
                jsonObject.put("text", (Object)this.text);
                jsonObject.put("icon", (Object)this.icon);
            }
            finally {
                final Throwable t;
                t.printStackTrace();
            }
            return jsonObject;
        }
    }
    
    public static class BackgroundImageLayout
    {
        private String bodyTextColor;
        private String image;
        private String titleTextColor;
        
        public String getBodyTextColor() {
            return this.bodyTextColor;
        }
        
        public String getImage() {
            return this.image;
        }
        
        public String getTitleTextColor() {
            return this.titleTextColor;
        }
    }
    
    public static class OSNotificationBuilder
    {
        private List<ActionButton> actionButtons;
        private JSONObject additionalData;
        private int androidNotificationId;
        private BackgroundImageLayout backgroundImageLayout;
        private String bigPicture;
        private String body;
        private String collapseId;
        private String fromProjectNumber;
        private String groupKey;
        private String groupMessage;
        private List<OSNotification> groupedNotifications;
        private String largeIcon;
        private String launchURL;
        private String ledColor;
        private int lockScreenVisibility;
        private NotificationCompat$Extender notificationExtender;
        private String notificationId;
        private int priority;
        private String rawPayload;
        private long sentTime;
        private String smallIcon;
        private String smallIconAccentColor;
        private String sound;
        private String templateId;
        private String templateName;
        private String title;
        private int ttl;
        
        public OSNotificationBuilder() {
            this.lockScreenVisibility = 1;
        }
        
        public OSNotification build() {
            final OSNotification osNotification = new OSNotification();
            osNotification.setNotificationExtender(this.notificationExtender);
            osNotification.setGroupedNotifications(this.groupedNotifications);
            osNotification.setAndroidNotificationId(this.androidNotificationId);
            osNotification.setNotificationId(this.notificationId);
            osNotification.setTemplateName(this.templateName);
            osNotification.setTemplateId(this.templateId);
            osNotification.setTitle(this.title);
            osNotification.setBody(this.body);
            osNotification.setAdditionalData(this.additionalData);
            osNotification.setSmallIcon(this.smallIcon);
            osNotification.setLargeIcon(this.largeIcon);
            osNotification.setBigPicture(this.bigPicture);
            osNotification.setSmallIconAccentColor(this.smallIconAccentColor);
            osNotification.setLaunchURL(this.launchURL);
            osNotification.setSound(this.sound);
            osNotification.setLedColor(this.ledColor);
            osNotification.setLockScreenVisibility(this.lockScreenVisibility);
            osNotification.setGroupKey(this.groupKey);
            osNotification.setGroupMessage(this.groupMessage);
            osNotification.setActionButtons(this.actionButtons);
            osNotification.setFromProjectNumber(this.fromProjectNumber);
            osNotification.setBackgroundImageLayout(this.backgroundImageLayout);
            osNotification.setCollapseId(this.collapseId);
            osNotification.setPriority(this.priority);
            osNotification.setRawPayload(this.rawPayload);
            osNotification.setSentTime(this.sentTime);
            osNotification.setTtl(this.ttl);
            return osNotification;
        }
        
        public OSNotificationBuilder setActionButtons(final List<ActionButton> actionButtons) {
            this.actionButtons = actionButtons;
            return this;
        }
        
        public OSNotificationBuilder setAdditionalData(final JSONObject additionalData) {
            this.additionalData = additionalData;
            return this;
        }
        
        public OSNotificationBuilder setAndroidNotificationId(final int androidNotificationId) {
            this.androidNotificationId = androidNotificationId;
            return this;
        }
        
        public OSNotificationBuilder setBackgroundImageLayout(final BackgroundImageLayout backgroundImageLayout) {
            this.backgroundImageLayout = backgroundImageLayout;
            return this;
        }
        
        public OSNotificationBuilder setBigPicture(final String bigPicture) {
            this.bigPicture = bigPicture;
            return this;
        }
        
        public OSNotificationBuilder setBody(final String body) {
            this.body = body;
            return this;
        }
        
        public OSNotificationBuilder setCollapseId(final String collapseId) {
            this.collapseId = collapseId;
            return this;
        }
        
        public OSNotificationBuilder setFromProjectNumber(final String fromProjectNumber) {
            this.fromProjectNumber = fromProjectNumber;
            return this;
        }
        
        public OSNotificationBuilder setGroupKey(final String groupKey) {
            this.groupKey = groupKey;
            return this;
        }
        
        public OSNotificationBuilder setGroupMessage(final String groupMessage) {
            this.groupMessage = groupMessage;
            return this;
        }
        
        public OSNotificationBuilder setGroupedNotifications(final List<OSNotification> groupedNotifications) {
            this.groupedNotifications = groupedNotifications;
            return this;
        }
        
        public OSNotificationBuilder setLargeIcon(final String largeIcon) {
            this.largeIcon = largeIcon;
            return this;
        }
        
        public OSNotificationBuilder setLaunchURL(final String launchURL) {
            this.launchURL = launchURL;
            return this;
        }
        
        public OSNotificationBuilder setLedColor(final String ledColor) {
            this.ledColor = ledColor;
            return this;
        }
        
        public OSNotificationBuilder setLockScreenVisibility(final int lockScreenVisibility) {
            this.lockScreenVisibility = lockScreenVisibility;
            return this;
        }
        
        public OSNotificationBuilder setNotificationExtender(final NotificationCompat$Extender notificationExtender) {
            this.notificationExtender = notificationExtender;
            return this;
        }
        
        public OSNotificationBuilder setNotificationId(final String notificationId) {
            this.notificationId = notificationId;
            return this;
        }
        
        public OSNotificationBuilder setPriority(final int priority) {
            this.priority = priority;
            return this;
        }
        
        public OSNotificationBuilder setRawPayload(final String rawPayload) {
            this.rawPayload = rawPayload;
            return this;
        }
        
        public OSNotificationBuilder setSenttime(final long sentTime) {
            this.sentTime = sentTime;
            return this;
        }
        
        public OSNotificationBuilder setSmallIcon(final String smallIcon) {
            this.smallIcon = smallIcon;
            return this;
        }
        
        public OSNotificationBuilder setSmallIconAccentColor(final String smallIconAccentColor) {
            this.smallIconAccentColor = smallIconAccentColor;
            return this;
        }
        
        public OSNotificationBuilder setSound(final String sound) {
            this.sound = sound;
            return this;
        }
        
        public OSNotificationBuilder setTTL(final int ttl) {
            this.ttl = ttl;
            return this;
        }
        
        public OSNotificationBuilder setTemplateId(final String templateId) {
            this.templateId = templateId;
            return this;
        }
        
        public OSNotificationBuilder setTemplateName(final String templateName) {
            this.templateName = templateName;
            return this;
        }
        
        public OSNotificationBuilder setTitle(final String title) {
            this.title = title;
            return this;
        }
    }
}
