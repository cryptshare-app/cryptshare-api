package com.uni.share.user.types;

import java.util.List;

public class SearchUserNamesTO {
    private List<String> userNames;

    public SearchUserNamesTO() {
    }

    public SearchUserNamesTO(List<String> userNames) {
        this.userNames = userNames;
    }

    public List<String> getUserNames() {
        return userNames;
    }

    public void setUserNames(List<String> userNames) {
        this.userNames = userNames;
    }
}
