package com.doesnothaveadomain.firebase_cloud_messaging.utilities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.doesnothaveadomain.firebase_cloud_messaging.Models.KeyValuePair;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil
{
	public static String NODE_DEVICETOKENS = "DeviceTokens";
	public static ArrayList<Object> mTravelDeals;
	public static boolean isAdmin;
	
	public static FirebaseDatabase mFirebaseDb;
	public static DatabaseReference mDbRef;
	public static FirebaseStorage mStorage;
	public static StorageReference mStorageRef;
	
	private static FirebaseUtil mFirebaseUtil;
	private static FirebaseAuth mFirebaseAuth;
	private static FirebaseAuth.AuthStateListener mAuthStateListner;
	private static Activity caller;
	private static final int RC_SIGN_IN = 123;
	
	private FirebaseUtil() { }
	
	public static void openFirebaseReference(final Activity callerActivity)
	{
		if(mFirebaseUtil == null)
		{
			mFirebaseUtil = new FirebaseUtil();
			mFirebaseDb = FirebaseDatabase.getInstance();
			mFirebaseAuth = FirebaseAuth.getInstance();
			caller = callerActivity;
			mAuthStateListner = new FirebaseAuth.AuthStateListener()
			{
				@Override
				public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
				{
					if(firebaseAuth.getCurrentUser() == null)
					{
						signin();
						Toast.makeText(callerActivity.getBaseContext(), "Welcome back!", Toast.LENGTH_SHORT).show();
						//FirebaseUtil.attachListner();
					}
					else
					{
						String uid = firebaseAuth.getCurrentUser().getUid();
						checkAdmin(uid);
					}
				}
			};
			connectStorage();
		}
		mTravelDeals = new ArrayList<>();
		mDbRef = mFirebaseDb.getReference();
	}
	
	
	
	private static void signin()
	{
		// Choose authentication providers
		List<AuthUI.IdpConfig> providers = Arrays.asList(
				new AuthUI.IdpConfig.EmailBuilder().build(),
				new AuthUI.IdpConfig.PhoneBuilder().build(),
				new AuthUI.IdpConfig.GoogleBuilder().build()
		);
		
		// Create and launch sign-in intent
		caller.startActivityForResult(
				AuthUI.getInstance()
						.createSignInIntentBuilder()
						.setAvailableProviders(providers)
						.build(),
				RC_SIGN_IN);
	}
	
	private static void checkAdmin(String uid)
	{
		FirebaseUtil.isAdmin = false;
		DatabaseReference dbRef = mFirebaseDb.getReference().child("admins").child(uid);
		ChildEventListener listner = new ChildEventListener()
		{
			@Override
			public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
			{
				FirebaseUtil.isAdmin = true;
				Log.d("admin-signed", "admin user signed-in.");
			}
			
			@Override
			public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
			{
			
			}
			
			@Override
			public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
			{
			
			}
			
			@Override
			public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
			{
			
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError)
			{
			
			}
		};
		dbRef.addChildEventListener(listner);
	}
	
	
	public static void attachListner()
	{
		mFirebaseAuth.addAuthStateListener(mAuthStateListner);
	}
	
	public static void detachListner()
	{
		mFirebaseAuth.removeAuthStateListener(mAuthStateListner);
	}
	
	public static void connectStorage()
	{
		mStorage = FirebaseStorage.getInstance();
		mStorageRef = mStorage.getReference().child("???"); // todo
	}
	
	public static void InsertDeviceToken(String deviceToken)
	{
		FirebaseUtil.mDbRef.child(FirebaseUtil.NODE_DEVICETOKENS)
				.push()
				.setValue(new KeyValuePair(Build.MODEL, deviceToken));
	}
}
