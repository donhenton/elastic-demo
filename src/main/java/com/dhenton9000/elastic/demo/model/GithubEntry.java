package com.dhenton9000.elastic.demo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class GithubEntry {

    public static GithubEntry createEntry(Map<String, Object> source) {
        GithubEntry g = new GithubEntry();
        g.setOwner(source.get("owner") + "");
        g.setDescription(source.get("description") + "");
        g.setUrl(source.get("url") + "");
        g.setStarCount(intParse(source.get("stars")));
        g.setForkCount(intParse(source.get("forks")));
        g.setWatcherCount(intParse(source.get("watchers")));
        g.setName((source.get("name") + ""));
        g.setLanguage((source.get("language") + ""));
        ArrayList<String> topics = (ArrayList<String>) source.get("topics");
        g.setTopics(topics);
        g.setAvatarUrl((source.get("avatar") + ""));
        String createDateString = source.get("created") + "";
        String[] dateParts = createDateString.split("Z");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime createDate = LocalDateTime.parse(dateParts[0], formatter);
        g.setCreated(createDate);
        Object id = source.get("id");
        if (id != null) {
            g.setId(id.toString());
        }
        

        return g;
    }

    private static int intParse(Object source) throws NumberFormatException {
        if (source == null) {
            return 0;
        }
        try {
            return Integer.parseInt(source + "");
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String name;
    private String owner;
    private String description;
    private String avatarUrl;
    private String url;
    private LocalDateTime created;
    private int starCount;
    private int forkCount;
    private ArrayList<String> topics;
    private String language;
    private int watcherCount;
    private String id = null;
    private String highlightText;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the avatarUrl
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * @param avatarUrl the avatarUrl to set
     */
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the created
     */
    public LocalDateTime getCreated() {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    /**
     * @return the starCount
     */
    public int getStarCount() {
        return starCount;
    }

    /**
     * @param starCount the starCount to set
     */
    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }

    /**
     * @return the forkCount
     */
    public int getForkCount() {
        return forkCount;
    }

    /**
     * @param forkCount the forkCount to set
     */
    public void setForkCount(int forkCount) {
        this.forkCount = forkCount;
    }

    /**
     * @return the topics
     */
    public ArrayList<String> getTopics() {
        return topics;
    }

    /**
     * @param topics the topics to set
     */
    public void setTopics(ArrayList<String> topics) {
        this.topics = topics;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return the watcherCount
     */
    public int getWatcherCount() {
        return watcherCount;
    }

    /**
     * @param watcherCount the watcherCount to set
     */
    public void setWatcherCount(int watcherCount) {
        this.watcherCount = watcherCount;
    }

    @Override
    public String toString() {
        return "GithubEntry{" + "name=" + name + ", owner=" + owner + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.url);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GithubEntry other = (GithubEntry) obj;
        if (!Objects.equals(this.url, other.url)) {
            return false;
        }
        return true;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the highlightText
     */
    public String getHighlightText() {
        return highlightText;
    }

    /**
     * @param highlightText the highlightText to set
     */
    public void setHighlightText(String highlightText) {
        this.highlightText = highlightText;
    }

}
