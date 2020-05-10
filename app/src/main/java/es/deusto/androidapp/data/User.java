package es.deusto.androidapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class User implements Parcelable {

    private String username;
    private String fullName;
    private String email;
    private String password;

    public User(String username, String fullName, String email, String password) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @NonNull
    @Override
    public String toString() {
        String user = "Name: " + this.fullName + "\n";
        user = user +  "Username: " + this.username + "\n";
        user = user +  "Email: " + this.email + "\n";
        user = user +  "Password: " + this.password + "\n";

        return user;
    }

    public User (Parcel p){
        username = p.readString();
        fullName = p.readString();
        email = p.readString();
        password = p.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.username);
        dest.writeString(this.fullName);
        dest.writeString(this.email);
        dest.writeString(this.password);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>(){
        @Override
        public User createFromParcel(Parcel parcel) {
            return new User(parcel);
        }

        @Override
        public User[] newArray(int s) {
            return new User[s];
        }
    };

}
