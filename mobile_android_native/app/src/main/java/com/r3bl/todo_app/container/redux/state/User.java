package com.r3bl.todo_app.container.redux.state;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;

/**
 * Created by nazmul on 11/10/16.
 */

public class User implements Serializable {

// make sure that anything you want to save to firebase is marked public
public Object  timestamp;
public String  uid;
public String  providerId;
public String  displayName;
public String  photoUrl;
public String  email;
public boolean emailVerified;
public boolean isAnonymous;

public User() {
  // default constructor required for firebase DataSnapshot.getValue(UserObject.class)
}

public User(FirebaseUser param) {
  if (param.getUid() != null) {
    uid = param.getUid();
  }
  if (param.getProviderId() != null) {
    providerId = param.getProviderId();
  }
  if (param.getDisplayName() != null) {
    displayName = param.getDisplayName();
  }
  if (param.getPhotoUrl() != null) {
    photoUrl = param.getPhotoUrl().toString();
  }
  if (param.getEmail() != null) {
    email = param.getEmail();
  }
  emailVerified = param.isEmailVerified();
  isAnonymous = param.isAnonymous();

  timestamp = ServerValue.TIMESTAMP;
}

/**
 * more info on firebase server side timestamps and android - https://goo.gl/l4FNQk
 */
public long timestampValue() {
  try {
    return (long) timestamp;
  } catch (Exception e) {
    return -1;
  }
}

@Override
public String toString() {

  Gson gson = new GsonBuilder().setPrettyPrinting()
                               .serializeNulls()
                               .create();
  return gson.toJson(this);

}

@Override
public boolean equals(Object o) {
  if (this == o) return true;
  if (o == null || getClass() != o.getClass()) return false;

  User user = (User) o;

  if (emailVerified != user.emailVerified) return false;
  if (isAnonymous != user.isAnonymous) return false;
  if (uid != null ? !uid.equals(user.uid) : user.uid != null) return false;
  if (providerId != null ? !providerId.equals(user.providerId) : user.providerId != null)
    return false;
  if (displayName != null ? !displayName.equals(user.displayName) : user.displayName != null)
    return false;
  if (photoUrl != null ? !photoUrl.equals(user.photoUrl) : user.photoUrl != null)
    return false;
  return email != null ? email.equals(user.email) : user.email == null;
}

@Override
public int hashCode() {
  int result = uid != null ? uid.hashCode() : 0;
  result = 31 * result + (providerId != null ? providerId.hashCode() : 0);
  result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
  result = 31 * result + (photoUrl != null ? photoUrl.hashCode() : 0);
  result = 31 * result + (email != null ? email.hashCode() : 0);
  result = 31 * result + (emailVerified ? 1 : 0);
  result = 31 * result + (isAnonymous ? 1 : 0);
  return result;
}
}// end class UserObject