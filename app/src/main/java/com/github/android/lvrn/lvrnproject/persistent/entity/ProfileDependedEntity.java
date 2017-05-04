package com.github.android.lvrn.lvrnproject.persistent.entity;

import android.support.annotation.NonNull;

/**
 * @author Vadim Boitsov <vadimboitsov1@gmail.com>
 */

public abstract class ProfileDependedEntity extends Entity {

    /**
     * An id of the profile, which the entity is belonged.
     */
    protected String profileId;

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    @NonNull
    @Override
    public String toString() {
        return "ProfileDependedEntity{" + super.toString() +
                "profileId='" + profileId + '\'' +
                '}';
    }
}