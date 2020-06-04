package es.deusto.androidapp.manager;

import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;

public class UserPropertyManager {

    private static UserPropertyManager instance;
    private int userCreator;
    private int userInfluencer;
    private int userPastryChef;
    private int userMeatEating;
    private int userFishEating;
    private int userVeggie;

    public static UserPropertyManager getInstance() {
        if (instance == null) {
            instance = new UserPropertyManager();
        }
        return instance;
    }

    private UserPropertyManager() {
        this.userCreator = 0;
        this.userInfluencer = 0;
        this.userPastryChef = 0;
        this.userMeatEating = 0;
        this.userFishEating = 0;
        this.userVeggie = 0;
    }

    public void registerUserAsCreator (Context context) {
        userCreator++;
        if (userCreator >= 3 && userCreator < 6) {
            FirebaseAnalytics.getInstance(context).setUserProperty("creator", "medium");
        } else if (userCreator >= 6) {
            FirebaseAnalytics.getInstance(context).setUserProperty("creator", "high");
        }
    }

    public void registerUserAsInfluencer (Context context) {
        userInfluencer++;
        if (userInfluencer >= 4) {
            FirebaseAnalytics.getInstance(context).setUserProperty("influencer", "high");
        }
    }

    public void registerUserAsPastryChef (Context context) {
        userPastryChef++;
        if (userPastryChef >= 4) {
            FirebaseAnalytics.getInstance(context).setUserProperty("pastry_chef", "high");
        }
    }

    public void registerUserAsMeatEating (Context context) {
        userMeatEating++;
        if (userMeatEating >= 5) {
            FirebaseAnalytics.getInstance(context).setUserProperty("meat_eating", "high");
        }
    }

    public void registerUserAsVeggie (Context context) {
        userVeggie++;
        if (userVeggie >= 5) {
            if (userMeatEating == 0 && userFishEating == 0) {
                FirebaseAnalytics.getInstance(context).setUserProperty("veggie", "high");
            }
        }
    }

    public void incrementFishEatingUser () {
        this.userFishEating++;
    }

    public void decrementPastryChefUser () {
        if (this.userPastryChef > 0) {
            this.userPastryChef--;
        }
    }

    public void decrementMeatEatingUser () {
        if (this.userMeatEating > 0) {
            this.userMeatEating--;
        }
    }

    public void decrementFishEatingUser () {
        if (this.userFishEating > 0) {
            this.userFishEating--;
        }
    }

    public void decrementVeggieEatingUser () {
        if (this.userVeggie > 0) {
            this.userVeggie--;
        }
    }

}
